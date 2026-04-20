package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

/**
 * Mapea las propiedades default-users.* del application.properties.
 */
@Getter
@ConfigurationProperties(prefix = "default-users")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class DefaultUserProperties {

    private final List<DefaultUser> users;

    /** Record inmutable — cada usuario del seed de datos. */
    public record DefaultUser(String username, String password, RolSeguridadEnum role) {}
}
