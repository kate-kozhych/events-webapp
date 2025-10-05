package daw.app.controller;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import daw.app.dto.UserDTO;
import daw.app.mapper.UserMapper;
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

    @Inject
    @DAOJpa
    private UserDAO userDAO;

    @Inject
    private UserMapper userMapper;

    private String username;
    private String password;

    public String login() {
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

        User user = userDAO.findByLogin(username);
        if (user == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid credentials", "No account exists with this email address"));
            logger.log(Level.WARNING, "Login attempt with non-existent email: {0}", username);
            return null;
        }

        if (!authService.verifyPassword(password, user.getPassword())) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Authentication failed", "Invalid password"));
            logger.log(Level.WARNING, "Failed password verification for: {0}", username);
            return null;
        }

        AuthenticationParameters ap = new AuthenticationParameters();
        Credential credentials = new UsernamePasswordCredential(username, password);
        ap.credential(credentials).newAuthentication(true);

        HttpServletResponse response = (HttpServletResponse) ec.getResponse();

        if (sc.authenticate(request, response, ap) == AuthenticationStatus.SUCCESS) {
            // Store DTO without password in session
            UserDTO userDTO = userMapper.toDTO(user);
            request.getSession().setAttribute("userDTO", userDTO);

            logger.log(Level.INFO, "User authenticated successfully: {0}", username);
            return "userlist?faces-redirect=true";
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Authentication failed", "Unable to authenticate"));
            logger.log(Level.WARNING, "Authentication failed for: {0}", username);
            return null;
        }
    }

    public String logout() throws ServletException {
        logger.log(Level.INFO, "User logging out: {0}", username);
        request.logout();
        request.getSession().invalidate();
        return "login?faces-redirect=true";
    }

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
}