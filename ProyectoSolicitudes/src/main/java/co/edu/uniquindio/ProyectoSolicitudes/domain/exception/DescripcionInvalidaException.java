package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class DescripcionInvalidaException extends RuntimeException {

    public DescripcionInvalidaException() {
        super("La descripción debe tener entre 10 y 1000 caracteres.");
    }

    public DescripcionInvalidaException(String mensaje) {
        super(mensaje);
    }
}