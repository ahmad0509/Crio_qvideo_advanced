CRIO TAKEHOME PROJECT
# 🎥 RentVideoAdvanced

RentVideo is a Spring Boot-based RESTful API for managing an online video rental system. The backend is powered by MySQL and features secure authentication, role-based authorization, and rental management functionalities.

## 🚀 Features

- **User Management**
  - Register with email, password (hashed with BCrypt), and role (defaults to CUSTOMER)
  - Login using JWT-based authentication
  - Roles: `CUSTOMER`, `ADMIN`

- **Video Management**
  - Browse available videos (open to all authenticated users)
  - Create, update, delete videos (restricted to `ADMIN` role)

- **Rental Management**
  - Rent videos (max 2 active rentals per user)
  - Return rented videos

- **Security**
  - JWT-based stateless authentication
  - Role-based access control
  - Public vs. Private endpoints

- **Robust Error Handling**
  - Proper HTTP status codes (`400`, `404`, etc.)

## 🛠️ Technologies Used

- Java 17
- Spring Boot
- Spring Security (JWT)
- MySQL
- JPA / Hibernate
- Maven
- JUnit + Mockito (for unit testing)

## 📦 Project Structure

```
com.crio.qvideorentaladvanced
│
├── Config/                      # Security configuration (JWT, roles, filters)
├── Controller/                 # REST controllers
├── DTO/                        # Request/response models
├── Entity/                     # JPA entities
├── Exception/                  # Custom exceptions
├── Repository/                 # Spring Data JPA repositories
├── Service/                    # Business logic
├── Util/                       # Helper methods (e.g., mappers)
└── Application.java            # Main Spring Boot application
```

## 🧪 Endpoints Summary

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST   | `/register` | Register a new user | Public |
| POST   | `/login`    | Login and get JWT | Public |
| GET    | `/videos`   | Get list of available videos | Authenticated |
| POST   | `/videos`   | Create a video | ADMIN only |
| PUT    | `/videos/{id}` | Update a video | ADMIN only |
| DELETE | `/videos/{id}` | Delete a video | ADMIN only |
| POST   | `/videos/{videoId}/rent` | Rent a video | Authenticated |
| POST   | `/videos/{videoId}/return` | Return a video | Authenticated |

## ✅ How to Run

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/rent-video.git
cd rent-video
```

2. **Configure application.properties**

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rentvideo
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
jwt.secret=your_jwt_secret
```

3. **Run the application**

```bash
mvn spring-boot:run
```

## 🧪 Run Tests

```bash
mvn test
```

## ✍️ Author

Ahmad Umair
