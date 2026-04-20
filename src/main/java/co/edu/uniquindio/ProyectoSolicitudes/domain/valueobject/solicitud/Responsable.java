package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import java.util.Objects;
import java.util.UUID;

/**
 * Referencia al usuario responsable asignado a una solicitud.
 * Guarda el ID y nombre del usuario en el momento de la asignación,
 * evitando dependencias directas entre los agregados Solicitud y Usuario.
 *
 * Regla de negocio: RN-04 — solo se asigna si el usuario está ACTIVO.
 */
public record Responsable(UUID usuarioId, String nombre) {
    public Responsable {
        Objects.requireNonNull(usuarioId, "El ID del responsable es obligatorio");
        Objects.requireNonNull(nombre, "El nombre del responsable es obligatorio");
    }
}