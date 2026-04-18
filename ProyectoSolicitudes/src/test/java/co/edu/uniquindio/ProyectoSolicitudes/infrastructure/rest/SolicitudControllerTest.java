package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.SolicitudMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests del SolicitudController usando @WebMvcTest.
 *
 * ✔ Solo carga la capa REST (controller)
 * ✔ Usa Mockito para simular dependencias
 * ✔ Valida respuestas HTTP
 *
 * Se usa String en contentType/accept para evitar warnings de null-safety
 * en Spring Boot 3.5+
 */
@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock del mapper para aislar el controller.
     */
    @MockitoBean
    private SolicitudMapper mapper;

    private static final String JSON = "application/json";

    // ─────────────────────────────────────────────
    // ───────────── VALUE OBJECTS ────────────────
    // ─────────────────────────────────────────────

    /**
     * Caso: Descripción muy corta.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoDescripcionMuyCorta() throws Exception {
        String json = """
                {
                    "descripcion": "corta",
                    "canalOrigen": "CORREO_ELECTRONICO",
                    "solicitanteId": "E-001"
                }
                """;

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON)
                        .accept(JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Descripción nula.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoDescripcionNula() throws Exception {
        String json = """
                {
                    "canalOrigen": "CORREO_ELECTRONICO",
                    "solicitanteId": "E-001"
                }
                """;

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON)
                        .accept(JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Canal de origen nulo.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoCanalNulo() throws Exception {
        String json = """
                {
                    "descripcion": "Necesito homologar materia cursada en otra universidad",
                    "solicitanteId": "E-001"
                }
                """;

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON)
                        .accept(JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // ───────────── ENDPOINTS GET ────────────────
    // ─────────────────────────────────────────────

    /**
     * Caso: Listar solicitudes.
     * Resultado esperado: 200 OK.
     */
    @Test
    void deberiaRetornar200AlListar() throws Exception {
        mockMvc.perform(get("/api/solicitudes")
                        .accept(JSON))
                .andExpect(status().isOk());
    }

    /**
     * Caso: Solicitud inexistente.
     * Resultado esperado: 404 Not Found.
     */
    @Test
    void deberiaRetornar404CuandoSolicitudNoExiste() throws Exception {
        mockMvc.perform(get("/api/solicitudes/abc-123/historial")
                        .accept(JSON))
                .andExpect(status().isNotFound());
    }
}