package com.example.profile_service.service;

import com.example.profile_service.dto.PrivetUserProfileDto;

import com.example.profile_service.entity.Profile;
import com.example.profile_service.entity.Image;
import com.example.profile_service.redis.ProfileRedisRepository;
import com.example.profile_service.mapper.ProfileMapper;
import com.example.profile_service.repository.ProfileRepository;
import com.example.support_module.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ImageService imageService;
    private final ProfileMapper profileMapper;
    private final ProfileRedisRepository profileRedisRepository;

    @Transactional(readOnly = true)
    public PrivetUserProfileDto getProfile(Long id) {
        Optional<PrivetUserProfileDto> cash = profileRedisRepository.getProfileSummery(id);
        if (cash.isPresent()) {
            return profileMapper.toDtoOption(cash);
        } else {
            Profile profile = profileRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден!"));
            profileRedisRepository.saveProfile(profile.getId(), profileMapper.toDto(profile));
            return profileMapper.toDto(profile);
        }
    }
    @Transactional
    public void setAvatar(Long profileId, MultipartFile file) throws IOException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден: " + profileId));

        Image newImage = imageService.createImage(file);
        newImage.setProfile(profile);
        profile.setAvatar(newImage);

        profileRedisRepository.deleteProfile(profile.getId());
        profileRepository.save(profile);
    }

    @Transactional
    public void deleteAvatar(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден: " + profileId));
        profile.setAvatar(null);
        profileRedisRepository.deleteProfile(profile.getId());
        profileRepository.save(profile);
    }
}
