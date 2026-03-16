package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Usuario usuarioValido() {
        return new Usuario(
                "1234567890",
                "Juan Pérez",
                new Email("juan@uniquindio.edu.co"),
                TipoUsuario.DOCENTE
        );
    }

    // ─── Construcción ─────────────────────────────────────────────────────────

    /**
     * Un usuario nuevo debe iniciar siempre en estado ACTIVO.
     */
    @Test
    void usuarioNuevoDebeIniciarEnEstadoActivo() {
        Usuario usuario = usuarioValido();
        assertEquals(EstadoUsuario.ACTIVO, usuario.getEstadoUsuario());
        assertTrue(usuario.estaActivo());
    }

    /**
     * Un usuario nuevo debe tener un ID generado automáticamente (no nulo).
     */
    @Test
    void usuarioNuevoDebeGenerarId() {
        Usuario usuario = usuarioValido();
        assertNotNull(usuario.getId());
    }

    /**
     * Construcción sin identificación debe lanzar IllegalArgumentException.
     */
    @Test
    void usuarioSinIdentificacionDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Usuario("", "Juan Pérez", new Email("juan@uniquindio.edu.co"), TipoUsuario.DOCENTE)
        );
    }

    /**
     * Construcción sin nombre debe lanzar IllegalArgumentException.
     */
    @Test
    void usuarioSinNombreDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Usuario("1234567890", "  ", new Email("juan@uniquindio.edu.co"), TipoUsuario.DOCENTE)
        );
    }

    /**
     * Construcción con email nulo debe lanzar IllegalArgumentException.
     */
    @Test
    void usuarioConEmailNuloDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Usuario("1234567890", "Juan Pérez", null, TipoUsuario.DOCENTE)
        );
    }

    /**
     * Construcción con tipo nulo debe lanzar IllegalArgumentException.
     */
    @Test
    void usuarioConTipoNuloDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Usuario("1234567890", "Juan Pérez", new Email("juan@uniquindio.edu.co"), null)
        );
    }

    // ─── Activar / Desactivar ─────────────────────────────────────────────────

    /**
     * Desactivar un usuario activo debe cambiar su estado a INACTIVO.
     */
    @Test
    void desactivarUsuarioActivoDebeCambiarEstadoAInactivo() {
        Usuario usuario = usuarioValido();
        usuario.desactivar();

        assertEquals(EstadoUsuario.INACTIVO, usuario.getEstadoUsuario());
        assertFalse(usuario.estaActivo());
    }

    /**
     * Desactivar y volver a activar debe dejar el usuario en estado ACTIVO.
     */
    @Test
    void usuarioDesactivadoPuedeVolverAActivarse() {
        Usuario usuario = usuarioValido();
        usuario.desactivar();
        usuario.activar();

        assertTrue(usuario.estaActivo());
    }

    /**
     * Activar un usuario ya activo debe lanzar IllegalStateException.
     */
    @Test
    void activarUsuarioYaActivoDebeLanzarIllegalStateException() {
        Usuario usuario = usuarioValido();
        assertThrows(IllegalStateException.class, usuario::activar);
    }

    /**
     * Desactivar un usuario ya inactivo debe lanzar IllegalStateException.
     */
    @Test
    void desactivarUsuarioYaInactivoDebeLanzarIllegalStateException() {
        Usuario usuario = usuarioValido();
        usuario.desactivar();
        assertThrows(IllegalStateException.class, usuario::desactivar);
    }

    // ─── tieneTipo ────────────────────────────────────────────────────────────

    /**
     * tieneTipo debe retornar true cuando el tipo coincide.
     */
    @Test
    void tieneTipoDebeRetornarTrueCuandoElTipoCoinide() {
        Usuario usuario = usuarioValido(); // TipoUsuario.DOCENTE
        assertTrue(usuario.tieneTipo(TipoUsuario.DOCENTE));
    }

    /**
     * tieneTipo debe retornar false cuando el tipo no coincide.
     */
    @Test
    void tieneTipoDebeRetornarFalseCuandoElTipoNoCoinide() {
        Usuario usuario = usuarioValido(); // TipoUsuario.DOCENTE
        assertFalse(usuario.tieneTipo(TipoUsuario.COORDINADOR));
        assertFalse(usuario.tieneTipo(TipoUsuario.ESTUDIANTE));
        assertFalse(usuario.tieneTipo(TipoUsuario.ADMINISTRATIVO));
    }
}
