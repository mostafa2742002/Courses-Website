# 📚 Courses-Website - Math Learning Platform

A modern, full-featured e-learning platform built with **Spring Boot** for delivering interactive math courses with quizzes, lessons, chapters, and integrated payment processing.

---

## 🎯 Project Overview

**Courses-Website** is a comprehensive REST API-based learning management system designed for delivering structured math education content. It provides a complete ecosystem for course management, student progression tracking, and assessment through lessons and quizzes.

### Key Features
- ✅ **Course Management** - Create, organize, and manage math courses
- ✅ **Chapter Structure** - Organize courses into logical chapters
- ✅ **Interactive Lessons** - Deliver lessons with integrated questions and answers
- ✅ **Quiz System** - Multiple-choice and answer-based assessments
- ✅ **Student Progress Tracking** - Monitor solved lessons and course completion
- ✅ **Multi-Payment Integration** - PayPal, Braintree, and Paymob support
- ✅ **JWT Authentication** - Secure user authentication and authorization
- ✅ **Email Notifications** - Automated email delivery for notifications
- ✅ **CORS Support** - Frontend-backend integration ready
- ✅ **API Documentation** - Built-in Swagger/OpenAPI documentation

---

## 🛠️ Tech Stack

| Layer | Technologies |
|-------|--------------|
| **Framework** | Spring Boot 3.2.5 |
| **Language** | Java 17 |
| **Database** | MongoDB (Cloud) |
| **Security** | Spring Security + JWT |
| **ORM** | Spring Data MongoDB |
| **Authentication** | JWT (java-jwt, JJWT) |
| **Email** | Spring Mail + Gmail SMTP |
| **Payment Gateways** | PayPal, Braintree, Paymob |
| **API Documentation** | Springdoc OpenAPI/Swagger UI |
| **Build Tool** | Maven |
| **Frontend Framework** | Bootstrap 4, jQuery |
| **Utilities** | Lombok, Google Guava |

---

## 📊 Project Architecture

```
Courses-Website/
├── src/main/java/com/web/CoursesQuiz/
│   ├── CoursesApplication.java           # Main Spring Boot Application
│   │
│   ├── configuration/                    # Application Configuration
│   │   ├── AppConfig.java
│   │   ├── CorsConfig.java
│   │   ├── MongoConfig.java
│   │   ├── SecurityConfig.java
│   │   └── SpringSecurityAuditorAware.java
│   │
│   ├── course/                           # Course Module
│   │   ├── controller/                   # REST Endpoints
│   │   ├── service/                      # Business Logic
│   │   ├── entity/                       # Data Models
│   │   ├── repo/                         # Data Access Layer
│   │   └── dto/                          # Data Transfer Objects
│   │
│   ├── chapter/                          # Chapter Module
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   └── repo/
│   │
│   ├── lesson/                           # Lesson Module
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/                       # Lesson, Question, Answer, SolvedLesson
│   │   ├── repo/
│   │   └── dto/
│   │
│   ├── user/                             # User Management Module
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   └── repo/
│   │
│   ├── payments/                         # Payment Processing Module
│   │   ├── paypal/                       # PayPal Integration
│   │   ├── braintree/                    # Braintree Integration
│   │   ├── paymob/                       # Paymob Integration
│   │   └── config/                       # Payment Configurations
│   │
│   ├── jwt/                              # JWT Security
│   │   ├── JwtService.java
│   │   ├── JwtFilter.java
│   │   └── JwtResponse.java
│   │
│   ├── email/                            # Email Service
│   │   ├── EmailService.java
│   │   └── Mail.java
│   │
│   ├── exception/                        # Custom Exception Handling
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UserAlreadyExistsException.java
│   │
│   └── constants/                        # Application Constants
│       └── ServerConstants.java
│
├── src/main/resources/
│   ├── application.properties             # Configuration Properties
│   ├── static/
│   │   └── pay.html                       # Payment Page
│   └── templates/
│       ├── home.html
│       ├── success.html
│       └── cancel.html
│
└── pom.xml                                # Maven Configuration
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **MongoDB** (Local or Atlas Cloud)
- **Git**

### Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/mostafa2742002/Courses-Website.git
   cd Courses-Website
   ```

2. **Configure Application Properties**
   
   Update `src/main/resources/application.properties` with your configuration:
   ```properties
   # MongoDB Configuration
   spring.data.mongodb.uri=mongodb+srv://<user>:<password>@cluster.mongodb.net/courses_app

   # Email Configuration
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password

   # Payment Gateway Credentials
   # PayPal
   paypal.client.id=YOUR_CLIENT_ID
   paypal.client.secret=YOUR_CLIENT_SECRET

   # Braintree
   braintree.publicKey=YOUR_PUBLIC_KEY
   braintree.privateKey=YOUR_PRIVATE_KEY
   braintree.merchantId=YOUR_MERCHANT_ID

   # Paymob
   paymob.api.publicKey=YOUR_PUBLIC_KEY
   paymob.api.secretkey=YOUR_SECRET_KEY
   ```

3. **Build the Project**
   ```bash
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or using Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the Application**
   - **API Swagger Documentation**: `http://localhost:8080/swagger-ui.html`
   - **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 📡 Core Modules & API Endpoints

### 1. Course Module
Manage course structure and content
```
GET    /api/courses              - Get all courses
POST   /api/courses              - Create new course
GET    /api/courses/{id}         - Get course details
PUT    /api/courses/{id}         - Update course
DELETE /api/courses/{id}         - Delete course
```

### 2. Chapter Module
Organize lessons within courses
```
GET    /api/chapters             - Get all chapters
POST   /api/chapters             - Create new chapter
GET    /api/chapters/{id}        - Get chapter details
PUT    /api/chapters/{id}        - Update chapter
DELETE /api/chapters/{id}        - Delete chapter
```

### 3. Lesson Module
Create and manage interactive lessons
```
GET    /api/lessons              - Get all lessons
POST   /api/lessons              - Create new lesson
GET    /api/lessons/{id}         - Get lesson details
PUT    /api/lessons/{id}         - Update lesson
DELETE /api/lessons/{id}         - Delete lesson
POST   /api/lessons/{id}/solve   - Submit lesson answers
```

### 4. User Module
User registration, authentication, and management
```
POST   /api/auth/register        - Register new user
POST   /api/auth/login           - User login (returns JWT)
GET    /api/users/{id}           - Get user profile
PUT    /api/users/{id}           - Update user profile
GET    /api/users/{id}/progress  - Get user learning progress
```

### 5. Payment Module
Integrated payment processing
```
POST   /api/payments/paypal      - PayPal payment
POST   /api/payments/braintree   - Braintree payment
POST   /api/payments/paymob      - Paymob payment
GET    /api/payments/{id}        - Get payment status
```

---

## 🔐 Security Features

- **JWT Authentication** - Stateless token-based authentication
- **Spring Security** - Integrated security framework
- **CORS Configuration** - Cross-origin resource sharing support
- **Input Validation** - Bean validation for data integrity
- **Password Encryption** - Secure password handling
- **Audit Trail** - Track entity creation and modification with `AuditableBase`

### Authentication Flow
1. User registers or logs in
2. Server returns JWT token
3. Client includes token in `Authorization: Bearer <token>` header
4. JwtFilter validates token on each request
5. Authorized access to protected resources

---

## 📧 Email Service

Automated email notifications for:
- User registration confirmations
- Password reset instructions
- Course enrollment confirmations
- Quiz completion notifications

Configured with Gmail SMTP for reliable delivery.

---

## 💳 Payment Integration

### Supported Payment Gateways
1. **PayPal** - Industry-standard payment solution
2. **Braintree** - Full-stack payment platform
3. **Paymob** - Local payment processing (Egypt)

Each gateway is configurable via `application.properties` with sandbox/production modes.

---

## 🗄️ Database Schema

### Key Collections (MongoDB)
- **courses** - Course information and metadata
- **chapters** - Chapter structure within courses
- **lessons** - Individual lesson content
- **questions** - Quiz questions for lessons
- **users** - User accounts and profiles
- **solvedLessons** - Student progress and answers
- **solvedCourses** - Course completion tracking
- **payments** - Payment transaction records

---

## 🧪 Testing

Run tests with:
```bash
mvn test
```

Test files located in `src/test/java/com/web/courses/`

---

## 📦 Docker Support

Build and run with Docker:

```bash
# Build Docker image
docker build -t courses-website .

# Run container
docker run -p 8080:8080 courses-website
```

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author & Contact

**Mostafa Mohamed**
- 📧 Email: [mostafa19500mahmoud@gmail.com](mailto:mostafa19500mahmoud@gmail.com)
- 🐙 GitHub: [@mostafa2742002](https://github.com/mostafa2742002)

---

## 🎓 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [JWT Introduction](https://jwt.io/introduction)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web/)

---

## 📊 Project Stats

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Database**: MongoDB
- **Lines of Code**: Building sophisticated e-learning solution
- **Status**: Active Development

---

## ⭐ If you find this project useful, please give it a star!

