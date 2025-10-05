package daw.app.mapper;

import daw.app.dto.ProfileUpdateDTO;
import daw.app.dto.UserDTO;
import daw.app.dto.UserRegistrationDTO;
import daw.app.model.User;
import jakarta.enterprise.context.ApplicationScoped;

//Mapper for conversion between Entity (User) and DTO.

@ApplicationScoped
public class UserMapper {

//User entity into UserDTO

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getAge(),
                user.getUjaEmail(),
                user.getEsnCard(),
                user.getRole()
        );
    }

//UserRegistrationDTO to User entity

    public User toEntity(UserRegistrationDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setAge(dto.getAge());
        user.setUjaEmail(dto.getUjaEmail());
        user.setEsnCard(dto.getEsnCard());
        user.setPassword(dto.getPassword()); // Plain password will be hashed in service
        user.setRole("student");

        return user;
    }


//Updating User entity from ProfileUpdateDTO
    public void updateEntityFromDTO(User user, ProfileUpdateDTO dto) {
        if (user == null || dto == null) {
            return;
        }

        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setAge(dto.getAge());
        user.setUjaEmail(dto.getUjaEmail());
    }


     //Creates ProfileUpdateDTO from UserDTO to fill the form

    public ProfileUpdateDTO toProfileUpdateDTO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setId(userDTO.getId());
        dto.setName(userDTO.getName());
        dto.setSurname(userDTO.getSurname());
        dto.setAge(userDTO.getAge());
        dto.setUjaEmail(userDTO.getUjaEmail());
        // newPassword stays null

        return dto;
    }
}