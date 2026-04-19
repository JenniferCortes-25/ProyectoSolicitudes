package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso: Consultas de solo lectura sobre solicitudes.
 * Guía 09 — @Transactional(readOnly = true) optimiza rendimiento en consultas.
 */
@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesUseCase {

    private final SolicitudRepository solicitudRepository;

    /**
     * Obtiene una solicitud por su ID.
     * readOnly = true porque no modifica estado.
     */
    @Transactional(readOnly = true)
    public Solicitud obtenerPorId(UUID id) {
        return solicitudRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + id));
    }

    /**
     * Lista todas las solicitudes, con filtro opcional por estado.
     * readOnly = true porque no modifica estado.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> listar(Optional<EstadoSolicitud> estado) {
        return estado
                .map(solicitudRepository::findByEstado)
                .orElseGet(solicitudRepository::findAll);
    }
}