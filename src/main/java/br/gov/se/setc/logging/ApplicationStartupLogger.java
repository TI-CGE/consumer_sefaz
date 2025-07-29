package br.gov.se.setc.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Listener para capturar eventos de inicialização da aplicação
 * e registrar no log mestre
 */
@Component
public class ApplicationStartupLogger {

    private final SimpleLogger simpleLogger;
    private final MarkdownLogger markdownLogger;
    private final UserFriendlyLogger userFriendlyLogger;
    private final Environment environment;

    @Autowired
    public ApplicationStartupLogger(SimpleLogger simpleLogger, MarkdownLogger markdownLogger,
                                  UserFriendlyLogger userFriendlyLogger, Environment environment) {
        this.simpleLogger = simpleLogger;
        this.markdownLogger = markdownLogger;
        this.userFriendlyLogger = userFriendlyLogger;
        this.environment = environment;
    }
    
    /**
     * Captura o evento de aplicação iniciada
     */
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted(ApplicationStartedEvent event) {
        String applicationName = environment.getProperty("spring.application.name", "SEFAZ Transparency Consumer");
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "DEV";
        }
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";

        // Log simples para usuário
        userFriendlyLogger.logApplicationStart(applicationName);

        // Log simplificado
        simpleLogger.success("APPLICATION", applicationName + " v" + version + " iniciada");

        // Log estruturado em markdown
        markdownLogger.logSimple("Inicialização da Aplicação",
                applicationName + " v" + version + " | Perfil: " + profile);
    }

    /**
     * Captura o evento de aplicação pronta
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        // Log simples para usuário
        userFriendlyLogger.logApplicationReady();

        // Log simplificado
        simpleLogger.success("APPLICATION", "Pronta para receber requisições");
    }
}
