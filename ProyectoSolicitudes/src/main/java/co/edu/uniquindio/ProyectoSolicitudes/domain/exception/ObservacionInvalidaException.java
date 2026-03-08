package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando la observación de cierre es nula, vacía
 * o tiene menos de 20 caracteres.
 *
 * Regla de negocio: RN-08
 */

public class ObservacionInvalidaException extends DomainException {

    public ObservacionInvalidaException() {
        super("La observación de cierre debe tener mínimo 20 caracteres.");
    }
}