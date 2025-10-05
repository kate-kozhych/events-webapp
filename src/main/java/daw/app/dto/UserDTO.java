package daw.app.dto;

import jakarta.validation.constraints.*;
import daw.app.model.validator.EsnCard;
import java.io.Serializable;


public class UserDTO implements Serializable {

    private Long id;

    @NotNull(message = "Name is required.")
    @Size(min = 2, message = "Name must be at least 2 characters long.")
    private String name;

    @NotNull(message = "Surname is required.")
    @Size(min = 2, message = "Surname must be at least 2 characters long.")
    private String surname;

    @NotNull(message = "Age is required.")
    @Min(value = 18, message = "You must be at least 18 years old.")
    @Max(value = 100, message = "Age must be less than 100.")
    private Integer age;

    @Email(message = "Student email must be a valid email address.")
    @NotNull(message = "Student email is required.")
    private String ujaEmail;

    @NotNull(message = "ESN Card is required.")
    @EsnCard
    private String esnCard;

    @NotNull(message = "Role is required.")
    private String role;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String surname, Integer age,
                   String ujaEmail, String esnCard, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.ujaEmail = ujaEmail;
        this.esnCard = esnCard;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUjaEmail() {
        return ujaEmail;
    }

    public void setUjaEmail(String ujaEmail) {
        this.ujaEmail = ujaEmail;
    }

    public String getEsnCard() {
        return esnCard;
    }

    public void setEsnCard(String esnCard) {
        this.esnCard = esnCard;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public void ensureNonNullFields() {
        if (this.name == null) this.name = "";
        if (this.surname == null) this.surname = "";
        if (this.age == null) this.age = 18;
        if (this.role == null) this.role = "student";
        if (this.esnCard == null) this.esnCard = "";
        if (this.ujaEmail == null) this.ujaEmail = "";
    }
}