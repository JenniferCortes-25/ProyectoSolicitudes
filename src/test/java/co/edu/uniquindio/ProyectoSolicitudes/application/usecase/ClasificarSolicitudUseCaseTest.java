package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.ClasificarSolicitudService;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
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
class ClasificarSolicitudUseCaseTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ClasificarSolicitudService clasificarSolicitudService;

    @InjectMocks
    private ClasificarSolicitudUseCase useCase;

    private Usuario coordinadorValido() {
        return new Usuario("C-001", "Coordinador Pérez",
                new Email("coord@uniquindio.edu.co"), TipoUsuario.COORDINADOR);
    }

    private Solicitud solicitudRegistrada() {
        Usuario solicitante = new Usuario("E-001", "Estudiante García",
                new Email("est@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        return new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                solicitante
        );
    }

    @Test
    void deberiaClasificarSolicitudCuandoDatosValidos() {
        // Arrange
        Solicitud solicitud = solicitudRegistrada();
        Usuario coordinador = coordinadorValido();

        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("C-001"))
                .thenReturn(Optional.of(coordinador));
        when(solicitudRepository.save(any()))
                .thenReturn(solicitud);

        // Act
        Solicitud resultado = useCase.ejecutar(
                solicitud.getId(),
                TipoSolicitud.HOMOLOGACION,
                NivelPrioridad.ALTA,
                "Tiene fecha límite próxima",
                "C-001"
        );

        // Assert
        assertNotNull(resultado);
        verify(solicitudRepository).findById(solicitud.getId());
        verify(usuarioRepository).findByIdentificacion("C-001");
        verify(clasificarSolicitudService).clasificar(any(), any(), any(), any());
        verify(solicitudRepository).save(solicitud);
    }

    @Test
    void deberiaLanzarExcepcionCuandoSolicitudNoExiste() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(solicitudRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(
                        idInexistente,
                        TipoSolicitud.HOMOLOGACION,
                        NivelPrioridad.ALTA,
                        "Tiene fecha límite próxima",
                        "C-001"
                )
        );

        verify(solicitudRepository, never()).save(any());
        verify(clasificarSolicitudService, never()).clasificar(any(), any(), any(), any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCoordinadorNoExiste() {
        // Arrange
        Solicitud solicitud = solicitudRegistrada();
        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("X-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(
                        solicitud.getId(),
                        TipoSolicitud.HOMOLOGACION,
                        NivelPrioridad.ALTA,
                        "Tiene fecha límite próxima",
                        "X-999"
                )
        );

        verify(solicitudRepository, never()).save(any());
    }
}