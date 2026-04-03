package com.api_gateway.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class,
		ReactiveSecurityAutoConfiguration.class
})
@ComponentScan(
		basePackages = {"com.api_gateway.api_gateway", "com.example.support_module"},
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = "com.example.support_module.security.*"))
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
