package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Solicitud {
    private UUID id;
    private DescripcionSolicitud descripcion;
    private TipoSolicitud tipo;
    private Prioridad prioridad;
    private EstadoSolicitud estado;          // inicia siempre en REGISTRADA
    private CanalOrigen canal;
    private UUID solicitanteId;
    private String solicitanteNombre;
    private Responsable responsable;
    private List<EntradaHistorial> historial = new ArrayList<>();
    private LocalDateTime fechaRegistro;

    // Constructor
    /**
     * Recibe el Usuario solicitante para validar que esté activo en el momento del registro.
     */
    public Solicitud(DescripcionSolicitud descripcion, CanalOrigen canal, Usuario solicitante) {
        Objects.requireNonNull(descripcion,  "La descripción no puede ser nula");
        Objects.requireNonNull(canal,        "El canal de origen no puede ser nulo");
        Objects.requireNonNull(solicitante,  "El solicitante no puede ser nulo");

        this.id                = UUID.randomUUID();
        this.descripcion       = descripcion;
        this.canal             = canal;
        this.solicitanteId     = solicitante.getId();
        this.solicitanteNombre = solicitante.getNombre();
        this.estado            = EstadoSolicitud.REGISTRADA;
        this.tipo              = null;
        this.prioridad         = null;
        this.responsable       = null;
        this.historial         = new ArrayList<>();
        this.fechaRegistro     = LocalDateTime.now();

        this.historial.add(new EntradaHistorial(
                this.fechaRegistro,
                "Solicitud registrada por canal: " + canal,
                solicitante.getNombre(),
                ""
        ));
    }

    // ─── Métodos del dominio ──────────────────────────────────────────────────

    /**
     * RF-02 / RN-01
     * Asigna el tipo a la solicitud.
     * Solo se puede clasificar si el estado actual es REGISTRADA.
     * Solo un COORDINADOR puede clasificar
     */
    public void clasificar(TipoSolicitud nuevoTipo, Usuario coordinador){
        validarNoEstaCerrada();

        Objects.requireNonNull(coordinador, "El coordinador no puede ser nulo");

        if (!coordinador.tieneTipo(TipoUsuario.COORDINADOR))
            throw new PermisoInsuficienteException("Solo un coordinador puede clasificar una solicitud");

        if (this.estado != EstadoSolicitud.REGISTRADA)
            throw new TransicionInvalidaException(
                    "Solo se puede clasificar en estado REGISTRADA. Estado actual: " + this.estado);

        if (nuevoTipo == null)
            throw new IllegalArgumentException("El tipo no puede ser nulo");

        this.tipo   = nuevoTipo;
        this.estado = EstadoSolicitud.CLASIFICADA;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Solicitud clasificada como: " + nuevoTipo,
                coordinador.getNombre(),
                ""
        ));
    }
    /**
     * RF-03 / RN-02
     * Solo se puede asignar prioridad si estado == CLASIFICADA.
     *  Solo un COORDINADOR puede asignar prioridad.
     */
    public void asignarPrioridad(Prioridad prioridad, Usuario coordinador){
        validarNoEstaCerrada();

        Objects.requireNonNull(coordinador, "El coordinador no puede ser nulo");

        if (!coordinador.tieneTipo(TipoUsuario.COORDINADOR))
            throw new PermisoInsuficienteException("Solo un coordinador puede asignar prioridad");

        if (this.estado != EstadoSolicitud.CLASIFICADA)
            throw new TransicionInvalidaException(
                    "Solo se puede asignar prioridad en estado CLASIFICADA. Estado actual: " + this.estado);

        if (prioridad == null)
            throw new IllegalArgumentException("La prioridad no puede ser nula");

        this.prioridad = prioridad;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Prioridad asignada: " + prioridad.nivel() + " - " + prioridad.justificacion(),
                coordinador.getNombre(),
                ""
        ));
    }

    /**
     * RF-05 / RN-03 / RN-04 / RN-10
     * Solo COORDINADOR puede asignar responsable.
     * El Responsable ya llega validado (activo) desde AsignarResponsableService.
     * La validación de actividad la hace el servicio.
     */
    public void asignarResponsable(Responsable nuevoResponsable, Usuario coordinador){
        validarNoEstaCerrada();

        Objects.requireNonNull(coordinador,     "El coordinador no puede ser nulo");
        Objects.requireNonNull(nuevoResponsable, "El responsable no puede ser nulo");

        if (!coordinador.tieneTipo(TipoUsuario.COORDINADOR))
            throw new PermisoInsuficienteException("Solo un coordinador puede asignar responsable");

        this.responsable = nuevoResponsable;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Responsable asignado: " + nuevoResponsable.nombre(),
                coordinador.getNombre(),
                ""
        ));
    }


    /**
     * RN-02 / RN-05
     * Cambia estado a EN_ATENCION.
     * Requiere responsable asignado y estado == CLASIFICADA.
     * Solo COORDINADOR o ADMINISTRATIVO pueden iniciar atención.
     */
    public void iniciarAtencion(Usuario responsable){
        validarNoEstaCerrada();

        Objects.requireNonNull(responsable, "El usuario no puede ser nulo");

        if (!responsable.tieneTipo(TipoUsuario.COORDINADOR)
                && !responsable.tieneTipo(TipoUsuario.ADMINISTRATIVO))
            throw new PermisoInsuficienteException(
                    "Solo coordinador o administrativo puede iniciar atención");

        if (this.responsable == null)
            throw new SinResponsableException("Debe tener un responsable asignado");

        if (this.estado != EstadoSolicitud.CLASIFICADA)
            throw new TransicionInvalidaException(
                    "Solo se puede iniciar atención si la solicitud está clasificada. Estado actual: " + this.estado);

        this.estado = EstadoSolicitud.EN_ATENCION;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Atención iniciada por: " + this.responsable.nombre(),
                responsable.getNombre(),
                ""
        ));
    }

    /**
     * RN-02
     * Cambia estado a ATENDIDA.
     * Estado debe ser EN_ATENCION.
     * Solo el responsable asignado puede marcar como atendida.
     */
    public void atender(String observacion, Usuario responsable){
        validarNoEstaCerrada();

        Objects.requireNonNull(responsable, "El usuario no puede ser nulo");

        if (this.responsable == null
                || !this.responsable.usuarioId().equals(responsable.getId()))
            throw new PermisoInsuficienteException(
                    "Solo el responsable asignado puede marcar la solicitud como atendida");

        if (this.estado != EstadoSolicitud.EN_ATENCION)
            throw new TransicionInvalidaException(
                    "Solo se puede atender en estado EN_ATENCION. Estado actual: " + this.estado);

        this.estado = EstadoSolicitud.ATENDIDA;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Solicitud atendida",
                responsable.getNombre(),
                observacion != null ? observacion : ""
        ));
    }

    /**
     * RF-08 / RN-01 / RN-02 / RN-08
     * Cierra definitivamente la solicitud.
     * Solo COORDINADOR puede cerrar.
     * Estado debe ser ATENDIDA. La validación de los 20 chars la hace ObservacionCierre.
     */
    public void cerrar(ObservacionCierre observacionCierre, Usuario coordinador){
        validarNoEstaCerrada();

        Objects.requireNonNull(coordinador,       "El coordinador no puede ser nulo");
        Objects.requireNonNull(observacionCierre, "La observación de cierre no puede ser nula");

        if (!coordinador.tieneTipo(TipoUsuario.COORDINADOR))
            throw new PermisoInsuficienteException("Solo un coordinador puede cerrar una solicitud");

        if (this.estado != EstadoSolicitud.ATENDIDA)
            throw new TransicionInvalidaException(
                    "La solicitud no se puede cerrar si no ha sido atendida. Estado actual: " + this.estado);

        this.estado = EstadoSolicitud.CERRADA;

        agregarHistorialInterno(
                "Solicitud cerrada",
                coordinador.getNombre(),
                observacionCierre.texto()
        );
    }

    /**
     * RF-06
     * Registra una EntradaHistorial desde fuera del agregado.
     * Solo disponible mientras no esté cerrada.
     */
    public void agregarHistorial(EntradaHistorial entradaHistorial){
        validarNoEstaCerrada();
        if(entradaHistorial == null) throw new IllegalArgumentException("La entrada no puede ser nula");
        this.historial.add(entradaHistorial);
    }

    /**
     * Método interno para que el método cerrar pueda registrarse
     * en el hisotrial sin lanzar una excepción
     */
    private void agregarHistorialInterno(String accion, String usuarioResponsable, String observaciones){
        this.historial.add(new EntradaHistorial(
                LocalDateTime.now(),
                accion,
                usuarioResponsable,
                observaciones
                )
        );
    }

    /** RN-01 */
    public void validarNoEstaCerrada(){
        if (this.estado == EstadoSolicitud.CERRADA){
            throw new SolicitudCerradaException();
        }
    }

    // ─── Getters (sin setters — estado solo cambia por métodos de negocio) ────

    public UUID getId()                          { return id; }
    public DescripcionSolicitud getDescripcion() { return descripcion; }
    public TipoSolicitud getTipo()               { return tipo; }
    public Prioridad getPrioridad()              { return prioridad; }
    public EstadoSolicitud getEstado()           { return estado; }
    public CanalOrigen getCanal()                { return canal; }
    public UUID getSolicitanteId()               { return solicitanteId; }
    public String getSolicitanteNombre()         { return solicitanteNombre; }
    public Responsable getResponsable()          { return responsable; }
    public LocalDateTime getFechaRegistro()      { return fechaRegistro; }

    /** Nadie modifica el historial directamente — solo lectura desde fuera. */
    public List<EntradaHistorial> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

}