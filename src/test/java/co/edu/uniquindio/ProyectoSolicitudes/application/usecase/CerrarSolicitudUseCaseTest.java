package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC.CerrarSolicitudUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
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
class CerrarSolicitudUseCaseTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CerrarSolicitudUseCase useCase;

    private Usuario coordinadorValido() {
        return new Usuario("C-001", "Coordinador Pérez",
                new Email("coord@uniquindio.edu.co"), TipoUsuario.COORDINADOR);
    }

    private Usuario docenteValido() {
        return new Usuario("D-001", "Docente López",
                new Email("docente@uniquindio.edu.co"), TipoUsuario.DOCENTE);
    }

    private Solicitud solicitudAtendida() {
        Usuario solicitante = new Usuario("E-001", "Estudiante García",
                new Email("est@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        Usuario coordinador = coordinadorValido();
        Usuario docente = docenteValido();

        Solicitud s = new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                solicitante
        );
        s.clasificar(TipoSolicitud.HOMOLOGACION, coordinador);
        s.asignarPrioridad(new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima"), coordinador);
        s.asignarResponsable(
                new Responsable(docente.getId(), docente.getNombre()), coordinador);
        s.iniciarAtencion(coordinador);
        s.atender("Proceso completado", docente);
        return s;
    }

    @Test
    void deberiaCerrarSolicitudCuandoEstadoAtendida() {
        // Arrange
        Usuario coordinador = coordinadorValido();
        Solicitud solicitud = solicitudAtendida();
        UUID solicitudId = solicitud.getId();

        when(solicitudRepository.findById(solicitudId))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("C-001"))
                .thenReturn(Optional.of(coordinador));
        when(solicitudRepository.save(any()))
                .thenReturn(solicitud);

        // Act
        Solicitud resultado = useCase.ejecutar(
                solicitudId,
                "Homologación aprobada por consejo de programa",
                "C-001"
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoSolicitud.CERRADA, resultado.getEstado());
        verify(solicitudRepository).findById(solicitudId);
        verify(usuarioRepository).findByIdentificacion("C-001");
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
                        "Homologación aprobada por consejo de programa",
                        "C-001"
                )
        );

        // Verify: nunca se intentó guardar
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCoordinadorNoExiste() {
        // Arrange
        Solicitud solicitud = solicitudAtendida();
        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByIdentificacion("X-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.ejecutar(
                        solicitud.getId(),
                        "Homologación aprobada por consejo de programa",
                        "X-999"
                )
        );

        verify(solicitudRepository, never()).save(any());
    }
}