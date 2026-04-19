package co.edu.uniquindio.ProyectoSolicitudes.domain.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia para Usuario.
 * Adaptado a convenciones Spring Data.
 */
public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByIdentificacion(String identificacion);
}