package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando se busca un usuario por ID y no existe en el repositorio.
 */

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}
