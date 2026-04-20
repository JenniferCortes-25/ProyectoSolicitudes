package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

/**
 * Define el canal por el cual fue recibida una solicitud académica.
 * Es obligatorio al momento de registrar una solicitud (RF-01).
 */

public enum CanalOrigen {
    PRESENCIAL, CORREO_ELECTRONICO, SAC, TELEFONICO, CSU,
}