package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias del UsuarioController con seguridad desactivada.
 * Verifica únicamente comportamiento HTTP: códigos de estado, validaciones
 * de entrada y mapeo de excepciones.
 *
 */
@WebMvcTest(
    controllers = UsuarioController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
class UsuarioControllerTest {

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
    // POST /api/usuarios — validaciones de entrada
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void deberiaRetornar400CuandoIdentificacionEsBlanca() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "identificacion": "",
                                    "nombre": "Docente López",
                                    "email": "docente@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoNombreEsBlanco() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "",
                                    "email": "docente@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoEmailEsInvalido() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "Docente López",
                                    "email": "no-es-un-email",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoEmailEsNulo() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "Docente López",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoTipoUsuarioEsNulo() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "Docente López",
                                    "email": "docente@uniquindio.edu.co"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoBodyEsVacio() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400CuandoIdentificacionDuplicada() throws Exception {
        when(crearUsuarioUseCase.ejecutar(any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Ya existe un usuario con identificación: D-001"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "identificacion": "D-001",
                                    "nombre": "Docente López",
                                    "email": "docente@uniquindio.edu.co",
                                    "tipoUsuario": "DOCENTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ══════════════════════════════════════════════════════════════════════
    // GET /api/usuarios
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void deberiaRetornar200AlListarUsuarios() throws Exception {
        when(consultarUsuariosUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios").accept(JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        when(consultarUsuariosUseCase.obtenerPorId(idInexistente))
                .thenThrow(new UsuarioNoEncontradoException(
                        "No existe usuario con ID: " + idInexistente));

        mockMvc.perform(get("/api/usuarios/" + idInexistente).accept(JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaRetornar400CuandoIdEsMalformado() throws Exception {
        mockMvc.perform(get("/api/usuarios/no-es-uuid").accept(JSON))
                .andExpect(status().isBadRequest());
    }

    // ══════════════════════════════════════════════════════════════════════
    // PUT /api/usuarios/{id}/activar y desactivar
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void deberiaRetornar404AlActivarUsuarioInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        when(activarUsuarioUseCase.ejecutar(idInexistente))
                .thenThrow(new UsuarioNoEncontradoException(
                        "No existe usuario con ID: " + idInexistente));

        mockMvc.perform(put("/api/usuarios/" + idInexistente + "/activar").accept(JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaRetornar409AlActivarUsuarioYaActivo() throws Exception {
        UUID id = UUID.randomUUID();
        when(activarUsuarioUseCase.ejecutar(id))
                .thenThrow(new IllegalStateException("El usuario ya está ACTIVO"));

        mockMvc.perform(put("/api/usuarios/" + id + "/activar").accept(JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRetornar400CuandoIdActivarEsMalformado() throws Exception {
        mockMvc.perform(put("/api/usuarios/id-invalido/activar").accept(JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404AlDesactivarUsuarioInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        when(desactivarUsuarioUseCase.ejecutar(idInexistente))
                .thenThrow(new UsuarioNoEncontradoException(
                        "No existe usuario con ID: " + idInexistente));

        mockMvc.perform(put("/api/usuarios/" + idInexistente + "/desactivar").accept(JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaRetornar409AlDesactivarUsuarioYaInactivo() throws Exception {
        UUID id = UUID.randomUUID();
        when(desactivarUsuarioUseCase.ejecutar(id))
                .thenThrow(new IllegalStateException("El usuario ya está INACTIVO"));

        mockMvc.perform(put("/api/usuarios/" + id + "/desactivar").accept(JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRetornar400CuandoIdDesactivarEsMalformado() throws Exception {
        mockMvc.perform(put("/api/usuarios/id-invalido/desactivar").accept(JSON))
                .andExpect(status().isBadRequest());
    }

    // ══════════════════════════════════════════════════════════════════════
    // PUT /api/usuarios/{id}/cambiar-password
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Flujo feliz: contraseña cambiada correctamente → 204 No Content.
     * cambiarPasswordUseCase.ejecutar es void; sin stubbing no lanza nada.
     */
    @Test
    void deberiaRetornar204AlCambiarPasswordExitosamente() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "passwordActual": "Password123",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """))
                .andExpect(status().isNoContent()); // 204
    }

    /**
     * Contraseña actual incorrecta → BadCredentialsException →
     * GlobalExceptionHandler la mapea a 401, pero en este test de capa web
     * (sin seguridad) el handler la convierte a 401.
     *
     * NOTA: el GlobalExceptionHandler mapea BadCredentialsException → 401.
     * Si tu handler la mapea a 400, cambia el andExpect a isBadRequest().
     */
    @Test
    void deberiaRetornar401AlCambiarPasswordConClaveIncorrecta() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new BadCredentialsException("La contraseña actual es incorrecta"))
                .when(cambiarPasswordUseCase).ejecutar(eq(id), any(), any());

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "passwordActual": "ClaveErrada",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """))
                .andExpect(status().isUnauthorized()); // 401 — ver GlobalExceptionHandler
    }

    /**
     * Body con campos vacíos → validación @NotBlank → 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoPasswordActualEsBlanca() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "passwordActual": "",
                                    "passwordNueva": "NuevaClave456"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    /**
     * passwordNueva con menos de 8 caracteres → @Size(min=8) → 400 Bad Request.
     */
    @Test
    void deberiaRetornar400CuandoPasswordNuevaMenorA8Caracteres() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/usuarios/" + id + "/cambiar-password")
                        .contentType(JSON).accept(JSON)
                        .content("""
                                {
                                    "passwordActual": "Password123",
                                    "passwordNueva": "corta"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}