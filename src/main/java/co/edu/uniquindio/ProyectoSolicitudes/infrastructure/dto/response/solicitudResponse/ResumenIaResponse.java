package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.solicitudResponse;

/**
 * Respuesta del asistente IA con resumen textual de la solicitud (RF-09).
 */
public record ResumenIaResponse(

        String resumen,
        boolean disponible   // false si el servicio IA no está accesible

) {}