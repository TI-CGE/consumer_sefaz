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
                        .description("API para consumo de dados do sistema de transparência da SEFAZ Sergipe. " +
                                   "Esta API permite o consumo automatizado de dados de contratos, empenhos, " +
                                   "liquidações, pagamentos e outras informações de transparência fiscal. " +
                                   "Os dados são consumidos das APIs oficiais da SEFAZ e armazenados localmente " +
                                   "para consulta e análise.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe SETC - Secretaria de Estado da Transparência e Controle")
                                .email("setc@se.gov.br")
                                .url("https://www.se.gov.br/setc")
                        )
                        .license(new License()
                                .name("Governo do Estado de Sergipe")
                                .url("https://www.se.gov.br")
                        )
                );
    }
}