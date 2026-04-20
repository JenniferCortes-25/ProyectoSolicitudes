package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

/**
 * Representa el ciclo de vida completo de una solicitud académica.
 * Solo son válidas las transiciones definidas en esTransicionValida().
 *
 * Flujo permitido:
 *   REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
 *
 * Regla de negocio: RN-02
 */
public enum EstadoSolicitud {

    /** Solicitud ingresada al sistema, pendiente de clasificar. */
    REGISTRADA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == CLASIFICADA;
        }
    },
    /** Tipo y prioridad asignados, pendiente de atención. */
    CLASIFICADA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == EN_ATENCION;
        }
    },
    /** Responsable trabajando activamente en la solicitud. */
    EN_ATENCION {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == ATENDIDA;
        }
    },
    /** Proceso completado, pendiente de cierre formal. */
    ATENDIDA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == CERRADA;
        }
    },
    /** Solicitud finalizada. Estado final, no modificable. */
    CERRADA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return false;
        }
    };

    /**
     * Verifica si la transición hacia el estado siguiente es válida.
     *
     * @param siguiente estado al que se desea transicionar
     * @return true si la transición está permitida, false si no
     */
    public abstract boolean esTransicionValida(EstadoSolicitud siguiente);
}