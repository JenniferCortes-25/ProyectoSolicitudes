package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.solicitudUC.ConsultarSolicitudesUseCase;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.CanalOrigen;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.DescripcionSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarSolicitudesUseCaseTest {

    @Mock private SolicitudRepository solicitudRepository;

    @InjectMocks
    private ConsultarSolicitudesUseCase useCase;

    private Solicitud solicitudMock() {
        Usuario solicitante = new Usuario("E-001", "Estudiante García",
                new Email("est@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        return new Solicitud(
                new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
                CanalOrigen.CORREO_ELECTRONICO,
                solicitante
        );
    }

    @Test
    void deberiaRetornarSolicitudCuandoExiste() {
        // Arrange
        Solicitud solicitud = solicitudMock();
        when(solicitudRepository.findById(solicitud.getId()))
                .thenReturn(Optional.of(solicitud));

        // Act
        Solicitud resultado = useCase.obtenerPorId(solicitud.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals(solicitud.getId(), resultado.getId());
        verify(solicitudRepository).findById(solicitud.getId());
    }

    @Test
    void deberiaLanzarExcepcionCuandoSolicitudNoExiste() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(solicitudRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () ->
                useCase.obtenerPorId(idInexistente)
        );
    }

    @Test
    void deberiaListarTodasCuandoNoHayFiltro() {
        // Arrange
        List<Solicitud> lista = List.of(solicitudMock(), solicitudMock());
        when(solicitudRepository.findAll()).thenReturn(lista);

        // Act
        List<Solicitud> resultado = useCase.listar(Optional.empty());

        // Assert
        assertEquals(2, resultado.size());
        verify(solicitudRepository).findAll();
        verify(solicitudRepository, never()).findByEstado(any());
    }

    @Test
    void deberiaListarPorEstadoCuandoHayFiltro() {
        // Arrange
        List<Solicitud> lista = List.of(solicitudMock());
        when(solicitudRepository.findByEstado(EstadoSolicitud.REGISTRADA))
                .thenReturn(lista);

        // Act
        List<Solicitud> resultado = useCase.listar(Optional.of(EstadoSolicitud.REGISTRADA));

        // Assert
        assertEquals(1, resultado.size());
        verify(solicitudRepository).findByEstado(EstadoSolicitud.REGISTRADA);
        verify(solicitudRepository, never()).findAll();
    }
}