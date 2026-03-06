package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class TransicionInvalidaException extends DomainException {

    public TransicionInvalidaException(String mensaje) {
        super("La transición de estado no es válida.");
    }
}