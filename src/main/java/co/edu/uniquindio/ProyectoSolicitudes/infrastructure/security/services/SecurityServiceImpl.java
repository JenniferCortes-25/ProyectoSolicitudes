package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.services;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.LoginRequest;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.TokenResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.services.SecurityService;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Implementación de SecurityService.
 * Reside en infraestructura porque depende de AuthenticationManager y JwtTokenProvider.
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiry}")
    private long expiry;

    @Override
    public TokenResponse login(LoginRequest request) {
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        final var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        final var now = Instant.now();
        final var expire = now.plus(expiry, ChronoUnit.MINUTES);

        return new TokenResponse(
                jwtTokenProvider.generateTokenAsString(authentication.getName(), roles, now, expire),
                "Bearer",
                expire,
                roles
        );
    }
}
