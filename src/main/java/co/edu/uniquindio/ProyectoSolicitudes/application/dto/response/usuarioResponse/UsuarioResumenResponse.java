package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.usuarioResponse;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;

import java.util.UUID;

/**
 * Vista completa de un usuario para listados.
 * Usado en: GET /api/usuarios
 * NO Incluye email porque en un listado no es necesario
 */
public record UsuarioResumenResponse (
    UUID id,
    String identificacion,
    String nombre,
    TipoUsuario tipoUsuario,
    EstadoUsuario estadoUsuario
) {}