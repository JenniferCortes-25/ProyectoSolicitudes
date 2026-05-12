package co.edu.uniquindio.ProyectoSolicitudes.application.usecase.usuarioUC;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.Email;
import co.edu.uniquindio.ProyectoSolicitudes.domain.valueobject.usuario.TipoUsuario;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Crear un nuevo usuario en el sistema.
 * Solo puede ser ejecutado por un COORDINADOR / ADMIN (RN-13).
 * Además de persistir el usuario en el dominio, crea su entrada
 * de seguridad (email + contraseña encriptada + rol) para que
 * pueda autenticarse con JWT.
 */
@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCase {

    private final UsuarioRepository             usuarioRepository;
    private final UsuarioSecurityJpaRepository  securityRepository;
    private final PasswordEncoder               passwordEncoder;

    /**
     * Contraseña inicial por defecto. En un sistema real se enviaría
     * al correo del usuario o se obligaría a cambiarla en el primer login.
     */
    private static final String PASSWORD_DEFAULT = "Password123";

    @Transactional
    public Usuario ejecutar(String identificacion,
                            String nombre,
                            String emailValor,
                            TipoUsuario tipoUsuario) {

        // ── Validar duplicado por identificación ────────────────────────────
        if (usuarioRepository.findByIdentificacion(identificacion).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con identificación: " + identificacion);
        }

        // ── Validar duplicado por email en seguridad ────────────────────────
        if (securityRepository.findByEmail(emailValor).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario de seguridad con email: " + emailValor);
        }

        // ── Crear usuario de dominio ────────────────────────────────────────
        Usuario usuario = new Usuario(
                identificacion,
                nombre,
                new Email(emailValor),
                tipoUsuario
        );
        Usuario guardado = usuarioRepository.save(usuario);

        // ── Crear credenciales de seguridad ────────────────────────────────
        // El rol ADMIN aplica a COORDINADOR (puede gestionar el sistema).
        // Cualquier otro tipo de usuario recibe rol USER.
        RolSeguridadEnum rol = (tipoUsuario == TipoUsuario.COORDINADOR)
                ? RolSeguridadEnum.ADMIN
                : RolSeguridadEnum.USER;

        UsuarioSecurityEntity credencial = new UsuarioSecurityEntity();
        credencial.setEmail(emailValor);
        credencial.setPassword(passwordEncoder.encode(PASSWORD_DEFAULT));
        credencial.setRol(rol);
        securityRepository.save(credencial);

        return guardado;
    }
}
