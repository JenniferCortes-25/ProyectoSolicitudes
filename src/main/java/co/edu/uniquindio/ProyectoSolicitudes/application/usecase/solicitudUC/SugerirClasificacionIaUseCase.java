package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.solicitudResponse.SugerenciaIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsistenteIaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Sugerir clasificación automática mediante IA (RF-10).
 *
 * La sugerencia es un ASISTENTE: el coordinador debe confirmar o ajustar
 * los valores antes de clasificar formalmente la solicitud.
 */
@Service
@RequiredArgsConstructor
public class SugerirClasificacionIaUseCase {

    private final AsistenteIaPort asistenteIa;

    /**
     * @param descripcion texto libre ingresado por el usuario al crear la solicitud
     * @return sugerencia de tipo y prioridad (puede tener disponible=false si IA no responde)
     */
    public SugerenciaIaResponse ejecutar(String descripcion) {
        return asistenteIa.sugerirClasificacion(descripcion);
    }
}