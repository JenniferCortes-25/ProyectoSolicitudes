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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración del UsuarioController con servidor real (H2 + JWT real).
 * La seguridad ESTÁ COMPLETAMENTE ACTIVA — los tokens se obtienen con un login real.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioSecurityJpaRepository securityRepo;

    private final ObjectMapper objMapper = new ObjectMapper();

    private static final String JSON = "application/json";

    // Credenciales guardadas en BD para los tests
    private UsuarioSecurityEntity adminGuardado;
    private UsuarioSecurityEntity userGuardado;

    @BeforeEach
    void setupBD() {
        securityRepo.deleteAll();
        // ADMIN: puede crear usuarios (POST /api/usuarios requiere ADMIN)
        adminGuardado = securityRepo.save(UsuarioSecurityTestDataLoader.adminBase());
        // USER : puede cambiar contraseña pero no crear usuarios
        userGuardado  = securityRepo.save(UsuarioSecurityTestDataLoader.userBase());
    }

    // ══════════════════════════════════════════════════════════════════════
    // Tests originales (sin cambios)
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void testListarUsuariosConTokenRealRetorna200() throws Exception {
        String token = LoginIntegrationTestUtil.obtenerToken(
                adminGuardado.getEmail(),
                "adminpass",
                mockMvc, objMapper
        );

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testListarUsuariosSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    // ══════════════════════════════════════════════════════════════════════
    // POST /api/usuarios con token real
    // ══════════════════════════════════════════════════════════════════════

    /**
     * POST /api/usuarios con token de ADMIN → 201 Created.
     * El body es válido y la identificación no existe aún en BD.
     */
    @Test
    void testCrearUsuarioConRolAdminRetorna201() throws Exception {
        // [ARRANGE] — login real → token de ADMIN
        String tokenAdmin = LoginIntegrationTestUtil.obtenerToken(
                adminGuardado.getEmail(),
                "adminpass",
                mockMvc, objMapper
        );

        // [ACT & ASSERT]
        mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(JSON)
                        .content("""
                                {
                                    "identificacion": "D-999",
                                    "nombre": "Docente Prueba",
                                    "email": "docente.prueba@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isCreated()); // 201
    }

    /**
     * POST /api/usuarios con token de USER → 403 Forbidden.
     * Solo ADMIN puede crear usuarios (@PreAuthorize("hasAuthority('ADMIN')")).
     */
    @Test
    void testCrearUsuarioConRolUserRetorna403() throws Exception {
        // [ARRANGE] — login real → token de USER
        String tokenUser = LoginIntegrationTestUtil.obtenerToken(
                userGuardado.getEmail(),
                "userpass",
                mockMvc, objMapper
        );

        // [ACT & ASSERT]
        mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(JSON)
                        .content("""
                                {
                                    "identificacion": "D-999",
                                    "nombre": "Docente Prueba",
                                    "email": "docente.prueba@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isForbidden()); // 403
    }

    // ══════════════════════════════════════════════════════════════════════
    // PUT /api/usuarios/{id}/cambiar-password con token real
    // ══════════════════════════════════════════════════════════════════════

    /**
     * PUT /api/usuarios/{id}/cambiar-password con contraseña correcta → 204 No Content.
     *
     * Estrategia:
     *  1. Hacemos login con el USER para obtener token y su ID (implícito en BD).
     *  2. Buscamos el ID real del usuario en la BD de seguridad.
     *  3. Llamamos al endpoint con la contraseña correcta.
     *
     * Para que el use case encuentre el usuario de dominio por UUID, primero
     * lo creamos con POST usando el token ADMIN, luego cambiamos su contraseña.
     */
    @Test
    void testCambiarPasswordConCredencialesCorrectasRetorna204() throws Exception {
        // [ARRANGE] — crear usuario de dominio con ADMIN para que exista en USUARIOS
        String tokenAdmin = LoginIntegrationTestUtil.obtenerToken(
                adminGuardado.getEmail(),
                "adminpass",
                mockMvc, objMapper
        );

        // 1. Crear usuario de dominio (devuelve 201 con Location header que contiene el UUID)
        var crearResult = mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(JSON)
                        .content("""
                                {
                                    "identificacion": "U-PASS",
                                    "nombre": "Usuario Cambio",
                                    "email": "cambio@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        // Extraer el UUID del header Location: /api/usuarios/{uuid}
        String location = crearResult.getResponse().getHeader("Location");
        String usuarioId = location.substring(location.lastIndexOf("/") + 1);

        // 2. Login como el mismo usuario que acabamos de crear
        //    (contraseña por defecto del CrearUsuarioUseCase es "Password123")
        String tokenNuevoUsuario = LoginIntegrationTestUtil.obtenerToken(
                "cambio@uniquindio.edu.co",
                "Password123",
                mockMvc, objMapper
        );

        // [ACT & ASSERT] — cambiar contraseña con clave actual correcta → 204
        mockMvc.perform(put("/api/usuarios/" + usuarioId + "/cambiar-password")
                        .header("Authorization", "Bearer " + tokenNuevoUsuario)
                        .contentType(JSON)
                        .content("""
                                {
                                    "passwordActual": "Password123",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """))
                .andExpect(status().isNoContent()); // 204
    }

    /**
     * PUT /api/usuarios/{id}/cambiar-password con contraseña actual incorrecta → 401.
     *
     * GlobalExceptionHandler mapea BadCredentialsException → 401 Unauthorized.
     */
    @Test
    void testCambiarPasswordConClaveIncorrectaRetorna401() throws Exception {
        // [ARRANGE] — crear usuario de dominio para obtener su UUID
        String tokenAdmin = LoginIntegrationTestUtil.obtenerToken(
                adminGuardado.getEmail(),
                "adminpass",
                mockMvc, objMapper
        );

        var crearResult = mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(JSON)
                        .content("""
                                {
                                    "identificacion": "U-WRONG",
                                    "nombre": "Usuario Errado",
                                    "email": "errado@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = crearResult.getResponse().getHeader("Location");
        String usuarioId = location.substring(location.lastIndexOf("/") + 1);

        // Login con el usuario recién creado (contraseña por defecto: Password123)
        String tokenUsuario = LoginIntegrationTestUtil.obtenerToken(
                "errado@uniquindio.edu.co",
                "Password123",
                mockMvc, objMapper
        );

        // [ACT & ASSERT] — contraseña actual es "ClaveErrada" → 401
        mockMvc.perform(put("/api/usuarios/" + usuarioId + "/cambiar-password")
                        .header("Authorization", "Bearer " + tokenUsuario)
                        .contentType(JSON)
                        .content("""
                                {
                                    "passwordActual": "ClaveErrada",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """))
                .andExpect(status().isUnauthorized()); // 401
    }
}