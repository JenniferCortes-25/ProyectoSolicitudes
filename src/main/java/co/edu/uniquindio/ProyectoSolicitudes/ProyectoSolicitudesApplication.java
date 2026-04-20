package co.edu.uniquindio.ProyectoSolicitudes;

import co.edu.uniquindio.ProyectoSolicitudes.infrastructure.config.setup.DefaultUserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DefaultUserProperties.class)
public class ProyectoSolicitudesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoSolicitudesApplication.class, args);
	}
}
