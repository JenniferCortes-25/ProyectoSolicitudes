package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

/**
 * Nivel de urgencia de una solicitud académica.
 * Forma parte del Value Object Prioridad junto con la justificación.
 *
 * Regla de negocio: RN-07
 */
public enum NivelPrioridad {
    /** Impacto directo en grado o situación académica urgente. */
    CRITICA,
    /** Tiene fecha límite próxima o afecta matrícula. */
    ALTA,
    /** Solicitud estándar sin urgencia especial. */
    MEDIA,
    /** Consulta informativa o sin impacto inmediato. */
    BAJA
}
