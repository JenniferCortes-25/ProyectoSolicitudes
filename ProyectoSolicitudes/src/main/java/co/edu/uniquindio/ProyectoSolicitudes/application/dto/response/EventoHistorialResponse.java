package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response;

import java.time.LocalDateTime;

/**
 * Representa una entrada del historial de una solicitud.
 * Usado en: GET /api/solicitudes/{id}/historial
 *
 * Cada vez que ocurre una acción sobre la solicitud se genera una entrada.
 */
public record EventoHistorialResponse(

        LocalDateTime fechaHora,
        String accion,
        String usuarioResponsable,
        String observaciones

) {}