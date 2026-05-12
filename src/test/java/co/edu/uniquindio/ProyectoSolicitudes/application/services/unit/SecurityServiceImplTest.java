package co.edu.uniquindio.ProyectoSolicitudes.application.services.unit;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.test.UsuarioSecurityTestDataLoader;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.dto.request.authRequest.LoginRequest;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.JwtTokenProvider;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.security.services.SecurityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private JwtTokenProvider jwtProviderMock;

    @Mock
    private AuthenticationManager authManagerMock;

    @InjectMocks
    private SecurityServiceImpl securityService;

    private UsuarioSecurityEntity baseUser;

    @BeforeEach
    void setUp() {
        baseUser = UsuarioSecurityTestDataLoader.userBase();
    }

    @Test
    void testLoginRetornaTokenResponseValido() {
        // [ARRANGE] — fingimos respuestas de dependencias
        Authentication dummyAuth = new UsernamePasswordAuthenticationToken(
                baseUser.getEmail(),
                "userpass",
                List.of(new SimpleGrantedAuthority(baseUser.getRol().name()))
        );

        when(authManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(dummyAuth);

        when(jwtProviderMock.generateTokenAsString(anyString(), anyList(), any(Instant.class), any(Instant.class)))
                .thenReturn("eyJmakeTokenFake");

        // [ACT]
        var response = securityService.login(new LoginRequest(baseUser.getEmail(), "userpass"));

        // [ASSERT]
        assertNotNull(response);
        assertEquals("Bearer", response.type());
        assertEquals("eyJmakeTokenFake", response.token());

        // Verificamos que sí llamó al authManager exactamente una vez
        verify(authManagerMock, times(1)).authenticate(any());
        verify(jwtProviderMock, times(1))
                .generateTokenAsString(anyString(), anyList(), any(), any());
    }

    @Test
    void testLoginPropagaExcepcionCuandoCredencialesInvalidas() {
        // [ARRANGE]
        when(authManagerMock.authenticate(any()))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // [ASSERT]
        assertThrows(RuntimeException.class, () ->
                securityService.login(new LoginRequest("malo@test.com", "clavemal"))
        );
    }
}