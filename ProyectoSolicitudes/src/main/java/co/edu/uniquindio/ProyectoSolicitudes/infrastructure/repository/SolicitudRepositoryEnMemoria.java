package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementación en memoria del repositorio de solicitudes.
 */
@Repository
public class SolicitudRepositoryEnMemoria implements SolicitudRepository {

    private final Map<UUID, Solicitud> solicitudes = new HashMap<>();

    @Override
    public void guardar(Solicitud solicitud) {
        solicitudes.put(solicitud.getId(), solicitud);
    }

    @Override
    public Optional<Solicitud> obtenerPorId(UUID id) {
        return Optional.ofNullable(solicitudes.get(id));
    }

    @Override
    public List<Solicitud> obtenerTodas() {
        return List.copyOf(solicitudes.values());
    }

    @Override
    public List<Solicitud> obtenerPorEstado(EstadoSolicitud estado) {
        return solicitudes.values().stream()
                .filter(s -> s.getEstado() == estado)
                .toList();
    }
}