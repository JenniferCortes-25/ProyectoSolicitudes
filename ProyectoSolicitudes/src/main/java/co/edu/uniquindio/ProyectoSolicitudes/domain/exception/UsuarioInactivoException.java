package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando se intenta asignar como responsable a un usuario
 * cuyo estado es INACTIVO.
 *
 * Un usuario inactivo no puede recibir nuevas asignaciones
 * ni operar en el sistema.
 *
 * Reglas de negocio: RN-04, RN-10
 */

public class UsuarioInactivoException extends DomainException {

    public UsuarioInactivoException(String mensaje) {
        super(mensaje);
    }
}