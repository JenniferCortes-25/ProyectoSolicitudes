package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializa datos de prueba al arrancar la aplicación.
 * Reemplaza el constructor de UsuarioRepositoryEnMemoria.
 * Solo carga datos si la tabla de usuarios está vacía.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!usuarioRepository.findAll().isEmpty()) {
            log.info("[DataInitializer] Datos ya existentes — omitiendo inicialización.");
            return;
        }

        log.info("[DataInitializer] Cargando usuarios iniciales...");

        usuarioRepository.save(new Usuario(
                "E-001", "Estudiante García",
                new Email("estudiante@uniquindio.edu.co"),
                TipoUsuario.ESTUDIANTE));

        usuarioRepository.save(new Usuario(
                "C-001", "Coordinador Pérez",
                new Email("coord@uniquindio.edu.co"),
                TipoUsuario.COORDINADOR));

        usuarioRepository.save(new Usuario(
                "D-001", "Docente López",
                new Email("docente@uniquindio.edu.co"),
                TipoUsuario.DOCENTE));

        usuarioRepository.save(new Usuario(
                "A-001", "Administrativo Gómez",
                new Email("admin@uniquindio.edu.co"),
                TipoUsuario.ADMINISTRATIVO));

        log.info("[DataInitializer] 4 usuarios cargados correctamente.");
    }
}
