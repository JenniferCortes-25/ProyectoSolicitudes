package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.EstadoSolicitudJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.NivelPrioridadJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Interfaz Spring Data JPA — genera implementación automática en tiempo de arranque.
 * Solo maneja SolicitudEntity (modelo de persistencia), NO el dominio.
 * Visibilidad package-private: solo la usa SolicitudJpaRepository.
 */
interface SolicitudJpaDataRepository extends JpaRepository<SolicitudEntity, String> {

    // Query methods por convención de nombres
    List<SolicitudEntity> findBySolicitanteId(String solicitanteId);

    List<SolicitudEntity> findByEstado(EstadoSolicitudJpa estado);

    List<SolicitudEntity> findByEstadoAndPrioridadNivel(
            EstadoSolicitudJpa estado,
            NivelPrioridadJpa nivel);

    long countByEstado(EstadoSolicitudJpa estado);

    // JPQL: solicitudes aún sin responsable y en estados iniciales
    @Query("SELECT s FROM SolicitudEntity s " +
           "WHERE s.estado IN ('REGISTRADA', 'CLASIFICADA') " +
           "AND s.responsableId IS NULL")
    List<SolicitudEntity> findSolicitudesPendientesDeAsignacion();

    // Consulta auxiliar con parámetros nombrados
    @Query("SELECT s FROM SolicitudEntity s " +
           "WHERE s.solicitanteId = :userId AND s.estado = :estado")
    List<SolicitudEntity> findBySolicitanteIdAndEstado(
            @Param("userId") String userId,
            @Param("estado") EstadoSolicitudJpa estado);
}