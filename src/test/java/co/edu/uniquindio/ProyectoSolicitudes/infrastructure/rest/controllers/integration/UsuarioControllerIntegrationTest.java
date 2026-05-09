package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.controllers.integration;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.test.LoginIntegrationTestUtil;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.test.UsuarioSecurityTestDataLoader;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSecurityJpaRepository securityRepo;

    private final ObjectMapper objMapper = new ObjectMapper();
    private UsuarioSecurityEntity adminGuardado;

    @BeforeEach
    void setupBD() {
        securityRepo.deleteAll();
        adminGuardado = securityRepo.save(UsuarioSecurityTestDataLoader.adminBase());
    }

    @Test
    void testListarUsuariosConTokenRealRetorna200() throws Exception {
        // 1. Login real → obtenemos token vivo de H2
        String token = LoginIntegrationTestUtil.obtenerToken(
                adminGuardado.getEmail(),
                "adminpass",   // contraseña antes de encriptar
                mockMvc, objMapper
        );

        // 2. Atacamos el endpoint protegido con el token real
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListarUsuariosSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }
}