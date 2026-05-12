package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC;

import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: Cambiar la contraseña de un usuario.
 * Verifica que la contraseña actual sea correcta antes de actualizar.
 */
@Service
@RequiredArgsConstructor
public class CambiarPasswordUseCase {

    private final UsuarioRepository            usuarioRepository;
    private final UsuarioSecurityJpaRepository securityRepository;
    private final PasswordEncoder              passwordEncoder;

    @Transactional
    public void ejecutar(UUID usuarioId, String passwordActual, String passwordNueva) {

        // Buscar usuario de dominio para obtener su email
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe usuario con ID: " + usuarioId));

        String email = usuario.getEmail().valor();

        // Buscar credenciales de seguridad por email
        UsuarioSecurityEntity credencial = securityRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe cuenta de seguridad para: " + email));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, credencial.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }

        // Actualizar con la nueva contraseña encriptada
        credencial.setPassword(passwordEncoder.encode(passwordNueva));
        securityRepository.save(credencial);
    }
}
