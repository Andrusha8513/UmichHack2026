package com.example.profile_service.kafka;

import com.example.profile_service.redis.ProfileRedisRepository;
import com.example.profile_service.service.ProfileService;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.dto.PrivetUserProfileDto;
import com.example.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileConsumerKafka {
    private final ProfileRepository profileRepository;
    private final ProfileRedisRepository profileRedisRepository;

    @Transactional
    @KafkaListener(topics = "profile",
            groupId = "profile")
    public void consumerProfile(PrivetUserProfileDto profileDto) {
        log.info("Принял профиль из кафки {}", profileDto.getId());

        Profile profile = profileRepository.findById(profileDto.getId())
                .orElse(new Profile());

        profile.setId(profileDto.getId());
        profile.setName(profileDto.getName());
        profile.setSecondName(profileDto.getSecondName());
        profile.setEmail(profileDto.getEmail());
        profile.setSurName(profileDto.getSurName());


        profileRedisRepository.deleteProfile(profile.getId());
        profileRepository.save(profile);
    }

}
