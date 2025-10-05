package daw.app.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

//with optional field for password
public class ProfileUpdateDTO implements Serializable {

    @NotNull(message = "User ID is required.")
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

    @Size(min = 6, message = "Password must be at least 6 characters long.")
    private String newPassword;

    public ProfileUpdateDTO() {
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}