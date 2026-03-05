package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import java.util.Objects;
import java.util.UUID;

public record Responsable(UUID usuarioId, String nombre) {
    public Responsable {
        Objects.requireNonNull(usuarioId); Objects.requireNonNull(nombre);
    }
}