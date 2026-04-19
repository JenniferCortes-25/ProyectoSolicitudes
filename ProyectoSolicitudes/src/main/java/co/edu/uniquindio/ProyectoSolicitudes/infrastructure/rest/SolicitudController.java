package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.*;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.*;
import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.SolicitudMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller REST para el agregado Solicitud.
 * Guías 07 y 08 — adaptador delgado entre HTTP y la capa de aplicación.
 * CERO lógica de negocio aquí.
 */
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Gestión del agregado Solicitud")
public class SolicitudController {

    private final SolicitudMapper mapper;
    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;
    private final ClasificarSolicitudUseCase clasificarSolicitudUseCase;
    private final AsignarResponsableUseCase asignarResponsableUseCase;
    private final CerrarSolicitudUseCase cerrarSolicitudUseCase;
    private final ConsultarSolicitudesUseCase consultarSolicitudesUseCase;

    @PostMapping
    @Operation(summary = "Registrar una nueva solicitud",
               description = "Crea la solicitud en estado REGISTRADA. El solicitante debe existir con su identificación.")
    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Solicitante no encontrado")
    public ResponseEntity<SolicitudDetalleResponse> crear(
            @Valid @RequestBody CrearSolicitudRequest request) {

        Solicitud solicitud = registrarSolicitudUseCase.ejecutar(
                request.descripcion(),
                request.canalOrigen(),
                request.solicitanteId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(solicitud.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDetalleResponse(solicitud));
    }

    @GetMapping
    @Operation(summary = "Listar solicitudes con filtro opcional por estado")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResumenResponse>> listar(
            @RequestParam(required = false) EstadoSolicitud estado) {

        List<Solicitud> resultado = consultarSolicitudesUseCase.listar(
                Optional.ofNullable(estado));
        return ResponseEntity.ok(mapper.toResumenResponseList(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una solicitud")
    @ApiResponse(responseCode = "200", description = "Detalle de la solicitud")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<SolicitudDetalleResponse> obtener(
            @PathVariable String id) {

        Solicitud solicitud = consultarSolicitudesUseCase.obtenerPorId(UUID.fromString(id));
        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    @PutMapping("/{id}/clasificar")
    @Operation(summary = "Clasificar una solicitud",
               description = "Asigna tipo y prioridad. Solo COORDINADOR. La solicitud debe estar en REGISTRADA.")
    @ApiResponse(responseCode = "200", description = "Solicitud clasificada exitosamente")
    @ApiResponse(responseCode = "400", description = "Transición de estado inválida o datos incorrectos")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    @ApiResponse(responseCode = "404", description = "Solicitud o coordinador no encontrado")
    public ResponseEntity<SolicitudDetalleResponse> clasificar(
            @PathVariable String id,
            @Valid @RequestBody ClasificarSolicitudRequest request) {

        Solicitud solicitud = clasificarSolicitudUseCase.ejecutar(
                UUID.fromString(id),
                request.tipo(),
                request.nivelPrioridad(),
                request.justificacionPrioridad(),
                request.coordinadorId()
        );
        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    @PutMapping("/{id}/asignar")
    @Operation(summary = "Asignar responsable a una solicitud",
               description = "Solo COORDINADOR puede asignar. El responsable debe estar activo.")
    @ApiResponse(responseCode = "200", description = "Responsable asignado exitosamente")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    @ApiResponse(responseCode = "404", description = "Solicitud, responsable o coordinador no encontrado")
    public ResponseEntity<SolicitudDetalleResponse> asignarResponsable(
            @PathVariable String id,
            @Valid @RequestBody AsignarResponsableRequest request) {

        Solicitud solicitud = asignarResponsableUseCase.ejecutar(
                UUID.fromString(id),
                request.responsableId(),
                request.coordinadorId()
        );
        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    @PutMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar una solicitud",
               description = "Solo COORDINADOR puede cerrar. La solicitud debe estar en ATENDIDA.")
    @ApiResponse(responseCode = "200", description = "Solicitud cerrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Transición de estado inválida")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    @ApiResponse(responseCode = "404", description = "Solicitud o coordinador no encontrado")
    public ResponseEntity<SolicitudDetalleResponse> cerrar(
            @PathVariable String id,
            @Valid @RequestBody CerrarSolicitudRequest request) {

        Solicitud solicitud = cerrarSolicitudUseCase.ejecutar(
                UUID.fromString(id),
                request.observacion(),
                request.coordinadorId()
        );
        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de una solicitud")
    @ApiResponse(responseCode = "200", description = "Historial de la solicitud")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<List<EventoHistorialResponse>> historial(
            @PathVariable String id) {

        Solicitud solicitud = consultarSolicitudesUseCase.obtenerPorId(UUID.fromString(id));
        return ResponseEntity.ok(mapper.toEventoResponseList(solicitud.getHistorial()));
    }
}