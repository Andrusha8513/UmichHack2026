package com.example.profile_service.service;

import com.example.profile_service.dto.PrivetUserProfileDto;
import com.example.profile_service.dto.ProfileDashboardResponseDto;
import com.example.profile_service.dto.PvzDetailsDto;
import com.example.profile_service.dto.PvzShortDto;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.entity.Image;
import com.example.profile_service.entity.Pvz;
import com.example.profile_service.mapper.ProfileDashboardMapper;
import com.example.profile_service.mapper.PvzMapper;
import com.example.profile_service.redis.ProfileRedisRepository;
import com.example.profile_service.repository.ImageRepository;
import com.example.profile_service.mapper.ImageMapper;
import com.example.profile_service.mapper.ProfileMapper;
import com.example.profile_service.repository.ProfileRepository;
import com.example.profile_service.repository.PvzRepository;
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
    private final PvzRepository pvzRepository;
    private final PvzMapper pvzMapper;
    private final ProfileDashboardMapper profileDashboardMapper;


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

    @Transactional(readOnly = true)
    public List<PvzShortDto> getMyPvzShort(Long id){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
         Long currentUserId = userDetails.getId();

         boolean isAdmin = authentication.getAuthorities().stream()
                 .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

         if (!isAdmin && !id.equals(currentUserId)){
             throw new RuntimeException("У вас нет доступа к списку ПВЗ другого пользователя!");
         }

        List<Pvz> pvzs = pvzRepository.findByOwnerId(id).stream()
                .filter(pvz -> !Boolean.TRUE.equals(pvz.getIsDeleted()))
                .toList();

            return pvzs.stream()
                    .map(pvzMapper::toShortDto)
                    .toList();
        }

        @Transactional(readOnly = true)
        public PvzDetailsDto getPvzDetailsDto(Long id){
            Pvz pvz =  pvzRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Пвз с таким  id= "+ id + "не найдено"));
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long currentUserId = userDetails.getId();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if(!isAdmin && !pvz.getOwner().getId().equals(currentUserId)){
                throw new RuntimeException("У вас нет доступа к чужому ПВЗ!");
            }
            return pvzMapper.toDetailsDto(pvz);
        }

    @Transactional(readOnly = true)
    public ProfileDashboardResponseDto findProfilee(String email) {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Профиля с такой почтой " + email + " не найдено!"));
        return profileDashboardMapper.toDto(profile);
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
