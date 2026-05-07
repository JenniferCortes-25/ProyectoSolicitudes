package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.solicitudRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para marcar una solicitud como atendida.
 * Cambia estado EN_ATENCION → ATENDIDA.
 * Solo el responsable asignado puede ejecutarlo.
 */
public record AtenderSolicitudRequest(

        @NotBlank(message = "La observación es obligatoria")
        @Size(min = 10, message = "La observación debe tener mínimo 10 caracteres")
        String observacion,

        @NotBlank(message = "El ID del responsable es obligatorio")
        String responsableId

) {}