package co.edu.uniquindio.ProyectoSolicitudes.domain.repository;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia para Solicitud.
 * Adaptado a convenciones Spring Data
 * Nombres: save, findById, findAll, findByEstado.
 */
public interface SolicitudRepository {

    Solicitud save(Solicitud solicitud);

    Optional<Solicitud> findById(UUID id);

    List<Solicitud> findAll();

    List<Solicitud> findByEstado(EstadoSolicitud estado);
}