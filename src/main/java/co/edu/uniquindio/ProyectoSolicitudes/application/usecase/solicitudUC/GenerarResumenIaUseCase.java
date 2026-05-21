package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse.ResumenIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsistenteIaPort;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.SolicitudNoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso: Generar resumen textual de la solicitud mediante IA (RF-09).
 */
@Service
@RequiredArgsConstructor
public class GenerarResumenIaUseCase {

    private final SolicitudRepository solicitudRepository;
    private final AsistenteIaPort     asistenteIa;

    /**
     * @param solicitudId UUID de la solicitud a resumir
     * @return resumen generado por el LLM (o mensaje de no disponibilidad)
     */
    public ResumenIaResponse ejecutar(UUID solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNoEncontradaException(
                        "No existe solicitud con ID: " + solicitudId));
        return asistenteIa.generarResumen(solicitud);
    }
}