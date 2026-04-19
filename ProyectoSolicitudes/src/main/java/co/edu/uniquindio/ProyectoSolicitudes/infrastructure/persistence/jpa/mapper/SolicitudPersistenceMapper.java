package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.*;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper MapStruct entre entidad de dominio Solicitud y entidad JPA SolicitudEntity.
 *
 * Domain → Entity : MapStruct genera la implementación automáticamente
 *                   a partir de las anotaciones @Mapping.
 * Entity → Domain : método default manual, porque necesita el factory
 *                   method Solicitud.reconstruirDesdeDB() y objetos Usuario
 *                   que solo el repositorio puede resolver.
 */
@Mapper(componentModel = "spring")
public interface SolicitudPersistenceMapper {

    // ── Domain → Entity ────────────────────────────────────────────────────

    @Mapping(target = "id",              expression = "java(solicitud.getId().toString())")
    @Mapping(target = "descripcion",     expression = "java(solicitud.getDescripcion().texto())")
    @Mapping(target = "tipoSolicitud",   source = "tipo")
    @Mapping(target = "prioridad",       source = "prioridad")
    @Mapping(target = "estado",          source = "estado")
    @Mapping(target = "canalOrigen",     source = "canal")
    @Mapping(target = "solicitanteId",   expression = "java(solicitud.getSolicitanteId().toString())")
    @Mapping(target = "solicitanteNombre", source = "solicitanteNombre")
    @Mapping(target = "responsableId",   expression = "java(solicitud.getResponsable() != null ? solicitud.getResponsable().usuarioId().toString() : null)")
    @Mapping(target = "responsableNombre", expression = "java(solicitud.getResponsable() != null ? solicitud.getResponsable().nombre() : null)")
    @Mapping(target = "fechaRegistro",   source = "fechaRegistro")
    @Mapping(target = "historial",       source = "historial")
    SolicitudEntity toEntity(Solicitud solicitud);

    // Mapeo Prioridad (VO) → PrioridadEmbeddable
    @Mapping(target = "nivel",         source = "nivel")
    @Mapping(target = "justificacion", source = "justificacion")
    PrioridadEmbeddable toPrioridadEmbeddable(Prioridad prioridad);

    // Mapeo EntradaHistorial (VO) → EntradaHistorialEmbeddable
    @Mapping(target = "fechaHora",         source = "fechaHora")
    @Mapping(target = "accion",            source = "accion")
    @Mapping(target = "usuarioResponsable", source = "usuarioResponsable")
    @Mapping(target = "observaciones",     source = "observaciones")
    EntradaHistorialEmbeddable toEntradaEmbeddable(EntradaHistorial entrada);

    List<EntradaHistorialEmbeddable> toEntradaEmbeddableList(List<EntradaHistorial> lista);

    // Conversiones de enums (mismo nombre de valores → MapStruct lo resuelve automáticamente)
    EstadoSolicitudJpa  toEstadoJpa(EstadoSolicitud estado);
    NivelPrioridadJpa   toNivelJpa(NivelPrioridad nivel);
    CanalOrigenJpa      toCanalJpa(CanalOrigen canal);
    TipoSolicitudJpa    toTipoJpa(TipoSolicitud tipo);

    EstadoSolicitud     toEstadoDomain(EstadoSolicitudJpa estado);
    NivelPrioridad      toNivelDomain(NivelPrioridadJpa nivel);
    CanalOrigen         toCanalDomain(CanalOrigenJpa canal);
    TipoSolicitud       toTipoDomain(TipoSolicitudJpa tipo);

    // ── Entity → Domain (manual — lógica compleja) ─────────────────────────

    /**
     * Reconstruye la entidad de dominio Solicitud desde la entidad JPA.
     * Recibe los objetos Usuario ya resueltos por el repositorio (solo
     * los IDs están en la entidad JPA).
     *
     * @param entity      entidad JPA leída desde H2
     * @param solicitante Usuario completo del solicitante
     * @param responsable Usuario completo del responsable (puede ser null)
     */
    default Solicitud toDomain(SolicitudEntity entity,
                               Usuario solicitante,
                               Usuario responsable) {
        if (entity == null) return null;

        // 1. Reconstruir Value Objects
        DescripcionSolicitud descripcion = new DescripcionSolicitud(entity.getDescripcion());
        EstadoSolicitud      estado      = toEstadoDomain(entity.getEstado());
        CanalOrigen          canal       = toCanalDomain(entity.getCanalOrigen());
        TipoSolicitud        tipo        = entity.getTipoSolicitud() != null
                                            ? toTipoDomain(entity.getTipoSolicitud()) : null;

        Prioridad prioridad = null;
        if (entity.getPrioridad() != null
                && entity.getPrioridad().getNivel() != null) {
            prioridad = new Prioridad(
                    toNivelDomain(entity.getPrioridad().getNivel()),
                    entity.getPrioridad().getJustificacion()
            );
        }

        Responsable responsableVO = null;
        if (entity.getResponsableId() != null) {
            String nombre = entity.getResponsableNombre() != null
                    ? entity.getResponsableNombre()
                    : (responsable != null ? responsable.getNombre() : "Desconocido");
            responsableVO = new Responsable(
                    UUID.fromString(entity.getResponsableId()),
                    nombre
            );
        }

        // 2. Reconstruir historial
        List<EntradaHistorial> historial = entity.getHistorial() == null
                ? List.of()
                : entity.getHistorial().stream()
                        .map(e -> new EntradaHistorial(
                                e.getFechaHora(),
                                e.getAccion(),
                                e.getUsuarioResponsable(),
                                e.getObservaciones() != null ? e.getObservaciones() : ""))
                        .collect(Collectors.toList());

        // 3. Usar factory method del dominio
        return Solicitud.reconstruirDesdeDB(
                UUID.fromString(entity.getId()),
                descripcion,
                tipo,
                prioridad,
                estado,
                canal,
                UUID.fromString(entity.getSolicitanteId()),
                entity.getSolicitanteNombre(),
                responsableVO,
                historial,
                entity.getFechaRegistro()
        );
    }
}