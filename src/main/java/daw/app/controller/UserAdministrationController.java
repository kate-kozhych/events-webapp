package daw.app.controller;

import daw.app.dto.UserDTO;
import daw.app.services.UserService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;


@Named
@ViewScoped
public class UserAdministrationController implements Serializable {

    private static final Logger logger = Logger.getLogger(UserAdministrationController.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private HttpServletRequest request;

    @Inject
    private UserSearchController userSearchController;


    public void deleteUser(Long userId) {
        try {
            HttpSession session = getSession();
            UserDTO currentUser = (UserDTO) session.getAttribute("userDTO");

            if (currentUser == null) {
                showErrorMessage("Error", "You must be logged in to perform this action");
                return;
            }

            boolean isAdmin = request.isUserInRole("ADMINISTRATORS") ||
                    "admin".equals(currentUser.getRole());

            if (!isAdmin) {
                showErrorMessage("Access Denied", "You must have administrator privileges to delete users");
                return;
            }

            if (currentUser.getId().equals(userId)) {
                showErrorMessage("Error", "You cannot delete your own account while logged in");
                return;
            }

            userService.deleteUser(userId, currentUser.getId(), isAdmin);

            if (userSearchController != null) {
                userSearchController.loadAllUsers();
            }

            showInfoMessage("Success", "User deleted successfully");
            logger.log(Level.INFO, "User {0} deleted by admin {1}",
                    new Object[]{userId, currentUser.getId()});

        } catch (UserService.UserServiceException e) {
            logger.log(Level.WARNING, "User deletion failed", e);
            showErrorMessage("Error", e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during user deletion", e);
            showErrorMessage("Error", "Failed to delete user: " + e.getMessage());
        }
    }


    public void requestAdminAccess() {//for future
        showWarnMessage("Feature Not Implemented",
                "Admin access request functionality is not yet available. Please contact system administrator.");
        logger.info("Admin access requested by user");
    }


    public String logout() {
        try {
            request.logout();
            request.getSession().invalidate();
            logger.info("User logged out successfully");
            return "login?faces-redirect=true";
        } catch (ServletException e) {
            logger.log(Level.SEVERE, "Logout failed", e);
            showErrorMessage("Logout Failed", "An error occurred during logout");
            return null;
        }
    }


    public boolean isCurrentUserAdmin() {
        try {
            HttpSession session = getSession();
            if (session == null) {
                return false;
            }

            UserDTO currentUser = (UserDTO) session.getAttribute("userDTO");
            if (currentUser == null) {
                return false;
            }

            return request.isUserInRole("ADMINISTRATORS") || "admin".equals(currentUser.getRole());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking admin status", e);
            return false;
        }
    }


    public boolean isCurrentUserRegular() {
        return !isCurrentUserAdmin() && request.isUserInRole("USERS");
    }

    private HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }

    private void showErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    private void showInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void showWarnMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }
}