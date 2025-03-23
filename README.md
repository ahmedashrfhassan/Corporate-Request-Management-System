# Corporate Request Management System

This Spring Boot application provides a comprehensive solution for managing corporate requests, with CRUD operations for users and request management functionalities.

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database
- REST APIs
- Docker
- Maven
- JUnit and Mockito for testing

## Prerequisites

- JDK 17 or later
- Maven 3.6+
- Docker (for containerized deployment)

## Getting Started

### Local Development Setup

1. **Clone the repository**
   link to the repo [Request Management System Repository](https://github.com/ahmedashrfhassan/Corporate-Request-Management-System)
   ```bash
   git clone https://github.com/ahmedashrfhassan/Corporate-Request-Management-System.git
   cd corporate-request-system
   ```

2. **Build the application**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

   The application will be available at `http://localhost:8080`

### Running Tests

To run all tests:
```bash
./mvnw test
```

### Generating Coverage Reports

This project uses JaCoCo for code coverage reporting:

1. **Generate coverage report**
   ```bash
   ./mvnw clean verify
   ```

2. **Access the coverage report**
   The detailed HTML report will be available at:
   ```
   target/site/jacoco/index.html
   ```

## Docker Setup

### Building and Running with Docker

1. **Build the Docker image**
   ```bash
   docker build -t app .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 -v h2-data:/data/db app
   ```

## API Documentation

### User Endpoints

- **Create User**: `POST /api/users`
- **Get User by ID**: `GET /api/users/{id}`
- **Update User**: `PUT /api/users/{id}`
- **Delete User**: `DELETE /api/users/{id}`

### Attachment Endpoints

- **Upload Attachment**: `POST /api/attachments/upload`
- **Get Attachment by ID**: `GET /api/attachments/{id}`
- **Download Attachment**: `DELETE /api/attachments/download/{id}`

### Request Endpoints

- **Create Request**: `POST /api/requests`
- **Get Request by ID**: `GET /api/requests/{id}`
- **Delete Request**: `DELETE /api/requests/{id}`

### Accessing Swagger UI

Once the application is running, you can interact with the API:

- Swagger UI: Open a browser and navigate to `http://localhost:8080/swagger-ui.html`

### OpenAPI JSON or YAML Specification

You can also access the OpenAPI specification in the following formats:

- JSON: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

## Database

The application uses H2 database with file storage configured through Docker volumes to persist data between container restarts.

H2 Console is available at: `http://localhost:8080/h2-console` with the following configuration:
- JDBC URL: `jdbc:h2:file:/data/db/corporate-requests`
- Username: `sa`
- Password: `password`

## Code Quality

The project uses various plugins to ensure code quality:

- Checkstyle
- PMD
- JaCoCo
