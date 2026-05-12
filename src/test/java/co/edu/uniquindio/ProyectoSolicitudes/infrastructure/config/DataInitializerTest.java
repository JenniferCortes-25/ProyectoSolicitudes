package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config;

import co.edu.uniquindio.ProyectoSolicitudes.domain.entity.Usuario;
import co.edu.uniquindio.ProyectoSolicitudes.domain.repository.UsuarioRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  1. No carga datos si la tabla ya tiene usuarios
 *  2. Crea exactamente 4 usuarios al arrancar con tabla vacía
 *  3. Cada usuario tiene su entrada correspondiente en USUARIOS_SEGURIDAD
 *
 *  - @SpringBootTest levanta el contexto completo (H2 + DataInitializer corre solo).
 *  - @DirtiesContext garantiza BD limpia entre tests: el contexto se recrea
 *    antes de cada método, disparando el runner de nuevo.
 *  - El test "no carga si ya hay datos" verifica que el runner es idempotente:
 *    el contexto arranca, el DataInitializer carga los 4 usuarios, y si se
 *    volviera a ejecutar (simulado llamándolo directamente) no agrega más.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DataInitializerTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioSecurityJpaRepository securityRepository;

    @Autowired
    private DataInitializer dataInitializer;

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 1: No carga datos si la tabla ya tiene usuarios
    //
    // El DataInitializer ya se ejecutó al arrancar el contexto (cargó 4 usuarios).
    // Volvemos a llamarlo manualmente: debe detectar que la tabla no está vacía
    // y NO agregar más registros.
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void noDeberiaCargarMasDatosSiTablaYaTieneUsuarios() throws Exception {
        // [ARRANGE] — el contexto arrancó y ya hay usuarios
        int cantidadAntes = usuarioRepository.findAll().size();
        assertTrue(cantidadAntes > 0,
                "Precondición: debe haber usuarios después del arranque");

        // [ACT] — ejecutar el initializer de nuevo manualmente
        dataInitializer.run(null);

        // [ASSERT] — el total no debe cambiar
        int cantidadDespues = usuarioRepository.findAll().size();
        assertEquals(cantidadAntes, cantidadDespues,
                "No debe agregar usuarios si la tabla ya tiene datos");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 2: Crea exactamente 4 usuarios al arrancar con tabla vacía
    //
    // Como cada test tiene contexto limpio (BEFORE_EACH), el DataInitializer
    // ya corrió y creó los 4 usuarios. Verificamos la cuenta directamente.
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void deberiaCrearExactamente4UsuariosAlArrancarConTablaVacia() {
        // [ACT] — el DataInitializer ya corrió en el arranque del contexto

        // [ASSERT]
        List<Usuario> usuarios = usuarioRepository.findAll();
        assertEquals(4, usuarios.size(),
                "DataInitializer debe crear exactamente 4 usuarios");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 3: Cada usuario tiene su entrada correspondiente en USUARIOS_SEGURIDAD
    //
    // Verifica que por cada usuario de dominio exista una credencial de seguridad
    // con el mismo email, y que la contraseña esté almacenada en forma encriptada
    // (no como texto plano "Password123").
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void cadaUsuarioDeberiaExistirEnUsuariosSeguridad() {
        // [ARRANGE] — emails que el DataInitializer crea
        List<String> emailsEsperados = List.of(
                "estudiante@uniquindio.edu.co",
                "coord@uniquindio.edu.co",
                "docente@uniquindio.edu.co",
                "admin@uniquindio.edu.co"
        );

        // [ACT + ASSERT] — cada email debe tener credencial en USUARIOS_SEGURIDAD
        for (String email : emailsEsperados) {
            var credencial = securityRepository.findByEmail(email);

            assertTrue(credencial.isPresent(),
                    "Debe existir credencial de seguridad para: " + email);

            // La contraseña no debe estar en texto plano
            assertNotEquals("Password123", credencial.get().getPassword(),
                    "La contraseña debe estar encriptada para: " + email);

            // Debe tener el prefijo de BCrypt delegado
            assertTrue(credencial.get().getPassword().startsWith("{bcrypt}"),
                    "La contraseña debe ser un hash BCrypt para: " + email);
        }

        // Además debe haber exactamente 4 entradas en USUARIOS_SEGURIDAD
        // (las del DataInitializer — el DefaultUserInitializer crea las suyas aparte
        //  pero en test esas se desactivan por application.properties de test)
        long totalSeguridad = securityRepository.findAll().stream()
                .filter(e -> emailsEsperados.contains(e.getEmail()))
                .count();
        assertEquals(4, totalSeguridad,
                "Deben existir exactamente 4 entradas en USUARIOS_SEGURIDAD para los usuarios del DataInitializer");
    }
}