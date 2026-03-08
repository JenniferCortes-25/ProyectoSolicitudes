package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando se intenta crear una prioridad sin justificación
 * o con una justificación menor a 5 caracteres.
 *
 * Regla de negocio: RN-07
 */

public class PrioridadSinJustificacionException extends DomainException {

    public PrioridadSinJustificacionException() {
        super("La prioridad requiere una justificación obligatoria.");
    }
}