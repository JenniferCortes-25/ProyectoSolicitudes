package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class SinResponsableException extends DomainException {

    public SinResponsableException() {
        super("No se puede iniciar la atención sin un responsable asignado.");
    }
}