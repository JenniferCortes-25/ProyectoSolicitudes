package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.solicitudRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cerrar una solicitud.
 * Mapea a: Solicitud.cerrar()
 *
 * Solo un COORDINADOR puede ejecutar esta operación.
 * La observación queda registrada en el historial como cierre formal.
 */
public record CerrarSolicitudRequest(

        @NotBlank(message = "La observación de cierre es obligatoria")
        @Size(min = 20, message = "La observación debe tener mínimo 20 caracteres")
        String observacion,

        @NotBlank(message = "El ID del coordinador es obligatorio")
        String coordinadorId

) {}