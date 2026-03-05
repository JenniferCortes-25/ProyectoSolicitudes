package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.valueobject.usuario.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.proyecto.domain.valueobject.usuario.TipoUsuario;

import java.util.UUID;

public class Usuario {
    private UUID id;
    private String identificacion;
    private String nombre;
    private Email email;
    private TipoUsuario tipoUsuario;
    private EstadoUsuario estadoUsuario; // inicia en ACTIVO

    public void activar() {

    }

    public void desactivar() {

    }

    public boolean estaActivo() {
        return false;
    }

    public boolean tieneTipo(TipoUsuario tipoUsuario) {
        return false;
    }
}
