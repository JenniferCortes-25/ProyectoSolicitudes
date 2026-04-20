package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import java.time.LocalDateTime;

/**
 * Registro inmutable de una acción realizada sobre una solicitud.
 * Forma parte del historial auditable (RF-06).
 *
 * Cada entrada es creada automáticamente por los métodos de dominio
 * de Solicitud — nunca se construye manualmente desde fuera del agregado.
 */
public record EntradaHistorial(
        LocalDateTime fechaHora,       // momento exacto de la acción
        String accion,                 // descripción de lo que ocurrió
        String usuarioResponsable,     // ID o nombre del actor
        String observaciones           // notas adicionales, puede estar vacío
) {}