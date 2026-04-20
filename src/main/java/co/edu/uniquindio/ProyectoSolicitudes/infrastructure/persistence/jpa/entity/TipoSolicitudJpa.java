package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity;

/** Réplica del enum de dominio para uso exclusivo de la capa JPA. */
public enum TipoSolicitudJpa {
    HOMOLOGACION, REGISTRO_ASIGNATURA, CANCELACION_ASIGNATURA,
    SOLICITUD_CUPO, CONSULTA_ACADEMICA, OTRO
}