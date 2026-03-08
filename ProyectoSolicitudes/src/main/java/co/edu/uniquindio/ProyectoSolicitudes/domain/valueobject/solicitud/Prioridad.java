package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.PrioridadSinJustificacionException;
import java.util.Objects;

public record Prioridad(NivelPrioridad nivel, String justificacion) {

    public Prioridad {
        Objects.requireNonNull(nivel, "El nivel de prioridad es obligatorio");

        if (justificacion == null || justificacion.isBlank() || justificacion.trim().length() < 5) {
            throw new PrioridadSinJustificacionException();
        }

        justificacion = justificacion.trim();
    }
}