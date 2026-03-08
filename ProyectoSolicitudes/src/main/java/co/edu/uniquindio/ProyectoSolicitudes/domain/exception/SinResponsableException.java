package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class SinResponsableException extends DomainException {

    public SinResponsableException(String mensaje) {
        super("No se puede iniciar la atención sin un responsable asignado.");
    }
}