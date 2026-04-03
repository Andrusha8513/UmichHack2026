package com.user_service.user_service.kafka;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.user_service.dto.EmailRequestDto;
import com.user_service.user_service.dto.ProfileDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, EmailRequestDto> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        JsonSerializer<EmailRequestDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                serializer);
    }
    @Bean
    public KafkaTemplate<String , EmailRequestDto> kafkaTemplate(
            ProducerFactory<String , EmailRequestDto> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ProducerFactory<String , ProfileDto> profileDtoProducerFactory(ObjectMapper objectMapper){
        Map<String , Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , bootstrapServers);
        JsonSerializer<ProfileDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                config ,
                new StringSerializer(),
                serializer
        );
    }


    @Bean
    public KafkaTemplate<String , ProfileDto> kafkaTemplateProfile(
            ProducerFactory<String , ProfileDto> producerFactory){
                return new KafkaTemplate<>(producerFactory);
    }

}
