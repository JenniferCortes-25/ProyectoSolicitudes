package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario;

/**
 * Indica si un usuario está habilitado para operar en el sistema.
 * Determina si puede recibir asignaciones y registrar solicitudes.
 *
 * Reglas de negocio: RN-04, RN-10
 */
public enum EstadoUsuario {
    /** Usuario habilitado, puede recibir asignaciones y operar normalmente. */
    ACTIVO,
    /** Usuario suspendido, no puede recibir nuevas asignaciones. */
    INACTIVO
}