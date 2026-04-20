package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para iniciar atención de una solicitud.
 * Cambia estado CLASIFICADA → EN_ATENCION.
 * Solo COORDINADOR o ADMINISTRATIVO pueden ejecutarlo.
 */
public record IniciarAtencionRequest(

        @NotBlank(message = "El ID del coordinador es obligatorio")
        String coordinadorId

) {}