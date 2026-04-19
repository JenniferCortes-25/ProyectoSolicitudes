package co.edu.uniquindio.ProyectoSolicitudes.domain.service;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioInactivoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.CanalOrigen;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.DescripcionSolicitud;

import org.springframework.stereotype.Service;

/**
 * Servicio de dominio que valida al solicitante antes de crear la solicitud.
 * Solicitud no puede verificar si el solicitante existe porque eso es
 * responsabilidad de este servicio.
 */

@Service
public class RegistrarSolicitudService {
    public Solicitud registrar(DescripcionSolicitud descripcion, CanalOrigen canal, Usuario solicitante) {
        if (!solicitante.estaActivo())
            throw new UsuarioInactivoException("El solicitante debe estar activo");

        return new Solicitud(descripcion, canal, solicitante);
    }
}
