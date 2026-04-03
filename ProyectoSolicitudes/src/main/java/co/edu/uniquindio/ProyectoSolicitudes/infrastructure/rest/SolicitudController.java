package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.*;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.*;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.SolicitudMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para el agregado Solicitud.
 *
 * Principio clave: este controller NO contiene lógica de negocio.
 * Solo recibe la petición HTTP, delega al servicio de dominio
 * y retorna la respuesta mapeada.
 *
 * Cuando Jennifer entregue los servicios de dominio se conectan aquí.
 */
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Gestión del agregado Solicitud")
public class SolicitudController {

    private final SolicitudMapper mapper;

    // ── AQUÍ SE INYECTAN LOS SERVICIOS CUANDO JENNIFER LOS ENTREGUE ──
    // private final RegistrarSolicitudService registrarSolicitudService;
    // private final ClasificarSolicitudService clasificarSolicitudService;
    // private final AsignarResponsableService asignarResponsableService;

    // ==================== POST /api/solicitudes ====================

    @PostMapping
    @Operation(summary = "Registrar una nueva solicitud")
    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<SolicitudDetalleResponse> crear(
            @Valid @RequestBody CrearSolicitudRequest request) {
        // TODO: conectar con RegistrarSolicitudService cuando Jennifer lo entregue
        return ResponseEntity.status(201).build();
    }

    // ==================== GET /api/solicitudes ====================

    @GetMapping
    @Operation(summary = "Listar solicitudes")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public ResponseEntity<List<SolicitudResumenResponse>> listar(
            @RequestParam(required = false) String estado) {
        // TODO: conectar con servicio de consulta
        return ResponseEntity.ok(List.of());
    }

    // ==================== GET /api/solicitudes/{id} ====================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una solicitud")
    @ApiResponse(responseCode = "200", description = "Detalle de la solicitud")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<SolicitudDetalleResponse> obtener(
            @PathVariable String id) {
        // TODO: conectar con servicio de consulta
        return ResponseEntity.ok().build();
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
        // TODO: conectar con ClasificarSolicitudService
        return ResponseEntity.ok().build();
    }

    // ==================== PUT /api/solicitudes/{id}/asignar ====================

    @PutMapping("/{id}/asignar")
    @Operation(summary = "Asignar responsable a una solicitud")
    @ApiResponse(responseCode = "200", description = "Responsable asignado exitosamente")
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes")
    public ResponseEntity<SolicitudDetalleResponse> asignarResponsable(
            @PathVariable String id,
            @Valid @RequestBody AsignarResponsableRequest request) {
        // TODO: conectar con AsignarResponsableService
        return ResponseEntity.ok().build();
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
        // TODO: conectar con servicio de cierre
        return ResponseEntity.ok().build();
    }

    // ==================== GET /api/solicitudes/{id}/historial ====================

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de una solicitud")
    @ApiResponse(responseCode = "200", description = "Historial de la solicitud")
    public ResponseEntity<List<EventoHistorialResponse>> historial(
            @PathVariable String id) {
        // TODO: conectar con servicio de consulta
        return ResponseEntity.ok(List.of());
    }
}