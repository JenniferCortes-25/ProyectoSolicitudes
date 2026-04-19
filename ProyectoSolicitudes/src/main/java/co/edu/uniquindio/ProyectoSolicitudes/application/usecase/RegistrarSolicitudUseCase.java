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

/**
 * Caso de uso: Registrar una nueva solicitud.
 *
 * 1. Obtiene el solicitante del repositorio
 * 2. Delega la creación al servicio de dominio
 * 3. Persiste la solicitud
 */
@Service
@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistrarSolicitudService registrarSolicitudService;

    public Solicitud ejecutar(String descripcionTexto,
                              CanalOrigen canal,
                              String solicitanteIdentificacion) {

        Usuario solicitante = usuarioRepository
                .obtenerPorIdentificacion(solicitanteIdentificacion)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitante con identificación: " + solicitanteIdentificacion));

        Solicitud solicitud = registrarSolicitudService.registrar(
                new DescripcionSolicitud(descripcionTexto),
                canal,
                solicitante
        );

        solicitudRepository.guardar(solicitud);
        return solicitud;
    }
}