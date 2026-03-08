package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.ObservacionInvalidaException;

/**
 * Texto obligatorio que debe registrarse al cerrar una solicitud.
 * Es inmutable y se valida en el constructor.
 *
 * Regla de negocio: RN-08 — debe tener mínimo 20 caracteres.
 */
public record ObservacionCierre(String texto) {
    public ObservacionCierre {
        if (texto == null || texto.isBlank() || texto.trim().length() < 20)
            throw new ObservacionInvalidaException();
    }
}