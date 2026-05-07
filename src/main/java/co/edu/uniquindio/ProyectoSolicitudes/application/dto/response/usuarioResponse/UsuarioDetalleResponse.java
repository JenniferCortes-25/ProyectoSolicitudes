package co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.usuarioResponse;

import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;

import java.util.UUID;

/**
 * Vista completa de un usuario
 * Usado en: GET /api/usuarios/{id}, POST /api/usuarios, 
 *           PUT /api/usuarios/{id}/activar, PUT /api/usuarios/{id}/desactivar
 * Incluye email porque es detalle individual
 */
public record UsuarioDetalleResponse (
    UUID id,
    String identificacion,
    String nombre,
    String email,
    TipoUsuario tipoUsuario,
    EstadoUsuario estadoUsuario
) {}
