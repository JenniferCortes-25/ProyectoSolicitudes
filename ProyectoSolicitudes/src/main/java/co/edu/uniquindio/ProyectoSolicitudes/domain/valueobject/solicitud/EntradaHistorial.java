package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud;

import java.time.LocalDateTime;

public record EntradaHistorial(LocalDateTime fechaHora, String accion,
                               String usuarioResponsable, String observaciones) {
}