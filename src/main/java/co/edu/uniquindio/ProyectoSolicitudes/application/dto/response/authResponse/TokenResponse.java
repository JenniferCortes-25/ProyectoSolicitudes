package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.authResponse;

import java.time.Instant;
import java.util.Collection;

/**
 * DTO inmutable (record) para la respuesta con token JWT.
 */
public record TokenResponse(
        String token,
        String type,
        Instant expireAt,
        Collection<String> roles
) {}
