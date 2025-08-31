package unicap.juryscan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/", description = "URL padrão do servidor")})
public class JuryscanApplication {

	public static void main(String[] args) {
		SpringApplication.run(JuryscanApplication.class, args);
	}

}
