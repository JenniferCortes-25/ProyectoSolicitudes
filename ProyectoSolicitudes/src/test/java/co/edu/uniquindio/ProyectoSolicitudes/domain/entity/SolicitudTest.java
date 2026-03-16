package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Solicitud solicitudValida() {
        return new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                UUID.randomUUID()
        );
    }

    private Solicitud solicitudClasificada() {
        Solicitud s = solicitudValida();
        s.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
        return s;
    }

    private Solicitud solicitudConResponsable() {
        Solicitud s = solicitudClasificada();
        s.asignarResponsable(responsableValido(), true, "coord-01");
        return s;
    }

    private Solicitud solicitudEnAtencion() {
        Solicitud s = solicitudConResponsable();
        s.iniciarAtencion("coord-01");
        return s;
    }

    private Solicitud solicitudAtendida() {
        Solicitud s = solicitudEnAtencion();
        s.atender("Proceso completado satisfactoriamente", "coord-01");
        return s;
    }

    private Solicitud solicitudCerrada() {
        Solicitud s = solicitudAtendida();
        s.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");
        return s;
    }

    private Responsable responsableValido() {
        return new Responsable(UUID.randomUUID(), "Juan Pérez");
    }

    private Prioridad prioridadValida() {
        return new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");
    }

    // =========================================================================
    // CONSTRUCCIÓN
    // =========================================================================

    /**
     * RF-01 — Una solicitud nueva debe iniciar siempre en estado REGISTRADA.
     */
    @Test
    void solicitudNuevaDebeIniciarEnEstadoRegistrada() {
        Solicitud solicitud = solicitudValida();
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
    }

    /**
     * RF-01 — Una solicitud nueva debe tener un ID generado automáticamente.
     */
    @Test
    void solicitudNuevaDebeGenerarId() {
        Solicitud solicitud = solicitudValida();
        assertNotNull(solicitud.getId());
    }

    /**
     * RF-01 — Una solicitud nueva debe tener fecha de registro.
     */
    @Test
    void solicitudNuevaDebeRegistrarFechaDeRegistro() {
        Solicitud solicitud = solicitudValida();
        assertNotNull(solicitud.getFechaRegistro());
    }

    /**
     * RF-06 — El historial debe tener una entrada al registrar la solicitud.
     */
    @Test
    void solicitudNuevaDebeRegistrarEntradaEnHistorial() {
        Solicitud solicitud = solicitudValida();
        assertEquals(1, solicitud.getHistorial().size());
    }

    /**
     * Construcción sin canal debe lanzar IllegalArgumentException.
     */
    @Test
    void solicitudSinCanalDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Solicitud(
                        new DescripcionSolicitud("Descripción con más de diez caracteres"),
                        null,
                        UUID.randomUUID()
                )
        );
    }

    /**
     * Construcción sin solicitante debe lanzar IllegalArgumentException.
     */
    @Test
    void solicitudSinSolicitanteDebeLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Solicitud(
                        new DescripcionSolicitud("Descripción con más de diez caracteres"),
                        CanalOrigen.PRESENCIAL,
                        null
                )
        );
    }

    /**
     * Historial no debe poder modificarse desde fuera (unmodifiable).
     */
    @Test
    void historialDebeSerInmutableDesdeAfuera() {
        Solicitud solicitud = solicitudValida();
        assertThrows(UnsupportedOperationException.class, () ->
                solicitud.getHistorial().add(
                        new EntradaHistorial(null, "hack", "hacker", "")
                )
        );
    }

    // =========================================================================
    // CLASIFICAR
    // =========================================================================

    /**
     * RF-02 / RN-01 — Clasificar debe cambiar el estado a CLASIFICADA.
     */
    @Test
    void clasificarDebeTransicionarAClasificada() {
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
    }

    /**
     * RF-02 — Clasificar debe asignar el tipo correctamente.
     */
    @Test
    void clasificarDebeAsignarElTipo() {
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.CONSULTA_ACADEMICA, "coord-01");
        assertEquals(TipoSolicitud.CONSULTA_ACADEMICA, solicitud.getTipo());
    }

    /**
     * RF-02 — Clasificar debe agregar una entrada al historial.
     */
    @Test
    void clasificarDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudValida();
        int antes = solicitud.getHistorial().size();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    // (Método duplicado eliminado)

    /**
     * RN-01 — Clasificar una solicitud ya CLASIFICADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void clasificarSolicitudYaClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.clasificar(TipoSolicitud.OTRO, "coord-01")
        );
    }

    /**
     * Clasificar con tipo nulo debe lanzar IllegalArgumentException.
     */
    @Test
    void clasificarConTipoNuloDebeLanzarIllegalArgumentException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.clasificar(null, "coord-01")
        );
    }

    // =========================================================================
    // ASIGNAR PRIORIDAD
    // =========================================================================

    /**
     * RF-03 / RN-02 — Asignar prioridad en estado CLASIFICADA debe funcionar.
     */
    @Test
    void asignarPrioridadEnClasificadaDebeAsignarCorrectamente() {
        Solicitud solicitud = solicitudClasificada();
        solicitud.asignarPrioridad(prioridadValida(), "coord-01");
        assertNotNull(solicitud.getPrioridad());
        assertEquals(NivelPrioridad.ALTA, solicitud.getPrioridad().nivel());
    }

    /**
     * RF-03 — Asignar prioridad debe agregar una entrada al historial.
     */
    @Test
    void asignarPrioridadDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudClasificada();
        int antes = solicitud.getHistorial().size();
        solicitud.asignarPrioridad(prioridadValida(), "coord-01");
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-02 — Asignar prioridad en estado REGISTRADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void asignarPrioridadEnRegistradaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.asignarPrioridad(prioridadValida(), "coord-01")
        );
    }

    /**
     * Asignar prioridad nula debe lanzar IllegalArgumentException.
     */
    @Test
    void asignarPrioridadNulaDebeLanzarIllegalArgumentException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.asignarPrioridad(null, "coord-01")
        );
    }

    // =========================================================================
    // ASIGNAR RESPONSABLE
    // =========================================================================

    /**
     * RF-05 / RN-03 — Asignar responsable activo debe funcionar correctamente.
     */
    @Test
    void asignarResponsableActivoDebeAsignarCorrectamente() {
        Solicitud solicitud = solicitudClasificada();
        Responsable responsable = responsableValido();
        solicitud.asignarResponsable(responsable, true, "coord-01");
        assertNotNull(solicitud.getResponsable());
        assertEquals(responsable.nombre(), solicitud.getResponsable().nombre());
    }

    /**
     * RF-05 — Asignar responsable debe agregar una entrada al historial.
     */
    @Test
    void asignarResponsableDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudClasificada();
        int antes = solicitud.getHistorial().size();
        solicitud.asignarResponsable(responsableValido(), true, "coord-01");
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-04 / RN-10 — Asignar responsable inactivo debe lanzar UsuarioInactivoException.
     */
    @Test
    void asignarResponsableInactivoDebeLanzarUsuarioInactivoException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(UsuarioInactivoException.class, () ->
                solicitud.asignarResponsable(responsableValido(), false, "coord-01")
        );
    }

    /**
     * Asignar responsable nulo debe lanzar IllegalArgumentException.
     */
    @Test
    void asignarResponsableNuloDebeLanzarIllegalArgumentException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.asignarResponsable(null, true, "coord-01")
        );
    }

    /**
     * RN-01 — Asignar responsable en solicitud cerrada debe lanzar SolicitudCerradaException.
     */
    @Test
    void asignarResponsableEnSolicitudCerradaDebeLanzarSolicitudCerradaException() {
        Solicitud solicitud = solicitudCerrada();
        assertThrows(SolicitudCerradaException.class, () ->
                solicitud.asignarResponsable(responsableValido(), true, "coord-01")
        );
    }

    // =========================================================================
    // INICIAR ATENCIÓN
    // =========================================================================

    /**
     * RN-02 / RN-05 — Iniciar atención con responsable asignado debe cambiar estado a EN_ATENCION.
     */
    @Test
    void iniciarAtencionConResponsableDebeTransicionarAEnAtencion() {
        Solicitud solicitud = solicitudConResponsable();
        solicitud.iniciarAtencion("coord-01");
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());
    }

    /**
     * RN-05 — Iniciar atención sin responsable debe lanzar SinResponsableException.
     */
    @Test
    void iniciarAtencionSinResponsableDebeLanzarSinResponsableException() {
        Solicitud solicitud = solicitudClasificada(); // sin responsable
        assertThrows(SinResponsableException.class, () ->
                solicitud.iniciarAtencion("coord-01")
        );
    }

    /**
     * RN-02 — Iniciar atención en estado REGISTRADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void iniciarAtencionEnRegistradaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudValida();
        solicitud.asignarResponsable(responsableValido(), true, "coord-01");
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.iniciarAtencion("coord-01")
        );
    }

    // =========================================================================
    // ATENDER
    // =========================================================================

    /**
     * RN-02 — Atender debe cambiar el estado a ATENDIDA.
     */
    @Test
    void atenderDebeTransicionarAAtendida() {
        Solicitud solicitud = solicitudEnAtencion();
        solicitud.atender("Proceso completado satisfactoriamente", "coord-01");
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    /**
     * Atender debe agregar una entrada al historial.
     */
    @Test
    void atenderDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudEnAtencion();
        int antes = solicitud.getHistorial().size();
        solicitud.atender("Proceso completado", "coord-01");
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-02 — Atender en estado CLASIFICADA (no EN_ATENCION) debe lanzar TransicionInvalidaException.
     */
    @Test
    void atenderEnClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.atender("Observación", "coord-01")
        );
    }

    /**
     * Atender con observación nula no debe lanzar excepción (se acepta null).
     */
    @Test
    void atenderConObservacionNulaDebeAceptarseCorrectamente() {
        Solicitud solicitud = solicitudEnAtencion();
        assertDoesNotThrow(() -> solicitud.atender(null, "coord-01"));
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    // =========================================================================
    // CERRAR
    // =========================================================================

    /**
     * RF-08 / RN-01 / RN-02 — Cerrar una solicitud ATENDIDA debe cambiar estado a CERRADA.
     */
    @Test
    void cerrarSolicitudAtendidaDebeTransicionarACerrada() {
        Solicitud solicitud = solicitudAtendida();
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");
        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

    /**
     * RF-08 — Cerrar debe agregar una entrada al historial.
     */
    @Test
    void cerrarDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudAtendida();
        int antes = solicitud.getHistorial().size();
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-02 — Cerrar una solicitud en estado CLASIFICADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void cerrarSolicitudClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
        );
    }

    /**
     * RN-02 — Cerrar en estado EN_ATENCION debe lanzar TransicionInvalidaException.
     */
    @Test
    void cerrarSolicitudEnAtencionDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudEnAtencion();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
        );
    }

    /**
     * RN-01 — Cualquier operación sobre una solicitud CERRADA debe lanzar SolicitudCerradaException.
     */
    @Test
    void clasificarSolicitudCerradaDebeLanzarSolicitudCerradaException() {
        Solicitud solicitud = solicitudCerrada();
        assertThrows(SolicitudCerradaException.class, () ->
                solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01")
        );
    }

    // =========================================================================
    // INVARIANTE DE ESTADO
    // =========================================================================

    /**
     * RN-02 — El estado NO debe cambiar cuando falla una transición inválida.
     */
    @Test
    void estadoNoDebeCambiarCuandoFallaTransicionInvalida() {
        Solicitud solicitud = solicitudClasificada();
        EstadoSolicitud estadoAntes = solicitud.getEstado();

        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
        );

        assertEquals(estadoAntes, solicitud.getEstado());
    }

    /**
     * El historial no debe crecer cuando una operación falla.
     */
    @Test
    void historialNoDebeCrecerCuandoFallaUnaOperacion() {
        Solicitud solicitud = solicitudClasificada();
        int historialAntes = solicitud.getHistorial().size();

        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
        );

        assertEquals(historialAntes, solicitud.getHistorial().size());
    }

    // =========================================================================
    // FLUJO COMPLETO
    // =========================================================================

    /**
     * Flujo completo — una solicitud puede recorrer todo el ciclo de vida
     * desde REGISTRADA hasta CERRADA sin errores.
     */
    @Test
    void solicitudDebePoderRecorrerCicloDeVidaCompleto() {
        Solicitud solicitud = solicitudValida();

        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
        solicitud.asignarPrioridad(prioridadValida(), "coord-01");
        solicitud.asignarResponsable(responsableValido(), true, "coord-01");
        solicitud.iniciarAtencion("coord-01");
        solicitud.atender("Proceso completado satisfactoriamente", "coord-01");
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
        assertTrue(solicitud.getHistorial().size() > 1);
    }

    /**
     * Flujo completo — el historial debe registrar todas las acciones del ciclo de vida.
     */
    @Test
    void cicloDeVidaCompletoDebeRegistrarTodasLasAccionesEnHistorial() {
        Solicitud solicitud = solicitudValida(); // 1 entrada

        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");          // +1 = 2
        solicitud.asignarPrioridad(prioridadValida(), "coord-01");             // +1 = 3
        solicitud.asignarResponsable(responsableValido(), true, "coord-01");   // +1 = 4
        solicitud.iniciarAtencion("coord-01");                                  // +1 = 5
        solicitud.atender("Proceso completado satisfactoriamente", "coord-01");// +1 = 6
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01"); // +1 = 7

        assertEquals(7, solicitud.getHistorial().size());
    }
}

