package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.authRequest;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO inmutable (record) para la petición de login.
 */
public record LoginRequest(
        @NotBlank(message = "El username no puede estar vacío") String username,
        @NotBlank(message = "La contraseña no puede estar vacía") String password
) {}
