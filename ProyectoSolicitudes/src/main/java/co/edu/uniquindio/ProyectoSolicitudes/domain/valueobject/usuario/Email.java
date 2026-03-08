package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.EmailInvalidoException;

/**
 * Correo electrónico institucional de un usuario.
 * Es inmutable y valida el formato en el constructor.
 *
 * Regla de negocio: RN-09 — debe tener formato válido (ej: usuario@dominio.com).
 */
public record Email(String valor) {
    public Email {
        if (valor == null || !valor.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$"))
            throw new EmailInvalidoException();
    }
}