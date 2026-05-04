package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Desactivar un usuario activo.
 * Un usuario inactivo no puede recibir nuevas asignaciones (RN-04, RN-10).
 * Solo puede ser ejecutado por un COORDINADOR (RN-13).
 * El @Transactional asegura revertir cambios si algo falla.
 */
@Service
@RequiredArgsConstructor
public class DesactivarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario ejecutar(UUID usuarioId) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe usuario con ID: " + usuarioId));

        usuario.desactivar();

        return usuarioRepository.save(usuario);
    }
}