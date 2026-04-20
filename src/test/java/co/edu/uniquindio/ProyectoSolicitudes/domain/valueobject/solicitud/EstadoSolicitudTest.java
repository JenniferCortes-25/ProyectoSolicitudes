package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoSolicitudTest {

    // ─── Transiciones VÁLIDAS ─────────────────────────────────────────────────

    /**
     * RN-02 — REGISTRADA → CLASIFICADA debe ser una transición válida.
     */
    @Test
    void registradaAClasificadaDebeSerTransicionValida() {
        assertTrue(EstadoSolicitud.REGISTRADA.esTransicionValida(EstadoSolicitud.CLASIFICADA));
    }

    /**
     * RN-02 — CLASIFICADA → EN_ATENCION debe ser una transición válida.
     */
    @Test
    void clasificadaAEnAtencionDebeSerTransicionValida() {
        assertTrue(EstadoSolicitud.CLASIFICADA.esTransicionValida(EstadoSolicitud.EN_ATENCION));
    }

    /**
     * RN-02 — EN_ATENCION → ATENDIDA debe ser una transición válida.
     */
    @Test
    void enAtencionAAtendidaDebeSerTransicionValida() {
        assertTrue(EstadoSolicitud.EN_ATENCION.esTransicionValida(EstadoSolicitud.ATENDIDA));
    }

    /**
     * RN-02 — ATENDIDA → CERRADA debe ser una transición válida.
     */
    @Test
    void atendidaACerradaDebeSerTransicionValida() {
        assertTrue(EstadoSolicitud.ATENDIDA.esTransicionValida(EstadoSolicitud.CERRADA));
    }

    // ─── Transiciones INVÁLIDAS ───────────────────────────────────────────────

    /**
     * RN-02 — REGISTRADA → EN_ATENCION (saltando estado) debe ser inválida.
     */
    @Test
    void registradaAEnAtencionDebeSerTransicionInvalida() {
        assertFalse(EstadoSolicitud.REGISTRADA.esTransicionValida(EstadoSolicitud.EN_ATENCION));
    }

    /**
     * RN-02 — REGISTRADA → CERRADA (saltando estados) debe ser inválida.
     */
    @Test
    void registradaACerradaDebeSerTransicionInvalida() {
        assertFalse(EstadoSolicitud.REGISTRADA.esTransicionValida(EstadoSolicitud.CERRADA));
    }

    /**
     * RN-02 — CLASIFICADA → ATENDIDA (saltando EN_ATENCION) debe ser inválida.
     */
    @Test
    void clasificadaAAtendidaDebeSerTransicionInvalida() {
        assertFalse(EstadoSolicitud.CLASIFICADA.esTransicionValida(EstadoSolicitud.ATENDIDA));
    }

    /**
     * RN-01 — CERRADA no tiene transiciones válidas hacia ningún estado.
     */
    @Test
    void cerradaNoTieneNingunaTransicionValida() {
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            assertFalse(EstadoSolicitud.CERRADA.esTransicionValida(estado),
                    "CERRADA no debería poder transicionar a: " + estado);
        }
    }

    /**
     * RN-02 — Ningún estado debe poder transicionar hacia REGISTRADA (retroceso inválido).
     */
    @Test
    void ningunEstadoPuedeRetrocederARegistrada() {
        assertFalse(EstadoSolicitud.CLASIFICADA.esTransicionValida(EstadoSolicitud.REGISTRADA));
        assertFalse(EstadoSolicitud.EN_ATENCION.esTransicionValida(EstadoSolicitud.REGISTRADA));
        assertFalse(EstadoSolicitud.ATENDIDA.esTransicionValida(EstadoSolicitud.REGISTRADA));
        assertFalse(EstadoSolicitud.CERRADA.esTransicionValida(EstadoSolicitud.REGISTRADA));
    }
}
