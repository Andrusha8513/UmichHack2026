package com.example.email_service.kafka;

import com.example.email_service.dto.EmailRequestDto;
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
    public ConsumerFactory<String , EmailRequestDto> consumerFactory(ObjectMapper objectMapper){
       Map<String , Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
       properties.put(ConsumerConfig.GROUP_ID_CONFIG , "email-service");

        JsonDeserializer<EmailRequestDto> jsonDeserializer = new JsonDeserializer<>(EmailRequestDto.class ,objectMapper);
        jsonDeserializer.addTrustedPackages("com.example.email_service.dto");


        return  new DefaultKafkaConsumerFactory<>(
                properties ,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , EmailRequestDto> kafkaListenerContainerFactory(
            ConsumerFactory<String , EmailRequestDto> consumerFactory){
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String , EmailRequestDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }
}
