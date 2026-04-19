package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para SolicitudJpaRepository.
 *
 * @SpringBootTest levanta el contexto completo con H2 en memoria.
 * @DirtiesContext reinicia el contexto (y la BD) entre tests.
 *
 * Estos tests validan el flujo completo:
 * Controller → UseCase → SolicitudRepository (JPA) → H2
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SolicitudJpaRepositoryTest {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario estudianteGuardado;

    @BeforeEach
    void setUp() {
        // Guardar un usuario de prueba antes de cada test
        estudianteGuardado = usuarioRepository.save(
                new Usuario("EST-TEST", "Estudiante Test",
                        new Email("est.test@uniquindio.edu.co"),
                        TipoUsuario.ESTUDIANTE));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Solicitud crearSolicitudDePrueba() {
        return new Solicitud(
                new DescripcionSolicitud("Necesito homologar Programación Básica"),
                CanalOrigen.CORREO_ELECTRONICO,
                estudianteGuardado
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe guardar una solicitud y recuperarla por ID")
    void deberiaGuardarYRecuperarPorId() {
        // Arrange
        Solicitud nueva = crearSolicitudDePrueba();

        // Act
        Solicitud guardada   = solicitudRepository.save(nueva);
        Optional<Solicitud> recuperada = solicitudRepository.findById(guardada.getId());

        // Assert
        assertTrue(recuperada.isPresent());
        assertEquals(guardada.getId(),                    recuperada.get().getId());
        assertEquals("Necesito homologar Programación Básica",
                     recuperada.get().getDescripcion().texto());
        assertEquals(EstadoSolicitud.REGISTRADA,          recuperada.get().getEstado());
        assertEquals(CanalOrigen.CORREO_ELECTRONICO,      recuperada.get().getCanal());
        assertEquals(estudianteGuardado.getNombre(),       recuperada.get().getSolicitanteNombre());
    }

    @Test
    @DisplayName("Debe listar todas las solicitudes guardadas")
    void deberiaListarTodasLasSolicitudes() {
        solicitudRepository.save(crearSolicitudDePrueba());
        solicitudRepository.save(crearSolicitudDePrueba());

        List<Solicitud> todas = solicitudRepository.findAll();

        assertEquals(2, todas.size());
    }

    @Test
    @DisplayName("Debe filtrar solicitudes por estado")
    void deberiaFiltrarPorEstado() {
        solicitudRepository.save(crearSolicitudDePrueba());
        solicitudRepository.save(crearSolicitudDePrueba());

        List<Solicitud> registradas = solicitudRepository.findByEstado(EstadoSolicitud.REGISTRADA);
        List<Solicitud> clasificadas = solicitudRepository.findByEstado(EstadoSolicitud.CLASIFICADA);

        assertEquals(2, registradas.size());
        assertTrue(clasificadas.isEmpty());
    }

    @Test
    @DisplayName("Debe contar solicitudes por estado")
    void deberiaContarPorEstado() {
        solicitudRepository.save(crearSolicitudDePrueba());
        solicitudRepository.save(crearSolicitudDePrueba());

        long total = solicitudRepository.countByEstado(EstadoSolicitud.REGISTRADA);

        assertEquals(2L, total);
    }

    @Test
    @DisplayName("Debe filtrar por solicitante")
    void deberiaFiltrarPorSolicitante() {
        // Crear segundo usuario
        Usuario otro = usuarioRepository.save(
                new Usuario("EST-OTRO", "Otro Estudiante",
                        new Email("otro@uniquindio.edu.co"),
                        TipoUsuario.ESTUDIANTE));

        solicitudRepository.save(crearSolicitudDePrueba()); // solicitante = estudianteGuardado
        solicitudRepository.save(new Solicitud(
                new DescripcionSolicitud("Solicitud del otro estudiante"),
                CanalOrigen.SAC, otro));

        List<Solicitud> delPrimero = solicitudRepository.findBySolicitanteId(
                estudianteGuardado.getId());

        assertEquals(1, delPrimero.size());
        assertEquals(estudianteGuardado.getNombre(),
                     delPrimero.get(0).getSolicitanteNombre());
    }

    @Test
    @DisplayName("Debe persistir el historial de la solicitud")
    void deberiaPersistirHistorial() {
        Solicitud solicitud = crearSolicitudDePrueba();
        Solicitud guardada  = solicitudRepository.save(solicitud);

        Optional<Solicitud> recuperada = solicitudRepository.findById(guardada.getId());

        assertTrue(recuperada.isPresent());
        // El constructor siempre agrega una entrada de "Solicitud registrada"
        assertFalse(recuperada.get().getHistorial().isEmpty());
        assertEquals(1, recuperada.get().getHistorial().size());
    }

    @Test
    @DisplayName("Debe encontrar solicitudes pendientes de asignacion")
    void deberiaEncontrarPendientesDeAsignacion() {
        solicitudRepository.save(crearSolicitudDePrueba()); // REGISTRADA, sin responsable

        List<Solicitud> pendientes =
                solicitudRepository.findSolicitudesPendientesDeAsignacion();

        assertEquals(1, pendientes.size());
    }

    @Test
    @DisplayName("Debe verificar existencia y eliminar por ID")
    void deberiaVerificarExistenciaYEliminar() {
        Solicitud guardada = solicitudRepository.save(crearSolicitudDePrueba());

        assertTrue(solicitudRepository.existsById(guardada.getId()));

        solicitudRepository.deleteById(guardada.getId());

        assertFalse(solicitudRepository.existsById(guardada.getId()));
    }

    @Test
    @DisplayName("Debe devolver Optional vacio para ID inexistente")
    void deberiaRetornarEmptyParaIdInexistente() {
        Optional<Solicitud> resultado =
                solicitudRepository.findById(java.util.UUID.randomUUID());

        assertTrue(resultado.isEmpty());
    }
}