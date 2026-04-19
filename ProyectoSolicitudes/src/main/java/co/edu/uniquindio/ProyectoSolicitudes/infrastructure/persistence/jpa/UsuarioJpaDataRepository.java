package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interfaz Spring Data JPA — genera implementación automática en tiempo de arranque.
 * Solo maneja UsuarioEntity (modelo de persistencia), NO el dominio.
 * Visibilidad package-private: solo la usa UsuarioJpaRepository.
 */
interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByIdentificacion(String identificacion);
}