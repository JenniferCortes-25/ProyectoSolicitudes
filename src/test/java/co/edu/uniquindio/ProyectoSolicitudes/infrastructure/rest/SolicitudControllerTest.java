package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.SolicitudMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = SolicitudController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JSON = "application/json";

    @MockitoBean private SolicitudMapper mapper;
    @MockitoBean private RegistrarSolicitudUseCase registrarSolicitudUseCase;
    @MockitoBean private ClasificarSolicitudUseCase clasificarSolicitudUseCase;
    @MockitoBean private AsignarResponsableUseCase asignarResponsableUseCase;
    @MockitoBean private IniciarAtencionUseCase iniciarAtencionUseCase;
    @MockitoBean private AtenderSolicitudUseCase atenderSolicitudUseCase;
    @MockitoBean private CerrarSolicitudUseCase cerrarSolicitudUseCase;
    @MockitoBean private ConsultarSolicitudesUseCase consultarSolicitudesUseCase;

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

    @Test
    void deberiaRetornar200AlListar() throws Exception {
        mockMvc.perform(get("/api/solicitudes").accept(JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar404CuandoSolicitudNoExiste() throws Exception {
        // UUID válido pero inexistente — el mock lanza la excepción de dominio
        UUID idInexistente = UUID.fromString("00000000-0000-0000-0000-000000000000");

        when(consultarSolicitudesUseCase.obtenerPorId(idInexistente))
                .thenThrow(new SolicitudNoEncontradaException(
                        "No existe solicitud con ID: " + idInexistente));

        mockMvc.perform(get("/api/solicitudes/" + idInexistente + "/historial")
                        .accept(JSON))
                .andExpect(status().isNotFound());
    }
}