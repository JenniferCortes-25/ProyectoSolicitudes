package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsignarResponsableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Asignar responsable a una solicitud.
 * El @Transactional asegura revertir cambios si algo falla.
 */
@Service
@RequiredArgsConstructor
public class AsignarResponsableUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignarResponsableService asignarResponsableService;

    @Transactional
    public Solicitud ejecutar(UUID solicitudId,
                              String responsableIdentificacion,
                              String coordinadorIdentificacion) {

        Solicitud solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + solicitudId));

        Usuario responsable = usuarioRepository
                .findByIdentificacion(responsableIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe responsable con identificación: " + responsableIdentificacion));

        Usuario coordinador = usuarioRepository
                .findByIdentificacion(coordinadorIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe coordinador con identificación: " + coordinadorIdentificacion));

        asignarResponsableService.asignar(solicitud, responsable, coordinador);

        return solicitudRepository.save(solicitud);
    }
}