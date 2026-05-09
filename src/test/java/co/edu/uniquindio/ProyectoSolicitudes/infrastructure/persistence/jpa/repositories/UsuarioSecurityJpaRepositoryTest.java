package co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.repositories;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.test.UsuarioSecurityTestDataLoader;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.UsuarioSecurityJpaRepository;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.persistence.jpa.entity.UsuarioSecurityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioSecurityJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioSecurityJpaRepository repositorio;

    private UsuarioSecurityEntity testAdmin;

    @BeforeEach
    void setUp() {
        repositorio.deleteAll();
        testAdmin = UsuarioSecurityTestDataLoader.adminBase();
        entityManager.persistAndFlush(testAdmin);
    }

    @Test
    void testBuscarPorEmailExistente() {
        // [ACT]
        var resultado = repositorio.findByEmail("admin@test.com");

        // [ASSERT]
        assertTrue(resultado.isPresent());
        assertEquals(RolSeguridadEnum.ADMIN, resultado.get().getRol());
    }

    @Test
    void testBuscarPorEmailInexistente() {
        var resultado = repositorio.findByEmail("fantasma@test.com");
        assertTrue(resultado.isEmpty());
    }
}