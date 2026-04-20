package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioEntity;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTADOR: implementa el puerto UsuarioRepository del dominio
 * usando H2 + Spring Data JPA como tecnología de persistencia.
 *
 * @Primary hace que Spring inyecte ESTA implementación en los Use Cases,
 * dejando UsuarioRepositoryEnMemoria disponible solo si se necesita.
 */
@Repository
@Primary
@Transactional
@RequiredArgsConstructor
public class UsuarioJpaRepository implements UsuarioRepository {

    private final UsuarioJpaDataRepository dataRepository;
    private final UsuarioPersistenceMapper  mapper;

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity    = mapper.toEntity(usuario);
        UsuarioEntity guardado  = dataRepository.save(entity);
        return mapper.toDomain(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(UUID id) {
        return dataRepository.findById(id.toString())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return dataRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        dataRepository.deleteById(id.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return dataRepository.existsById(id.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByIdentificacion(String identificacion) {
        return dataRepository.findByIdentificacion(identificacion)
                .map(mapper::toDomain);
    }
}