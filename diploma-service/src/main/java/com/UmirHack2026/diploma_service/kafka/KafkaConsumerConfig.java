package com.UmirHack2026.diploma_service.kafka;


import com.UmirHack2026.diploma_service.dto.ProfileDto;
import com.UmirHack2026.diploma_service.dto.UniversityDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String , ProfileDto> consumerFactory(ObjectMapper objectMapper){
        Map<String , Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG , "diploma-service");

        JsonDeserializer<ProfileDto> jsonDeserializer = new JsonDeserializer<>(ProfileDto.class ,objectMapper);
        jsonDeserializer.addTrustedPackages("*");


        return  new DefaultKafkaConsumerFactory<>(
                properties ,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , ProfileDto> kafkaListenerContainerFactory(
            ConsumerFactory<String , ProfileDto> consumerFactory){
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String , ProfileDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }

    @Bean
    public ConsumerFactory<String , UniversityDto> consumerFactoryUniversity(ObjectMapper objectMapper){
        Map<String , Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG , "diploma-service");

        JsonDeserializer<UniversityDto> jsonDeserializer = new JsonDeserializer<>(UniversityDto.class ,objectMapper);
        jsonDeserializer.addTrustedPackages("*");


        return  new DefaultKafkaConsumerFactory<>(
                properties ,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , UniversityDto> kafkaListenerContainerFactoryUniversity(
            ConsumerFactory<String , UniversityDto> consumerFactory){
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String , UniversityDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }


}
