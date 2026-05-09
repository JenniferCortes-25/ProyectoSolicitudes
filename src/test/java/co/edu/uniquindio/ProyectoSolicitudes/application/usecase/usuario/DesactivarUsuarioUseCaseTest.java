package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.DesactivarUsuarioUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesactivarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DesactivarUsuarioUseCase useCase;

    private Usuario docenteActivo() {
        return new Usuario(
                "D-001",
                "Docente Lopez",
                new Email("docente@uniquindio.edu.co"),
                TipoUsuario.DOCENTE
        );
    }

    private Usuario docenteInactivo() {
        Usuario u = docenteActivo();
        u.desactivar();
        return u;
    }

    @Test
    void deberiaDesactivarUsuarioCuandoEstaActivo() {
        // Arrange
        Usuario activo = docenteActivo();
        UUID id = activo.getId();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(activo));
        when(usuarioRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        Usuario resultado = useCase.ejecutar(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoUsuario.INACTIVO, resultado.getEstadoUsuario());
        assertFalse(resultado.estaActivo());
        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(activo);
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(idInexistente)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarIllegalStateExceptionCuandoUsuarioYaEstaInactivo() {
        // Arrange
        Usuario inactivo = docenteInactivo();
        UUID id = inactivo.getId();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(inactivo));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                useCase.ejecutar(id)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deberiaInvocarSaveConElMismoUsuarioEncontrado() {
        // Arrange
        Usuario activo = docenteActivo();
        UUID id = activo.getId();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(activo));
        when(usuarioRepository.save(activo))
                .thenReturn(activo);

        // Act
        useCase.ejecutar(id);

        // Assert
        verify(usuarioRepository).save(activo);
    }
}