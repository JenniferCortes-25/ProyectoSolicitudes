package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;

import java.time.LocalDateTime;

/**
 * Vista resumida de una solicitud para listados.
 * Usado en: GET /api/solicitudes
 *
 * descripcionBreve muestra solo los primeros 80 caracteres de la descripción.
 * responsableNombre puede ser null si aún no hay responsable asignado.
 */
public record SolicitudResumenResponse(

        String id,
        String descripcionBreve,
        EstadoSolicitud estado,
        String nivelPrioridad,
        String solicitanteNombre,
        String responsableNombre,
        LocalDateTime fechaRegistro

) {}