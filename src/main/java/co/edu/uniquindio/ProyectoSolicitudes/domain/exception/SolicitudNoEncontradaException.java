package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra una solicitud por su ID.
 * El GlobalExceptionHandler la mapea a 404 Not Found.
 */
public class SolicitudNoEncontradaException extends RuntimeException {

    public SolicitudNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}