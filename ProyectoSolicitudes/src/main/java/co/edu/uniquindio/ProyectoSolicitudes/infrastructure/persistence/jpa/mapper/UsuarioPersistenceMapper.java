package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.EstadoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.EstadoUsuarioJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.TipoUsuarioJpa;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * Mapper MapStruct entre entidad de dominio Usuario y entidad JPA UsuarioEntity.
 * Domain → Entity: MapStruct genera la implementación automáticamente.
 * Entity → Domain: método default manual (necesita factory method reconstruirDesdeDB).
 */
@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    // ── Domain → Entity ────────────────────────────────────────────────────
    @Mapping(target = "id",           expression = "java(usuario.getId().toString())")
    @Mapping(target = "identificacion", source = "identificacion")
    @Mapping(target = "nombre",         source = "nombre")
    @Mapping(target = "email",          expression = "java(usuario.getEmail().valor())")
    @Mapping(target = "tipoUsuario",    source = "tipoUsuario")
    @Mapping(target = "estadoUsuario",  source = "estadoUsuario")
    UsuarioEntity toEntity(Usuario usuario);

    // Conversiones de enums (mismo nombre de valores → automático)
    TipoUsuarioJpa   toTipoJpa(TipoUsuario tipo);
    EstadoUsuarioJpa toEstadoJpa(EstadoUsuario estado);
    TipoUsuario      toTipoDomain(TipoUsuarioJpa tipo);
    EstadoUsuario    toEstadoDomain(EstadoUsuarioJpa estado);

    // ── Entity → Domain ────────────────────────────────────────────────────
    default Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        return Usuario.reconstruirDesdeDB(
                UUID.fromString(entity.getId()),
                entity.getIdentificacion(),
                entity.getNombre(),
                new Email(entity.getEmail()),
                toTipoDomain(entity.getTipoUsuario()),
                toEstadoDomain(entity.getEstadoUsuario())
        );
    }
}