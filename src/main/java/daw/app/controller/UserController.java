package daw.app.controller;

import daw.app.model.User;
import daw.app.model.dao.UserDAO;
import daw.app.qualifiers.DAOJpa;
import daw.app.services.AuthService;
import daw.app.services.UserService;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class UserController {

    @Inject
    @DAOJpa
    private UserDAO userDAO;

    @Inject
    private UserService userService;

    private static final Logger logger = Logger.getLogger(UserController.class.getName());


    private User user = new User();  // User object for holding the form data

    // This method will be used to load user data when the page is loaded
    public void loadUserData() {
        try {
            HttpSession session = getSession();

            // Check if user is logged in
            if (session == null || session.getAttribute("user") == null) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("login.xhtml");
                return;
            }
            User sessionUser = (User) session.getAttribute("user");
            // Refresh user data from database
            User freshUser = userService.getUserById(sessionUser.getId());
            if (freshUser == null) {
                showErrorMessage("Error", "User account no longer exists");
                request.logout();
                session.invalidate();
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("login.xhtml");
                return;
            }
            // Update session with fresh data
            this.user = freshUser;
            this.user.ensureNonNullFields();
            session.setAttribute("user", this.user);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to redirect", e);
        } catch (ServletException e) {
            logger.log(Level.SEVERE, "Failed to logout", e);
        } catch (Exception e) {
            showErrorMessage("Error", "Failed to load user data: " + e.getMessage());
        }
    }



    // Register method
    public String register() {
        try {
            userService.registerUser(user);
            return "login?faces-redirect=true";
        } catch (UserService.UserServiceException e) {
            showErrorMessage("Error", e.getMessage());
            return "signup";
        } catch (Exception e) {
            showErrorMessage("Error", "Registration failed: " + e.getMessage());
            return "signup";
        }
    }


    @Inject
    private HttpServletRequest request;

    @Inject
    private AuthService authService;

    public String logout() throws ServletException {
        try {
            request.logout();
            request.getSession().invalidate();
            return "login";
        } catch (ServletException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Logout Failed", "An error occurred during logout"));
            return null;
        }
    }

    public String updateUserInfo() {
        try {
            // Get user ID from session if not set
            if (user.getId() == null) {
                HttpSession session = getSession();
                User sessionUser = (User) session.getAttribute("user");
                if (sessionUser != null) {
                    user.setId(sessionUser.getId());
                }
            }

            // Update through service
            User updatedUser = userService.updateUser(user.getId(), user, user.getPassword());

            // Update session with fresh data
            HttpSession session = getSession();
            session.setAttribute("user", updatedUser);

            showInfoMessage("Success", "Profile updated successfully");
            return "userlist?faces-redirect=true";

        } catch (UserService.UserServiceException e) {
            showErrorMessage("Error", e.getMessage());
            return null;
        } catch (Exception e) {
            showErrorMessage("Error", "Update failed: " + e.getMessage());
            return null;
        }
    }



    public String deleteUser(Long userId) {
        try {
            HttpSession session = getSession();
            User currentUser = (User) session.getAttribute("user");
            boolean isAdmin = request.isUserInRole("ADMINISTRATORS") ||
                    (currentUser != null && "admin".equals(currentUser.getRole()));
            userService.deleteUser(userId, currentUser.getId(), isAdmin);
            searchResults = userService.getAllUsers();
            showInfoMessage("Success", "User has been deleted successfully");
            return null;

        } catch (UserService.UserServiceException e) {
            showErrorMessage("Error", e.getMessage());
            return null;
        } catch (Exception e) {
            showErrorMessage("Error", "Failed to delete user: " + e.getMessage());
            return null;
        }
    }

    // Search users by ESN card
    private String searchEsnCard;
    private List<User> searchResults;

    public void searchUsersByEsnCard() {
        searchResults = userService.searchByEsnCard(searchEsnCard);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    public String goToHomePage() {
        return "userlist?faces-redirect=true";
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



    // Getters and setters for the user object
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSearchEsnCard() {
        return searchEsnCard;
    }

    public void setSearchEsnCard(String searchEsnCard) {
        this.searchEsnCard = searchEsnCard;
    }

    public List<User> getSearchResults() {
        // Initialize search results if not yet done
        if (searchResults == null) {
            searchResults = userDAO.findAll();
        }
        return searchResults;
    }

    public void setSearchResults(List<User> searchResults) {
        this.searchResults = searchResults;
    }


}
