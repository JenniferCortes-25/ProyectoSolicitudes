package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Embeddable JPA para el Value Object EntradaHistorial.
 * Se almacena en la tabla historial_solicitud con FK a solicitud.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaHistorialEmbeddable {

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "accion", nullable = false, length = 500)
    private String accion;

    @Column(name = "usuario_responsable", nullable = false, length = 150)
    private String usuarioResponsable;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;
}