package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.rest;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.authRequest.LoginRequest;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.authResponse.TokenResponse;
import co.edu.uniquindio.ProyectoSolicitudes.application.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de autenticación — expone /api/auth/login público.
 * Devuelve un Bearer Token JWT con vida media configurable.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Generación de tokens JWT")
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Retorna un Bearer Token JWT válido para consumir endpoints protegidos.")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(securityService.login(request));
    }
}
