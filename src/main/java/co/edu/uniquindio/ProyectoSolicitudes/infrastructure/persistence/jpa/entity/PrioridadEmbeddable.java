package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable JPA para el Value Object Prioridad.
 * Sus campos se "aplanan" como columnas en la tabla solicitudes.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrioridadEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_nivel", length = 20)
    private NivelPrioridadJpa nivel;

    @Column(name = "prioridad_justificacion", length = 500)
    private String justificacion;
}