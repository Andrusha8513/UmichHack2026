//package com.example.profile_service.feignClient;
//
//import feign.codec.Encoder;
//import feign.form.spring.SpringFormEncoder;
//import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
//import org.springframework.cloud.openfeign.support.SpringEncoder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Scope;
//import org.springframework.http.converter.ByteArrayHttpMessageConverter;
//
//@Configuration
//public class FeignMultipartConfig {
//
//    @Bean
//    @Primary
//    @Scope("prototype")
//    public Encoder multipartFormEncoder() {
//        return new SpringFormEncoder(new SpringEncoder(() ->
//                new HttpMessageConverters(new ByteArrayHttpMessageConverter())));
//    }
//}