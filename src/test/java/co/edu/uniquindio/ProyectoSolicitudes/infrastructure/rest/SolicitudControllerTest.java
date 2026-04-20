package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.*;
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
 * Guía 08 — solo carga la capa REST, usa Mockito para simular dependencias.
 * Verifica que Bean Validation y el GlobalExceptionHandler funcionen correctamente.
 */
@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JSON = "application/json";

    @MockitoBean private SolicitudMapper mapper;
    @MockitoBean private RegistrarSolicitudUseCase registrarSolicitudUseCase;
    @MockitoBean private ClasificarSolicitudUseCase clasificarSolicitudUseCase;
    @MockitoBean private AsignarResponsableUseCase asignarResponsableUseCase;
    @MockitoBean private CerrarSolicitudUseCase cerrarSolicitudUseCase;
    @MockitoBean private ConsultarSolicitudesUseCase consultarSolicitudesUseCase;


    /**
     * Caso: Descripción muy corta.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoDescripcionMuyCorta() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "descripcion": "corta",
                                    "canalOrigen": "CORREO_ELECTRONICO",
                                    "solicitanteId": "E-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Descripción nula.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoDescripcionNula() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "canalOrigen": "CORREO_ELECTRONICO",
                                    "solicitanteId": "E-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Canal de origen nulo.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoCanalNulo() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "descripcion": "Necesito homologar materia cursada en otra universidad",
                                    "solicitanteId": "E-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: SolicitnanteId blanco.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoSolicitanteIdBlanco() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "descripcion": "Necesito homologar materia cursada en otra universidad",
                                    "canalOrigen": "CORREO_ELECTRONICO",
                                    "solicitanteId": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Clasificar sin tipo.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoClasificarSinTipo() throws Exception {
        mockMvc.perform(put("/api/solicitudes/some-id/clasificar")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "nivelPrioridad": "ALTA",
                                    "justificacionPrioridad": "Tiene fecha límite próxima",
                                    "coordinadorId": "C-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Justificación muy corta.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoJustificacionMuyCorta() throws Exception {
        mockMvc.perform(put("/api/solicitudes/some-id/clasificar")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "tipo": "HOMOLOGACION",
                                    "nivelPrioridad": "ALTA",
                                    "justificacionPrioridad": "ok",
                                    "coordinadorId": "C-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Observación de cierre muy corta.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoObservacionCierreMuyCorta() throws Exception {
        mockMvc.perform(put("/api/solicitudes/some-id/cerrar")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "observacion": "muy corta",
                                    "coordinadorId": "C-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * Caso: Cerrar sin coordinadorId.
     * Resultado esperado: 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoCerrarSinCoordinadorId() throws Exception {
        mockMvc.perform(put("/api/solicitudes/some-id/cerrar")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "observacion": "Homologación aprobada por consejo de programa"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }


    // =========================================================================
    // GET — respuestas esperadas con mocks vacíos
    // =========================================================================

    /**
     * Caso: Listar solicitudes.
     * Resultado esperado: 200 OK.
     */
    @Test
    void deberiaRetornar200AlListar() throws Exception {
        mockMvc.perform(get("/api/solicitudes").accept(JSON))
                .andExpect(status().isOk());
    }

    /**
     * Caso: Solicitud inexistente.
     * Resultado esperado: 404 Not Found.
     */
    @Test
    void deberiaRetornar404CuandoSolicitudNoExiste() throws Exception {
        mockMvc.perform(get("/api/solicitudes/abc-123/historial").accept(JSON))
                .andExpect(status().isNotFound());
    }
}