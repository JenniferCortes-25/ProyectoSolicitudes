package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.controllers.unit;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.*;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.UsuarioController;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.UsuarioMapper;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.JwtConfig;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class UsuarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private UsuarioMapper mapper;
    @MockitoBean private CrearUsuarioUseCase crearUsuarioUseCase;
    @MockitoBean private ConsultarUsuariosUseCase consultarUsuariosUseCase;
    @MockitoBean private ActivarUsuarioUseCase activarUsuarioUseCase;
    @MockitoBean private DesactivarUsuarioUseCase desactivarUsuarioUseCase;

    @Test
    void testListarUsuariosConTokenValidoRetorna200() throws Exception {
        // [ARRANGE]
        when(consultarUsuariosUseCase.listarTodos()).thenReturn(List.of());

        // [ACT & ASSERT]
        mockMvc.perform(get("/api/usuarios")
                .with(jwt().jwt(b -> b.subject("admin@test.com"))
                        .authorities(new org.springframework.security.core.authority
                                .SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void testListarUsuariosSinTokenRetorna401() throws Exception {
        // Sin .with(jwt()) → el escudo JWT rechaza la petición
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testListarUsuariosConRolUserRetorna200() throws Exception {
        when(consultarUsuariosUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios")
                .with(jwt().jwt(b -> b.subject("user@test.com"))
                        .authorities(new org.springframework.security.core.authority
                                .SimpleGrantedAuthority("USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void testCrearUsuarioConRolUserRetorna403() throws Exception {
        // POST /api/usuarios requiere ADMIN — USER debe recibir 403
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/usuarios")
                        .contentType("application/json")
                        .content("{\"identificacion\":\"123\",\"nombre\":\"Test\",\"email\":\"t@t.com\",\"tipoUsuario\":\"DOCENTE\"}")
                        .with(jwt().jwt(b -> b.subject("user@test.com"))
                                .authorities(new org.springframework.security.core.authority
                                        .SimpleGrantedAuthority("USER"))))
                .andExpect(status().isForbidden());
    }
}