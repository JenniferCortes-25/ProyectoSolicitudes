package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.SolicitudCerradaException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.TransicionInvalidaException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        agregarHistorial("Solicitud clasificacion como: " + nuevoTipo, usuarioResponsable, "");
    }

    public void clasificar(TipoSolicitud nuevoTipo){}

    public void asignarPrioridad(Prioridad prioridad){}

    public void asignarResponsable(Responsable responsable){}

    public void iniciarAtencion(){}

    public void atender(String observacion){}

    public void cerrar(ObservacionCierre observacionCierre){}

    public void agregarHistorial(String s, String usuarioResponsable, String s1) {
    }

    public void validarNoEstaCerrada(){
        if (this.estado == EstadoSolicitud.CERRADA){
            throw new SolicitudCerradaException();
        }
    }

}