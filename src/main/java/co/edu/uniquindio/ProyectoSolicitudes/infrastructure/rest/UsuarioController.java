package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.usuarioRequest.CambiarPasswordRequest;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.usuarioRequest.CrearUsuarioRequest;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.usuarioResponse.UsuarioDetalleResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.usuarioResponse.UsuarioResumenResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC.*;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios")
public class UsuarioController {

    private final UsuarioMapper            mapper;
    private final CrearUsuarioUseCase      crearUsuarioUseCase;
    private final ConsultarUsuariosUseCase consultarUsuariosUseCase;
    private final ActivarUsuarioUseCase    activarUsuarioUseCase;
    private final DesactivarUsuarioUseCase desactivarUsuarioUseCase;
    private final CambiarPasswordUseCase   cambiarPasswordUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Crear un nuevo usuario (solo coordinador)")
    public ResponseEntity<UsuarioDetalleResponse> crear(
            @Valid @RequestBody CrearUsuarioRequest request) {

        Usuario usuario = crearUsuarioUseCase.ejecutar(
                request.identificacion(),
                request.nombre(),
                request.email(),
                request.tipoUsuario()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDetalleResponse(usuario));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioResumenResponse>> listar() {
        List<Usuario> usuarios = consultarUsuariosUseCase.listarTodos();
        return ResponseEntity.ok(mapper.toResumenResponseList(usuarios));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Operation(summary = "Obtener detalle de un usuario por ID")
    public ResponseEntity<UsuarioDetalleResponse> obtenerPorId(
            @PathVariable String id) {
        Usuario usuario = consultarUsuariosUseCase.obtenerPorId(UUID.fromString(id));
        return ResponseEntity.ok(mapper.toDetalleResponse(usuario));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Activar un usuario inactivo")
    public ResponseEntity<UsuarioDetalleResponse> activar(
            @PathVariable String id) {
        Usuario usuario = activarUsuarioUseCase.ejecutar(UUID.fromString(id));
        return ResponseEntity.ok(mapper.toDetalleResponse(usuario));
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Desactivar un usuario activo")
    public ResponseEntity<UsuarioDetalleResponse> desactivar(
            @PathVariable String id) {
        Usuario usuario = desactivarUsuarioUseCase.ejecutar(UUID.fromString(id));
        return ResponseEntity.ok(mapper.toDetalleResponse(usuario));
    }

    @PutMapping("/{id}/cambiar-password")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @Operation(summary = "Cambiar contraseña del usuario")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable String id,
            @Valid @RequestBody CambiarPasswordRequest request) {

        cambiarPasswordUseCase.ejecutar(
                UUID.fromString(id),
                request.passwordActual(),
                request.passwordNueva()
        );
        return ResponseEntity.noContent().build();
    }
}
