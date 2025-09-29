package daw.app.model.dao;

import daw.app.model.User;
import daw.app.qualifiers.DAOMap;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@DAOMap
public class UserDAOMap implements UserDAO {

    private Map<Long, User> users = new HashMap<>();  // In-memory storage for users using a Map (by ID)
    private int userId = 1;  // ID generator for new users

    public UserDAOMap() {
        // Initialize with sample data (for testing purposes)
        users.put(1L, new User(1L, "Test", "User",30, "password123", "admin", "ESN001", "test@example.com"));
        users.put(2L, new User(2L, "John", "Doe",22, "password123", "student", "ESN002", "john@example.com"));
        users.put(3L, new User(3L, "Maria", "Garcia",21, "password123", "student", "ESN003", "maria@example.com"));
        users.put(4L, new User(4L, "Ahmed", "Hassan",28, "password123", "student", "ESN004", "ahmed@example.com"));
        users.put(5L, new User(5L, "Sophie", "Martin",27, "password123", "student", "ESN005", "sophie@example.com"));

        // Update userId to account for preset users
        userId = 6;
    }

    // Save a new user
    @Override
    public void save(User user) {
        user.setId((long) userId++);  // Automatically generating an ID for new user
        users.put(user.getId(), user);  // Storing user in the Map using ID as key
    }

    // Find a user by their email (login)
    @Override
    public User findByLogin(String login) {
        // Loop through users and find by email
        return users.values().stream()
                .filter(user -> user.getUjaEmail().equals(login))
                .findFirst()
                .orElse(null);  // Return null if not found
    }

    // Find a user by ID
    @Override
    public User findById(Long id) {
        return users.get(id);  // Retrieve user from Map using ID as the key
    }

    // Check if the entered password matches the stored password
    @Override
    public boolean checkPassword(User user, String password) {
        return user.getPassword().equals(password);  // Comparing password
    }

    // Update an existing user's information
    @Override
    public void update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);  // Update user in the Map using ID as the key
        }
    }

    // Get a list of all users
    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());  // Return a list of all users in the Map
    }

    // Delete a user
    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    // Find users by ESN card number
    @Override
    public List<User> findByEsnCard(String esnCard) {
        return users.values().stream()
                .filter(user -> {
                    // Check if user's ESN card is null
                    String userEsnCard = user.getEsnCard();
                    if (userEsnCard == null) {
                        return false; // Skip users with null ESN card
                    }
                    return userEsnCard.contains(esnCard);
                })
                .collect(Collectors.toList());
    }

}
