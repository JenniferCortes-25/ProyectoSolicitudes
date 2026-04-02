package co.edu.uniquindio.ProyectoSolicitudes.domain.service;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.Prioridad;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.TipoSolicitud;

/**
 * Orquesta la clasificación: asigna tipo y prioridad en una sola operación.
 * Ambas acciones las realiza el coordinador.
 */

public class ClasificarSolicitudService {
    public void clasificar(Solicitud solicitud, TipoSolicitud tipo, Prioridad prioridad, Usuario coordinador) {
        solicitud.clasificar(tipo, coordinador);
        solicitud.asignarPrioridad(prioridad, coordinador);
    }
}
