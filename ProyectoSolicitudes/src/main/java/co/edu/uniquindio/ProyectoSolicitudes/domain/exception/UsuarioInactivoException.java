package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class UsuarioInactivoException extends DomainException {

    public UsuarioInactivoException() {
        super("El usuario está INACTIVO y no puede recibir asignaciones.");
    }
}