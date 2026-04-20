package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para Solicitud.
 * Vive en infrastructure — no en domain.
 * UUID almacenado como VARCHAR(36).
 */
@Entity
@Table(name = "solicitudes", indexes = {
        @Index(name = "idx_solicitud_estado",       columnList = "estado"),
        @Index(name = "idx_solicitud_solicitante",  columnList = "solicitante_id")
})
@Getter
@Setter
@NoArgsConstructor
public class SolicitudEntity {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private String id;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitud", length = 30)
    private TipoSolicitudJpa tipoSolicitud;

    // Value Object Prioridad aplanado como columnas embebidas
    @Embedded
    private PrioridadEmbeddable prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitudJpa estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_origen", nullable = false, length = 25)
    private CanalOrigenJpa canalOrigen;

    @Column(name = "solicitante_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String solicitanteId;

    @Column(name = "solicitante_nombre", nullable = false, length = 150)
    private String solicitanteNombre;

    // Responsable: referencia ligera (solo id y nombre)
    @Column(name = "responsable_id", columnDefinition = "VARCHAR(36)")
    private String responsableId;

    @Column(name = "responsable_nombre", length = 150)
    private String responsableNombre;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Colección de Value Objects EntradaHistorial.
     * @ElementCollection persiste en tabla separada sin necesitar @Entity propio.
     * CascadeType se maneja automáticamente — los registros de historial
     * viven y mueren con su solicitud (orphanRemoval implícito).
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "historial_solicitud",
            joinColumns = @JoinColumn(name = "solicitud_id")
    )
    @OrderColumn(name = "orden_historial")
    private List<EntradaHistorialEmbeddable> historial = new ArrayList<>();
}