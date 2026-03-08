package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

/**
 * Categoría de una solicitud académica.
 * Se asigna durante la clasificación (RF-02).
 */
public enum TipoSolicitud {
    /** Reconocimiento de materias cursadas en otra institución. */
    HOMOLOGACION,
    /** Inscripción de una asignatura en el periodo académico. */
    REGISTRO_ASIGNATURA,
    /** Baja de una asignatura ya inscrita. */
    CANCELACION_ASIGNATURA,
    /** Petición de cupo adicional en grupo lleno. */
    SOLICITUD_CUPO,
    /** Pregunta o duda sobre procesos académicos. */
    CONSULTA_ACADEMICA,
    /** Tipo no categorizado explícitamente. */
    OTRO
}