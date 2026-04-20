package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando la descripción de una solicitud es nula,
 * vacía, menor a 10 caracteres o mayor a 1000 caracteres.
 *
 * Regla de negocio: RN-06
 */

public class DescripcionInvalidaException extends DomainException {

    public DescripcionInvalidaException() {
        super("La descripción debe tener entre 10 y 1000 caracteres.");
    }
}