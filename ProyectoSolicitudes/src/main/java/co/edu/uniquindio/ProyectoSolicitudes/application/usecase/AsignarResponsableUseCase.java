package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsignarResponsableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso: Asignar responsable a una solicitud.
 *
 * 1. Obtiene solicitud, responsable y coordinador del repositorio
 * 2. Delega la asignación al servicio de dominio
 * 3. Persiste los cambios
 */
@Service
@RequiredArgsConstructor
public class AsignarResponsableUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignarResponsableService asignarResponsableService;

    public Solicitud ejecutar(UUID solicitudId,
                              String responsableIdentificacion,
                              String coordinadorIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .obtenerPorId(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario responsable = usuarioRepository
                .obtenerPorIdentificacion(responsableIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe responsable con identificación: " + responsableIdentificacion));

        Usuario coordinador = usuarioRepository
                .obtenerPorIdentificacion(coordinadorIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe coordinador con identificación: " + coordinadorIdentificacion));

        asignarResponsableService.asignar(solicitud, responsable, coordinador);

        solicitudRepository.guardar(solicitud);
        return solicitud;
    }
}