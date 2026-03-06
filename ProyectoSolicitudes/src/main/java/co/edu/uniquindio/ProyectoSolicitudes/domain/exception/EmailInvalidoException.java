package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class EmailInvalidoException extends DomainException {

    public EmailInvalidoException() {
        super("El correo electrónico no tiene un formato válido.");
    }
}