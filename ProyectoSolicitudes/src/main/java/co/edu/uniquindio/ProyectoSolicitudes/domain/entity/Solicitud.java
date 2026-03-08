package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Solicitud {
    private UUID id;
    private DescripcionSolicitud descripcion;
    private TipoSolicitud tipo;
    private Prioridad prioridad;
    private EstadoSolicitud estado;          // inicia siempre en REGISTRADA
    private CanalOrigen canal;
    private UUID solicitanteId;
    private Responsable responsable; // null hasta asignar
    private List<EntradaHistorial> historial = new ArrayList<>();
    private LocalDateTime fechaRegistro;

    // Constructor
    public Solicitud(DescripcionSolicitud descricpion, CanalOrigen canal, UUID solicitanteId){
        if (canal == null) throw new IllegalArgumentException("El canal de origen es obligatorio");
        if (solicitanteId == null) throw new IllegalArgumentException("El solicitante es obligatorio");

        this.id = UUID.randomUUID();
        this.descripcion = descricpion;
        this.canal         = canal;
        this.solicitanteId = solicitanteId;
        this.estado        = EstadoSolicitud.REGISTRADA;  // siempre inicia aquí
        this.tipo          = null;
        this.prioridad     = null;
        this.responsable   = null;
        this.historial     = new ArrayList<>();
        this.fechaRegistro = LocalDateTime.now();

        // Primera entrada del historial
        this.historial.add(new EntradaHistorial(
                this.fechaRegistro,
                "Solicitud registrada por canal: " + canal,
                solicitanteId.toString(),
                ""
        ));
    }

    // ─── Métodos del dominio ──────────────────────────────────────────────────

    /**
     * RF-02 / RN-01
     * Asigna el tipo a la solicitud.
     * Solo se puede clasificar si el estado actual es REGISTRADA.
     */
    public void clasificar(TipoSolicitud nuevoTipo, String usuarioResponsable){
        validarNoEstaCerrada();

        if(this.estado != EstadoSolicitud.REGISTRADA){
            throw new TransicionInvalidaException(
                    "Solo se puede clasificar en estado REGISTRADA. Estado actual: " + this.estado
            );
        }
        if (nuevoTipo == null) throw new IllegalArgumentException("El tipo no puede ser nulo");
        this.tipo = nuevoTipo;
        this.estado = EstadoSolicitud.CLASIFICADA;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Solicitud clasificacion como: " + nuevoTipo,
                usuarioResponsable, ""
        ));
    }
    /**
     * RF-03 / RN-02
     * Solo se puede asignar prioridad si estado == CLASIFICADA.
     */
    public void asignarPrioridad(Prioridad nuevaprioridad, String usuarioResponsable){
        validarNoEstaCerrada();

        if(this.estado != EstadoSolicitud.CLASIFICADA){
            throw new TransicionInvalidaException(
                    "Solo se puede asignar prioridad en estado Clasificada. Estado actual " + this.estado
            );
        }
        if (nuevaprioridad == null) throw new IllegalArgumentException("La prioridad no puede ser nula");
        this.prioridad = nuevaprioridad;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Prioridad aasignada: " + nuevaprioridad.nivel() + "-" + nuevaprioridad.justificacion(),
                usuarioResponsable,
                ""
        ));
    }

    /**
     * RF-05 / RN-03 / RN-04 / RN-10
     * El responsable debe estar activo y la solicitud no puede estar cerrada.
     * Se recibe el boolean estaActivo para no depender de la entidad Usuario directamente.
     */
    public void asignarResponsable(Responsable nuevoResponsable, boolean responsableActivo, String usuarioResponsable){
        validarNoEstaCerrada();

        if(!responsableActivo){
            throw new UsuarioInactivoException("El usuario debe estar activo");
        }

        if (nuevoResponsable == null) throw new IllegalArgumentException("El responsable no puede ser nulo ");

        this.responsable = nuevoResponsable;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Responsable asignado: " + nuevoResponsable.nombre(),
                usuarioResponsable,
                ""
        ));
    }

    /**
     * RN-02 / RN-05
     * Cambia estado a EN_ATENCION.
     * Requiere responsable asignado y estado == CLASIFICADA.
     */
    public void iniciarAtencion(String usuarioResponsable){
        validarNoEstaCerrada();

        if(this.responsable == null){
            throw new SinResponsableException("Debe tener un responsable asignado");
        }
        if(this.estado != EstadoSolicitud.CLASIFICADA){
            throw new TransicionInvalidaException("Solo se puede inciar atención si la solicitud ya esta clasificada");
        }

        this.estado = EstadoSolicitud.EN_ATENCION;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Atención iniciada por: " + this.responsable.nombre(),
                usuarioResponsable,
                ""
        ));
    }

    /**
     * RN-02
     * Cambia estado a ATENDIDA.
     * Estado debe ser EN_ATENCION.
     */
    public void atender(String observacion, String usuarioResponsable){
        validarNoEstaCerrada();

        if(this.estado != EstadoSolicitud.EN_ATENCION){
            throw new TransicionInvalidaException("Solo se puede atender una solicitud en estado EN_ATENCION. Estado actual: " + this.estado);
        }

        this.estado = EstadoSolicitud.ATENDIDA;

        agregarHistorial(new EntradaHistorial(
                LocalDateTime.now(),
                "Solicitud atendida",
                usuarioResponsable,
                observacion != null ? observacion : ""
        ));
    }

    /**
     * RF-08 / RN-01 / RN-02 / RN-08
     * Cierra definitivamente la solicitud.
     * Estado debe ser ATENDIDA. La validación de los 20 chars la hace ObservacionCierre.
     */
    public void cerrar(ObservacionCierre observacionCierre, String usuarioResponsable){
        validarNoEstaCerrada();

        if(this.estado != EstadoSolicitud.ATENDIDA){
            throw new TransicionInvalidaException("La solicitud no se puede cerrar si no ha sido atendida. Estado actual: " + this.estado );
        }
        this.estado = EstadoSolicitud.CERRADA;

        agregarHistorialInterno(
                "Solicitud cerrada",
                usuarioResponsable,
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
    public Responsable getResponsable()          { return responsable; }
    public LocalDateTime getFechaRegistro()      { return fechaRegistro; }

    /** Nadie modifica el historial directamente — solo lectura desde fuera. */
    public List<EntradaHistorial> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

}