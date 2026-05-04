package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Crear un nuevo usuario en el sistema.
 * Solo puede ser ejecutado por un COORDINADOR (RN-13).
 * El @Transactional asegura revertir cambios si algo falla.
 */
@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario ejecutar(String identificacion,
                            String nombre,
                            String emailValor,
                            TipoUsuario tipoUsuario) {

        if (usuarioRepository.findByIdentificacion(identificacion).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con identificación: " + identificacion);
        }

        Usuario usuario = new Usuario(
                identificacion,
                nombre,
                new Email(emailValor),
                tipoUsuario
        );

        return usuarioRepository.save(usuario);
    }
}