package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.ia;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración del adaptador IA.
 * Leer desde application.properties con prefijo "anthropic".
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "anthropic")
public class AnthropicIaProperties {

    /**
     * Clave de API de Anthropic.
     * Configurar vía variable de entorno ANTHROPIC_API_KEY
     * o en application.properties (nunca en código fuente).
     */
    private String apiKey = "TU_API_KEY_AQUI";
}