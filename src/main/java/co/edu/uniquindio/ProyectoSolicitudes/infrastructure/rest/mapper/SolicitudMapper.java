package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest.mapper;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse.EventoHistorialResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse.SolicitudDetalleResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.solicitudResponse.SolicitudResumenResponse;
import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.DescripcionSolicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EntradaHistorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper que convierte entidades del dominio a DTOs de respuesta.
 * MapStruct genera la implementación automáticamente en tiempo de compilación.
 *
 * Nunca retornar entidades del dominio directamente desde el Controller —
 * siempre pasar por este mapper primero.
 */
@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    /**
     * Convierte una Solicitud completa a su vista de detalle.
     * Usado en GET /api/solicitudes/{id}
     */
    @Mapping(target = "descripcion",           source = "descripcion",    qualifiedByName = "descripcionToString")
    @Mapping(target = "canalOrigen",           source = "canal")
    @Mapping(target = "solicitanteId",         expression = "java(solicitud.getSolicitanteId().toString())")
    @Mapping(target = "solicitanteNombre",     ignore = true)
    @Mapping(target = "responsableId",         expression = "java(solicitud.getResponsable() != null ? solicitud.getResponsable().usuarioId().toString() : null)")
    @Mapping(target = "responsableNombre",     expression = "java(solicitud.getResponsable() != null ? solicitud.getResponsable().nombre() : null)")
    @Mapping(target = "nivelPrioridad",        expression = "java(solicitud.getPrioridad() != null ? solicitud.getPrioridad().nivel().name() : null)")
    @Mapping(target = "justificacionPrioridad",expression = "java(solicitud.getPrioridad() != null ? solicitud.getPrioridad().justificacion() : null)")
    @Mapping(target = "totalEntradaHistorial", expression = "java(solicitud.getHistorial().size())")
    SolicitudDetalleResponse toDetalleResponse(Solicitud solicitud);

    /**
     * Convierte una Solicitud a su vista resumida para listados.
     * Usado en GET /api/solicitudes
     */
    @Mapping(target = "descripcionBreve",  expression = "java(solicitud.getDescripcion().texto().substring(0, Math.min(80, solicitud.getDescripcion().texto().length())))")
    @Mapping(target = "nivelPrioridad",    expression = "java(solicitud.getPrioridad() != null ? solicitud.getPrioridad().nivel().name() : null)")
    @Mapping(target = "solicitanteNombre", ignore = true)
    @Mapping(target = "responsableNombre", expression = "java(solicitud.getResponsable() != null ? solicitud.getResponsable().nombre() : null)")
    SolicitudResumenResponse toResumenResponse(Solicitud solicitud);

    /**
     * Convierte una lista de Solicitudes a lista de resúmenes.
     */
    List<SolicitudResumenResponse> toResumenResponseList(List<Solicitud> solicitudes);

    /**
     * Convierte una EntradaHistorial a su DTO de respuesta.
     */
    EventoHistorialResponse toEventoResponse(EntradaHistorial entrada);

    /**
     * Convierte una lista de EntradaHistorial a lista de DTOs.
     */
    List<EventoHistorialResponse> toEventoResponseList(List<EntradaHistorial> entradas);

    /**
     * Método auxiliar para convertir DescripcionSolicitud a String.
     * MapStruct no sabe hacerlo automáticamente porque es un record.
     */
    @Named("descripcionToString")
    default String descripcionToString(DescripcionSolicitud descripcion) {
        return descripcion != null ? descripcion.texto() : null;
    }
}