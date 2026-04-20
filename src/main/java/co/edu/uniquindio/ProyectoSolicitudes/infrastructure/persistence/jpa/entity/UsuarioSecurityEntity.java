package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad JPA exclusiva para credenciales de seguridad (Spring Security / JWT).
 * Separada semánticamente de UsuarioEntity del dominio.
 */
@Entity
@Table(name = "usuarios_seguridad")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioSecurityEntity {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolSeguridadEnum rol;
}
