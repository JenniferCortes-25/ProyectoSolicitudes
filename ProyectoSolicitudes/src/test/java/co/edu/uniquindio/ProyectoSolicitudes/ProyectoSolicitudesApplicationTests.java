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
		return new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");
	}

	// =========================================================================
	// PARTE 1 — VALUE OBJECTS
	// =========================================================================

	/**
	 * RN-09 — Email con formato inválido no debe crearse.
	 */
	@Test
	void emailSinArrobaDebeLanzarEmailInvalidoException() {
		// Arrange & Act & Assert
		assertThrows(EmailInvalidoException.class, () ->
				new Email("correo-sin-arroba")
		);
	}

	/**
	 * Igualdad por valor — dos Email con el mismo texto deben ser iguales.
	 * Los records de Java implementan equals/hashCode por valor automáticamente.
	 */
	@Test
	void dosEmailsConMismoValorDebenSerIguales() {
		// Arrange
		Email e1 = new Email("juan@uniquindio.edu.co");
		Email e2 = new Email("juan@uniquindio.edu.co");

		// Assert
		assertEquals(e1, e2);
		assertEquals(e1.hashCode(), e2.hashCode());
	}

	/**
	 * RN-07 — Prioridad sin justificación no debe crearse.
	 */
	@Test
	void prioridadSinJustificacionDebeLanzarPrioridadSinJustificacionException() {
		// Arrange & Act & Assert
		assertThrows(PrioridadSinJustificacionException.class, () ->
				new Prioridad(NivelPrioridad.ALTA, "")
		);
	}

	/**
	 * Igualdad por valor — dos Prioridad con los mismos datos deben ser iguales.
	 */
	@Test
	void dosPrioridadesConMismosValoresDebenSerIguales() {
		// Arrange
		Prioridad p1 = new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");
		Prioridad p2 = new Prioridad(NivelPrioridad.ALTA, "Tiene fecha límite próxima");

		// Assert
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
	}

	/**
	 * RN-08 — ObservacionCierre menor a 20 chars no debe crearse.
	 */
	@Test
	void observacionCierreCortoDebeLanzarObservacionInvalidaException() {
		// Arrange & Act & Assert
		assertThrows(ObservacionInvalidaException.class, () ->
				new ObservacionCierre("muy corta")
		);
	}

	/**
	 * RN-06 — Descripción menor a 10 chars no debe crearse.
	 */
	@Test
	void descripcionMenorA10CaracteresDebeLanzarDescripcionInvalidaException() {
		// Arrange & Act & Assert
		assertThrows(DescripcionInvalidaException.class, () ->
				new DescripcionSolicitud("corta")
		);
	}

	// =========================================================================
	// PARTE 2 — ENTIDADES
	// =========================================================================

	/**
	 * RF-01 — Una solicitud nueva debe iniciar siempre en estado REGISTRADA.
	 */
	@Test
	void solicitudNuevaDebeIniciarEnEstadoRegistrada() {
		// Arrange & Act
		Solicitud solicitud = solicitudValida();

		// Assert
		assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
	}

	/**
	 * RF-06 — El historial debe crecer al registrar la solicitud.
	 */
	@Test
	void solicitudNuevaDebeRegistrarEntradaEnHistorial() {
		// Arrange & Act
		Solicitud solicitud = solicitudValida();

		// Assert
		assertEquals(1, solicitud.getHistorial().size());
	}

	/**
	 * Usuario — activar un usuario ya activo debe lanzar excepción.
	 */
	@Test
	void activarUsuarioYaActivoDebeLanzarIllegalStateException() {
		// Arrange
		Usuario usuario = new Usuario(
				"1234567890",
				"Juan Pérez",
				new Email("juan@uniquindio.edu.co"),
				TipoUsuario.DOCENTE
		);

		// Act & Assert
		assertThrows(IllegalStateException.class, usuario::activar);
	}

	/**
	 * Usuario — desactivar y volver a activar debe funcionar correctamente.
	 */
	@Test
	void usuarioDesactivadoPuedeVolverAActivarse() {
		// Arrange
		Usuario usuario = new Usuario(
				"1234567890",
				"Juan Pérez",
				new Email("juan@uniquindio.edu.co"),
				TipoUsuario.DOCENTE
		);

		// Act
		usuario.desactivar();
		usuario.activar();

		// Assert
		assertTrue(usuario.estaActivo());
	}

	// =========================================================================
	// PARTE 3 — AGREGADO (INVARIANTES)
	// =========================================================================

	/**
	 * RN-01 — Clasificar una solicitud CERRADA debe lanzar SolicitudCerradaException.
	 */
	@Test
	void clasificarSolicitudCerradaDebeLanzarSolicitudCerradaException() {
		// Arrange — llevar la solicitud hasta CERRADA
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
		solicitud.asignarPrioridad(prioridadValida(), "coord-01");
		solicitud.asignarResponsable(responsableValido(), true, "coord-01");
		solicitud.iniciarAtencion("coord-01");
		solicitud.atender("Atendida correctamente", "coord-01");
		solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");

		// Act & Assert
		assertThrows(SolicitudCerradaException.class, () ->
				solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01")
		);
	}

	/**
	 * RN-02 — Cerrar una solicitud en estado CLASIFICADA debe lanzar TransicionInvalidaException.
	 */
	@Test
	void cerrarSolicitudClasificadaDebeLanzarTransicionInvalidaException() {
		// Arrange
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");

		// Act & Assert
		assertThrows(TransicionInvalidaException.class, () ->
				solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
		);
	}

	/**
	 * RN-02 — El estado NO debe cambiar cuando falla una transición inválida.
	 */
	@Test
	void estadoNoDebeCambiarCuandoFallaTransicionInvalida() {
		// Arrange
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
		EstadoSolicitud estadoAntes = solicitud.getEstado(); // CLASIFICADA

		// Act — intento de transición inválida
		assertThrows(TransicionInvalidaException.class, () ->
				solicitud.cerrar(new ObservacionCierre("Observación de cierre con más de 20 chars"), "coord-01")
		);

		// Assert — el estado debe seguir siendo el mismo
		assertEquals(estadoAntes, solicitud.getEstado());
	}

	/**
	 * RN-04 / RN-10 — Asignar responsable inactivo debe lanzar UsuarioInactivoException.
	 */
	@Test
	void asignarResponsableInactivoDebeLanzarUsuarioInactivoException() {
		// Arrange
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");

		// Act & Assert
		assertThrows(UsuarioInactivoException.class, () ->
				solicitud.asignarResponsable(responsableValido(), false, "coord-01")
		);
	}

	/**
	 * RN-05 — Iniciar atención sin responsable debe lanzar SinResponsableException.
	 */
	@Test
	void iniciarAtencionSinResponsableDebeLanzarSinResponsableException() {
		// Arrange
		Solicitud solicitud = solicitudValida();
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");

		// Act & Assert
		assertThrows(SinResponsableException.class, () ->
				solicitud.iniciarAtencion("coord-01")
		);
	}

	/**
	 * Flujo completo — verificar que una solicitud puede recorrer todo el ciclo de vida
	 * desde REGISTRADA hasta CERRADA sin errores.
	 */
	@Test
	void solicitudDebePoderRecorrerCicloDeVidaCompleto() {
		// Arrange
		Solicitud solicitud = solicitudValida();

		// Act — flujo completo
		solicitud.clasificar(TipoSolicitud.HOMOLOGACION, "coord-01");
		solicitud.asignarPrioridad(prioridadValida(), "coord-01");
		solicitud.asignarResponsable(responsableValido(), true, "coord-01");
		solicitud.iniciarAtencion("coord-01");
		solicitud.atender("Proceso completado satisfactoriamente", "coord-01");
		solicitud.cerrar(new ObservacionCierre("Homologación aprobada por consejo de programa"), "coord-01");

		// Assert
		assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
		assertTrue(solicitud.getHistorial().size() > 1);
	}
}