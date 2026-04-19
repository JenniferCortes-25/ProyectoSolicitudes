package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso: Consultas de solo lectura sobre solicitudes.
 */
@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesUseCase {

    private final SolicitudRepository solicitudRepository;

    /**
     * Obtiene una solicitud por su ID.
     * Lanza UsuarioNoEncontradoException si no existe.
     */
    public Solicitud obtenerPorId(UUID id) {
        return solicitudRepository
                .obtenerPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + id));
    }

    /**
     * Lista todas las solicitudes, con filtro opcional por estado.
     */
    public List<Solicitud> listar(Optional<EstadoSolicitud> estado) {
        return estado
                .map(solicitudRepository::obtenerPorEstado)
                .orElseGet(solicitudRepository::obtenerTodas);
    }
}