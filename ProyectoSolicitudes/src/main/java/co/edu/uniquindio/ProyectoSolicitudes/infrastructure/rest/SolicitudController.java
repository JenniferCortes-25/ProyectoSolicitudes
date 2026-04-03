package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.*;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsignarResponsableService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.ClasificarSolicitudService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.RegistrarSolicitudService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Controller REST para el agregado Solicitud.
 *
 * Principio clave: este controller NO contiene lógica de negocio.
 * Solo recibe la petición HTTP, delega al servicio de dominio
 * y retorna la respuesta mapeada.
 */
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Gestión del agregado Solicitud")
public class SolicitudController {

    private final SolicitudMapper mapper;

    // Servicios de dominio — se instancian aquí hasta Hito 3 con repositorios
    private final RegistrarSolicitudService registrarSolicitudService = new RegistrarSolicitudService();
    private final ClasificarSolicitudService clasificarSolicitudService = new ClasificarSolicitudService();
    private final AsignarResponsableService asignarResponsableService = new AsignarResponsableService();

    // Lista en memoria temporal — se reemplaza con repositorio en Hito 3
    private final List<Solicitud> solicitudes = new ArrayList<>();

    // ==================== POST /api/solicitudes ====================

    @PostMapping
    @Operation(summary = "Registrar una nueva solicitud")
    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<SolicitudDetalleResponse> crear(
            @Valid @RequestBody CrearSolicitudRequest request) {

        // Solicitante temporal hasta que exista repositorio de usuarios
        Usuario solicitante = new Usuario(
                request.solicitanteId(), "Solicitante",
                new Email("solicitante@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE
        );

        Solicitud solicitud = registrarSolicitudService.registrar(
                new DescripcionSolicitud(request.descripcion()),
                request.canalOrigen(),
                solicitante
        );

        solicitudes.add(solicitud);

        SolicitudDetalleResponse response = mapper.toDetalleResponse(solicitud);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(solicitud.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // ==================== GET /api/solicitudes ====================

    @GetMapping
    @Operation(summary = "Listar solicitudes")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResumenResponse>> listar(
            @RequestParam(required = false) String estado) {

        List<Solicitud> resultado = estado == null ? solicitudes :
                solicitudes.stream()
                        .filter(s -> s.getEstado().name().equals(estado))
                        .toList();

        return ResponseEntity.ok(mapper.toResumenResponseList(resultado));
    }

    // ==================== GET /api/solicitudes/{id} ====================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una solicitud")
    @ApiResponse(responseCode = "200", description = "Detalle de la solicitud")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<SolicitudDetalleResponse> obtener(
            @PathVariable String id) {

        return solicitudes.stream()
                .filter(s -> s.getId().toString().equals(id))
                .findFirst()
                .map(s -> ResponseEntity.ok(mapper.toDetalleResponse(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== PUT /api/solicitudes/{id}/clasificar ====================

    @PutMapping("/{id}/clasificar")
    @Operation(summary = "Clasificar una solicitud")
    @ApiResponse(responseCode = "200", description = "Solicitud clasificada exitosamente")
    @ApiResponse(responseCode = "400", description = "Transición de estado inválida")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    public ResponseEntity<SolicitudDetalleResponse> clasificar(
            @PathVariable String id,
            @Valid @RequestBody ClasificarSolicitudRequest request) {

        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) return ResponseEntity.notFound().build();

        // Coordinador temporal hasta repositorio
        Usuario coordinador = new Usuario(
                request.coordinadorId(), "Coordinador",
                new Email("coordinador@uniquindio.edu.co"), TipoUsuario.COORDINADOR
        );

        clasificarSolicitudService.clasificar(
                solicitud,
                request.tipo(),
                new Prioridad(request.nivelPrioridad(), request.justificacionPrioridad()),
                coordinador
        );

        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    // ==================== PUT /api/solicitudes/{id}/asignar ====================

    @PutMapping("/{id}/asignar")
    @Operation(summary = "Asignar responsable a una solicitud")
    @ApiResponse(responseCode = "200", description = "Responsable asignado exitosamente")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    public ResponseEntity<SolicitudDetalleResponse> asignarResponsable(
            @PathVariable String id,
            @Valid @RequestBody AsignarResponsableRequest request) {

        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) return ResponseEntity.notFound().build();

        Usuario coordinador = new Usuario(
                request.coordinadorId(), "Coordinador",
                new Email("coordinador@uniquindio.edu.co"), TipoUsuario.COORDINADOR
        );

        Usuario responsable = new Usuario(
                request.responsableId(), "Responsable",
                new Email("responsable@uniquindio.edu.co"), TipoUsuario.DOCENTE
        );

        asignarResponsableService.asignar(solicitud, responsable, coordinador);

        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    // ==================== PUT /api/solicitudes/{id}/cerrar ====================

    @PutMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar una solicitud")
    @ApiResponse(responseCode = "200", description = "Solicitud cerrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Transición de estado inválida")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    public ResponseEntity<SolicitudDetalleResponse> cerrar(
            @PathVariable String id,
            @Valid @RequestBody CerrarSolicitudRequest request) {

        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) return ResponseEntity.notFound().build();

        Usuario coordinador = new Usuario(
                request.coordinadorId(), "Coordinador",
                new Email("coordinador@uniquindio.edu.co"), TipoUsuario.COORDINADOR
        );

        solicitud.cerrar(new ObservacionCierre(request.observacion()), coordinador);

        return ResponseEntity.ok(mapper.toDetalleResponse(solicitud));
    }

    // ==================== GET /api/solicitudes/{id}/historial ====================

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de una solicitud")
    @ApiResponse(responseCode = "200", description = "Historial de la solicitud")
    public ResponseEntity<List<EventoHistorialResponse>> historial(
            @PathVariable String id) {

        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(mapper.toEventoResponseList(solicitud.getHistorial()));
    }

    // ==================== Helper privado ====================

    private Solicitud buscarPorId(String id) {
        return solicitudes.stream()
                .filter(s -> s.getId().toString().equals(id))
                .findFirst()
                .orElse(null);
    }
}