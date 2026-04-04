package com.UmirHack2026.diploma_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.UmirHack2026.diploma_service", "com.example.support_module"})
@SpringBootApplication
public class DiplomaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiplomaServiceApplication.class, args);
	}

}
