package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.EmailInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    // ─── Casos VÁLIDOS ────────────────────────────────────────────────────────

    /**
     * RN-09 — Un email con formato correcto debe crearse sin excepciones.
     */
    @Test
    void emailValidoDebeCrearseSinExcepcion() {
        assertDoesNotThrow(() -> new Email("juan@uniquindio.edu.co"));
    }

    /**
     * RN-09 — Un email con subdominio debe ser válido.
     */
    @Test
    void emailConSubdominioDebeSerValido() {
        assertDoesNotThrow(() -> new Email("estudiante@correo.uniquindio.edu.co"));
    }

    /**
     * Igualdad por valor — dos Email con el mismo texto deben ser iguales.
     */
    @Test
    void dosEmailsConMismoValorDebenSerIguales() {
        Email e1 = new Email("juan@uniquindio.edu.co");
        Email e2 = new Email("juan@uniquindio.edu.co");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    /**
     * Dos Email con diferente texto deben ser distintos.
     */
    @Test
    void dosEmailsConDiferenteValorNoDenSerIguales() {
        Email e1 = new Email("juan@uniquindio.edu.co");
        Email e2 = new Email("pedro@uniquindio.edu.co");

        assertNotEquals(e1, e2);
    }

    // ─── Casos INVÁLIDOS ──────────────────────────────────────────────────────

    /**
     * RN-09 — Email sin arroba debe lanzar EmailInvalidoException.
     */
    @Test
    void emailSinArrobaDebeLanzarEmailInvalidoException() {
        assertThrows(EmailInvalidoException.class, () -> new Email("correo-sin-arroba"));
    }

    /**
     * RN-09 — Email sin dominio debe lanzar EmailInvalidoException.
     */
    @Test
    void emailSinDominioDebeLanzarEmailInvalidoException() {
        assertThrows(EmailInvalidoException.class, () -> new Email("juan@"));
    }

    /**
     * RN-09 — Email nulo debe lanzar EmailInvalidoException.
     */
    @Test
    void emailNuloDebeLanzarEmailInvalidoException() {
        assertThrows(EmailInvalidoException.class, () -> new Email(null));
    }

    /**
     * RN-09 — Email vacío debe lanzar EmailInvalidoException.
     */
    @Test
    void emailVacioDebeLanzarEmailInvalidoException() {
        assertThrows(EmailInvalidoException.class, () -> new Email(""));
    }

    /**
     * RN-09 — Email sin extensión de dominio (sin punto) debe lanzar EmailInvalidoException.
     */
    @Test
    void emailSinPuntoEnDominioDebeLanzarEmailInvalidoException() {
        assertThrows(EmailInvalidoException.class, () -> new Email("juan@uniquindio"));
    }
}