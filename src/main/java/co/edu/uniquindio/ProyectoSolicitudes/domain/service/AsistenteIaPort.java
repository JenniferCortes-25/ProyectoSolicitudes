package co.edu.uniquindio.ProyectoSolicitudes.domain.service;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse.ResumenIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse.SugerenciaIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;

/**
 * Puerto de dominio que abstrae la interacción con el modelo de lenguaje.
 * Permite que el sistema opere sin IA (RF-11): las implementaciones deben
 * retornar respuestas con disponible=false cuando el LLM no esté accesible.
 */
public interface AsistenteIaPort {

    /**
     * Sugiere tipo de solicitud y prioridad a partir de la descripción libre
     * escrita por el usuario (RF-10).
     *
     * @param descripcion texto descriptivo ingresado por el usuario
     * @return sugerencia con tipo, prioridad y justificación
     */
    SugerenciaIaResponse sugerirClasificacion(String descripcion);

    /**
     * Genera un resumen textual del estado actual e historial de la solicitud (RF-09).
     *
     * @param solicitud entidad de dominio con historial completo
     * @return resumen comprensible para el responsable
     */
    ResumenIaResponse generarResumen(Solicitud solicitud);
}