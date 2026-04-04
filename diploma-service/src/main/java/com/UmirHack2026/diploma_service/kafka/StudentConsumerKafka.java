package com.UmirHack2026.diploma_service.kafka;

import com.UmirHack2026.diploma_service.dto.ProfileDto;
import com.UmirHack2026.diploma_service.dto.UniversityDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.entity.StudentProfile;
import com.UmirHack2026.diploma_service.entity.University;
import com.UmirHack2026.diploma_service.repository.DiplomaRepository;
import com.UmirHack2026.diploma_service.repository.StudentRepository;
import com.UmirHack2026.diploma_service.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentConsumerKafka {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;

    @Transactional
    @KafkaListener(
            topics = "profile",
            groupId = "diploma-profile-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumerProfile(ProfileDto profileDto) {
        log.info("Принял профиль из кафки {}", profileDto.id());

        StudentProfile studentProfile = studentRepository.findById(profileDto.id())
                .orElse(new StudentProfile());

        studentProfile.setId(profileDto.id());
        studentProfile.setName(profileDto.name());
        studentProfile.setSecondName(profileDto.secondName());
        studentProfile.setStudentEmail(profileDto.email());
        studentProfile.setSurName(profileDto.surName());

        studentRepository.save(studentProfile);
    }


    @Transactional
    @KafkaListener(
            topics = "university",
            groupId = "diploma-university-group",
            containerFactory = "kafkaListenerContainerFactoryUniversity"
    )
    public void consumeUniversity(UniversityDto universityDto) {
        log.info("Принял профиль универа из кафки {}", universityDto.email());

        University university = universityRepository.findById(universityDto.id())
                .orElse(new University());

        university.setUniversityId(universityDto.id());

        university.setUniversityName(universityDto.name());
        university.setUniversitySecondName(universityDto.secondName());
        university.setUniversityEmail(universityDto.email());
        university.setUniversitySurName(universityDto.surName());
        university.setUniversity(universityDto.university());

        universityRepository.save(university);
    }
}