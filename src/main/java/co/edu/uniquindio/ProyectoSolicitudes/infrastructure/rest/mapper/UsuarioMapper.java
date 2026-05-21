package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.usuarioResponse.UsuarioDetalleResponse;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.response.usuarioResponse.UsuarioResumenResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "email", expression = "java(usuario.getEmail().valor())")
    UsuarioDetalleResponse toDetalleResponse(Usuario usuario);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", expression = "java(usuario.getEmail().valor())")
    UsuarioResumenResponse toResumenResponse(Usuario usuario);

    List<UsuarioResumenResponse> toResumenResponseList(List<Usuario> usuarios);
}