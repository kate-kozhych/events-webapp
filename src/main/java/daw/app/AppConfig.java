package daw.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.Default;
import jakarta.faces.annotation.FacesConfig;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

import java.util.logging.Logger;

@FacesConfig
@Named("")
@Default
@ApplicationScoped
@FormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login.xhtml",
                errorPage = "/login.xhtml?error=true",
                useForwardToLogin = false
        )
)
public class AppConfig {

    private final Logger log = Logger.getLogger(AppConfig.class.getName());

    private final String message = "Welcome to ESNts!";

    public AppConfig() {
        log.info(">>> Application starting...");
    }

    /** Automatically called when all dependencies are satisfied */
    public void onStartup(@Observes Startup event) {
        log.info(">>> Application ready");
    }

    public String getWelcomeMessage() {
        return message;
    }
}