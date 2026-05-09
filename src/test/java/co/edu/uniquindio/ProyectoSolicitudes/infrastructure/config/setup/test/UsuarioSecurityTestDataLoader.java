package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.test;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsuarioSecurityTestDataLoader {

    public static final PasswordEncoder encoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /** Credencial de ADMIN lista para guardar en BD de prueba */
    public static UsuarioSecurityEntity adminBase() {
        UsuarioSecurityEntity admin = new UsuarioSecurityEntity();
        admin.setEmail("admin@test.com");
        admin.setPassword(encoder.encode("adminpass"));
        admin.setRol(RolSeguridadEnum.ADMIN);
        return admin;
    }

    /** Credencial de USER lista para guardar en BD de prueba */
    public static UsuarioSecurityEntity userBase() {
        UsuarioSecurityEntity user = new UsuarioSecurityEntity();
        user.setEmail("user@test.com");
        user.setPassword(encoder.encode("userpass"));
        user.setRol(RolSeguridadEnum.USER);
        return user;
    }
}