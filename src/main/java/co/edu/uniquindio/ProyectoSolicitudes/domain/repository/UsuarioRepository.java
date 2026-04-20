package co.edu.uniquindio.ProyectoSolicitudes.domain.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto (interfaz) del dominio para persistencia de Usuario.
 * No conoce nada de JPA, Hibernate ni H2.
 */
public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(UUID id);

    List<Usuario> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);

    Optional<Usuario> findByIdentificacion(String identificacion);
}