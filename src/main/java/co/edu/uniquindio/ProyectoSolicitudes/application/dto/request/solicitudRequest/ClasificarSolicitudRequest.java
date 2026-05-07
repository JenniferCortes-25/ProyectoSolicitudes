package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.solicitudRequest;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para clasificar una solicitud existente.
 * Mapea a: Solicitud.clasificar() + Solicitud.asignarPrioridad()
 *
 * Solo un COORDINADOR puede ejecutar esta operación.
 */
public record ClasificarSolicitudRequest(

        @NotNull(message = "El tipo de solicitud es obligatorio")
        TipoSolicitud tipo,

        @NotNull(message = "El nivel de prioridad es obligatorio")
        NivelPrioridad nivelPrioridad,

        @NotBlank(message = "La justificación de prioridad es obligatoria")
        @Size(min = 5, message = "La justificación debe tener mínimo 5 caracteres")
        String justificacionPrioridad,

        @NotBlank(message = "El ID del coordinador es obligatorio")
        String coordinadorId

) {}