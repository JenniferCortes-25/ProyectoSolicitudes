package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.ObservacionCierre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso: Cerrar una solicitud.
 *
 * 1. Obtiene la solicitud y el coordinador del repositorio
 * 2. Invoca cerrar() en el dominio
 * 3. Persiste los cambios
 */
@Service
@RequiredArgsConstructor
public class CerrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public Solicitud ejecutar(UUID solicitudId,
                              String observacionTexto,
                              String coordinadorIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .obtenerPorId(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario coordinador = usuarioRepository
                .obtenerPorIdentificacion(coordinadorIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe coordinador con identificación: " + coordinadorIdentificacion));

        solicitud.cerrar(new ObservacionCierre(observacionTexto), coordinador);

        solicitudRepository.guardar(solicitud);
        return solicitud;
    }
}