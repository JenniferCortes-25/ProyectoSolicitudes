package co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario;

import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.EmailInvalidoException;

public record Email(String valor) {
    public Email {
        if (valor == null || !valor.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$"))
            throw new EmailInvalidoException();
    }
}