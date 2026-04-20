package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Excepción base para todas las excepciones del dominio.
 * Extiende RuntimeException para no obligar el uso de try/catch.
 */

public abstract class DomainException extends RuntimeException {

    public DomainException(String mensaje) {
        super(mensaje);
    }
}