package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Implementación en memoria del repositorio de solicitudes.
 * Nombres de métodos en convenciones Spring Data.
 */
// @Repository — desactivado; reemplazado por SolicitudJpaRepository (Guía 10)
public class SolicitudRepositoryEnMemoria implements SolicitudRepository {

    private final Map<UUID, Solicitud> solicitudes = new HashMap<>();

    @Override
    public Solicitud save(Solicitud solicitud) {
        solicitudes.put(solicitud.getId(), solicitud);
        return solicitud;
    }

    @Override
    public Optional<Solicitud> findById(UUID id) {
        return Optional.ofNullable(solicitudes.get(id));
    }

    @Override
    public List<Solicitud> findAll() {
        return List.copyOf(solicitudes.values());
    }

    @Override
    public void deleteById(UUID id) {
        solicitudes.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return solicitudes.containsKey(id);
    }

    @Override
    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        return solicitudes.values().stream()
                .filter(s -> s.getEstado() == estado)
                .toList();
    }

    @Override
    public List<Solicitud> findBySolicitanteId(UUID solicitanteId) {
        return solicitudes.values().stream()
                .filter(s -> s.getSolicitanteId().equals(solicitanteId))
                .toList();
    }

    @Override
    public List<Solicitud> findByEstadoAndPrioridad(
            co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud estado,
            co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad prioridad) {
        return solicitudes.values().stream()
                .filter(s -> s.getEstado() == estado
                        && s.getPrioridad() != null
                        && s.getPrioridad().nivel() == prioridad)
                .toList();
    }

    @Override
    public long countByEstado(EstadoSolicitud estado) {
        return solicitudes.values().stream()
                .filter(s -> s.getEstado() == estado)
                .count();
    }

    @Override
    public List<Solicitud> findSolicitudesPendientesDeAsignacion() {
        return solicitudes.values().stream()
                .filter(s -> (s.getEstado() == EstadoSolicitud.REGISTRADA
                        || s.getEstado() == EstadoSolicitud.CLASIFICADA)
                        && s.getResponsable() == null)
                .toList();
    }

    @Override
    public Page<Solicitud> findByEstadoNot(EstadoSolicitud estado, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByEstadoNot'");
    }
}