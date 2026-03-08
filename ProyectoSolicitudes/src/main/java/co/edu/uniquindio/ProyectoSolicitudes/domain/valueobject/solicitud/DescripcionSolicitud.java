package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.DescripcionInvalidaException;

/**
 * Texto libre que describe el motivo de una solicitud académica.
 * Es inmutable y se valida en el constructor.
 *
 * Regla de negocio: RN-06 — debe tener entre 10 y 1000 caracteres.
 */
public record DescripcionSolicitud(String texto) {
    public DescripcionSolicitud {
        if (texto == null || texto.isBlank() || texto.length() < 10 || texto.length() > 1000)
            throw new DescripcionInvalidaException();
    }
}