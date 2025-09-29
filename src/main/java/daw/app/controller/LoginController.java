package daw.app.controller;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import daw.app.model.User;
import daw.app.model.dao.UserDAO;
import daw.app.qualifiers.DAOJpa;
import daw.app.services.AuthService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ViewScoped
@Named
public class LoginController implements Serializable {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @Inject
    FacesContext fc;

    @Inject
    SecurityContext sc;

    @Inject
    ExternalContext ec;

    @Inject
    HttpServletRequest request;

    @Inject
    private AuthService authService;

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Inject @DAOJpa
    private UserDAO userDAO;

    public String login() {
        String view = "";

        if (username == null || username.trim().isEmpty()) {
            fc.addMessage("loginForm:username", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Email is required", "Please enter your email address"));
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            fc.addMessage("loginForm:password", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Password is required", "Please enter your password"));
            return null;
        }

        // First check if user exists
        User user = userDAO.findByLogin(username);
        if (user == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid credentials", "No account exists with this email address"));
            return null;
        }

        // Checking with AuthService
        if (authService.verifyPassword(password, user.getPassword())) {
            // Prepare data for programmatic authentication
            AuthenticationParameters ap = new AuthenticationParameters();
            Credential credentials = new UsernamePasswordCredential(username, password);

            ap.credential(credentials).newAuthentication(true);

            HttpServletResponse response = (HttpServletResponse) ec.getResponse();

            // Programmatic authentication
            if (sc.authenticate(request, response, ap) == AuthenticationStatus.SUCCESS) {
                // Store user in session for access in other pages
                request.getSession().setAttribute("user", user);
                view = "userlist?faces-redirect=true";
                logger.log(Level.INFO, "User authenticated: " + username);
            } else {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Authentication failed", "Invalid password"));
                logger.log(Level.WARNING, "Authentication error for: " + username);
            }
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Authentication failed", "Invalid password"));
            logger.log(Level.WARNING, "Password verification failed for: " + username);
        }

        return view;
    }

    public String logout() throws ServletException {
        request.logout();
        request.getSession().invalidate();
        return "login?faces-redirect=true";
    }
}