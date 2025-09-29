package daw.app.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @PostConstruct
    public void init() {
        // Algorithm hashing
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        passwordHash.initialize(parameters);
    }

    // Password checking
    public boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        try {
            // If password is not hashed
            if (!hashedPassword.startsWith("PBKDF2WithHmacSHA512")) {
                return password.equals(hashedPassword);
            }

            return passwordHash.verify(password.toCharArray(), hashedPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при проверке пароля", e);
            return false;
        }
    }

    // Encrypting
    public String encryptPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        String encryptedPass = passwordHash.generate(password.toCharArray());
        logger.log(Level.INFO, "Password encrypted successfully");
        return encryptedPass;
    }
}