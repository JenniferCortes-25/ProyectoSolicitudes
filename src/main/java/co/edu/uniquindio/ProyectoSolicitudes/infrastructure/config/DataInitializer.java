package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa usuarios de dominio y sus credenciales de seguridad al arrancar.
 * Se ejecuta después del DefaultUserInitializer (Order 2).
 * Solo carga datos si la tabla USUARIOS está vacía.
 *
 * Contraseña por defecto de todos los usuarios de prueba: Password123
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository            usuarioRepository;
    private final UsuarioSecurityJpaRepository securityRepository;
    private final PasswordEncoder              passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!usuarioRepository.findAll().isEmpty()) {
            log.info("[DataInitializer] Datos ya existentes — omitiendo inicialización.");
            return;
        }

        log.info("[DataInitializer] Cargando usuarios iniciales...");

        crearUsuario("E-001", "Estudiante García",  "estudiante@uniquindio.edu.co", TipoUsuario.ESTUDIANTE,     RolSeguridadEnum.USER);
        crearUsuario("C-001", "Coordinador Pérez",  "coord@uniquindio.edu.co",      TipoUsuario.COORDINADOR,    RolSeguridadEnum.ADMIN);
        crearUsuario("D-001", "Docente López",       "docente@uniquindio.edu.co",    TipoUsuario.DOCENTE,        RolSeguridadEnum.USER);
        crearUsuario("A-001", "Administrativo Gómez","admin@uniquindio.edu.co",      TipoUsuario.ADMINISTRATIVO, RolSeguridadEnum.USER);

        log.info("[DataInitializer] 4 usuarios cargados. Contraseña por defecto: Password123");
    }

    private void crearUsuario(String identificacion, String nombre,
                              String email, TipoUsuario tipo, RolSeguridadEnum rol) {
        // Usuario de dominio
        usuarioRepository.save(new Usuario(identificacion, nombre, new Email(email), tipo));

        // Credenciales de seguridad (solo si no existe ya)
        if (securityRepository.findByEmail(email).isEmpty()) {
            UsuarioSecurityEntity credencial = new UsuarioSecurityEntity();
            credencial.setEmail(email);
            credencial.setPassword(passwordEncoder.encode("Password123"));
            credencial.setRol(rol);
            securityRepository.save(credencial);
        }
    }
}
