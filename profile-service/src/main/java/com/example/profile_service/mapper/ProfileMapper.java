package com.example.profile_service.mapper;


import com.example.profile_service.dto.PrivetUserProfileDto;
import com.example.profile_service.entity.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ImageMapper imageMapper;


    public PrivetUserProfileDto toDtoOption(Optional<PrivetUserProfileDto> profile){
        PrivetUserProfileDto profileResponseDto = new PrivetUserProfileDto();
        profileResponseDto.setId(profile.get().getId());
        profileResponseDto.setName(profile.get().getName());
        profileResponseDto.setSecondName(profile.get().getSecondName());
        profileResponseDto.setSurName(profile.get().getSurName());
        profileResponseDto.setEmail(profile.get().getEmail());
        profileResponseDto.setAvatarId(profile.get().getAvatarId());

        return profileResponseDto;
    }

    public PrivetUserProfileDto toDto(Profile profile){
        PrivetUserProfileDto profileResponseDto = new PrivetUserProfileDto();
        profileResponseDto.setId(profile.getId());
        profileResponseDto.setName(profile.getName());
        profileResponseDto.setSecondName(profile.getSecondName());
        profileResponseDto.setSurName(profile.getSurName());
        profileResponseDto.setEmail(profile.getEmail());
        if (profile.getAvatar() != null) {
            profileResponseDto.setAvatarId(profile.getAvatar().getId());
        }

        return profileResponseDto;
    }
}
