package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.solicitudRequest;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.CanalOrigen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para registrar una nueva solicitud académica.
 *
 * estado y fechaRegistro — los maneja el dominio automáticamente.
 */
public record CrearSolicitudRequest(

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String descripcion,

        @NotNull(message = "El canal de origen es obligatorio")
        CanalOrigen canalOrigen,

        @NotBlank(message = "El ID del solicitante es obligatorio")
        String solicitanteId

) {}