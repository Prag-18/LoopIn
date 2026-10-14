# LoopIn - Campus Peer-to-Peer Item Borrowing & Lending Platform
## Spring Boot Application (Framework - Part 1)

This project implements the backend for **LoopIn** using **Java 23**, **Spring Boot 4.0.0**, and **Maven**, strictly following the Three-Tier Architecture pattern and Spring Boot auto-configuration best practices.

---

## 1. Application Name & Problem Description
- **Application Name**: `LoopIn`
- **Problem Description**: 
  College students frequently need items (calculators, lab coats, textbooks, bike pumps, DSLR cameras, multi-port adapters) for short durations. Buying brand-new items for temporary needs is expensive and wasteful. LoopIn connects verified students within their campus trust network (1st degree friends, 2nd degree friends of friends, and campus community) to request and lend items safely and frictionlessly.

---

## 2. Architecture & Request Flow
The application strictly implements the **Three-Tier Architecture**:

```
[ Client / Frontend (Vite/React or cURL) ]
                 │  HTTP (JSON)
                 ▼
[ Presentation Layer: RequestController / ListingController (@RestController) ]
                 │  Java Method Calls
                 ▼
[ Business Layer: RequestServiceImpl (@Service) ]
  • Enforces Business Rule 1 (Dates & Borrow Duration)
  • Enforces Business Rule 2 (Campus Email & Trust Radius)
  • Enforces Business Rule 3 (State Transition Integrity)
                 │  Data Access Calls
                 ▼
[ Data Access Layer: JdbcRequestRepository / JdbcListingRepository (@Repository) ]
                 │  Spring JdbcTemplate / HikariCP
                 ▼
[ MySQL Database (LoopIn) on port 3306 ]
  • Tables: item_requests, item_listings
```

---

## 3. MySQL Database & JDBC Configuration
The application reads its credentials from environment variables / `.env` with secure defaults:
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:LoopIn}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASS:Mysql@0108}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Connection Pool Settings
spring.datasource.hikari.pool-name=LoopInHikariPool
spring.datasource.hikari.maximum-pool-size=10
```

- Schema auto-generation: `src/main/resources/schema.sql`
- Seed data auto-population: `src/main/resources/data.sql`

---

## 3. Package Structure
```
backend/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/
├── src/
│   ├── main/
│   │   ├── java/com/loopin/
│   │   │   ├── LoopInApplication.java            # Spring Boot Entry Point (@SpringBootApplication)
│   │   │   ├── model/
│   │   │   │   ├── ItemRequest.java              # Domain Entity 1
│   │   │   │   └── ItemListing.java              # Domain Entity 2
│   │   │   ├── repository/
│   │   │   │   ├── RequestRepository.java        # Data Access Interface
│   │   │   │   ├── InMemoryRequestRepository.java# Repository Implementation (@Repository)
│   │   │   │   ├── ListingRepository.java        # Data Access Interface
│   │   │   │   └── InMemoryListingRepository.java# Repository Implementation (@Repository)
│   │   │   ├── service/
│   │   │   │   ├── RequestService.java           # Business Service Interface
│   │   │   │   └── RequestServiceImpl.java       # Service Implementation (@Service)
│   │   │   ├── controller/
│   │   │   │   ├── RequestController.java        # REST Presentation Layer (@RestController)
│   │   │   │   └── ListingController.java        # REST Presentation Layer (@RestController)
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java   # Exception Handling (@RestControllerAdvice)
│   │   └── resources/
│   │       └── application.properties            # Configuration file
│   └── test/
│       └── java/com/loopin/
│           ├── LoopInApplicationTests.java       # Spring Boot Context Test
│           ├── service/
│           │   └── RequestServiceTest.java       # Automated Business Rule Tests (JUnit 5)
│           └── controller/
│               └── RequestControllerTest.java    # Automated REST Endpoint Tests (Mockito/JUnit 5)
```

---

## 4. How to Run the Application

### Running with Maven Wrapper:
```bash
# In the backend directory:
.\mvnw.cmd spring-boot:run
```

### Running Automated Tests:
```bash
.\mvnw.cmd test
```
Result: **10/10 Tests Passed** (0 Failures, 0 Errors).
