package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResponsableTest {

    // ─── Casos VÁLIDOS ────────────────────────────────────────────────────────

    /**
     * Un Responsable con ID y nombre válidos debe crearse sin excepciones.
     */
    @Test
    void responsableValidoDebeCrearseSinExcepcion() {
        assertDoesNotThrow(() -> new Responsable(UUID.randomUUID(), "Juan Pérez"));
    }

    /**
     * Igualdad por valor — dos Responsable con los mismos datos deben ser iguales.
     */
    @Test
    void dosResponsablesConMismosDatosDebenSerIguales() {
        UUID id = UUID.randomUUID();
        Responsable r1 = new Responsable(id, "Juan Pérez");
        Responsable r2 = new Responsable(id, "Juan Pérez");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    /**
     * Dos Responsable con diferente ID no deben ser iguales.
     */
    @Test
    void responsablesConDiferenteIdNoDenSerIguales() {
        Responsable r1 = new Responsable(UUID.randomUUID(), "Juan Pérez");
        Responsable r2 = new Responsable(UUID.randomUUID(), "Juan Pérez");

        assertNotEquals(r1, r2);
    }

    // ─── Casos INVÁLIDOS ──────────────────────────────────────────────────────

    /**
     * ID nulo debe lanzar NullPointerException (Objects.requireNonNull).
     */
    @Test
    void idNuloDebeLanzarNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Responsable(null, "Juan Pérez"));
    }

    /**
     * Nombre nulo debe lanzar NullPointerException (Objects.requireNonNull).
     */
    @Test
    void nombreNuloDebeLanzarNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Responsable(UUID.randomUUID(), null));
    }
}
