package co.edu.uniquindio.ProyectoSolicitudes.application.usecase;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Solicitud;
import co.edu.uniquindio.ProyectoSolicitudes.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.SolicitudRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.solicitud.EstadoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso: Consultas de solo lectura sobre solicitudes.
 * Guía 09 — @Transactional(readOnly = true) optimiza rendimiento en consultas.
 */
@Service
@RequiredArgsConstructor
public class ConsultarSolicitudesUseCase {

    private final SolicitudRepository solicitudRepository;

    /**
     * Obtiene una solicitud por su ID.
     * readOnly = true porque no modifica estado.
     */
    @Transactional(readOnly = true)
    public Solicitud obtenerPorId(UUID id) {
        return solicitudRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe solicitud con ID: " + id));
    }

    /**
     * Lista todas las solicitudes, con filtro opcional por estado.
     * readOnly = true porque no modifica estado.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> listar(Optional<EstadoSolicitud> estado) {
        return estado
                .map(solicitudRepository::findByEstado)
                .orElseGet(solicitudRepository::findAll);
    }

    /**
     * Lista solicitudes activas (no CERRADAS) de forma paginada.
     * @param pagina  número de página base 0 (0 = primera página)
     * @param tamanio cantidad de elementos por página
     * @return Page con los resultados y metadatos de paginación
     */
    @Transactional(readOnly = true)
    public Page<Solicitud> listarPaginado(int pagina, int tamanio) {
 
    // Construye el objeto Pageable con ordenamiento por fecha descendente
    Pageable paginaSolicitada = PageRequest.of(
            pagina,
            tamanio,
            Sort.by("fechaRegistro").descending()
        );
 
    // Excluye las CERRADAS — equivale al 'estado distinto a ELIMINADO' de la guía
    return solicitudRepository.findByEstadoNot(
            EstadoSolicitud.CERRADA,
            paginaSolicitada
        );
    }




}