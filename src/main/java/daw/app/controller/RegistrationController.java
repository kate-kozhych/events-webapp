package daw.app.controller;

import daw.app.dto.UserRegistrationDTO;
import daw.app.services.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;


@Named
@RequestScoped
public class RegistrationController {

    private static final Logger logger = Logger.getLogger(RegistrationController.class.getName());

    @Inject
    private UserService userService;

    private UserRegistrationDTO registrationData = new UserRegistrationDTO();


    public String register() {
        try {
            if (registrationData.getName() == null || registrationData.getName().trim().isEmpty()) {
                showErrorMessage("Error", "Name is required");
                return null;
            }

            if (registrationData.getSurname() == null || registrationData.getSurname().trim().isEmpty()) {
                showErrorMessage("Error", "Surname is required");
                return null;
            }

            if (registrationData.getPassword() == null || registrationData.getPassword().length() < 6) {
                showErrorMessage("Error", "Password must be at least 6 characters long");
                return null;
            }

            userService.registerUser(registrationData);

            logger.log(Level.INFO, "New user registered: {0}", registrationData.getUjaEmail());
            showInfoMessage("Success", "Registration successful! Please login.");

            return "login?faces-redirect=true";

        } catch (UserService.UserServiceException e) {
            logger.log(Level.WARNING, "Registration failed", e);
            showErrorMessage("Registration Error", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during registration", e);
            showErrorMessage("Error", "Registration failed. Please try again later.");
            return null;
        }
    }


    public String backToLogin() {
        return "login?faces-redirect=true";
    }

    private void showErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    private void showInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    // Getters and Setters
    public UserRegistrationDTO getRegistrationData() {
        return registrationData;
    }

    public void setRegistrationData(UserRegistrationDTO registrationData) {
        this.registrationData = registrationData;
    }
}