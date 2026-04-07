package br.gov.se.setc.logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
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
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted(ApplicationStartedEvent event) {
        String applicationName = environment.getProperty("spring.application.name", "SEFAZ Transparency Consumer");
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "DEV";
        }
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";
        userFriendlyLogger.logApplicationStart(applicationName);
        simpleLogger.success("APPLICATION", applicationName + " v" + version + " iniciada");
        markdownLogger.logSimple("Inicialização da Aplicação",
                applicationName + " v" + version + " | Perfil: " + profile);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        userFriendlyLogger.logApplicationReady();
        simpleLogger.success("APPLICATION", "Pronta para receber requisições");
        exibirLinksDisponiveis();
    }
    private void exibirLinksDisponiveis() {
        String port = environment.getProperty("server.port", "8083");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        if (contextPath == null || contextPath.trim().isEmpty()) {
            contextPath = "";
        }
        String baseUrl = "http://localhost:" + port + contextPath;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🔗 LINKS DISPONÍVEIS:");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📚 Swagger UI:        " + baseUrl + "/swagger-ui.html");
        System.out.println("📖 API Docs:          " + baseUrl + "/api-docs");
        System.out.println("🏠 Página Inicial:    " + baseUrl + "/");
        System.out.println("❤️  Health Check:      " + baseUrl + "/health");
        System.out.println("📊 Monitor de Logs:   " + baseUrl + "/logs/status");
        System.out.println("📝 Gerenciamento Logs: " + baseUrl + "/api/logs/status");
        System.out.println("⏰ Scheduler Info:    " + baseUrl + "/scheduler/info");
        System.out.println("🔑 Token Status:      " + baseUrl + "/api/token/status");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
    }
}