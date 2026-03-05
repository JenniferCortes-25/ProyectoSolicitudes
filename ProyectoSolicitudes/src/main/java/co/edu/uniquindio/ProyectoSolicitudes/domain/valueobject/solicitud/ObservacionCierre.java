package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.ObservacionInvalidaException;

public record ObservacionCierre(String texto) {

    public ObservacionCierre {
        if (texto == null || texto.isBlank() || texto.trim().length() < 20) {
            throw new ObservacionInvalidaException();
        }
    }
}