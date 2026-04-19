package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.ClasificarSolicitudService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.Prioridad;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso: Clasificar una solicitud existente.
 *
 * 1. Obtiene la solicitud y el coordinador del repositorio
 * 2. Delega la clasificación al servicio de dominio
 * 3. Persiste los cambios
 */
@Service
@RequiredArgsConstructor
public class ClasificarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClasificarSolicitudService clasificarSolicitudService;

    public Solicitud ejecutar(UUID solicitudId,
                              TipoSolicitud tipo,
                              NivelPrioridad nivelPrioridad,
                              String justificacion,
                              String coordinadorIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .obtenerPorId(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario coordinador = usuarioRepository
                .obtenerPorIdentificacion(coordinadorIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe coordinador con identificación: " + coordinadorIdentificacion));

        clasificarSolicitudService.clasificar(
                solicitud,
                tipo,
                new Prioridad(nivelPrioridad, justificacion),
                coordinador
        );

        solicitudRepository.guardar(solicitud);
        return solicitud;
    }
}