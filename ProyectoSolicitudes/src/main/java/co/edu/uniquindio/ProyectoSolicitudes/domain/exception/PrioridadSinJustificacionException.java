package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class PrioridadSinJustificacionException extends DomainException {

    public PrioridadSinJustificacionException() {
        super("La prioridad requiere una justificación obligatoria.");
    }
}