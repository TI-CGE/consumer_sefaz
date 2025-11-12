package br.gov.se.setc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Controller para servir o index.html com context path injetado dinamicamente
 * Garante que a aplicação funcione tanto como JAR standalone quanto como WAR no Tomcat
 */
@Controller
public class IndexController {

    /**
     * Serve o index.html com o context path correto injetado
     * Funciona tanto em JAR (context path = "") quanto em WAR (context path = "/nome-do-war")
     */
    @GetMapping(value = {"/", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index(HttpServletRequest request) throws IOException {
        // Lê o arquivo index.html original
        ClassPathResource resource = new ClassPathResource("static/index.html");
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        
        // Obtém o context path da requisição
        String contextPath = request.getContextPath();
        
        // Injeta o context path no meta tag
        // Substitui: <meta name="context-path" content="">
        // Por: <meta name="context-path" content="/nome-do-war"> (ou "" se for raiz)
        html = html.replace(
            "<meta name=\"context-path\" content=\"\">",
            "<meta name=\"context-path\" content=\"" + contextPath + "\">"
        );
        
        return html;
    }
}

