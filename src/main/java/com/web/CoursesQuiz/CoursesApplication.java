package com.web.CoursesQuiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Courses REST API Documentation",
				description = "Math Courses REST API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Mostafa Mohamed",
						email = "mostafa19500mahmoud@gmail.com"
				)
		)
)
public class CoursesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoursesApplication.class, args);
	}
	// sudo systemctl start mongod
}