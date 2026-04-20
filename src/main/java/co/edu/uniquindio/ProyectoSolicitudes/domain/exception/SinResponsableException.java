package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando se intenta iniciar la atención de una solicitud
 * sin tener un responsable asignado previamente.
 *
 * Regla de negocio: RN-05
 */

public class SinResponsableException extends DomainException {

    public SinResponsableException(String mensaje) {
        super(mensaje);
    }
}