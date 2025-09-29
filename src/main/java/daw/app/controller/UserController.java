package daw.app.controller;

import daw.app.model.User;
import daw.app.model.dao.UserDAO;
import daw.app.qualifiers.DAOJpa;
import daw.app.services.AuthService;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Named
@RequestScoped
public class UserController {

    @Inject @DAOJpa
    private UserDAO userDAO;  // Injecting the UserDAO for interaction with user data

    private User user = new User();  // User object for holding the form data

    // This method will be used to load user data when the page is loaded
    public void loadUserData() {
        try {
            System.out.println("DEBUG: Loading user data...");
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User sessionUser = (User) session.getAttribute("user");
                if (sessionUser.getId() != null) {
                    User foundUser = userDAO.findById(sessionUser.getId());
                    if (foundUser != null) {
                        String currentRole = sessionUser.getRole();
                        this.user = foundUser;
                        this.user.setRole(currentRole);

                        if (this.user.getName() == null) this.user.setName("");
                        if (this.user.getSurname() == null) this.user.setSurname("");
                        if (this.user.getAge() == null) this.user.setAge(18);
                        if (this.user.getUjaEmail() == null) this.user.setUjaEmail("");
                        if (this.user.getPassword() == null) this.user.setPassword("");

                        session.setAttribute("user", this.user);
                        this.user.ensureNonNullFields();
                        System.out.println("DEBUG: Loaded user data - Name: " + this.user.getName());
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                        "Error", "User data could not be loaded"));
                    }
                }
            } else {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("login.xhtml?faces-redirect=true");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Failed to load user data: " + e.getMessage()));
            e.printStackTrace();
        }
    }


    // Register method
    public String register() {

        if (user.getAge() == null) {
            FacesContext.getCurrentInstance().addMessage("age",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Age is required", "Age is required"));
            return null;
        }
        // Check if user is underage and display warning
        if (user.getAge() < 18) {
            FacesContext.getCurrentInstance().addMessage("age",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Underage User", "You are under 18 years old"));
            user.setAge(18); // Set to minimum valid age
        }

        if (user.getAge() > 100) {
            FacesContext.getCurrentInstance().addMessage("age",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Age", "Age must be less than 100"));
            return null;
        }

        // email format check
        if (user.getUjaEmail() == null || !user.getUjaEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            String errorMessage = "Please enter a valid email address";
            FacesContext.getCurrentInstance().addMessage("email",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, errorMessage));
            return null;
        }


        // Check if email already exists
        if (userDAO.findByLogin(user.getUjaEmail()) != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email already in use"));
            return "signup";
        }

        // Check if ESN card already exists
        if (userDAO.findByEsnCard(user.getEsnCard()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ESN Card number already in use"));
            return "signup";
        }

        String plainPassword = user.getPassword();
        user.setPassword(authService.encryptPassword(plainPassword));

        // Set the role to "student" by default
        user.setRole("student");

        // Save the new user
        userDAO.save(user);

        // After registration, redirect to login page
        return "login?faces-redirect=true";
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
            System.out.println("DEBUG: Updating user with ID: " + user.getId() + ", Name: " + user.getName());

            // Checking ID
            if (user.getId() == null) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                User sessionUser = (User) session.getAttribute("user");
                if (sessionUser != null) {
                    user.setId(sessionUser.getId());
                    System.out.println("DEBUG: Got ID from session: " + user.getId());
                }
            }

            if (user.getId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User ID is missing"));
                return null;
            }

            if (user.getName() == null || user.getName().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("name",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Name is required", "Name is required"));
                return null;
            }

            if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("surname",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Surname is required", "Surname is required"));
                return null;
            }

            // Age validation
            if (user.getAge() == null) {
                FacesContext.getCurrentInstance().addMessage("age",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Age is required", "Age is required"));
                return null;
            }

            if (user.getAge() < 18) {
                FacesContext.getCurrentInstance().addMessage("age",
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Underage User", "You are under 18 years old"));
                user.setAge(18); // Set to minimum valid age
            }

            if (user.getAge() > 100) {
                FacesContext.getCurrentInstance().addMessage("age",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Age", "Age must be less than 100"));
                return null;
            }

            if (user.getUjaEmail() == null || user.getUjaEmail().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("email",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email is required", "Email is required"));
                return null;
            }

            if (user.getId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User ID is missing"));
                return null;
            }


            // Getting the original user from the database
            User originalUser = userDAO.findById(user.getId());
            if (originalUser == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User not found in database"));
                return null;
            }

            // Updating only this fields
            originalUser.setName(user.getName());
            originalUser.setSurname(user.getSurname());
            originalUser.setAge(user.getAge());
            originalUser.setUjaEmail(user.getUjaEmail());

            // If the password changed we encrypt it
            if (user.getPassword() != null && !user.getPassword().isEmpty() &&
                    !authService.verifyPassword(user.getPassword(), originalUser.getPassword())) {

                // Minimal length
                if (user.getPassword().length() < 6) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Error", "Password must be at least 6 characters long"));
                    return null;
                }

                // Saving
                originalUser.setPassword(authService.encryptPassword(user.getPassword()));
            }

            userDAO.update(originalUser);

            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            session.setAttribute("user", originalUser);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated successfully"));

            return "userlist?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Update failed: " + e.getMessage()));
            return null;
        }
    }


    // Get all users for display
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }


    public String deleteUser(Long userId) {
        try {
            System.out.println("DEBUG: Attempting to delete user with ID: " + userId);

            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(false);
            User currentUser = (User) session.getAttribute("user");

            System.out.println("DEBUG: Current user has role: " + (currentUser != null ? currentUser.getRole() : "null"));

            // Checking admin rights
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
                    .getExternalContext().getRequest();
            boolean isAdmin = req.isUserInRole("ADMINISTRATORS") ||
                    (currentUser != null && "admin".equals(currentUser.getRole()));

            System.out.println("DEBUG: Is admin? " + isAdmin);

            if (!isAdmin) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Permission Denied", "You must have administrator privileges to delete users"));
                return null;
            }

            if (currentUser != null && currentUser.getId().equals(userId)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error", "You cannot delete your own account while logged in"));
                return null;
            }

            // Delete user and reload list
            userDAO.delete(userId);

            // Reload search results
            searchResults = userDAO.findAll();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Success", "User has been deleted successfully"));

            return null; // Stay on the same page but with updated list
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Failed to delete user: " + e.getMessage()));
            return null;
        }
    }

    // Search users by ESN card
    private String searchEsnCard;
    private List<User> searchResults;

    public void searchUsersByEsnCard() {
        if (searchEsnCard != null && !searchEsnCard.isEmpty()) {
            searchResults = userDAO.findByEsnCard(searchEsnCard);
        } else {
            // If search is empty, show all users
            searchResults = userDAO.findAll();
        }
    }

    // Switch to admin function
//    public String switchToAdminRole() {
//        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//        if (session != null) {
//            User currentUser = (User) session.getAttribute("user");
//            if (currentUser != null) {
//                currentUser.setRole("admin");
//                userDAO.update(currentUser);
//                session.setAttribute("user", currentUser);
//
//                FacesContext.getCurrentInstance().addMessage(null,
//                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Role changed to administrator"));
//            }
//        }
//        return "userlist?faces-redirect=true";
//    }

    public String requestAdminAccess() {
        try {
            // Current user from the session
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            User currentUser = (User) session.getAttribute("user");

            if (currentUser != null) {
                // Changing to admin
                currentUser.setRole("admin");
                userDAO.update(currentUser);

                // Reload to change the privileges
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Admin access granted", "Please log out and log in again to apply new privileges"));

                return null;
            }

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Could not find current user"));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Failed to update user role: " + e.getMessage()));
            return null;
        }
    }

    public String goToHomePage() {
        return "userlist?faces-redirect=true";
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
