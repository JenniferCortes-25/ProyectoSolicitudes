package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public abstract class DomainException extends RuntimeException {

    public DomainException(String mensaje) {
        super(mensaje);
    }
}