package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.PrioridadSinJustificacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrioridadTest {

    // ─── Casos VÁLIDOS ────────────────────────────────────────────────────────

    /**
     * RN-07 — Una prioridad con nivel y justificación válidos debe crearse correctamente.
     */
    @Test
    void prioridadValidaDebeCrearseSinExcepcion() {
        assertDoesNotThrow(() -> new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima"));
    }

    /**
     * RN-07 — La justificación con exactamente 5 caracteres (límite inferior) debe ser válida.
     */
    @Test
    void justificacionConExactamente5CaracteresDebeSerValida() {
        assertDoesNotThrow(() -> new Prioridad(NivelPrioridad.MEDIA, "abcde"));
    }

    /**
     * RN-07 — Se deben poder crear los cuatro niveles de prioridad.
     */
    @Test
    void todosLosNivelesDePrioridadDebenPoderCrearse() {
        assertDoesNotThrow(() -> new Prioridad(NivelPrioridad.CRITICA, "Impacto directo en grado"));
        assertDoesNotThrow(() -> new Prioridad(NivelPrioridad.ALTA,    "Tiene fecha límite próxima"));
        assertDoesNotThrow(() -> new Prioridad(NivelPrioridad.MEDIA,   "Solicitud estándar"));
        assertDoesNotThrow(() -> new Prioridad(NivelPrioridad.BAJA,    "Consulta informativa"));
    }

    /**
     * RN-07 — La justificación debe ser recortada de espacios en los extremos (trim).
     */
    @Test
    void justificacionDebeSerRecortadaDeTrim() {
        Prioridad p = new Prioridad(NivelPrioridad.ALTA, "  fecha límite próxima  ");
        assertEquals("fecha límite próxima", p.justificacion());
    }

    /**
     * Igualdad por valor — dos Prioridad con los mismos datos deben ser iguales.
     */
    @Test
    void dosPrioridadesConMismosValoresDebenSerIguales() {
        Prioridad p1 = new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");
        Prioridad p2 = new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    /**
     * Dos Prioridad con diferente nivel deben ser distintas.
     */
    @Test
    void dosPrioridadesConDiferenteNivelNoDenSerIguales() {
        Prioridad p1 = new Prioridad(NivelPrioridad.ALTA,  "Tiene fecha límite próxima");
        Prioridad p2 = new Prioridad(NivelPrioridad.MEDIA, "Tiene fecha límite próxima");

        assertNotEquals(p1, p2);
    }

    // ─── Casos INVÁLIDOS ──────────────────────────────────────────────────────

    /**
     * RN-07 — Prioridad con justificación vacía debe lanzar PrioridadSinJustificacionException.
     */
    @Test
    void prioridadSinJustificacionDebeLanzarPrioridadSinJustificacionException() {
        assertThrows(PrioridadSinJustificacionException.class,
                () -> new Prioridad(NivelPrioridad.ALTA, ""));
    }

    /**
     * RN-07 — Justificación con menos de 5 caracteres debe lanzar excepción.
     */
    @Test
    void justificacionMenorA5CaracteresDebeLanzarPrioridadSinJustificacionException() {
        assertThrows(PrioridadSinJustificacionException.class,
                () -> new Prioridad(NivelPrioridad.ALTA, "abc"));
    }

    /**
     * RN-07 — Justificación nula debe lanzar PrioridadSinJustificacionException.
     */
    @Test
    void justificacionNulaDebeLanzarPrioridadSinJustificacionException() {
        assertThrows(PrioridadSinJustificacionException.class,
                () -> new Prioridad(NivelPrioridad.ALTA, null));
    }

    /**
     * RN-07 — Justificación con solo espacios debe lanzar excepción (blank check).
     */
    @Test
    void justificacionSoloEspaciosDebeLanzarPrioridadSinJustificacionException() {
        assertThrows(PrioridadSinJustificacionException.class,
                () -> new Prioridad(NivelPrioridad.ALTA, "     "));
    }

    /**
     * RN-07 — Nivel nulo debe lanzar NullPointerException (Objects.requireNonNull).
     */
    @Test
    void nivelNuloDebeLanzarNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Prioridad(null, "Justificación válida"));
    }
}
