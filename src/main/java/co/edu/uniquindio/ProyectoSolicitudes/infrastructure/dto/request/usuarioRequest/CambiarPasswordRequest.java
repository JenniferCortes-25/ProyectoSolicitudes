package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.request.usuarioRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cambiar la contraseña de un usuario.
 * passwordActual: contraseña vigente (para verificar identidad).
 * passwordNueva:  nueva contraseña (mín. 8 caracteres).
 */
public record CambiarPasswordRequest(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 8, message = "La contraseña nueva debe tener al menos 8 caracteres")
        String passwordNueva
) {}
