package daw.app.controller;

import daw.app.dto.UserDTO;
import daw.app.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;


@Named
@ViewScoped
public class UserSearchController implements Serializable {

    private static final Logger logger = Logger.getLogger(UserSearchController.class.getName());

    @Inject
    private UserService userService;

    private String searchEsnCard;
    private List<UserDTO> searchResults;


    @PostConstruct
    public void init() {
        loadAllUsers();
    }


    public void loadAllUsers() {
        searchResults = userService.getAllUsers();
        logger.info("Loaded " + (searchResults != null ? searchResults.size() : 0) + " users");
    }

    public void searchUsersByEsnCard() {
        if (searchEsnCard == null || searchEsnCard.trim().isEmpty()) {
            // Если поле поиска пустое - показываем всех
            loadAllUsers();
        } else {
            searchResults = userService.searchByEsnCard(searchEsnCard.trim());
            logger.info("Search found " + (searchResults != null ? searchResults.size() : 0) + " users");
        }
    }

    public void clearSearch() {
        searchEsnCard = null;
        loadAllUsers();
    }

    //for future functionality
    public String goToProfile() {
        return "profile?faces-redirect=true";
    }


    public boolean isCurrentUser(Long userId) {
        return false;
    }

    // Getters and Setters
    public String getSearchEsnCard() {
        return searchEsnCard;
    }

    public void setSearchEsnCard(String searchEsnCard) {
        this.searchEsnCard = searchEsnCard;
    }

    public List<UserDTO> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<UserDTO> searchResults) {
        this.searchResults = searchResults;
    }
}