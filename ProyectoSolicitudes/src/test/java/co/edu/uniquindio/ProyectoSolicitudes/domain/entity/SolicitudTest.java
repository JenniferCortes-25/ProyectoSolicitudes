package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    // =========================================================================
    // INSTANCIAS FIJAS — evitan el problema de UUID diferente en cada llamada
    // =========================================================================

    private final Usuario DOCENTE_FIJO = new Usuario(
            "D-001",
            "Docente López",
            new Email("docente@uniquindio.edu.co"),
            TipoUsuario.DOCENTE
    );

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

    // Siempre devuelve la misma instancia — mismo UUID garantizado
    private Usuario docenteValido() {
        return DOCENTE_FIJO;
    }

    private Prioridad prioridadValida() {
        return new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");
    }

    // Usa DOCENTE_FIJO para que el UUID coincida con docenteValido()
    private Responsable responsableValido() {
        return new Responsable(DOCENTE_FIJO.getId(), DOCENTE_FIJO.getNombre());
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
        // DOCENTE_FIJO tiene el mismo UUID que el Responsable asignado — coincide correctamente
        s.atender("Proceso completado satisfactoriamente", DOCENTE_FIJO);
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

    @Test
    void solicitudNuevaDebeIniciarEnEstadoRegistrada() {
        Solicitud solicitud = solicitudValida();
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
    }

    @Test
    void solicitudNuevaDebeGenerarPrimeraEntradaEnHistorial() {
        Solicitud solicitud = solicitudValida();
        assertEquals(1, solicitud.getHistorial().size());
    }

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

    @Test
    void clasificarEnRegistradaDebeTransicionarAClasificada() {
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipo());
    }

    @Test
    void clasificarDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudValida();
        int antes = solicitud.getHistorial().size();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    @Test
    void clasificarSolicitudYaClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.clasificar(TipoSolicitud.OTRO, coordinadorValido())
        );
    }

    @Test
    void clasificarConTipoNuloDebeLanzarIllegalArgumentException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.clasificar(null, coordinadorValido())
        );
    }

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

    @Test
    void asignarPrioridadEnClasificadaDebeAsignarCorrectamente() {
        Solicitud solicitud = solicitudClasificada();
        assertNotNull(solicitud.getPrioridad());
        assertEquals(NivelPrioridad.ALTA, solicitud.getPrioridad().nivel());
    }

    @Test
    void asignarPrioridadEnRegistradaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudValida();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.asignarPrioridad(prioridadValida(), coordinadorValido())
        );
    }

    @Test
    void asignarPrioridadNulaDebeLanzarIllegalArgumentException() {
        // clasificar sin asignar prioridad para quedar en CLASIFICADA limpia
        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido());
        assertThrows(IllegalArgumentException.class, () ->
                solicitud.asignarPrioridad(null, coordinadorValido())
        );
    }

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

    @Test
    void asignarResponsableDebeAsignarCorrectamente() {
        Solicitud solicitud = solicitudClasificada();
        Responsable responsable = responsableValido();
        solicitud.asignarResponsable(responsable, coordinadorValido());
        assertNotNull(solicitud.getResponsable());
        assertEquals(responsable.nombre(), solicitud.getResponsable().nombre());
    }

    @Test
    void asignarResponsableDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudClasificada();
        int antes = solicitud.getHistorial().size();
        solicitud.asignarResponsable(responsableValido(), coordinadorValido());
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    @Test
    void asignarResponsableNuloDebeLanzarNullPointerException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(NullPointerException.class, () ->
                solicitud.asignarResponsable(null, coordinadorValido())
        );
    }

    @Test
    void asignarResponsableEnSolicitudCerradaDebeLanzarSolicitudCerradaException() {
        Solicitud solicitud = solicitudCerrada();
        assertThrows(SolicitudCerradaException.class, () ->
                solicitud.asignarResponsable(responsableValido(), coordinadorValido())
        );
    }

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

    @Test
    void iniciarAtencionConResponsableDebeTransicionarAEnAtencion() {
        Solicitud solicitud = solicitudConResponsable();
        solicitud.iniciarAtencion(coordinadorValido());
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());
    }

    @Test
    void iniciarAtencionSinResponsableDebeLanzarSinResponsableException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(SinResponsableException.class, () ->
                solicitud.iniciarAtencion(coordinadorValido())
        );
    }

    @Test
    void iniciarAtencionEnRegistradaDebeLanzarTransicionInvalidaException() {
        // solicitudValida() está en REGISTRADA — asignamos responsable pero
        // no podemos porque asignarResponsable no valida el estado,
        // pero iniciarAtencion sí exige CLASIFICADA
        Solicitud solicitud = solicitudValida();
        solicitud.asignarResponsable(responsableValido(), coordinadorValido());
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.iniciarAtencion(coordinadorValido())
        );
    }

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

    @Test
    void atenderDebeTransicionarAAtendida() {
        Solicitud solicitud = solicitudEnAtencion();
        solicitud.atender("Proceso completado satisfactoriamente", DOCENTE_FIJO);
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    @Test
    void atenderDebeAgregarEntradaAlHistorial() {
        Solicitud solicitud = solicitudEnAtencion();
        int antes = solicitud.getHistorial().size();
        solicitud.atender("Proceso completado", DOCENTE_FIJO);
        assertEquals(antes + 1, solicitud.getHistorial().size());
    }

    @Test
    void atenderEnClasificadaDebeLanzarTransicionInvalidaException() {
        Solicitud solicitud = solicitudClasificada();
        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.atender("Observación", DOCENTE_FIJO)
        );
    }

    @Test
    void atenderConObservacionNulaDebeAceptarseCorrectamente() {
        Solicitud solicitud = solicitudEnAtencion();
        assertDoesNotThrow(() -> solicitud.atender(null, DOCENTE_FIJO));
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    @Test
    void atenderConUsuarioDistintoAlResponsableDebeLanzarPermisoInsuficienteException() {
        Solicitud solicitud = solicitudEnAtencion();
        // coordinadorValido() tiene UUID diferente al DOCENTE_FIJO — debe fallar
        assertThrows(PermisoInsuficienteException.class, () ->
                solicitud.atender("Observación", coordinadorValido())
        );
    }

    // =========================================================================
    // CERRAR
    // =========================================================================

    @Test
    void cerrarSolicitudAtendidaDebeTransicionarACerrada() {
        Solicitud solicitud = solicitudAtendida();
        solicitud.cerrar(
                new ObservacionCierre("Homologación aprobada por consejo de programa"),
                coordinadorValido()
        );
        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

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

    @Test
    void clasificarSolicitudCerradaDebeLanzarSolicitudCerradaException() {
        Solicitud solicitud = solicitudCerrada();
        assertThrows(SolicitudCerradaException.class, () ->
                solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coordinadorValido())
        );
    }

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

    @Test
    void solicitudDebePoderRecorrerCicloDeVidaCompleto() {
        Usuario coord = coordinadorValido();
        Responsable r = new Responsable(DOCENTE_FIJO.getId(), DOCENTE_FIJO.getNombre());

        Solicitud solicitud = solicitudValida();
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coord);
        solicitud.asignarPrioridad(prioridadValida(), coord);
        solicitud.asignarResponsable(r, coord);
        solicitud.iniciarAtencion(coord);
        solicitud.atender("Proceso completado satisfactoriamente", DOCENTE_FIJO);
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), coord);

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
        assertTrue(solicitud.getHistorial().size() > 1);
    }

    @Test
    void cicloDeVidaCompletoDebeRegistrarTodasLasAccionesEnHistorial() {
        Usuario coord = coordinadorValido();
        Responsable r = new Responsable(DOCENTE_FIJO.getId(), DOCENTE_FIJO.getNombre());

        Solicitud solicitud = solicitudValida();                                                          // 1
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, coord);                                         // 2
        solicitud.asignarPrioridad(prioridadValida(), coord);                                            // 3
        solicitud.asignarResponsable(r, coord);                                                          // 4
        solicitud.iniciarAtencion(coord);                                                                // 5
        solicitud.atender("Proceso completado satisfactoriamente", DOCENTE_FIJO);                        // 6
        solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), coord); // 7

        assertEquals(7, solicitud.getHistorial().size());
    }
}