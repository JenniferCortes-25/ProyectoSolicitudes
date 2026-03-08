package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

public enum EstadoSolicitud {

    REGISTRADA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == CLASIFICADA;
        }
    },
    CLASIFICADA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == EN_ATENCION;
        }
    },
    EN_ATENCION {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == ATENDIDA;
        }
    },
    ATENDIDA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return siguiente == CERRADA;
        }
    },
    CERRADA {
        @Override
        public boolean esTransicionValida(EstadoSolicitud siguiente) {
            return false; // estado final, ninguna transición válida
        }
    };

    public abstract boolean esTransicionValida(EstadoSolicitud siguiente);
}