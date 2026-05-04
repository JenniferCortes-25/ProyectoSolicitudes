package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC;

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
 * Caso de uso: Marcar una solicitud como atendida.
 * Cambia el estado de EN_ATENCION → ATENDIDA.
 * Solo el responsable asignado puede ejecutar esta acción.
 */
@Service
@RequiredArgsConstructor
public class AtenderSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Solicitud ejecutar(UUID solicitudId,
                              String observacion,
                              String responsableIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario responsable = usuarioRepository
                .findByIdentificacion(responsableIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe responsable con identificación: " + responsableIdentificacion));

        solicitud.atender(observacion, responsable);

        return solicitudRepository.save(solicitud);
    }
}