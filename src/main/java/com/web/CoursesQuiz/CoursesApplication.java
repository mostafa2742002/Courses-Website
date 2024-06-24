package com.web.CoursesQuiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import com.braintreegateway.BraintreeGateway;
import com.web.CoursesQuiz.payments.config.BraintreeGatewayFactory;

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
	public static String DEFAULT_CONFIG_FILENAME = "config.properties";
    public static BraintreeGateway gateway;

	public static void main(String[] args) {
		File configFile = new File(DEFAULT_CONFIG_FILENAME);
        try {
            if(configFile.exists() && !configFile.isDirectory()) {
                gateway = BraintreeGatewayFactory.fromConfigFile(configFile);
            } else {
                gateway = BraintreeGatewayFactory.fromConfigMapping(System.getenv());
            }
        } catch (NullPointerException e) {
            System.err.println("Could not load Braintree configuration from config file or system environment.");
            System.exit(1);
        }
		SpringApplication.run(CoursesApplication.class, args);
	}
	// sudo systemctl start mongod
}