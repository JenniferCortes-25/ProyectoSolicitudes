package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Engancha el repositorio JPA de usuarios con el sistema de autenticación de Spring Security.
 */
@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsServiceFromDataBase(UsuarioSecurityJpaRepository repository) {
        return username -> repository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario inexistente: " + username));
    }
}
