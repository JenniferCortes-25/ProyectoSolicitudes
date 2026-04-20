package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA para usuarios de seguridad (credenciales JWT).
 */
public interface UsuarioSecurityJpaRepository extends JpaRepository<UsuarioSecurityEntity, String> {
    Optional<UsuarioSecurityEntity> findByEmail(String email);
}
