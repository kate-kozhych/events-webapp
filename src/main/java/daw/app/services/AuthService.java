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
            logger.log(Level.WARNING, "Password verification failed: null input");
            return false;
        }

        try {
            // Only accept PBKDF2 hashed passwords
            if (!hashedPassword.startsWith("PBKDF2WithHmacSHA512")) {
                logger.log(Level.SEVERE,
                        "Password verification failed: password not properly hashed");
                return false;
            }

            return passwordHash.verify(password.toCharArray(), hashedPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
            return false;
        }
    }

    // Encrypting
    public String encryptPassword(String password) {
        if (password == null || password.isEmpty()) {
            logger.log(Level.WARNING, "Attempted to encrypt null/empty password");
            return null;
        }

        try {
            String encryptedPass = passwordHash.generate(password.toCharArray());
            logger.log(Level.INFO, "Password encrypted successfully");
            return encryptedPass;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
            return null;
        }
    }
}