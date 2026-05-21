package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.CrearUsuarioUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {

    // ─── Las 3 dependencias que CrearUsuarioUseCase necesita ─────────────────
    @Mock private UsuarioRepository            usuarioRepository;
    @Mock private UsuarioSecurityJpaRepository securityRepository;
    @Mock private PasswordEncoder              passwordEncoder;

    @InjectMocks
    private CrearUsuarioUseCase useCase;

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Usuario usuarioEjemplo(String identificacion, String nombre,
                                   String email, TipoUsuario tipo) {
        return new Usuario(identificacion, nombre, new Email(email), tipo);
    }

    // ─── Test 1: Crea usuario de dominio y entrada de seguridad correctamente ─

    @Test
    void deberiaCrearUsuarioDeDominioYEntradaDeSeguridad() {
        // Arrange
        Usuario esperado = usuarioEjemplo(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        when(usuarioRepository.findByIdentificacion("D-001")).thenReturn(Optional.empty());
        when(securityRepository.findByEmail("docente@uniquindio.edu.co")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}hashedPassword");
        when(usuarioRepository.save(any())).thenReturn(esperado);

        // Act
        Usuario resultado = useCase.ejecutar(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        // Assert — usuario de dominio creado
        assertNotNull(resultado);
        assertEquals("D-001", resultado.getIdentificacion());
        assertEquals("Docente López", resultado.getNombre());
        assertEquals(TipoUsuario.DOCENTE, resultado.getTipoUsuario());
        assertEquals(EstadoUsuario.ACTIVO, resultado.getEstadoUsuario());

        // Assert — entrada de seguridad creada
        verify(securityRepository).save(any(UsuarioSecurityEntity.class));
    }

    // ─── Test 2: Lanza excepción si la identificación ya existe ──────────────

    @Test
    void deberiaLanzarExcepcionSiIdentificacionYaExiste() {
        // Arrange
        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.of(usuarioEjemplo(
                        "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE)));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                useCase.ejecutar("D-001", "Otro", "otro@uniquindio.edu.co", TipoUsuario.DOCENTE));

        assertTrue(ex.getMessage().contains("D-001"));

        // Nada debe guardarse
        verify(usuarioRepository, never()).save(any());
        verify(securityRepository, never()).save(any());
    }

    // ─── Test 3: Lanza excepción si el email ya existe en USUARIOS_SEGURIDAD ─

    @Test
    void deberiaLanzarExcepcionSiEmailYaExisteEnUsuariosSeguridad() {
        // Arrange — identificación libre, pero email ya tomado en seguridad
        when(usuarioRepository.findByIdentificacion("D-002")).thenReturn(Optional.empty());

        UsuarioSecurityEntity existente = new UsuarioSecurityEntity();
        existente.setEmail("docente@uniquindio.edu.co");
        when(securityRepository.findByEmail("docente@uniquindio.edu.co"))
                .thenReturn(Optional.of(existente));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                useCase.ejecutar("D-002", "Otro Docente",
                        "docente@uniquindio.edu.co", TipoUsuario.DOCENTE));

        assertTrue(ex.getMessage().contains("docente@uniquindio.edu.co"));

        // El usuario de dominio tampoco debe guardarse
        verify(usuarioRepository, never()).save(any());
    }

    // ─── Test 4: La contraseña se guarda encriptada (no en texto plano) ──────

    @Test
    void deberiaGuardarPasswordEncriptadaNoEnTextoPlano() {
        // Arrange
        String passwordEncriptada = "{bcrypt}$2a$10$hashedValue";
        Usuario guardado = usuarioEjemplo(
                "E-001", "Estudiante García", "est@uniquindio.edu.co", TipoUsuario.ESTUDIANTE);

        when(usuarioRepository.findByIdentificacion("E-001")).thenReturn(Optional.empty());
        when(securityRepository.findByEmail("est@uniquindio.edu.co")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn(passwordEncriptada);
        when(usuarioRepository.save(any())).thenReturn(guardado);

        // Act
        useCase.ejecutar("E-001", "Estudiante García",
                "est@uniquindio.edu.co", TipoUsuario.ESTUDIANTE);

        // Assert — capturamos el objeto que se pasó a securityRepository.save
        ArgumentCaptor<UsuarioSecurityEntity> captor =
                ArgumentCaptor.forClass(UsuarioSecurityEntity.class);
        verify(securityRepository).save(captor.capture());

        UsuarioSecurityEntity credencial = captor.getValue();

        // La contraseña guardada NO debe ser el texto plano
        assertNotEquals("Password123", credencial.getPassword());

        // Debe ser exactamente el valor que devolvió el encoder
        assertEquals(passwordEncriptada, credencial.getPassword());
    }

    // ─── Test 5: Rol ADMIN para COORDINADOR, USER para los demás ─────────────

    @Test
    void deberiaAsignarRolAdminACoordinadorYUserALosDemas() {
        // ── COORDINADOR → ADMIN ──────────────────────────────────────────────
        Usuario coordinador = usuarioEjemplo(
                "C-001", "Coordinador Pérez", "coord@uniquindio.edu.co", TipoUsuario.COORDINADOR);

        when(usuarioRepository.findByIdentificacion("C-001")).thenReturn(Optional.empty());
        when(securityRepository.findByEmail("coord@uniquindio.edu.co")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}hash");
        when(usuarioRepository.save(any())).thenReturn(coordinador);

        useCase.ejecutar("C-001", "Coordinador Pérez",
                "coord@uniquindio.edu.co", TipoUsuario.COORDINADOR);

        ArgumentCaptor<UsuarioSecurityEntity> captorAdmin =
                ArgumentCaptor.forClass(UsuarioSecurityEntity.class);
        verify(securityRepository).save(captorAdmin.capture());
        assertEquals(RolSeguridadEnum.ADMIN, captorAdmin.getValue().getRol());

        // Reset para el segundo escenario
        reset(usuarioRepository, securityRepository, passwordEncoder);

        // ── DOCENTE → USER ───────────────────────────────────────────────────
        Usuario docente = usuarioEjemplo(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        when(usuarioRepository.findByIdentificacion("D-001")).thenReturn(Optional.empty());
        when(securityRepository.findByEmail("docente@uniquindio.edu.co")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}hash");
        when(usuarioRepository.save(any())).thenReturn(docente);

        useCase.ejecutar("D-001", "Docente López",
                "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        ArgumentCaptor<UsuarioSecurityEntity> captorUser =
                ArgumentCaptor.forClass(UsuarioSecurityEntity.class);
        verify(securityRepository).save(captorUser.capture());
        assertEquals(RolSeguridadEnum.USER, captorUser.getValue().getRol());
    }
}