package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.DescripcionInvalidaException;

public record DescripcionSolicitud(String texto ) {
    public DescripcionSolicitud {
        if (texto == null || texto.isBlank() || texto.length() < 10 || texto.length() > 1000)
            throw new DescripcionInvalidaException();
    }
}