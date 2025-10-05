package daw.app.services;

import daw.app.dto.ProfileUpdateDTO;
import daw.app.dto.UserDTO;
import daw.app.dto.UserRegistrationDTO;
import daw.app.mapper.UserMapper;
import daw.app.model.User;
import daw.app.model.dao.UserDAO;
import daw.app.qualifiers.DAOJpa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ApplicationScoped
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Inject
    @DAOJpa
    private UserDAO userDAO;

    @Inject
    private AuthService authService;

    @Inject
    private UserMapper userMapper;

    @Transactional  // Entire operation executes in a single transaction
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) throws UserServiceException {
        logger.log(Level.INFO, "Registering new user: {0}", registrationDTO.getUjaEmail());

        validateAge(registrationDTO.getAge());
        validateEmail(registrationDTO.getUjaEmail());

        if (userDAO.findByLogin(registrationDTO.getUjaEmail()) != null) {
            throw new UserServiceException("Email already in use");
        }

        if (!userDAO.findByEsnCard(registrationDTO.getEsnCard()).isEmpty()) {
            throw new UserServiceException("ESN Card number already in use");
        }

        if (registrationDTO.getPassword() == null || registrationDTO.getPassword().length() < 6) {
            throw new UserServiceException("Password must be at least 6 characters long");
        }

        User user = userMapper.toEntity(registrationDTO);
        String hashedPassword = authService.encryptPassword(registrationDTO.getPassword());
        user.setPassword(hashedPassword);

        userDAO.save(user);
        logger.log(Level.INFO, "User registered successfully: {0}", user.getUjaEmail());

        return userMapper.toDTO(user);
    }

    //fully new safe function to change the info about the user without passing id or password
    @Transactional
    public UserDTO updateProfile(ProfileUpdateDTO updateDTO) throws UserServiceException {
        logger.log(Level.INFO, "Updating user profile with ID: {0}", updateDTO.getId());

        if (updateDTO.getId() == null) {
            throw new UserServiceException("User ID is missing");
        }

        User existingUser = userDAO.findById(updateDTO.getId());
        if (existingUser == null) {
            throw new UserServiceException("User not found in database");
        }

        validateAge(updateDTO.getAge());
        validateEmail(updateDTO.getUjaEmail());

        // Check email uniqueness if changed
        if (!existingUser.getUjaEmail().equals(updateDTO.getUjaEmail())) {
            User userWithSameEmail = userDAO.findByLogin(updateDTO.getUjaEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(updateDTO.getId())) {
                throw new UserServiceException("Email already in use by another user");
            }
        }

        userMapper.updateEntityFromDTO(existingUser, updateDTO);

        // Update password if new one provided
        if (updateDTO.getNewPassword() != null && !updateDTO.getNewPassword().isEmpty()) {
            if (!authService.verifyPassword(updateDTO.getNewPassword(), existingUser.getPassword())) {
                if (updateDTO.getNewPassword().length() < 6) {
                    throw new UserServiceException("Password must be at least 6 characters long");
                }
                existingUser.setPassword(authService.encryptPassword(updateDTO.getNewPassword()));
            }
        }

        userDAO.update(existingUser);
        logger.log(Level.INFO, "User profile updated successfully: {0}", updateDTO.getId());

        return userMapper.toDTO(existingUser);
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


    public List<UserDTO> searchByEsnCard(String esnCard) {
        List<User> users;

        if (esnCard != null && !esnCard.trim().isEmpty()) {
            users = userDAO.findByEsnCard(esnCard);
        } else {
            users = userDAO.findAll();
        }

        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllUsers() {
        return userDAO.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long userId) {
        User user = userDAO.findById(userId);
        return userMapper.toDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userDAO.findByLogin(email);
        return userMapper.toDTO(user);
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