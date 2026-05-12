package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.CambiarPasswordUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CambiarPasswordUseCase.
 * Carpeta: src/test/java/.../application/usecase/usuario/
 *
 * Cubre:
 *  1. Cambia contraseña exitosamente cuando la actual es correcta
 *  2. Lanza BadCredentialsException cuando la contraseña actual es incorrecta
 *  3. Lanza IllegalArgumentException cuando el usuario de dominio no existe
 *  4. Lanza IllegalArgumentException cuando no existe entrada en USUARIOS_SEGURIDAD
 */
@ExtendWith(MockitoExtension.class)
class CambiarPasswordUseCaseTest {

    // ── Mocks (clones de sombra — no tocan base de datos real) ────────────────
    @Mock private UsuarioRepository            usuarioRepository;
    @Mock private UsuarioSecurityJpaRepository securityRepository;
    @Mock private PasswordEncoder              passwordEncoder;

    // ── Víctima: el use case que usará los mocks de arriba ────────────────────
    @InjectMocks
    private CambiarPasswordUseCase useCase;

    // ── Datos de prueba reutilizables ─────────────────────────────────────────
    private UUID       usuarioId;
    private Usuario    usuarioDominio;
    private UsuarioSecurityEntity credencial;

    @BeforeEach
    void setUp() {
        // [ARRANGE] — preparar entidades base para cada escenario
        usuarioId      = UUID.randomUUID();
        usuarioDominio = new Usuario("D-001", "Docente López",
                new Email("docente@uniquindio.edu.co"), TipoUsuario.DOCENTE);

        credencial = new UsuarioSecurityEntity();
        credencial.setEmail("docente@uniquindio.edu.co");
        credencial.setPassword("{bcrypt}$2a$10$hashFicticio");
        credencial.setRol(RolSeguridadEnum.USER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 1: Flujo feliz — contraseña se actualiza correctamente
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void cambiaPasswordExitosamenteCuandoActualEsCorrecta() {
        // [ARRANGE] — mentiras verdaderas: el repositorio "encuentra" al usuario
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioDominio));
        when(securityRepository.findByEmail("docente@uniquindio.edu.co"))
                .thenReturn(Optional.of(credencial));
        // El encoder dice que la contraseña actual SÍ coincide
        when(passwordEncoder.matches("Password123", credencial.getPassword()))
                .thenReturn(true);
        // El encoder devuelve un hash ficticio para la nueva contraseña
        when(passwordEncoder.encode("NuevaClave456"))
                .thenReturn("{bcrypt}$2a$10$nuevoHash");

        // [ACT] — ejecutar el caso de uso
        assertDoesNotThrow(() ->
                useCase.ejecutar(usuarioId, "Password123", "NuevaClave456"));

        // [ASSERT] — verificar que se guardó la credencial con el nuevo hash
        verify(securityRepository, times(1)).save(credencial);
        assertEquals("{bcrypt}$2a$10$nuevoHash", credencial.getPassword());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 2: Contraseña actual incorrecta → BadCredentialsException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void lanzaBadCredentialsCuandoPasswordActualEsIncorrecta() {
        // [ARRANGE]
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioDominio));
        when(securityRepository.findByEmail("docente@uniquindio.edu.co"))
                .thenReturn(Optional.of(credencial));
        // El encoder dice que la contraseña NO coincide
        when(passwordEncoder.matches("ClaveErrada", credencial.getPassword()))
                .thenReturn(false);

        // [ACT & ASSERT]
        assertThrows(BadCredentialsException.class, () ->
                useCase.ejecutar(usuarioId, "ClaveErrada", "NuevaClave456"));

        // El save NO debe haberse invocado nunca
        verify(securityRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 3: Usuario de dominio inexistente → IllegalArgumentException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void lanzaIllegalArgumentCuandoUsuarioNoPerteneceAlDominio() {
        // [ARRANGE] — el repositorio de dominio devuelve vacío
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.empty());

        // [ACT & ASSERT]
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                useCase.ejecutar(usuarioId, "Password123", "NuevaClave456"));

        assertTrue(ex.getMessage().contains(usuarioId.toString()));
        // Nunca debe consultar seguridad si no hay usuario de dominio
        verify(securityRepository, never()).findByEmail(anyString());
        verify(securityRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 4: Usuario existe en dominio pero sin entrada en USUARIOS_SEGURIDAD
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void lanzaIllegalArgumentCuandoNoExisteEntradaEnSeguridadTable() {
        // [ARRANGE] — dominio OK pero seguridad vacía
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioDominio));
        when(securityRepository.findByEmail("docente@uniquindio.edu.co"))
                .thenReturn(Optional.empty());

        // [ACT & ASSERT]
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                useCase.ejecutar(usuarioId, "Password123", "NuevaClave456"));

        assertTrue(ex.getMessage().contains("docente@uniquindio.edu.co"));
        verify(securityRepository, never()).save(any());
    }
}