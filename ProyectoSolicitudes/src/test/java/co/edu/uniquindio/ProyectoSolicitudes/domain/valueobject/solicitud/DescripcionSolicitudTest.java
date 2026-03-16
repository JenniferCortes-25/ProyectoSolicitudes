package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.DescripcionInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DescripcionSolicitudTest {

    // ─── Casos VÁLIDOS ────────────────────────────────────────────────────────

    /**
     * RN-06 — Una descripción con exactamente 10 caracteres debe ser válida (límite inferior).
     */
    @Test
    void descripcionConExactamente10CaracteresDebeSerValida() {
        assertDoesNotThrow(() -> new DescripcionSolicitud("1234567890"));
    }

    /**
     * RN-06 — Una descripción con 1000 caracteres debe ser válida (límite superior).
     */
    @Test
    void descripcionConExactamente1000CaracteresDebeSerValida() {
        String texto = "A".repeat(1000);
        assertDoesNotThrow(() -> new DescripcionSolicitud(texto));
    }

    /**
     * RN-06 — Una descripción dentro del rango normal debe crearse sin problema.
     */
    @Test
    void descripcionValidaDebeCrearseSinExcepcion() {
        assertDoesNotThrow(() ->
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad")
        );
    }

    /**
     * Igualdad por valor — dos descripciones con el mismo texto deben ser iguales.
     */
    @Test
    void dosDescripcionesConMismoTextoDebenSerIguales() {
        DescripcionSolicitud d1 = new DescripcionSolicitud("Solicitud de homologación de materia");
        DescripcionSolicitud d2 = new DescripcionSolicitud("Solicitud de homologación de materia");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    // ─── Casos INVÁLIDOS ──────────────────────────────────────────────────────

    /**
     * RN-06 — Descripción menor a 10 caracteres debe lanzar DescripcionInvalidaException.
     */
    @Test
    void descripcionMenorA10CaracteresDebeLanzarDescripcionInvalidaException() {
        assertThrows(DescripcionInvalidaException.class, () -> new DescripcionSolicitud("corta"));
    }

    /**
     * RN-06 — Descripción con exactamente 9 caracteres (límite inferior - 1) debe fallar.
     */
    @Test
    void descripcionCon9CaracteresDebeLanzarDescripcionInvalidaException() {
        assertThrows(DescripcionInvalidaException.class, () -> new DescripcionSolicitud("123456789"));
    }

    /**
     * RN-06 — Descripción con 1001 caracteres (límite superior + 1) debe fallar.
     */
    @Test
    void descripcionCon1001CaracteresDebeLanzarDescripcionInvalidaException() {
        String texto = "A".repeat(1001);
        assertThrows(DescripcionInvalidaException.class, () -> new DescripcionSolicitud(texto));
    }

    /**
     * RN-06 — Descripción nula debe lanzar DescripcionInvalidaException.
     */
    @Test
    void descripcionNulaDebeLanzarDescripcionInvalidaException() {
        assertThrows(DescripcionInvalidaException.class, () -> new DescripcionSolicitud(null));
    }

    /**
     * RN-06 — Descripción vacía debe lanzar DescripcionInvalidaException.
     */
    @Test
    void descripcionVaciaDebeLanzarDescripcionInvalidaException() {
        assertThrows(DescripcionInvalidaException.class, () -> new DescripcionSolicitud(""));
    }

    /**
     * RN-06 — Descripción con solo espacios debe lanzar DescripcionInvalidaException.
     */
    @Test
    void descripcionSoloEspaciosDebeLanzarDescripcionInvalidaException() {
        assertThrows(DescripcionInvalidaException.class, () -> new DescripcionSolicitud("          "));
    }
}
