package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementación en memoria del repositorio de usuarios.
 * Nombres de métodos en convenciones Spring Data.
 */
@Repository
public class UsuarioRepositoryEnMemoria implements UsuarioRepository {

    private final Map<UUID, Usuario> usuarios = new HashMap<>();

    public UsuarioRepositoryEnMemoria() {
        save(new Usuario("E-001", "Estudiante García",
                new Email("estudiante@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE));
        save(new Usuario("C-001", "Coordinador Pérez",
                new Email("coord@uniquindio.edu.co"), TipoUsuario.COORDINADOR));
        save(new Usuario("D-001", "Docente López",
                new Email("docente@uniquindio.edu.co"), TipoUsuario.DOCENTE));
        save(new Usuario("A-001", "Administrativo Gómez",
                new Email("admin@uniquindio.edu.co"), TipoUsuario.ADMINISTRATIVO));
    }

    @Override
    public Usuario save(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public Optional<Usuario> findByIdentificacion(String identificacion) {
        return usuarios.values().stream()
                .filter(u -> u.getIdentificacion().equals(identificacion))
                .findFirst();
    }
}
