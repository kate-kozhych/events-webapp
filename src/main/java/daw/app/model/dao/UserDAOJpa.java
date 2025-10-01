package daw.app.model.dao;

import daw.app.model.User;
import daw.app.qualifiers.DAOJpa;
import daw.app.services.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
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
@Transactional  // Automatic transaction handling
public class UserDAOJpa implements UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAOJpa.class.getName());

    @PersistenceContext  // Injects the EntityManager for JPA operations
    private EntityManager em;

    @Override
    public void save(User user) {
        try {
            // Persist a new user to the database
            em.persist(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving user", e);
        }
    }

    @Override
    public User findByLogin(String login) {
        try {
            // Create a JPQL query to find a user by email
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.ujaEmail = :email", User.class);
            query.setParameter("email", login);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // No user found with the given email
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by login", e);
            return null;
        }
    }

    @Override
    public boolean checkPassword(User user, String password) {
        try {
            // Injecting auth service
            AuthService authService = CDI.current().select(AuthService.class).get();

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
            System.out.println("DEBUG: UserDAOJpa updating user with ID: " + user.getId());
            // Проверка на null и ID
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("User or User ID is null");
            }

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

            System.out.println("DEBUG: User update successful");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user", e);
            throw e;
        }
    }

    @Override
    public User findById(Long id) {
        try {
            // Find an entity by its primary key
            return em.find(User.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by id", e);
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            // JPQL query to get all users
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all users", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(Long id) {
        try {
            System.out.println("DEBUG: UserDAOJpa deleting user with ID: " + id);
            if (id == null) {
                throw new IllegalArgumentException("User ID is null");
            }

            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                em.flush();
                System.out.println("DEBUG: User deletion successful");
            } else {
                System.out.println("WARN: User with ID " + id + " not found for deletion");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting user", e);
            throw e;
        }
    }

    @Override
    public List<User> findByEsnCard(String esnCard) {
        try {
            // JPQL query with LIKE operator for partial matching
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