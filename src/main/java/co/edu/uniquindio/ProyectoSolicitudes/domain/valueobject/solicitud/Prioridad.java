package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.PrioridadSinJustificacionException;
import java.util.Objects;

/**
 * Encapsula el nivel de urgencia y su justificación obligatoria.
 * Es inmutable y se valida en el constructor.
 *
 * Regla de negocio: RN-07 — la justificación debe tener mínimo 5 caracteres.
 */
public record Prioridad(NivelPrioridad nivel, String justificacion) {
    public Prioridad {
        Objects.requireNonNull(nivel, "El nivel de prioridad es obligatorio");
        if (justificacion == null || justificacion.isBlank() || justificacion.trim().length() < 5)
            throw new PrioridadSinJustificacionException();
        justificacion = justificacion.trim();
    }
}