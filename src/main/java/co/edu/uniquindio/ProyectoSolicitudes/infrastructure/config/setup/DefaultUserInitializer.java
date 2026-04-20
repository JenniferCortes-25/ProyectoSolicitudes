package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seed de datos: inserta usuarios de seguridad (ADMIN/USER) en H2 al arrancar.
 * Solo se ejecuta si la tabla está vacía.
 */
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements CommandLineRunner {

    private final DefaultUserProperties props;
    private final UsuarioSecurityJpaRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            props.getUsers().forEach(defaultUser -> {
                UsuarioSecurityEntity entity = new UsuarioSecurityEntity();
                entity.setEmail(defaultUser.username());
                entity.setPassword(encoder.encode(defaultUser.password()));
                entity.setRol(defaultUser.role());
                repository.save(entity);
            });
            System.out.println("✅ Security Seed Data finalizado en H2");
        }
    }
}
