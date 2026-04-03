package com.user_service.user_service.dto.mapping;


import com.user_service.user_service.Users;
import com.user_service.user_service.dto.ProfileDto;
import com.user_service.user_service.dto.UserRegistrationDTO;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public UserRegistrationDTO toDto(Users users){
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(users.getEmail());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setSurName(users.getSurName());
        dto.setPassword(users.getPassword());
        return dto;
    }
    public Users toEntity(UserRegistrationDTO userDto){
        Users users = new Users();
        users.setEmail(userDto.getEmail());
        users.setName(userDto.getName());
        users.setSecondName(userDto.getSecondName());
        users.setSurName(userDto.getSurName());
        users.setPassword(userDto.getPassword());
        return users;
    }

    public ProfileDto toTestProfileDto(Users users){
        ProfileDto dto = new ProfileDto();
        dto.setId(users.getId());
        dto.setName(users.getName());
        dto.setSecondName(users.getSecondName());
        dto.setEmail(users.getEmail());
        dto.setSurName(users.getSurName());
        return dto;
    }
}
