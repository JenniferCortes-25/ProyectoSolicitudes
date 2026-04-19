package co.edu.uniquindio.ProyectoSolicitudes.domain.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia para Usuario.
 */
public interface UsuarioRepository {

    void guardar(Usuario usuario);
    Optional<Usuario> obtenerPorId(UUID id);
    Optional<Usuario> obtenerPorIdentificacion(String identificacion);
}