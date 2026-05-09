package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.CrearUsuarioUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {

    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CrearUsuarioUseCase useCase;

    private Usuario usuarioGuardado(String identificacion, String nombre,
                                    String email, TipoUsuario tipo) {
        return new Usuario(identificacion, nombre, new Email(email), tipo);
    }

    @Test
    void deberiaCrearUsuarioCuandoDatosValidos() {
        // Arrange
        Usuario esperado = usuarioGuardado(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.save(any()))
                .thenReturn(esperado);

        // Act
        Usuario resultado = useCase.ejecutar(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        // Assert
        assertNotNull(resultado);
        assertEquals("D-001", resultado.getIdentificacion());
        assertEquals("Docente López", resultado.getNombre());
        assertEquals(TipoUsuario.DOCENTE, resultado.getTipoUsuario());
        assertEquals(EstadoUsuario.ACTIVO, resultado.getEstadoUsuario());
        verify(usuarioRepository).findByIdentificacion("D-001");
        verify(usuarioRepository).save(any());
    }

    @Test
    void usuarioCreadoDebeIniciarEnEstadoActivo() {
        // Arrange
        Usuario esperado = usuarioGuardado(
                "E-001", "Estudiante García", "est@uniquindio.edu.co", TipoUsuario.ESTUDIANTE);

        when(usuarioRepository.findByIdentificacion("E-001"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.save(any()))
                .thenReturn(esperado);

        // Act
        Usuario resultado = useCase.ejecutar(
                "E-001", "Estudiante García", "est@uniquindio.edu.co", TipoUsuario.ESTUDIANTE);

        // Assert
        assertTrue(resultado.estaActivo());
        assertEquals(EstadoUsuario.ACTIVO, resultado.getEstadoUsuario());
    }

    @Test
    void deberiaLanzarExcepcionCuandoIdentificacionYaExiste() {
        // Arrange
        Usuario existente = usuarioGuardado(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.of(existente));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                useCase.ejecutar(
                        "D-001", "Otro Docente", "otro@uniquindio.edu.co", TipoUsuario.DOCENTE)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void mensajeExcepcionDuplicadaDebeMencionarLaIdentificacion() {
        // Arrange
        Usuario existente = usuarioGuardado(
                "D-001", "Docente López", "docente@uniquindio.edu.co", TipoUsuario.DOCENTE);

        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.of(existente));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                useCase.ejecutar(
                        "D-001", "Otro Docente", "otro@uniquindio.edu.co", TipoUsuario.DOCENTE)
        );

        assertTrue(ex.getMessage().contains("D-001"));
    }

    @Test
    void siempreDebeVerificarDuplicadoAntesDeGuardar() {
        // Arrange
        when(usuarioRepository.findByIdentificacion("N-001"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.save(any()))
                .thenReturn(usuarioGuardado(
                        "N-001", "Nuevo Usuario", "nuevo@uniquindio.edu.co", TipoUsuario.ESTUDIANTE));

        // Act
        useCase.ejecutar("N-001", "Nuevo Usuario", "nuevo@uniquindio.edu.co", TipoUsuario.ESTUDIANTE);

        // Assert — el orden importa: primero findByIdentificacion, luego save
        var inOrder = inOrder(usuarioRepository);
        inOrder.verify(usuarioRepository).findByIdentificacion("N-001");
        inOrder.verify(usuarioRepository).save(any());
    }
}