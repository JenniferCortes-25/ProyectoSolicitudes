package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.EstadoSolicitudJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.NivelPrioridadJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.SolicitudEntity;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.mapper.SolicitudPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTADOR: implementa el puerto SolicitudRepository del dominio
 * usando H2 + Spring Data JPA como tecnología de persistencia.
 *
 * Principio de inversión de dependencias:
 *   Los Use Cases dependen de SolicitudRepository (interfaz del dominio).
 *   Este adaptador es un detalle de implementación que el dominio NO conoce.
 *
 * @Primary hace que Spring inyecte ESTA implementación en lugar de
 * SolicitudRepositoryEnMemoria, sin modificar ningún Use Case.
 */
@Repository
@Primary
@Transactional
@RequiredArgsConstructor
public class SolicitudJpaRepository implements SolicitudRepository {

    private final SolicitudJpaDataRepository dataRepository;
    private final SolicitudPersistenceMapper  mapper;
    private final UsuarioRepository           usuarioRepository;

    // ── CRUD ───────────────────────────────────────────────────────────────

    @Override
    public Solicitud save(Solicitud solicitud) {
        SolicitudEntity entity   = mapper.toEntity(solicitud);
        SolicitudEntity guardado = dataRepository.save(entity);
        return toDomainConRelaciones(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findById(UUID id) {
        return dataRepository.findById(id.toString())
                .map(this::toDomainConRelaciones);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findAll() {
        return dataRepository.findAll().stream()
                .map(this::toDomainConRelaciones)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        dataRepository.deleteById(id.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return dataRepository.existsById(id.toString());
    }

    // ── Consultas por criterio ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        EstadoSolicitudJpa estadoJpa = EstadoSolicitudJpa.valueOf(estado.name());
        return dataRepository.findByEstado(estadoJpa).stream()
                .map(this::toDomainConRelaciones)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findBySolicitanteId(UUID solicitanteId) {
        return dataRepository.findBySolicitanteId(solicitanteId.toString()).stream()
                .map(this::toDomainConRelaciones)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByEstadoAndPrioridad(EstadoSolicitud estado,
                                                     NivelPrioridad prioridad) {
        EstadoSolicitudJpa estadoJpa    = EstadoSolicitudJpa.valueOf(estado.name());
        NivelPrioridadJpa  prioridadJpa = NivelPrioridadJpa.valueOf(prioridad.name());
        return dataRepository.findByEstadoAndPrioridadNivel(estadoJpa, prioridadJpa).stream()
                .map(this::toDomainConRelaciones)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByEstado(EstadoSolicitud estado) {
        EstadoSolicitudJpa estadoJpa = EstadoSolicitudJpa.valueOf(estado.name());
        return dataRepository.countByEstado(estadoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findSolicitudesPendientesDeAsignacion() {
        return dataRepository.findSolicitudesPendientesDeAsignacion().stream()
                .map(this::toDomainConRelaciones)
                .collect(Collectors.toList());
    }

    // ── Método auxiliar ────────────────────────────────────────────────────

    /**
     * Resuelve los objetos Usuario a partir de los IDs almacenados en la
     * entidad JPA, y delega la reconstrucción completa al mapper.
     */
    private Solicitud toDomainConRelaciones(SolicitudEntity entity) {
        Usuario solicitante = usuarioRepository
                .findById(UUID.fromString(entity.getSolicitanteId()))
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario solicitante no encontrado: " + entity.getSolicitanteId()));

        Usuario responsable = null;
        if (entity.getResponsableId() != null) {
            responsable = usuarioRepository
                    .findById(UUID.fromString(entity.getResponsableId()))
                    .orElse(null);
        }

        return mapper.toDomain(entity, solicitante, responsable);
    }
}