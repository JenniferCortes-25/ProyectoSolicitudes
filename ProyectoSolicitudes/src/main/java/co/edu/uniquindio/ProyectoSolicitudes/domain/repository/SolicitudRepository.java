package co.edu.uniquindio.ProyectoSolicitudes.domain.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia para Solicitud.
 * La implementación concreta vive en infrastructure/repository/.
 */
public interface SolicitudRepository {
    
    void guardar(Solicitud solicitud);
    Optional<Solicitud> obtenerPorId(UUID id);
    List<Solicitud> obtenerTodas();
    List<Solicitud> obtenerPorEstado(EstadoSolicitud estado);
}