package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.request.solicitudRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitar sugerencia de tipo y prioridad mediante IA (RF-10).
 */
public record SugerenciaIaRequest(

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String descripcion

) {}