package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;
// Al cerrar cada solicitud es obligatorio hacer una observación de minimo 20 caracteres

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.ObservacionInvalidaException;

public record ObservacionCierre(String texto) {

    public ObservacionCierre {
        if (texto == null || texto.length() < 20)
            throw new ObservacionInvalidaException();
    }
}