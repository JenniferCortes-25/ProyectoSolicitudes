package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando se intenta modificar una solicitud que ya
 * se encuentra en estado CERRADA.
 *
 * Una solicitud cerrada es inmutable — ninguna operación puede
 * alterar su estado, historial ni responsable.
 *
 * Regla de negocio: RN-01
 */

public class SolicitudCerradaException extends DomainException {

    public SolicitudCerradaException() {
        super("La solicitud está CERRADA y no puede modificarse.");
    }
}