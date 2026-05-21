package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.ia;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.solicitudResponse.ResumenIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.solicitudResponse.SugerenciaIaResponse;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsistenteIaPort;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.NivelPrioridad;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.TipoSolicitud;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Adaptador que conecta el dominio con la API de Hugging Face (RF-09, RF-10).
 * Usa el modelo Mistral-7B-Instruct via Inference API (gratuito).
 * Si la API no está disponible retorna disponible=false, garantizando RF-11.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnthropicAsistenteIaAdapter implements AsistenteIaPort {

    private final AnthropicIaProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.3/v1/chat/completions";
    private static final String MODEL   = "mistralai/Mistral-7B-Instruct-v0.3";

    // ────────────────────────────────────────────────────────────────────────
    // RF-10: Sugerencia de tipo y prioridad
    // ────────────────────────────────────────────────────────────────────────

    @Override
    public SugerenciaIaResponse sugerirClasificacion(String descripcion) {
        if (!isApiKeyConfigured()) {
            log.warn("API key no configurada. Retornando sugerencia no disponible.");
            return new SugerenciaIaResponse(TipoSolicitud.OTRO, NivelPrioridad.MEDIA,
                    "Servicio de IA no disponible.", false);
        }

        String tipos      = Arrays.stream(TipoSolicitud.values())
                .map(Enum::name).collect(Collectors.joining(", "));
        String prioridades = Arrays.stream(NivelPrioridad.values())
                .map(Enum::name).collect(Collectors.joining(", "));

        String prompt = """
                Eres un asistente académico. Analiza la descripción y responde ÚNICAMENTE con JSON válido, sin texto adicional, sin markdown.

                Tipos disponibles: %s
                Prioridades disponibles: %s

                Descripción: "%s"

                Responde exactamente con este formato:
                {"tipo":"TIPO_SOLICITUD","prioridad":"NIVEL_PRIORIDAD","justificacion":"Explicación breve en español"}
                """.formatted(tipos, prioridades, descripcion);

        try {
            String responseText = llamarApi(prompt);
            // Extraer solo el JSON si el modelo devuelve texto extra
            int inicio = responseText.indexOf('{');
            int fin    = responseText.lastIndexOf('}');
            if (inicio == -1 || fin == -1) throw new RuntimeException("Respuesta sin JSON: " + responseText);
            String jsonLimpio = responseText.substring(inicio, fin + 1);

            JsonNode json = objectMapper.readTree(jsonLimpio);

            TipoSolicitud tipo = TipoSolicitud.valueOf(
                    json.get("tipo").asText().trim().toUpperCase());
            NivelPrioridad prioridad = NivelPrioridad.valueOf(
                    json.get("prioridad").asText().trim().toUpperCase());
            String justificacion = json.get("justificacion").asText();

            return new SugerenciaIaResponse(tipo, prioridad, justificacion, true);

        } catch (Exception e) {
            log.error("Error al obtener sugerencia de clasificación IA: {}", e.getMessage(), e);
            return new SugerenciaIaResponse(TipoSolicitud.OTRO, NivelPrioridad.MEDIA,
                    "No fue posible obtener sugerencia automática.", false);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // RF-09: Resumen del historial
    // ────────────────────────────────────────────────────────────────────────

    @Override
    public ResumenIaResponse generarResumen(Solicitud solicitud) {
        if (!isApiKeyConfigured()) {
            log.warn("API key no configurada. Retornando resumen no disponible.");
            return new ResumenIaResponse("Servicio de IA no disponible.", false);
        }

        String historialTexto = solicitud.getHistorial().stream()
                .map(e -> "- [%s] %s por %s. %s".formatted(
                        e.fechaHora(), e.accion(), e.usuarioResponsable(),
                        e.observaciones() != null ? e.observaciones() : ""))
                .collect(Collectors.joining("\n"));

        String prompt = """
                Eres un asistente administrativo académico. Genera un resumen ejecutivo
                claro y conciso (máximo 150 palabras) de la siguiente solicitud académica.

                ID: %s
                Descripción: %s
                Tipo: %s
                Estado: %s
                Prioridad: %s
                Responsable: %s
                Fecha de registro: %s

                Historial:
                %s

                Responde SOLO con el texto del resumen, sin títulos ni formato.
                """.formatted(
                solicitud.getId(),
                solicitud.getDescripcion().texto(),
                solicitud.getTipo() != null ? solicitud.getTipo().name() : "Sin clasificar",
                solicitud.getEstado().name(),
                solicitud.getPrioridad() != null ? solicitud.getPrioridad().nivel().name() : "Sin prioridad",
                solicitud.getResponsable() != null ? solicitud.getResponsable().nombre() : "Sin asignar",
                solicitud.getFechaRegistro(),
                historialTexto.isBlank() ? "Sin acciones registradas." : historialTexto
        );

        try {
            String resumen = llamarApi(prompt);
            return new ResumenIaResponse(resumen.trim(), true);
        } catch (Exception e) {
            log.error("Error al generar resumen IA: {}", e.getMessage());
            return new ResumenIaResponse("No fue posible generar el resumen automático.", false);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Llamada HTTP a Hugging Face (formato OpenAI compatible)
    // ────────────────────────────────────────────────────────────────────────

    private String llamarApi(String userPrompt) throws Exception {
        log.info("Iniciando llamada a HuggingFace...");
        String requestBody = """
                {
                  "model": "%s",
                  "max_tokens": 512,
                  "messages": [
                    { "role": "user", "content": %s }
                  ]
                }
                """.formatted(MODEL, objectMapper.writeValueAsString(userPrompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Status: {}", response.statusCode());
        log.info("Body: {}", response.body());
        if (response.statusCode() != 200) {
            throw new RuntimeException("HuggingFace API devolvió HTTP " + response.statusCode()
                    + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        log.info("Respuesta HuggingFace: {}", response.body());
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private boolean isApiKeyConfigured() {
        String key = properties.getApiKey();
        return key != null && !key.isBlank() && !key.equals("TU_API_KEY_AQUI");
    }
}