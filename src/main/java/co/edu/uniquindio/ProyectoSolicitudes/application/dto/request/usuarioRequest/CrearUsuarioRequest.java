package co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.usuarioRequest;
 
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
 
/**
 * DTO inmutable (record) para la petición de creación de usuario.
 * Solo el COORDINADOR puede invocar el endpoint que lo recibe (RN-13).
 */
public record CrearUsuarioRequest(
 
        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,
 
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
 
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,
 
        @NotNull(message = "El tipo de usuario es obligatorio")
        TipoUsuario tipoUsuario
) {}