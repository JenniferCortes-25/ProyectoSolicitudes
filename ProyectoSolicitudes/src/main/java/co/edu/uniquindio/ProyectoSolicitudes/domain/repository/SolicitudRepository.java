package co.edu.uniquindio.ProyectoSolicitudes.domain.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto (interfaz) del dominio para persistencia de Solicitud.
 * No conoce nada de JPA, Hibernate ni H2.
 * Describe QUÉ operaciones necesita el dominio, no CÓMO se implementan.
 */
public interface SolicitudRepository {

    // ── Operaciones CRUD básicas ────────────────────────────────────────────
    Solicitud save(Solicitud solicitud);

    Optional<Solicitud> findById(UUID id);

    List<Solicitud> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);

    // ── Consultas por criterio ──────────────────────────────────────────────
    List<Solicitud> findByEstado(EstadoSolicitud estado);

    List<Solicitud> findBySolicitanteId(UUID solicitanteId);

    List<Solicitud> findByEstadoAndPrioridad(EstadoSolicitud estado, NivelPrioridad prioridad);

    long countByEstado(EstadoSolicitud estado);

    /** Solicitudes REGISTRADAS o CLASIFICADAS aún sin responsable asignado. */
    List<Solicitud> findSolicitudesPendientesDeAsignacion();
}