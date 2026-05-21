package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.TipoSolicitud;

/**
 * Respuesta del asistente IA con sugerencias de clasificación (RF-10).
 * Las sugerencias DEBEN ser confirmadas o ajustadas por un usuario humano.
 */
public record SugerenciaIaResponse(

        TipoSolicitud tipoSugerido,
        NivelPrioridad prioridadSugerida,
        String justificacion,
        boolean disponible   // false si el servicio IA no está accesible

) {}