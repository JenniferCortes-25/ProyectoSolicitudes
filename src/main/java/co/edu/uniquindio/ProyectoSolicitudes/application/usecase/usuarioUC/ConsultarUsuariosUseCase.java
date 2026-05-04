package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: Consultas de solo lectura sobre usuarios.
 * @Transactional(readOnly = true) optimiza rendimiento en consultas.
 */
@Service
@RequiredArgsConstructor
public class ConsultarUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene un usuario por su UUID.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(UUID id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe usuario con ID: " + id));
    }

    /**
     * Obtiene un usuario por su número de identificación.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerPorIdentificacion(String identificacion) {
        return usuarioRepository
                .findByIdentificacion(identificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe usuario con identificación: " + identificacion));
    }

    /**
     * Lista todos los usuarios registrados en el sistema.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}
