package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

/**
 * Genera el token JWT firmado con HMAC/HS256.
 * Embebe claims: issuer, subject, roles, iat, exp.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder encoder;

    public String generateTokenAsString(String username, Collection<String> roles,
                                        Instant issuedAt, Instant expiresAt) {
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("solicitudes-app")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("roles", roles)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
