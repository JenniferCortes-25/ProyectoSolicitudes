package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class ObservacionInvalidaException extends RuntimeException {

    public ObservacionInvalidaException() {
        super("La observación de cierre debe tener mínimo 20 caracteres.");
    }

    public ObservacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}