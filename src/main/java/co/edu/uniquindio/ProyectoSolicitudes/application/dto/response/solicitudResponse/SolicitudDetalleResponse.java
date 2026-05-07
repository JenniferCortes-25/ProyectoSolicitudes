package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.CanalOrigen;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.TipoSolicitud;

import java.time.LocalDateTime;

/**
 * Vista completa de una solicitud.
 * Usado en: GET /api/solicitudes/{id} y como respuesta de operaciones de negocio.
 *
 * responsableId y responsableNombre pueden ser null si aún no hay responsable asignado.
 */
public record SolicitudDetalleResponse(

        String id,
        String descripcion,
        CanalOrigen canalOrigen,
        EstadoSolicitud estado,
        TipoSolicitud tipo,
        String nivelPrioridad,
        String justificacionPrioridad,
        String solicitanteId,
        String solicitanteNombre,
        String responsableId,
        String responsableNombre,
        LocalDateTime fechaRegistro,
        int totalEntradaHistorial

) {}