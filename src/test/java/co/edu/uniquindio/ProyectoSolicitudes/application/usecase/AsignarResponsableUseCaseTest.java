package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC.AsignarResponsableUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.service.AsignarResponsableService;
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
class AsignarResponsableUseCaseTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AsignarResponsableService asignarResponsableService;

    @InjectMocks
    private AsignarResponsableUseCase useCase;

    private Usuario coordinadorValido() {
        return new Usuario("C-001", "Coordinador Pérez",
                new Email("coord@uniquindio.edu.co"), TipoUsuario.COORDINADOR);
    }

    private Usuario docenteValido() {
        return new Usuario("D-001", "Docente López",
                new Email("docente@uniquindio.edu.co"), TipoUsuario.DOCENTE);
    }

    private Solicitud solicitudClasificada() {
        Usuario solicitante = new Usuario("E-001", "Estudiante García",
                new Email("est@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        Usuario coordinador = coordinadorValido();
        Solicitud s = new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                solicitante
        );
        s.clasificar(TipoSolicitud.HOMOLOGACION, coordinador);
        s.asignarPrioridad(
                new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima"), coordinador);
        return s;
    }

    @Test
    void deberiaAsignarResponsableCuandoDatosValidos() {
        // Arrange
        Solicitud solicitud = solicitudClasificada();
        Usuario coordinador = coordinadorValido();
        Usuario docente = docenteValido();

        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.of(docente));
        when(usuarioRepository.findByIdentificacion("C-001"))
                .thenReturn(Optional.of(coordinador));
        when(solicitudRepository.save(any()))
                .thenReturn(solicitud);

        // Act
        Solicitud resultado = useCase.ejecutar(
                solicitud.getId(),
                "D-001",
                "C-001"
        );

        // Assert
        assertNotNull(resultado);
        verify(solicitudRepository).findById(solicitud.getId());
        verify(usuarioRepository).findByIdentificacion("D-001");
        verify(usuarioRepository).findByIdentificacion("C-001");
        verify(asignarResponsableService).asignar(any(), any(), any());
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
                useCase.ejecutar(idInexistente, "D-001", "C-001")
        );

        verify(asignarResponsableService, never()).asignar(any(), any(), any());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoResponsableNoExiste() {
        // Arrange
        Solicitud solicitud = solicitudClasificada();
        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("X-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(solicitud.getId(), "X-999", "C-001")
        );

        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCoordinadorNoExiste() {
        // Arrange
        Solicitud solicitud = solicitudClasificada();
        Usuario docente = docenteValido();

        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("D-001"))
                .thenReturn(Optional.of(docente));
        when(usuarioRepository.findByIdentificacion("X-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(solicitud.getId(), "D-001", "X-999")
        );

        verify(solicitudRepository, never()).save(any());
    }
}