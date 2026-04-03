package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para asignar un responsable a una solicitud.
 * Mapea a: Solicitud.asignarResponsable()
 *
 * Solo un COORDINADOR puede ejecutar esta operación.
 */
public record AsignarResponsableRequest(

        @NotBlank(message = "El ID del responsable es obligatorio")
        String responsableId,

        @NotBlank(message = "El ID del coordinador es obligatorio")
        String coordinadorId

) {}