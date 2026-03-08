package co.edu.uniquindio.ProyectoSolicitudes;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DominioTest {

	// ─── Helpers ─────────────────────────────────────────────────────────────

	private Solicitud solicitudValida() {
		return new Solicitud(
				new DescripcionSolicitud("Necesito homologar materia cursada en otra universidad"),
				CanalOrigen.CORREO_ELECTRONICO,
				UUID.randomUUID()
		);
	}

	private Responsable responsableValido() {
		return new Responsable(UUID.randomUUID(), "Juan Pérez");
	}

	private Prioridad prioridadValida() {
		return new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima"); // ← NivelPrioridad directo
	}

	// ─── Test 1: Solicitud válida inicia en REGISTRADA ────────────────────────

	@Test
	void solicitudNuevaDebeIniciarEnEstadoRegistrada() {
		Solicitud solicitud = solicitudValida();
		assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
	}

	// ─── Test 2: Historial crece al registrar la solicitud ───────────────────

	@Test
	void solicitudNuevaDebeRegistrarEntradaEnHistorial() {
		Solicitud solicitud = solicitudValida();
		assertEquals(1, solicitud.getHistorial().size());
	}

	// ─── Test 3: Clasificar en estado CERRADA lanza SolicitudCerradaException ─

	@Test
	void clasificarSolicitudCerradaDebeLanzarSolicitudCerradaException() {
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
		solicitud.asignarPrioridad(prioridadValida(), "coord-01");
		solicitud.asignarResponsable(responsableValido(), true, "coord-01");
		solicitud.iniciarAtencion("coord-01");
		solicitud.atender("Atendida correctamente", "coord-01");
		solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");

		assertThrows(SolicitudCerradaException.class, () ->
				solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01")
		);
	}

	// ─── Test 4: Cerrar en estado CLASIFICADA lanza TransicionInvalidaException

	@Test
	void cerrarSolicitudClasificadaDebeLanzarTransicionInvalidaException() {
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");

		assertThrows(TransicionInvalidaException.class, () ->
				solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
		);
	}

	// ─── Test 5: Asignar responsable inactivo lanza UsuarioInactivoException ──

	@Test
	void asignarResponsableInactivoDebeLanzarUsuarioInactivoException() {
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");

		assertThrows(UsuarioInactivoException.class, () ->
				solicitud.asignarResponsable(responsableValido(), false, "coord-01")
		);
	}

	// ─── Test 6: Email inválido lanza EmailInvalidoException ─────────────────

	@Test
	void emailSinArrobaDebeLanzarEmailInvalidoException() {
		assertThrows(EmailInvalidoException.class, () ->
				new Email("correo-sin-arroba")
		);
	}

	// ─── Test 7: Prioridad sin justificación lanza PrioridadSinJustificacionException

	@Test
	void prioridadSinJustificacionDebeLanzarPrioridadSinJustificacionException() {
		assertThrows(PrioridadSinJustificacionException.class, () ->
				new Prioridad(NivelPrioridad.ALTA, "") // ← NivelPrioridad directo
		);
	}

	// ─── Test 8: ObservacionCierre menor a 20 chars lanza ObservacionInvalidaException

	@Test
	void observacionCierreCortoDebeLanzarObservacionInvalidaException() {
		assertThrows(ObservacionInvalidaException.class, () ->
				new ObservacionCierre("muy corta")
		);
	}

	// ─── Test 9: iniciarAtencion sin responsable lanza SinResponsableException

	@Test
	void iniciarAtencionSinResponsableDebeLanzarSinResponsableException() {
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");

		assertThrows(SinResponsableException.class, () ->
				solicitud.iniciarAtencion("coord-01")
		);
	}

	// ─── Test 10: Activar usuario ya activo lanza IllegalStateException ───────

	@Test
	void activarUsuarioYaActivoDebeLanzarIllegalStateException() {
		Usuario usuario = new Usuario(
				"1234567890",
				"Juan Pérez",
				new Email("juan@uniquindio.edu.co"),
				TipoUsuario.DOCENTE
		);

		assertThrows(IllegalStateException.class, usuario::activar);
	}
}