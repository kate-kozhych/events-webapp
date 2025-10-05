package daw.app.controller;

import daw.app.dto.ProfileUpdateDTO;
import daw.app.dto.UserDTO;
import daw.app.mapper.UserMapper;
import daw.app.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;


@Named
@ViewScoped
public class ProfileController implements Serializable {

    private static final Logger logger = Logger.getLogger(ProfileController.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private HttpServletRequest request;

    private ProfileUpdateDTO profileData;
    private UserDTO currentUser;


    public void loadUserData() {
        try {
            HttpSession session = getSession();

            if (session == null || session.getAttribute("userDTO") == null) {
                redirectToLogin();
                return;
            }

            UserDTO sessionUser = (UserDTO) session.getAttribute("userDTO");

            currentUser = userService.getUserById(sessionUser.getId());

            if (currentUser == null) {
                showErrorMessage("Error", "User account no longer exists");
                logout();
                return;
            }

            profileData = userMapper.toProfileUpdateDTO(currentUser);

            session.setAttribute("userDTO", currentUser);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load user data", e);
            showErrorMessage("Error", "Failed to load user data: " + e.getMessage());
        }
    }


    public String updateProfile() {
        try {
            if (profileData == null || profileData.getId() == null) {
                showErrorMessage("Error", "Profile data is missing");
                return null;
            }

            UserDTO updatedUser = userService.updateProfile(profileData);

            HttpSession session = getSession();
            session.setAttribute("userDTO", updatedUser);

            showInfoMessage("Success", "Profile updated successfully");
            return "userlist?faces-redirect=true";

        } catch (UserService.UserServiceException e) {
            logger.log(Level.WARNING, "Profile update failed", e);
            showErrorMessage("Error", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during profile update", e);
            showErrorMessage("Error", "Update failed: " + e.getMessage());
            return null;
        }
    }


    public String backToUserList() {
        return "userlist?faces-redirect=true";
    }


    private HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }

    private void redirectToLogin() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("login.xhtml");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to redirect to login", e);
        }
    }

    private void logout() {
        try {
            request.logout();
            request.getSession().invalidate();
            redirectToLogin();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Logout failed", e);
        }
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
    public ProfileUpdateDTO getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileUpdateDTO profileData) {
        this.profileData = profileData;
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }
}