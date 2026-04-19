package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.EstadoSolicitudJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.NivelPrioridadJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.SolicitudEntity;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.TipoSolicitudJpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



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


       // Solicitudes de tipo HOMOLOGACION ordenadas por prioridad (mayor a menor)
       List<SolicitudEntity> findByTipoSolicitudOrderByPrioridadNivelDesc(TipoSolicitudJpa tipo);
       
       // s.prioridad.nivel navega el campo embebido PrioridadEmbeddable
       @Query("SELECT s FROM SolicitudEntity s " +
              "WHERE s.tipoSolicitud = :tipo " +
              "ORDER BY s.prioridad.nivel DESC")
       List<SolicitudEntity> buscarHomologacionesPorPrioridad(
              @Param("tipo") TipoSolicitudJpa tipo);

       // Ejercicio 3 — Filtro opcional: si solicitudId es null, usa solicitanteId
       // (:param IS NULL OR campo = :param) ignora el filtro si el parámetro es null
       @Query("SELECT s FROM SolicitudEntity s " +
              "WHERE (:solicitudId IS NULL OR s.id = :solicitudId) " +
              "AND (:solicitanteId IS NULL OR s.solicitanteId = :solicitanteId)")
       List<SolicitudEntity> buscarPorIdOSolicitante(
              @Param("solicitudId")  String solicitudId,
              @Param("solicitanteId") String solicitanteId);

       // paginación: devuelve solicitudes cuyo estado NO sea el dado, ordenadas por fecha de creación descendente
       // Pageable lleva la información de página, tamaño y ordenamiento
       // Page<T> es el wrapper que devuelve los resultados + metadatos de paginación
       Page<SolicitudEntity> findByEstadoNot(
              EstadoSolicitudJpa estado,
              Pageable pageable);




}