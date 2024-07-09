# Course & Lesson Questions Application

This is a Spring Boot application for managing courses and lessons of questions. The application is secured with JWT and supports payments through Paymob and PayPal Braintree. API documentation is provided using Swagger.

## Features

- JWT Authentication
- Course and Lesson Management
- Question Management
- Payment Integration (Paymob & PayPal Braintree)
- API Documentation with Swagger

## Technologies Used

- Spring Boot
- JWT
- MongoDB
- Paymob
- PayPal Braintree
- Swagger

## Prerequisites

- Java 8 or higher
- Maven 3.6.0 or higher

## Getting Started

### Clone the repository

```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
```
Build the project
```
mvn clean install
```

Run the application
```
mvn spring-boot:run
```

## Accessing the application

-The application will be available at http://localhost:8080.
-Swagger UI will be available at http://localhost:8080/swagger-ui.html.

## Application Properties

Configure your application properties in src/main/resources/application.properties:
```
spring.application.name=courses
spring.data.mongodb.database=courses_app
# spring.data.mongodb.uri=
spring.data.mongodb.uri=

spring.mail.username=
spring.mail.password=
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# spring.cache.type=simple

DISCOUNT_VALUE=10

braintree.environment=
braintree.merchantId=
braintree.privateKey=
braintree.publicKey=
card.integration.id=
paymob.api.publicKey=
paymob.api.secretkey=
wallet.integration.id=
```
## Usage
### Authentication
Use JWT for authenticating API requests. Obtain a token by logging in with valid credentials, and include the token in the Authorization header of your requests:
```
Authorization: Bearer your-jwt-token
```

### API Documentation
#### Access the Swagger UI to explore and test the API endpoints:
```
http://localhost:8080/swagger-ui.html
```
