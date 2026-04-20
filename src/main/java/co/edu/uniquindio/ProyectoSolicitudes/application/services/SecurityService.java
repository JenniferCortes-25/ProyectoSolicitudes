package co.edu.uniquindio.ProyectoSolicitudes.application.services;

import co.edu.uniquindio.ProyectoSolicitudes.application.dto.request.LoginRequest;
import co.edu.uniquindio.ProyectoSolicitudes.application.dto.response.TokenResponse;

/**
 * Puerto de aplicación para operaciones de seguridad.
 * La implementación reside en infraestructura (SecurityServiceImpl).
 */
public interface SecurityService {
    TokenResponse login(LoginRequest request);
}
