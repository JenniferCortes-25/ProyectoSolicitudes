package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.request.solicitudRequest.SugerenciaIaRequest;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.solicitudResponse.ResumenIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.solicitudResponse.SugerenciaIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC.GenerarResumenIaUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC.SugerirClasificacionIaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoints del asistente IA (RF-09, RF-10).
 *
 * Estos endpoints son OPCIONALES; el sistema funciona sin ellos (RF-11).
 * Requieren autenticación JWT igual que el resto de la API.
 */
@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@Tag(name = "Asistente IA", description = "Sugerencias y resúmenes generados por modelo de lenguaje (RF-09, RF-10)")
public class AsistenteIaController {

    private final SugerirClasificacionIaUseCase sugerirClasificacionIaUseCase;
    private final GenerarResumenIaUseCase        generarResumenIaUseCase;

    // ────────────────────────────────────────────────────────────────────────
    // POST /api/ia/sugerir-clasificacion  (RF-10)
    // ────────────────────────────────────────────────────────────────────────

    @PostMapping("/sugerir-clasificacion")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Operation(
            summary = "Sugerir tipo y prioridad mediante IA (RF-10)",
            description = """
            Recibe la descripción de la solicitud y devuelve una sugerencia de tipo y prioridad.
            La respuesta SIEMPRE debe ser confirmada o ajustada por un usuario humano antes de clasificar.
            Si el servicio IA no está disponible, retorna disponible=false y valores por defecto.
            """
    )
    @ApiResponse(responseCode = "200", description = "Sugerencia generada (verificar campo 'disponible')")
    @ApiResponse(responseCode = "400", description = "Descripción inválida")
    public ResponseEntity<SugerenciaIaResponse> sugerirClasificacion(
            @Valid @RequestBody SugerenciaIaRequest request) {

        SugerenciaIaResponse respuesta = sugerirClasificacionIaUseCase.ejecutar(request.descripcion());
        return ResponseEntity.ok(respuesta);
    }

    // ────────────────────────────────────────────────────────────────────────
    // GET /api/ia/resumen/{id}  (RF-09)
    // ────────────────────────────────────────────────────────────────────────

    @GetMapping("/resumen/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Operation(
            summary = "Generar resumen de una solicitud mediante IA (RF-09)",
            description = """
            Genera un resumen ejecutivo del estado e historial de la solicitud indicada.
            Si el servicio IA no está disponible, retorna disponible=false.
            """
    )
    @ApiResponse(responseCode = "200", description = "Resumen generado (verificar campo 'disponible')")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<ResumenIaResponse> generarResumen(
            @PathVariable String id) {

        ResumenIaResponse respuesta = generarResumenIaUseCase.ejecutar(UUID.fromString(id));
        return ResponseEntity.ok(respuesta);
    }
}