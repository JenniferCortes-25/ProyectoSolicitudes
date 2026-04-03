package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando un usuario intenta realizar una operación
 * para la cual no tiene el rol requerido.
 *
 * Regla de negocio: RN-13
 */

public class PermisoInsuficienteException extends RuntimeException {
    public PermisoInsuficienteException(String message) {
        super(message);
    }
}
