package br.gov.se.setc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desenvolvimento")
                ))
                .info(new Info()
                        .title("SEFAZ Transparency Consumer API")
                        .description("API para consumo e teste do sistema de transparência SEFAZ")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe SETC")
                                .email("setc@se.gov.br")
                        )
                        .license(new License()
                                .name("Governo do Estado de Sergipe")
                        )
                );
    }
}
