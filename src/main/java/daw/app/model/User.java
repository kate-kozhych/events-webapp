package daw.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import daw.app.model.validator.EsnCard;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required.")
    @Size(min = 2, message = "Name is required.")
    private String name;

    @NotNull(message = "Surname is required.")
    @Size(min = 2, message = "Surname is required.")
    private String surname;


    @NotNull(message = "Age is required.")
    @Min(value = 18, message = "You must be at least 18 years old.")
    @Max(value = 100, message = "Age must be less than 100.")
    private Integer age;

    @NotNull(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    private String password;

    @NotNull(message = "Role is required.")
    private String role;

    @NotNull(message = "ESN Card is required.")
    @EsnCard
    private String esnCard;

    @Email(message = "Student email must be a valid email address.")
    @NotNull(message = "Student email is required.")
    private String ujaEmail;

    public User(Long id, String name, String surname, Integer age, String password, String role, String esnCard, String ujaEmail) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.password = password;
        this.role = role;
        this.esnCard = esnCard;
        this.ujaEmail = ujaEmail;
    }

    public User() {
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEsnCard() {
        return esnCard;
    }

    public void setEsnCard(String esnCard) {
        this.esnCard = esnCard;
    }

    public String getUjaEmail() {
        return ujaEmail;
    }

    public void setUjaEmail(String ujaEmail) {
        this.ujaEmail = ujaEmail;
    }

    // Add this method to your User class
    public void ensureNonNullFields() {
        if (this.name == null) this.name = "";
        if (this.surname == null) this.surname = "";
        if (this.age == null) this.age = 18;
        if (this.password == null) this.password = "";
        if (this.role == null) this.role = "student";
        if (this.esnCard == null) this.esnCard = "";
        if (this.ujaEmail == null) this.ujaEmail = "";
    }
}