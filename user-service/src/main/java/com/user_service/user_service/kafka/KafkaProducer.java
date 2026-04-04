package com.user_service.user_service.kafka;


import com.user_service.user_service.dto.EmailRequestDto;
import com.user_service.user_service.dto.ProfileDto;
import com.user_service.user_service.dto.UniversityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, EmailRequestDto> kafkaTemplate;


    private final KafkaTemplate<String , ProfileDto> kafkaTemplateProfile;

    private final KafkaTemplate<String , UniversityDto> kafkaTemplateUniversity;

    public void sendEmailToKafka(EmailRequestDto emailRequestDto) {
        kafkaTemplate.send("email", emailRequestDto);
        log.info("Отправил сообщение в kafka: to={}", emailRequestDto.getTo());
    }


    public void sendPrivetProfileToKafka(ProfileDto profileDto){
        kafkaTemplateProfile.send("profile" , profileDto);
        log.info("Отправил профиль в кафка: to={}" , profileDto.getEmail());
    }

    public void sendPrivetUniversityToKafka(UniversityDto universityDto){
        kafkaTemplateUniversity.send("university" , universityDto);
        log.info("Отправил профиль в кафка: to={}" , universityDto.getEmail());
    }
}
