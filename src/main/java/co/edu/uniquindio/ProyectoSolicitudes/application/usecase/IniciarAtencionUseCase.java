package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Iniciar atención de una solicitud.
 * Cambia el estado de CLASIFICADA → EN_ATENCION.
 */
@Service
@RequiredArgsConstructor
public class IniciarAtencionUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Solicitud ejecutar(UUID solicitudId, String coordinadorIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario coordinador = usuarioRepository
                .findByIdentificacion(coordinadorIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe coordinador con identificación: " + coordinadorIdentificacion));

        solicitud.iniciarAtencion(coordinador);

        return solicitudRepository.save(solicitud);
    }
}