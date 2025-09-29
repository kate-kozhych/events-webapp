package daw.app;

import daw.app.model.User;
import daw.app.model.dao.UserDAO;
import daw.app.qualifiers.DAOJpa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.HashSet;
import java.util.Set;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

@ApplicationScoped
public class EsnIdentityStore implements IdentityStore {

    @Inject @DAOJpa
    private UserDAO userDAO;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;
            String email = usernamePassword.getCaller();
            String password = usernamePassword.getPasswordAsString();

            // Method findByLogin to check the email
            User user = userDAO.findByLogin(email);

            if (user != null && userDAO.checkPassword(user, password)) {
                // Get roles
                Set<String> groups = new HashSet<>();

                // Depending on the role adding to the group
                if ("admin".equals(user.getRole())) {
                    groups.add("ADMINISTRATORS");
                }
                // All authentificated users are  USERS
                groups.add("USERS");

                // Saving user for the session
                return new CredentialValidationResult(user.getId().toString(), groups);
            }
        }

        return INVALID_RESULT;
    }
}