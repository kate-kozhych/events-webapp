package daw.app.model.dao;

import daw.app.model.User;
import daw.app.qualifiers.DAOJpa;
import daw.app.services.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@ApplicationScoped
@DAOJpa
@Transactional
public class UserDAOJpa implements UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAOJpa.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private AuthService authService;

    @Override
    public void save(User user) {
        try {
            em.persist(user);
            logger.log(Level.INFO, "User saved successfully with ID: {0}", user.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving user", e);
            throw e;
        }
    }

    @Override
    public User findByLogin(String login) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.ujaEmail = :email", User.class);
            query.setParameter("email", login);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No user found with email: {0}", login);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by login", e);
            return null;
        }
    }

    @Override
    public boolean checkPassword(User user, String password) {
        try {
            if (user == null || password == null) {
                logger.log(Level.WARNING, "Password check failed: null input");
                return false;
            }
            return authService.verifyPassword(password, user.getPassword());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking password", e);
            return false;
        }
    }

    @Override
    @Transactional
    public void update(User user) {
        try {
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("User or User ID is null");
            }

            logger.log(Level.INFO, "Updating user with ID: {0}", user.getId());

            User existingUser = em.find(User.class, user.getId());
            if (existingUser == null) {
                throw new IllegalArgumentException("User with ID " + user.getId() + " not found");
            }

            existingUser.setName(user.getName());
            existingUser.setSurname(user.getSurname());
            existingUser.setAge(user.getAge());
            existingUser.setUjaEmail(user.getUjaEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setRole(user.getRole());
            existingUser.setEsnCard(user.getEsnCard());

            em.merge(existingUser);
            em.flush();

            logger.log(Level.INFO, "User updated successfully: {0}", user.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user with ID: " + user.getId(), e);
            throw e;
        }
    }

    @Override
    public User findById(Long id) {
        try {
            return em.find(User.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by id: " + id, e);
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all users", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("User ID is null");
            }

            logger.log(Level.INFO, "Deleting user with ID: {0}", id);

            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                em.flush();
                logger.log(Level.INFO, "User deleted successfully: {0}", id);
            } else {
                logger.log(Level.WARNING, "User with ID {0} not found for deletion", id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            throw e;
        }
    }

    @Override
    public List<User> findByEsnCard(String esnCard) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.esnCard LIKE :esnCard", User.class);
            query.setParameter("esnCard", "%" + esnCard + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding users by ESN card", e);
            return new ArrayList<>();
        }
    }
}