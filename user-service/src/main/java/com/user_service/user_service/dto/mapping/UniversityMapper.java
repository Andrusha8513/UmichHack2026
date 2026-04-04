package com.user_service.user_service.dto.mapping;

import com.user_service.user_service.Users;
import com.user_service.user_service.dto.UniversityDto;
import com.user_service.user_service.dto.UniversityRegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.userdetails.User;

@Mapper(componentModel = "spring")
public interface UniversityMapper {
    Users toEntity(UniversityRegistrationDto universityRegistrationDto);
    @Mapping(target = "university", ignore = true)
    UniversityDto toDto(Users users);

}
