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

@Configuration
public class OpenApiConfig {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        String basePath = (contextPath == null || contextPath.isEmpty()) ? "" : contextPath;
        String serverUrl = basePath.isEmpty() ? "/" : basePath.endsWith("/") ? basePath : basePath + "/";
        List<Server> servers = List.of(
                new Server().url(serverUrl).description("Servidor atual"));
        return new OpenAPI()
                .servers(servers)
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
                                .url("https://www.se.gov.br/setc"))
                        .license(new License()
                                .name("Governo do Estado de Sergipe")
                                .url("https://www.se.gov.br")));
    }
}