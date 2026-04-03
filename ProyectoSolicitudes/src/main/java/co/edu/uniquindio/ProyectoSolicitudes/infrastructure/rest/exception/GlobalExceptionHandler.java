package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.exception;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * RN-04 / RN-10 — Usuario inactivo no puede recibir asignaciones.
     * → 400 Bad Request
     */
    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioInactivo(
            UsuarioInactivoException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * RN-05 — No se puede iniciar atención sin responsable.
     * → 400 Bad Request
     */
    @ExceptionHandler(SinResponsableException.class)
    public ResponseEntity<Map<String, Object>> handleSinResponsable(
            SinResponsableException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * Permisos insuficientes para la operación.
     * → 403 Forbidden
     */
    @ExceptionHandler(PermisoInsuficienteException.class)
        public ResponseEntity<Map<String, Object>> handlePermisoInsuficiente(
            PermisoInsuficienteException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    /**
     * Usuario o solicitud no encontrados.
     * → 404 Not Found
     */
    @ExceptionHandler(UsuarioNoEncontradoException.class)
     public ResponseEntity<Map<String, Object>> handleNoEncontrado(
            UsuarioNoEncontradoException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * Cualquier otra excepción no controlada.
     * → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor", null);
    }

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