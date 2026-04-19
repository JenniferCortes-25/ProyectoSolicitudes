package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.RegistrarSolicitudService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.CanalOrigen;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.DescripcionSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Registrar una nueva solicitud.
 * El @Transactional asegura revertir cambios si algo falla.
 */
@Service
@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistrarSolicitudService registrarSolicitudService;

    @Transactional
    public Solicitud ejecutar(String descripcionTexto,
                              CanalOrigen canal,
                              String solicitanteIdentificacion) {

        Usuario solicitante = usuarioRepository
                .findByIdentificacion(solicitanteIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitante con identificación: " + solicitanteIdentificacion));

        Solicitud solicitud = registrarSolicitudService.registrar(
                new DescripcionSolicitud(descripcionTexto),
                canal,
                solicitante
        );

        return solicitudRepository.save(solicitud);
    }
}