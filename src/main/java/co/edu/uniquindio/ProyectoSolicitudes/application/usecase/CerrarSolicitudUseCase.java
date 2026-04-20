package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.ObservacionCierre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Cerrar una solicitud.
 * El @Transactional asegura revertir cambios si algo falla.
 */
@Service
@RequiredArgsConstructor
public class CerrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Solicitud ejecutar(UUID solicitudId,
                              String observacionTexto,
                              String coordinadorIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario coordinador = usuarioRepository
                .findByIdentificacion(coordinadorIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe coordinador con identificación: " + coordinadorIdentificacion));

        solicitud.cerrar(new ObservacionCierre(observacionTexto), coordinador);

        return solicitudRepository.save(solicitud);
    }
}