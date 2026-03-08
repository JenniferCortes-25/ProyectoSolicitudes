package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando el correo electrónico no tiene un formato válido,
 * por ejemplo si no contiene '@' o le falta el dominio.
 *
 * Regla de negocio: RN-09
 */

public class EmailInvalidoException extends DomainException {

    public EmailInvalidoException() {
        super("El correo electrónico no tiene un formato válido.");
    }
}