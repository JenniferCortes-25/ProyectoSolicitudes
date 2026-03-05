package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class EmailInvalidoException extends RuntimeException {

    public EmailInvalidoException() {
        super("El correo electrónico no tiene un formato válido.");
    }

    public EmailInvalidoException(String mensaje) {
        super(mensaje);
    }
}