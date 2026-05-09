package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.ConsultarUsuariosUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarUsuariosUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConsultarUsuariosUseCase useCase;

    private Usuario docenteActivo() {
        return new Usuario(
                "D-001",
                "Docente Lopez",
                new Email("docente@uniquindio.edu.co"),
                TipoUsuario.DOCENTE
        );
    }

    private Usuario coordinadorActivo() {
        return new Usuario(
                "C-001",
                "Coordinador Perez",
                new Email("coord@uniquindio.edu.co"),
                TipoUsuario.COORDINADOR
        );
    }

    @Test
    void deberiaRetornarUsuarioCuandoExistePorId() {
        // Arrange
        Usuario docente = docenteActivo();
        when(usuarioRepository.findById(docente.getId()))
                .thenReturn(Optional.of(docente));

        // Act
        Usuario resultado = useCase.obtenerPorId(docente.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals(docente.getId(), resultado.getId());
        assertEquals("D-001", resultado.getIdentificacion());
        verify(usuarioRepository).findById(docente.getId());
    }

    @Test
    void deberiaLanzarExcepcionCuandoNoExistePorId() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.obtenerPorId(idInexistente)
        );

        verify(usuarioRepository).findById(idInexistente);
    }

    @Test
    void deberiaRetornarUsuarioCuandoExistePorIdentificacion() {
        // Arrange
        Usuario docente = docenteActivo();
        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.of(docente));

        // Act
        Usuario resultado = useCase.obtenerPorIdentificacion("D-001");

        // Assert
        assertNotNull(resultado);
        assertEquals("D-001", resultado.getIdentificacion());
        verify(usuarioRepository).findByIdentificacion("D-001");
    }

    @Test
    void deberiaLanzarExcepcionCuandoNoExistePorIdentificacion() {
        // Arrange
        when(usuarioRepository.findByIdentificacion("X-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.obtenerPorIdentificacion("X-999")
        );
    }

    @Test
    void deberiaListarTodosLosUsuarios() {
        // Arrange
        List<Usuario> lista = List.of(docenteActivo(), coordinadorActivo());
        when(usuarioRepository.findAll()).thenReturn(lista);

        // Act
        List<Usuario> resultado = useCase.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of());

        // Act
        List<Usuario> resultado = useCase.listarTodos();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findAll();
    }

    @Test
    void listarNoDebeInvocarOtrosMetodosDelRepositorio() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(docenteActivo()));

        // Act
        useCase.listarTodos();

        // Assert
        verify(usuarioRepository).findAll();
        verify(usuarioRepository, never()).findById(any());
        verify(usuarioRepository, never()).findByIdentificacion(any());
    }
}