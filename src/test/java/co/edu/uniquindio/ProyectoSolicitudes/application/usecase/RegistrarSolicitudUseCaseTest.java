package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.RegistrarSolicitudService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.CanalOrigen;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.DescripcionSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
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
class RegistrarSolicitudUseCaseTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RegistrarSolicitudService registrarSolicitudService;

    @InjectMocks
    private RegistrarSolicitudUseCase useCase;

    private Usuario solicitanteValido() {
        return new Usuario("E-001", "Estudiante García",
                new Email("estudiante@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
    }

    private Solicitud solicitudMock(Usuario solicitante) {
        return new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                solicitante
        );
    }

    @Test
    void deberiaRegistrarSolicitudCuandoSolicitanteExiste() {
        // Arrange
        Usuario solicitante = solicitanteValido();
        Solicitud solicitud = solicitudMock(solicitante);

        when(usuarioRepository.findByIdentificacion("E-001"))
                .thenReturn(Optional.of(solicitante));
        when(registrarSolicitudService.registrar(any(), any(), any()))
                .thenReturn(solicitud);
        when(solicitudRepository.save(any()))
                .thenReturn(solicitud);

        // Act
        Solicitud resultado = useCase.ejecutar(
                "Necesito homologar materia cursada en otra universidad",
                CanalOrigen.CORREO_ELECTRONICO,
                "E-001"
        );

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository).findByIdentificacion("E-001");
        verify(registrarSolicitudService).registrar(any(), any(), any());
        verify(solicitudRepository).save(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoSolicitanteNoExiste() {
        // Arrange
        when(usuarioRepository.findByIdentificacion("X-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(
                        "Necesito homologar materia cursada en otra universidad",
                        CanalOrigen.PRESENCIAL,
                        "X-999"
                )
        );

        // Verify: nunca se intentó guardar
        verify(solicitudRepository, never()).save(any());
    }
}