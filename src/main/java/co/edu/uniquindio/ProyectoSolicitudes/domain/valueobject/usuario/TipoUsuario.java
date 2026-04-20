package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario;

/**
 * Define el rol de un usuario en el sistema.
 * Determina qué operaciones puede realizar (RF-13).
 */
public enum TipoUsuario {
    /** Puede registrar solicitudes y consultar las propias. */
    ESTUDIANTE,
    /** Puede atender solicitudes asignadas y registrar observaciones. */
    DOCENTE,
    /** Puede clasificar, priorizar, asignar, cerrar y gestionar usuarios. */
    COORDINADOR,
    /** Puede registrar solicitudes y consultar las de su área. */
    ADMINISTRATIVO
}