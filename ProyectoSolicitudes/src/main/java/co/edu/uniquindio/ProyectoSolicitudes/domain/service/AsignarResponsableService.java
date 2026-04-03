package co.edu.uniquindio.ProyectoSolicitudes.domain.service;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioInactivoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.Responsable;

/**
 * Verifica que el responsable exista y esté activo antes de asignarlo.
 * Solicitud solo recibe el Responsable ya validado.
 */

public class AsignarResponsableService {
    public void asignar(Solicitud solicitud, Usuario responsable, Usuario coordinador) {
        if (!responsable.estaActivo())
            throw new UsuarioInactivoException("El responsable debe estar activo");

        Responsable r = new Responsable(responsable.getId(), responsable.getNombre());
        solicitud.asignarResponsable(r, coordinador);
    }
}
