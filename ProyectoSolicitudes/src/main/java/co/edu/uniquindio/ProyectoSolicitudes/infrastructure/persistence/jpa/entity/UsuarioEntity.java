package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * Entidad JPA para Usuario.
 * Vive en infrastructure — no en domain.
 * Lombok genera getters, setters y constructor vacío (requerido por JPA).
 */
@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "idx_usuario_identificacion", columnList = "identificacion", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private String id;   // UUID almacenado como VARCHAR

    @Column(name = "identificacion", nullable = false, unique = true, length = 50)
    private String identificacion;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuarioJpa tipoUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_usuario", nullable = false, length = 20)
    private EstadoUsuarioJpa estadoUsuario;
}