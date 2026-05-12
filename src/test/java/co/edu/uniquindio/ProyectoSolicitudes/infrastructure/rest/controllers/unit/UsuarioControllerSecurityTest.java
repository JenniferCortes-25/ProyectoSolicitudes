package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.controllers.unit;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.UsuarioController;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.UsuarioMapper;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.JwtConfig;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de seguridad del UsuarioController con JWT simulado.
 * La seguridad ESTÁ ACTIVA (no se excluye SecurityAutoConfiguration).
 * Usa .with(jwt()) de spring-security-test para simular tokens sin levantar
 * un servidor real ni golpear la BD.
 */
@WebMvcTest(controllers = UsuarioController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class UsuarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JSON = "application/json";

    @MockitoBean private UsuarioMapper mapper;
    @MockitoBean private CrearUsuarioUseCase crearUsuarioUseCase;
    @MockitoBean private ConsultarUsuariosUseCase consultarUsuariosUseCase;
    @MockitoBean private ActivarUsuarioUseCase activarUsuarioUseCase;
    @MockitoBean private DesactivarUsuarioUseCase desactivarUsuarioUseCase;
    @MockitoBean private CambiarPasswordUseCase cambiarPasswordUseCase;

    // ══════════════════════════════════════════════════════════════════════
    // GET /api/usuarios — tests originales (sin cambios)
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void testListarUsuariosConTokenValidoRetorna200() throws Exception {
        when(consultarUsuariosUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios")
                .with(jwt().jwt(b -> b.subject("admin@test.com"))
                        .authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void testListarUsuariosSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testListarUsuariosConRolUserRetorna200() throws Exception {
        when(consultarUsuariosUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios")
                .with(jwt().jwt(b -> b.subject("user@test.com"))
                        .authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void testCrearUsuarioConRolUserRetorna403() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON)
                        .content("{\"identificacion\":\"123\",\"nombre\":\"Test\",\"email\":\"t@t.com\",\"tipoUsuario\":\"DOCENTE\"}")
                        .with(jwt().jwt(b -> b.subject("user@test.com"))
                                .authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isForbidden()); // 403
    }

    // ══════════════════════════════════════════════════════════════════════
    // POST /api/usuarios — NUEVOS tests de seguridad con roles
    // ══════════════════════════════════════════════════════════════════════

    /**
     * POST /api/usuarios con rol ADMIN → 201 Created.
     * Verifica que el endpoint está protegido pero permite ADMIN.
     */
    @Test
    void testCrearUsuarioConRolAdminRetorna201() throws Exception {
        // [ARRANGE] — el use case devuelve un usuario de dominio válido
        Usuario usuarioCreado = new Usuario(
                "D-001", "Docente López",
                new Email("docente@uniquindio.edu.co"), TipoUsuario.DOCENTE);

        when(crearUsuarioUseCase.ejecutar(any(), any(), any(), any()))
                .thenReturn(usuarioCreado);
        // El mapper puede devolver null — el controller solo llama toDetalleResponse
        when(mapper.toDetalleResponse(any())).thenReturn(null);

        // [ACT & ASSERT]
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "Docente López",
                                    "email": "docente@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """)
                        .with(jwt().jwt(b -> b.subject("coord@uniquindio.edu.co"))
                                .authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isCreated()); // 201
    }

    /**
     * POST /api/usuarios sin token → 401 Unauthorized.
     */
    @Test
    void testCrearUsuarioSinTokenRetorna401() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "Docente López",
                                    "email": "docente@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isUnauthorized()); // 401
    }

    // ══════════════════════════════════════════════════════════════════════
    // PUT /api/usuarios/{id}/cambiar-password — NUEVOS tests
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Cambiar contraseña con credenciales correctas y token válido → 204 No Content.
     * Cualquier rol autenticado (ADMIN o USER) puede cambiar su propia contraseña.
     */
    @Test
    void testCambiarPasswordConCredencialesCorrectasRetorna204() throws Exception {
        UUID id = UUID.randomUUID();
        // cambiarPasswordUseCase.ejecutar es void; sin stubbing de excepción → éxito

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON)
                        .content("""
                                {
                                    "passwordActual": "Password123",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """)
                        .with(jwt().jwt(b -> b.subject("docente@uniquindio.edu.co"))
                                .authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isNoContent()); // 204
    }

    /**
     * Cambiar contraseña con contraseña actual incorrecta → BadCredentialsException
     * → GlobalExceptionHandler la convierte en 401 Unauthorized.
     */
    @Test
    void testCambiarPasswordConClaveIncorrectaRetorna401() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new BadCredentialsException("La contraseña actual es incorrecta"))
                .when(cambiarPasswordUseCase).ejecutar(eq(id), any(), any());

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON)
                        .content("""
                                {
                                    "passwordActual": "ClaveErrada",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """)
                        .with(jwt().jwt(b -> b.subject("docente@uniquindio.edu.co"))
                                .authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isUnauthorized()); // 401
    }

    /**
     * Cambiar contraseña sin token → 401 Unauthorized (no autenticado).
     */
    @Test
    void testCambiarPasswordSinTokenRetorna401() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON)
                        .content("""
                                {
                                    "passwordActual": "Password123",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """))
                .andExpect(status().isUnauthorized()); // 401
    }
}