package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    // =========================================================================
    // HELPERS
    // =========================================================================

    private Usuario coordinadorValido() {
        return new Usuario(
                "C-001",
                "Coordinador Pérez",
                new Email("coord@uniquindio.edu.co"),
                TipoUsuario.COORDINADOR
        );
    }

    private Usuario solicitanteValido() {
        return new Usuario(
                "E-001",
                "Estudiante García",
                new Email("estudiante@uniquindio.edu.co"),
                TipoUsuario.ESTUDIANTE
        );
    }

    private Usuario docenteValido() {
        return new Usuario(
                "D-001",
                "Docente López",
                new Email("docente@uniquindio.edu.co"),
                TipoUsuario.DOCENTE
        );
    }

    private Prioridad prioridadValida() {
        return new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");
    }

    private Responsable responsableValido() {
        Usuario docente = docenteValido();
        return new Responsable(docente.getId(), docente.getNombre());
    }

    /** Estado: REGISTRADA */
    private Solicitud solicitudValida() {
        return new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                solicitanteValido()
        );
    }

    /** Estado: CLASIFICADA (tipo + prioridad asignados) */
    private Solicitud solicitudClasificada() {
        Solicitud s = solicitudValida();
        Usuario coord = coordinadorValido();
        s.clasificar(TipoSolicitud.HOMOLOGACION, coord);
        s.asignarPrioridad(prioridadValida(), coord);
        return s;
    }

    /** Estado: CLASIFICADA con responsable asignado, pero aún no EN_ATENCION */
    private Solicitud solicitudConResponsable() {
        Solicitud s = solicitudClasificada();
        s.asignarResponsable(responsableValido(), coordinadorValido());
        return s;
    }

    /** Estado: EN_ATENCION */
    private Solicitud solicitudEnAtencion() {
        Solicitud s = solicitudConResponsable();
        s.iniciarAtencion(coordinadorValido());
        return s;
    }

    /** Estado: ATENDIDA */
    private Solicitud solicitudAtendida() {
        Solicitud s = solicitudEnAtencion();
        // atender() requiere el responsable asignado — usamos el mismo docente
        Usuario docente = docenteValido();
        Responsable r = new Responsable(docente.getId(), docente.getNombre());
        // Necesitamos el Usuario cuyo ID coincida con el Responsable asignado
        s.atender("Proceso completado satisfactoriamente", docente);
        return s;
    }

    /** Estado: CERRADA */
    private Solicitud solicitudCerrada() {
        Solicitud s = solicitudAtendida();
        s.cerrar(
                new ObservacionCierre("Homologación aprobada por consejo de programa"),
                coordinadorValido()
        );
        return s;
    }

    // =========================================================================
    // CONSTRUCCIÓN
    // =========================================================================

    /**
     * RF-01 — Una solicitud nueva debe iniciar en estado REGISTRADA.
     */
    @Test
    void solicitudNuevaDebeIniciarEnEstadoRegistrada() {
        Solicitud solicitud = solicitudValida();
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
    }

    /**
     * RF-01 — Una solicitud nueva debe tener historial con una entrada.
     */
    @Test
    void solicitudNuevaDebeGenerarPrimeraEntradaEnHistorial() {
        Solicitud solicitud = solicitudValida();
        assertEquals(1, solicitud.getHistorial().size());
    }

    /**
     * RF-01 — Crear solicitud con canal nulo debe lanzar NullPointerException.
     */
    @Test
    void crearSolicitudConCanalNuloDebeLanzarNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Solicitud(
                        new DescripcionSolicitud("Descripción válida de la solicitud"),
                        null,
                        solicitanteValido()
                )
        );
    }

    /**
     * RF-01 — Crear solicitud con solicitante nulo debe lanzar NullPointerException.
     */
    @Test
    void crearSolicitudConSolicitanteNuloDebeLanzarNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Solicitud(
                        new DescripcionSolicitud("Descripción válida de la solicitud"),
                        CanalOrigen.PRESENCIAL,
                        null
                )
        );
    }

    /**
     * RN-04 — Crear solicitud con solicitante INACTIVO debe lanzar UsuarioInactivoException.
     */
    @Test
    void crearSolicitudConSolicitanteInactivoDebeLanzarUsuarioInactivoException() {
        Usuario solicitante = solicitanteValido();
        solicitante.desactivar();
        assertThrows(UsuarioInactivoException.class, () ->
                new Solicitud(
                        new DescripcionSolicitud("Descripción válida de la solicitud"),
                        CanalOrigen.PRESENCIAL,
                        solicitante
                )
        );
    }

    /**
     * RF-01 — La solicitud debe guardar el ID y nombre del solicitante.
     */
    @Test
    void solicitudDebeGuardarDatosDelSolicitante() {
        Usuario solicitante = solicitanteValido();
        Solicitud solicitud = new Solicitud(
                new DescripcionSolicitud("Descripción válida de la solicitud"),
                CanalOrigen.PRESENCIAL,
                solicitante
        );
        assertEquals(solicitante.getId(), solicitud.getSolicitanteId());
        assertEquals(solicitante.getNombre(), solicitud.getSolicitanteNombre());
    }

    // =========================================================================
    // CLASIFICAR
    // =========================================================================

    /**
     * RF-02 / RN-02 — Clasificar en estado REGISTRADA debe cambiar estado a CLASIFICADA.
     */
    @Test
    void clasificarEnRegistradaDebeTransicionarAClasificada() {
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipo());
    }

    /**
     * RF-02 — Clasificar debe agregar una entrada al historial.
     */
    @Test
    void clasificarDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudValida();
        int antes = solicitud.getHistorial().size();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-01 — Clasificar una solicitud ya CLASIFICADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void clasificarSolicitudYaClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.clasificar(TipoSolicitud.OTRO, coordinadorValido())
        );
    }

    /**
     * Clasificar con tipo nulo debe lanzar IllegalArgumentException.
     */
    @Test
    void clasificarConTipoNuloDebeLanzarIllegalArgumentException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.clasificar(null, coordinadorValido())
        );
    }

    /**
     * RN-13 — Clasificar con usuario que no es COORDINADOR debe lanzar PermisoInsuficienteException.
     */
    @Test
    void clasificarConUsuarioNoCoordinadorDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.clasificar(TipoSolicitud.HOMOLOGACION, docenteValido())
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
        // La solicitudClasificada() ya asigna prioridad internamente,
        // verificamos que quedó correctamente asignada
        assertNotNull(solicitud.getPrioridad());
        assertEquals(NivelPrioridad.ALTA, solicitud.getPrioridad().nivel());
    }

    /**
     * RN-02 — Asignar prioridad en estado REGISTRADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void asignarPrioridadEnRegistradaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.asignarPrioridad(prioridadValida(), coordinadorValido())
        );
    }

    /**
     * Asignar prioridad nula debe lanzar IllegalArgumentException.
     */
    @Test
    void asignarPrioridadNulaDebeLanzarIllegalArgumentException() {
        // Necesitamos estado CLASIFICADA pero sin prioridad asignada aún
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.asignarPrioridad(null, coordinadorValido())
        );
    }

    /**
     * RN-13 — Asignar prioridad con usuario no COORDINADOR debe lanzar PermisoInsuficienteException.
     */
    @Test
    void asignarPrioridadConUsuarioNoCoordinadorDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.asignarPrioridad(prioridadValida(), docenteValido())
        );
    }

    // =========================================================================
    // ASIGNAR RESPONSABLE
    // =========================================================================

    /**
     * RF-05 / RN-03 — Asignar responsable debe funcionar correctamente.
     */
    @Test
    void asignarResponsableDebeAsignarCorrectamente() {
        Solicitud solicitud = solicitudClasificada();
        Responsable responsable = responsableValido();
        solicitud.asignarResponsable(responsable, coordinadorValido());
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
        solicitud.asignarResponsable(responsableValido(), coordinadorValido());
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * Asignar responsable nulo debe lanzar NullPointerException.
     */
    @Test
    void asignarResponsableNuloDebeLanzarNullPointerException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(NullPointerException.class, () ->
                solicitud.asignarResponsable(null, coordinadorValido())
        );
    }

    /**
     * RN-01 — Asignar responsable en solicitud cerrada debe lanzar SolicitudCerradaException.
     */
    @Test
    void asignarResponsableEnSolicitudCerradaDebeLanzarSolicitudCerradaException() {
        Solicitud solicitud = solicitudCerrada();
        assertThrows(SolicitudCerradaException.class, () ->
                solicitud.asignarResponsable(responsableValido(), coordinadorValido())
        );
    }

    /**
     * RN-13 — Asignar responsable con usuario no COORDINADOR debe lanzar PermisoInsuficienteException.
     */
    @Test
    void asignarResponsableConUsuarioNoCoordinadorDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.asignarResponsable(responsableValido(), docenteValido())
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
        solicitud.iniciarAtencion(coordinadorValido());
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());
    }

    /**
     * RN-05 — Iniciar atención sin responsable debe lanzar SinResponsableException.
     */
    @Test
    void iniciarAtencionSinResponsableDebeLanzarSinResponsableException() {
        Solicitud solicitud = solicitudClasificada(); // sin responsable asignado
        assertThrows(SinResponsableException.class, () ->
                solicitud.iniciarAtencion(coordinadorValido())
        );
    }

    /**
     * RN-02 — Iniciar atención en estado REGISTRADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void iniciarAtencionEnRegistradaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudValida();
        solicitud.asignarResponsable(responsableValido(), coordinadorValido());
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.iniciarAtencion(coordinadorValido())
        );
    }

    /**
     * RN-13 — Iniciar atención con ESTUDIANTE debe lanzar PermisoInsuficienteException.
     */
    @Test
    void iniciarAtencionConEstudianteDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudConResponsable();
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.iniciarAtencion(solicitanteValido())
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
        solicitud.atender("Proceso completado satisfactoriamente", docenteValido());
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    /**
     * Atender debe agregar una entrada al historial.
     */
    @Test
    void atenderDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudEnAtencion();
        int antes = solicitud.getHistorial().size();
        solicitud.atender("Proceso completado", docenteValido());
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-02 — Atender en estado CLASIFICADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void atenderEnClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.atender("Observación", docenteValido())
        );
    }

    /**
     * Atender con observación nula no debe lanzar excepción (se acepta null).
     */
    @Test
    void atenderConObservacionNulaDebeAceptarseCorrectamente() {
        Solicitud solicitud = solicitudEnAtencion();
        assertDoesNotThrow(() -> solicitud.atender(null, docenteValido()));
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    /**
     * RN-13 — Atender con usuario distinto al responsable asignado
     * debe lanzar PermisoInsuficienteException.
     */
    @Test
    void atenderConUsuarioDistintoAlResponsableDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudEnAtencion();
        // coordinadorValido() tiene un ID diferente al del responsable asignado (docenteValido)
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.atender("Observación", coordinadorValido())
        );
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
        solicitud.cerrar(
                new ObservacionCierre("Homologación aprobada por consejo de programa"),
                coordinadorValido()
        );
        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

    /**
     * RF-08 — Cerrar debe agregar una entrada al historial.
     */
    @Test
    void cerrarDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudAtendida();
        int antes = solicitud.getHistorial().size();
        solicitud.cerrar(
                new ObservacionCierre("Homologación aprobada por consejo de programa"),
                coordinadorValido()
        );
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    /**
     * RN-02 — Cerrar en estado CLASIFICADA debe lanzar TransicionInvalidaException.
     */
    @Test
    void cerrarSolicitudClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar(
                        new ObservacionCierre("Observación de cierre con más de 20 chars"),
                        coordinadorValido()
                )
        );
    }

    /**
     * RN-02 — Cerrar en estado EN_ATENCION debe lanzar TransicionInvalidaException.
     */
    @Test
    void cerrarSolicitudEnAtencionDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudEnAtencion();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar(
                        new ObservacionCierre("Observación de cierre con más de 20 chars"),
                        coordinadorValido()
                )
        );
    }

    /**
     * RN-01 — Clasificar sobre solicitud CERRADA debe lanzar SolicitudCerradaException.
     */
    @Test
    void clasificarSolicitudCerradaDebeLanzarSolicitudCerradaException() {
        Solicitud solicitud = solicitudCerrada();
        assertThrows(SolicitudCerradaException.class, () ->
                solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido())
        );
    }

    /**
     * RN-13 — Cerrar con usuario no COORDINADOR debe lanzar PermisoInsuficienteException.
     */
    @Test
    void cerrarConUsuarioNoCoordinadorDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudAtendida();
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.cerrar(
                        new ObservacionCierre("Observación de cierre con más de 20 chars"),
                        docenteValido()
                )
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
                solicitud.cerrar(
                        new ObservacionCierre("Observación de cierre con más de 20 chars"),
                        coordinadorValido()
                )
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
                solicitud.cerrar(
                        new ObservacionCierre("Observación de cierre con más de 20 chars"),
                        coordinadorValido()
                )
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
        Usuario coord   = coordinadorValido();
        Usuario docente = docenteValido();
        Responsable r   = new Responsable(docente.getId(), docente.getNombre());

        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coord);
        solicitud.asignarPrioridad(prioridadValida(), coord);
        solicitud.asignarResponsable(r, coord);
        solicitud.iniciarAtencion(coord);
        solicitud.atender("Proceso completado satisfactoriamente", docente);
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), coord);

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
        assertTrue(solicitud.getHistorial().size() > 1);
    }

    /**
     * Flujo completo — el historial debe registrar todas las acciones del ciclo de vida.
     */
    @Test
    void cicloDeVidaCompletoDebeRegistrarTodasLasAccionesEnHistorial() {
        Usuario coord   = coordinadorValido();
        Usuario docente = docenteValido();
        Responsable r   = new Responsable(docente.getId(), docente.getNombre());

        Solicitud solicitud = solicitudValida();                                              // 1
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coord);                             // 2
        solicitud.asignarPrioridad(prioridadValida(), coord);                                // 3
        solicitud.asignarResponsable(r, coord);                                              // 4
        solicitud.iniciarAtencion(coord);                                                    // 5
        solicitud.atender("Proceso completado satisfactoriamente", docente);                 // 6
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), coord); // 7

        assertEquals(7, solicitud.getHistorial().size());
    }
}