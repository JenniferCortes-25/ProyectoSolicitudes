package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.request.authRequest.LoginRequest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginIntegrationTestUtil {

    /**
     * Hace un login real contra /api/auth/login y extrae el token JWT de la respuesta.
     * Úsalo en pruebas @SpringBootTest donde no puedes simular el token.
     */
    public static String obtenerToken(String email, String password,
                                       MockMvc mvc, ObjectMapper mapper) throws Exception {
        var req = new LoginRequest(email, password);

        var result = mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        return JsonPath.parse(json).read("$.token").toString();
    }
}