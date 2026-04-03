package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.SolicitudController;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.SolicitudMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests del SolicitudController usando @WebMvcTest.
 * No levanta toda la aplicación — solo el controller y sus dependencias.
 * Mockito simula el mapper y los servicios para aislar el controller.
 */
@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudMapper mapper;

    // ── VALUE OBJECTS ──

    /**
     * Descripción menor a 10 chars debe retornar 400.
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Descripción nula debe retornar 400.
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Canal nulo debe retornar 400.
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * GET /api/solicitudes debe retornar 200.
     */
    @Test
    void deberiaRetornar200AlListar() throws Exception {
        mockMvc.perform(get("/api/solicitudes"))
                .andExpect(status().isOk());
    }

    /**
     * GET /api/solicitudes/{id}/historial debe retornar 200.
     */
    @Test
    void deberiaRetornar200AlObtenerHistorial() throws Exception {
        mockMvc.perform(get("/api/solicitudes/abc-123/historial"))
                .andExpect(status().isOk());
    }
}