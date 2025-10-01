package daw.app.services;

import daw.app.model.User;
import daw.app.model.dao.UserDAO;
import daw.app.qualifiers.DAOJpa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@ApplicationScoped
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Inject
    @DAOJpa
    private UserDAO userDAO;

    @Inject
    private AuthService authService;

    @Transactional  // Entire operation executes in a single transaction
    public void registerUser(User user) throws UserServiceException {
        logger.log(Level.INFO, "Registering new user: {0}", user.getUjaEmail());
        validateAge(user.getAge());
        validateEmail(user.getUjaEmail());
        if (userDAO.findByLogin(user.getUjaEmail()) != null) {
            throw new UserServiceException("Email already in use");
        }

        if (!userDAO.findByEsnCard(user.getEsnCard()).isEmpty()) {
            throw new UserServiceException("ESN Card number already in use");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new UserServiceException("Password must be at least 6 characters long");
        }

        String hashedPassword = authService.encryptPassword(user.getPassword());
        user.setPassword(hashedPassword);

        user.setRole("student");

        userDAO.save(user);

        logger.log(Level.INFO, "User registered successfully: {0}", user.getUjaEmail());
    }

    @Transactional
    public User updateUser(Long userId, User updatedData, String newPassword) throws UserServiceException {
        logger.log(Level.INFO, "Updating user with ID: {0}", userId);
        if (userId == null) {
            throw new UserServiceException("User ID is missing");
        }
        User existingUser = userDAO.findById(userId);
        if (existingUser == null) {
            throw new UserServiceException("User not found in database");
        }

        if (updatedData.getName() == null || updatedData.getName().trim().isEmpty()) {
            throw new UserServiceException("Name is required");
        }
        if (updatedData.getSurname() == null || updatedData.getSurname().trim().isEmpty()) {
            throw new UserServiceException("Surname is required");
        }

        validateAge(updatedData.getAge());

        if (updatedData.getUjaEmail() == null || updatedData.getUjaEmail().trim().isEmpty()) {
            throw new UserServiceException("Email is required");
        }
        validateEmail(updatedData.getUjaEmail());

        // Check new email uniqueness (if changed)
        if (!existingUser.getUjaEmail().equals(updatedData.getUjaEmail())) {
            User userWithSameEmail = userDAO.findByLogin(updatedData.getUjaEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(userId)) {
                throw new UserServiceException("Email already in use by another user");
            }
        }

        // Update fields
        existingUser.setName(updatedData.getName());
        existingUser.setSurname(updatedData.getSurname());
        existingUser.setAge(updatedData.getAge());
        existingUser.setUjaEmail(updatedData.getUjaEmail());

        // Update password (if new one provided)
        if (newPassword != null && !newPassword.isEmpty()) {
            // Check if this is actually a new password
            if (!authService.verifyPassword(newPassword, existingUser.getPassword())) {
                // Validate new password
                if (newPassword.length() < 6) {
                    throw new UserServiceException("Password must be at least 6 characters long");
                }
                // Hash new password
                existingUser.setPassword(authService.encryptPassword(newPassword));
            }
        }

        // Save to database
        userDAO.update(existingUser);

        logger.log(Level.INFO, "User updated successfully: {0}", userId);

        return existingUser;  // Return updated user
    }


    @Transactional
    public void deleteUser(Long userId, Long currentUserId, boolean isAdmin) throws UserServiceException {
        logger.log(Level.INFO, "Attempting to delete user with ID: {0}", userId);
        if (!isAdmin) {
            throw new UserServiceException("You must have administrator privileges to delete users");
        }

        if (currentUserId.equals(userId)) {
            throw new UserServiceException("You cannot delete your own account while logged in");
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new UserServiceException("User not found");
        }

        userDAO.delete(userId);

        logger.log(Level.INFO, "User deleted successfully: {0}", userId);
    }


    public List<User> searchByEsnCard(String esnCard) {
        if (esnCard != null && !esnCard.isEmpty()) {
            return userDAO.findByEsnCard(esnCard);
        }
        return userDAO.findAll();
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(Long userId) {
        return userDAO.findById(userId);
    }

    public User getUserByEmail(String email) {
        return userDAO.findByLogin(email);
    }

    private void validateAge(Integer age) throws UserServiceException {
        if (age == null) {
            throw new UserServiceException("Age is required");
        }
        if (age < 18) {
            throw new UserServiceException("You must be at least 18 years old");
        }
        if (age > 100) {
            throw new UserServiceException("Age must be less than 100");
        }
    }

    private void validateEmail(String email) throws UserServiceException {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new UserServiceException("Please enter a valid email address");
        }
    }

    public static class UserServiceException extends Exception {
        public UserServiceException(String message) {
            super(message);
        }

        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}