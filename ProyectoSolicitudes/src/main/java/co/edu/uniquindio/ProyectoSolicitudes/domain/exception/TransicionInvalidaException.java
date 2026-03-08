package co.edu.uniquindio.ProyectoSolicitudes.domain.exception;

/**
 * Se lanza cuando se intenta realizar una transición de estado inválida.
 * Las transiciones permitidas son:
 *
 *   REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
 *
 * Regla de negocio: RN-02
 */

public class TransicionInvalidaException extends DomainException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}