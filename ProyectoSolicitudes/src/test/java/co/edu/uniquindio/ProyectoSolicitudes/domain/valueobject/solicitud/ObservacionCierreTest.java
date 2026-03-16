package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.ObservacionInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObservacionCierreTest {

    // ─── Casos VÁLIDOS ────────────────────────────────────────────────────────

    /**
     * RN-08 — Una observación con exactamente 20 caracteres (límite inferior) debe ser válida.
     */
    @Test
    void observacionConExactamente20CaracteresDebeSerValida() {
        assertDoesNotThrow(() -> new ObservacionCierre("12345678901234567890"));
    }

    /**
     * RN-08 — Una observación larga y descriptiva debe crearse sin excepciones.
     */
    @Test
    void observacionValidaDebeCrearseSinExcepcion() {
        assertDoesNotThrow(() ->
                new ObservacionCierre("Homologación aprobada por consejo de programa")
        );
    }

    /**
     * Igualdad por valor — dos ObservacionCierre con el mismo texto deben ser iguales.
     */
    @Test
    void dosObservacionesConMismoTextoDebenSerIguales() {
        ObservacionCierre o1 = new ObservacionCierre("Homologación aprobada por consejo de programa");
        ObservacionCierre o2 = new ObservacionCierre("Homologación aprobada por consejo de programa");

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    // ─── Casos INVÁLIDOS ──────────────────────────────────────────────────────

    /**
     * RN-08 — Observación menor a 20 chars debe lanzar ObservacionInvalidaException.
     */
    @Test
    void observacionCortaDebeLanzarObservacionInvalidaException() {
        assertThrows(ObservacionInvalidaException.class,
                () -> new ObservacionCierre("muy corta"));
    }

    /**
     * RN-08 — Observación con exactamente 19 caracteres (límite - 1) debe fallar.
     */
    @Test
    void observacionCon19CaracteresDebeLanzarObservacionInvalidaException() {
        assertThrows(ObservacionInvalidaException.class,
                () -> new ObservacionCierre("1234567890123456789"));
    }

    /**
     * RN-08 — Observación nula debe lanzar ObservacionInvalidaException.
     */
    @Test
    void observacionNulaDebeLanzarObservacionInvalidaException() {
        assertThrows(ObservacionInvalidaException.class,
                () -> new ObservacionCierre(null));
    }

    /**
     * RN-08 — Observación vacía debe lanzar ObservacionInvalidaException.
     */
    @Test
    void observacionVaciaDebeLanzarObservacionInvalidaException() {
        assertThrows(ObservacionInvalidaException.class,
                () -> new ObservacionCierre(""));
    }

    /**
     * RN-08 — Observación con solo espacios debe lanzar ObservacionInvalidaException.
     */
    @Test
    void observacionSoloEspaciosDebeLanzarObservacionInvalidaException() {
        assertThrows(ObservacionInvalidaException.class,
                () -> new ObservacionCierre("                    "));
    }
}