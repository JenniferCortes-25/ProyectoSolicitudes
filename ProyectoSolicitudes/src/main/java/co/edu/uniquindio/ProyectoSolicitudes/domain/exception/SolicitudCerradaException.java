package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

public class SolicitudCerradaException extends DomainException {

    public SolicitudCerradaException() {
        super("La solicitud está CERRADA y no puede modificarse.");
    }
}