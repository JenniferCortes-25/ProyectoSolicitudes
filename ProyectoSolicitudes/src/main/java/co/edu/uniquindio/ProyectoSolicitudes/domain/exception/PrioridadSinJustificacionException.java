package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class PrioridadSinJustificacionException extends RuntimeException {

    public PrioridadSinJustificacionException() {
        super("La prioridad requiere una justificación obligatoria.");
    }

    public PrioridadSinJustificacionException(String mensaje) {
        super(mensaje);
    }
}