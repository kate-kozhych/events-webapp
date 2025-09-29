package daw.app.model.dao;

import daw.app.model.User;

import java.util.List;

public interface UserDAO {

    // Method to save a new user
    void save(User user);

    // Method to find user via login (email)
    User findByLogin(String login);

    // Method to check the password
    boolean checkPassword(User user, String password);

    // Method to update user info
    void update(User user);

    // Method to get user by id
    User findById(Long id);  // New method to find a user by id

    // Method to get all the users
    List<User> findAll();

    // Method to delete a user
    void delete(Long id);

    // Method to find users by ESN card number
    List<User> findByEsnCard(String esnCard);
}
