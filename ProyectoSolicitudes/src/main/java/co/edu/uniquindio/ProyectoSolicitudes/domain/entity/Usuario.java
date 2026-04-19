package co.edu.uniquindio.ProyectoSolicitudes.domain.entity;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;

import java.util.UUID;

public class Usuario {
    private UUID id;
    private String identificacion;
    private String nombre;
    private Email email;
    private TipoUsuario tipoUsuario;
    private EstadoUsuario estadoUsuario; // inicia en ACTIVO

    // ─── Constructor ──────────────────────────────────────────────────────────

    public Usuario(String identificacion, String nombre, Email email, TipoUsuario tipoUsuario) {
        if (identificacion == null || identificacion.isBlank())
            throw new IllegalArgumentException("La identificación es obligatoria");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (email == null)
            throw new IllegalArgumentException("El email es obligatorio");
        if (tipoUsuario == null)
            throw new IllegalArgumentException("El tipo de usuario es obligatorio");

        this.id            = UUID.randomUUID();
        this.identificacion = identificacion;
        this.nombre        = nombre;
        this.email         = email;
        this.tipoUsuario   = tipoUsuario;
        this.estadoUsuario = EstadoUsuario.ACTIVO; // siempre inicia ACTIVO
    }

    /**
     * Reactiva un usuario inactivo.
     * Solo aplica si el estado actual es INACTIVO.
     */
    public void activar() {
        if (this.estadoUsuario == EstadoUsuario.ACTIVO) {
            throw new IllegalStateException("El usuario ya está ACTIVO");
        }
        this.estadoUsuario = EstadoUsuario.ACTIVO;
    }

    /**
     * Desactiva un usuario activo.
     * Una vez inactivo no puede recibir nuevas asignaciones.
     */
    public void desactivar() {
        if (this.estadoUsuario == EstadoUsuario.INACTIVO) {
            throw new IllegalStateException("El usuario ya está INACTIVO");
        }
        this.estadoUsuario = EstadoUsuario.INACTIVO;
    }

    /**
     * Retorna true si el usuario puede operar en el sistema.
     * Se consulta antes de asignar responsable (RN-04).
     */
    public boolean estaActivo() {
        return this.estadoUsuario == EstadoUsuario.ACTIVO;
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     * Usado para autorización de operaciones (RN-13).
     */
    public boolean tieneTipo(TipoUsuario tipoUsuario) {
        return this.tipoUsuario == tipoUsuario;
    }

    public UUID getId()                  { return id; }
    public String getIdentificacion()    { return identificacion; }
    public String getNombre()            { return nombre; }
    public Email getEmail()              { return email; }
    public TipoUsuario getTipoUsuario()  { return tipoUsuario; }
    public EstadoUsuario getEstadoUsuario() { return estadoUsuario; }

    // ─── Factory method de reconstrucción ─────────────────────────────────────

    /**
     * Reconstruye un Usuario desde la BD sin pasar por validaciones de creación.
     * Solo debe usarse desde el mapper de persistencia.
     */
    public static Usuario reconstruirDesdeDB(
            UUID id,
            String identificacion,
            String nombre,
            Email email,
            TipoUsuario tipoUsuario,
            EstadoUsuario estadoUsuario) {

        Usuario u = new Usuario();
        u.id             = id;
        u.identificacion = identificacion;
        u.nombre         = nombre;
        u.email          = email;
        u.tipoUsuario    = tipoUsuario;
        u.estadoUsuario  = estadoUsuario;
        return u;
    }

    /** Constructor vacío privado — solo para uso del factory method reconstruirDesdeDB. */
    private Usuario() {}
}
