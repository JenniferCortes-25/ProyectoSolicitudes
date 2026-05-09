package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.exception;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 * Traduce las excepciones del dominio a respuestas HTTP apropiadas.
 *
 * El Controller nunca maneja excepciones directamente —
 * todas pasan por aquí automáticamente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ══════════════════════════════════════════════════════════════════════
    // VALIDACIÓN DE ENTRADA
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Errores de validación Bean Validation (@Valid en el Controller).
     * → 400 Bad Request con detalle de cada campo inválido.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", errores);
    }

    // ══════════════════════════════════════════════════════════════════════
    // EXCEPCIONES DE DOMINIO — SOLICITUD
    // ══════════════════════════════════════════════════════════════════════

    /**
     * RN-01 — Solicitud cerrada no puede modificarse.
     * → 400 Bad Request
     */
    @ExceptionHandler(SolicitudCerradaException.class)
    public ResponseEntity<Map<String, Object>> handleSolicitudCerrada(
            SolicitudCerradaException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * RN-02 — Transición de estado inválida.
     * → 400 Bad Request
     */
    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleTransicionInvalida(
            TransicionInvalidaException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * RN-05 — No se puede iniciar atención sin responsable asignado.
     * → 400 Bad Request
     */
    @ExceptionHandler(SinResponsableException.class)
    public ResponseEntity<Map<String, Object>> handleSinResponsable(
            SinResponsableException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * Solicitud no encontrada por su ID.
     * → 404 Not Found
     */
    @ExceptionHandler(SolicitudNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSolicitudNoEncontrada(
            SolicitudNoEncontradaException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // ══════════════════════════════════════════════════════════════════════
    // EXCEPCIONES DE DOMINIO — USUARIO
    // ══════════════════════════════════════════════════════════════════════

    /**
     * RN-04 / RN-10 — Usuario inactivo no puede recibir asignaciones.
     * → 400 Bad Request
     */
    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioInactivo(
            UsuarioInactivoException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * Usuario ya está en el estado que se intenta aplicar.
     * Lanzado por Usuario.activar() si ya es ACTIVO, o desactivar() si ya es INACTIVO.
     * → 409 Conflict (el recurso existe pero su estado impide la operación)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(
            IllegalStateException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // ══════════════════════════════════════════════════════════════════════
    // NO ENCONTRADO
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Usuario no encontrado por ID o identificación.
     * → 404 Not Found
     */
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * UUID con formato inválido en el path, o duplicado de identificación al crear usuario.
     * → 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // ══════════════════════════════════════════════════════════════════════
    // AUTORIZACIÓN Y AUTENTICACIÓN
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Permisos de dominio insuficientes para la operación.
     * → 403 Forbidden
     */
    @ExceptionHandler(PermisoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handlePermisoInsuficiente(
            PermisoInsuficienteException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    /**
     * Credenciales incorrectas durante el login.
     * → 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", null);
    }

    /**
     * Cualquier otro fallo de autenticación de Spring Security.
     * → 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "No autenticado: " + ex.getMessage(), null);
    }

    /**
     * Acceso denegado por roles insuficientes (@PreAuthorize).
     * → 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                "No tienes permisos para realizar esta acción", null);
    }

    // ══════════════════════════════════════════════════════════════════════
    // FALLBACK GENÉRICO
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Cualquier otra excepción no controlada.
     * → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor", null);
    }

    // ══════════════════════════════════════════════════════════════════════
    // UTILIDAD
    // ══════════════════════════════════════════════════════════════════════

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String mensaje, Map<String, String> errores) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", mensaje);
        if (errores != null) response.put("errors", errores);

        return ResponseEntity.status(status).body(response);
    }
}