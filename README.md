# LoopIn - Campus Peer-to-Peer Item Borrowing & Lending Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-23-orange.svg)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-19.0-blue.svg)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-6.1-purple.svg)](https://vitejs.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Build Status](https://img.shields.io/badge/Tests-10%2F10%20Passed-success.svg)]()

> **LoopIn** is a full-stack campus community platform that enables students to borrow and lend essential gear (graphing calculators, lab coats, textbooks, electronics, and cameras) through a tiered trust radius (1st-degree friends, 2nd-degree mutual connections, and campus-wide).

---

## 1. Project Structure

The project is structured into independent **frontend** and **backend** layers:

```text
LoopIn/
├── frontend/                              # Client-side React Application
│   ├── package.json                       # Frontend dependencies & scripts
│   ├── vite.config.js                     # Vite build configuration
│   ├── index.html                         # SPA Entry HTML
│   ├── dist/                              # Production build bundle
│   └── src/
│       ├── main.jsx                       # React DOM root entry
│       ├── App.jsx                        # Routing and global layout
│       ├── index.css                      # Global styles & design system
│       ├── context/
│       │   └── AppContext.jsx             # React State & borrow request context
│       ├── components/
│       │   ├── Navbar.jsx                 # Navigation component
│       │   ├── Hero.jsx                   # Hero banner component
│       │   └── Footer.jsx                 # Footer component
│       └── pages/
│           ├── Home.jsx                   # Landing page
│           ├── Listings.jsx               # Available gear listings & campus feed
│           ├── Request.jsx                # Item borrow request form
│           ├── SignIn.jsx                 # Student login page
│           └── SignUp.jsx                 # Student registration page
│
├── backend/                               # Server-side Spring Boot Application
│   ├── pom.xml                            # Maven project object model & dependencies
│   ├── mvnw / mvnw.cmd                    # Maven wrapper executable scripts
│   ├── .env                               # Database environment variables
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/loopin/
│   │   │   │   ├── LoopInApplication.java           # Application entry point (@SpringBootApplication)
│   │   │   │   ├── DatabaseConnectionRunner.java    # Startup JDBC connection diagnostics
│   │   │   │   ├── controller/
│   │   │   │   │   ├── RequestController.java       # Presentation: Requests REST Controller
│   │   │   │   │   └── ListingController.java       # Presentation: Listings REST Controller
│   │   │   │   ├── service/
│   │   │   │   │   ├── RequestService.java          # Business logic interface
│   │   │   │   │   └── RequestServiceImpl.java      # Service implementation (Business Rules 1, 2, 3)
│   │   │   │   ├── repository/
│   │   │   │   │   ├── RequestRepository.java       # Data access interface for requests
│   │   │   │   │   ├── JdbcRequestRepository.java   # MySQL JDBC repository (@Primary)
│   │   │   │   │   ├── InMemoryRequestRepository.java # In-memory fallback repository
│   │   │   │   │   ├── ListingRepository.java       # Data access interface for listings
│   │   │   │   │   ├── JdbcListingRepository.java   # MySQL JDBC listing repository (@Primary)
│   │   │   │   │   └── InMemoryListingRepository.java # In-memory listing repository
│   │   │   │   ├── model/
│   │   │   │   │   ├── ItemRequest.java             # Domain Entity 1 (Borrow requests)
│   │   │   │   │   └── ItemListing.java             # Domain Entity 2 (Lender listings)
│   │   │   │   └── exception/
│   │   │   │       └── GlobalExceptionHandler.java  # Centralized REST error handler
│   │   │   └── resources/
│   │   │       ├── application.properties           # Spring Boot & HikariCP datasource settings
│   │   │       ├── schema.sql                       # MySQL DDL table initialization
│   │   │       └── data.sql                         # MySQL DML seed data
│   │   └── test/
│   │       └── java/com/loopin/
│   │           ├── LoopInApplicationTests.java      # Auto-configuration & context test
│   │           ├── service/
│   │           │   └── RequestServiceTest.java      # JUnit 5 business rule unit tests (6 tests)
│   │           └── controller/
│   │               └── RequestControllerTest.java   # Mockito presentation layer tests (3 tests)
│
├── .gitignore                             # Root git ignore
└── README.md                              # Main documentation (this file)
```

---

## 2. Three-Tier Architecture & Data Flow

```
[ Client / Frontend (Vite/React or Browser/Postman) ]
                        │
                        │ HTTP / JSON (Port 8080)
                        ▼
┌─────────────────────────────────────────────────────────────┐
│ 1. PRESENTATION LAYER (Spring Boot Web / Controllers)       │
│    • RequestController (@RestController, @RequestMapping)   │
│    • ListingController (@RestController, @RequestMapping)   │
│    • GlobalExceptionHandler (@RestControllerAdvice)         │
└──────────────────────────────┬──────────────────────────────┘
                               │ Method Calls
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. BUSINESS LAYER (Spring Service Layer)                    │
│    • RequestService / RequestServiceImpl (@Service)         │
│      ├── Rule 1: Dates & Borrow Duration (<= 30 days)       │
│      ├── Rule 2: Campus Member (.edu/.ac.in) & Radius (1-3) │
│      └── Rule 3: State Transition Integrity (OPEN only)     │
└──────────────────────────────┬──────────────────────────────┘
                               │ Repository Calls
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. DATA ACCESS LAYER (Spring JDBC & Repositories)           │
│    • RequestRepository / JdbcRequestRepository (@Primary)   │
│    • ListingRepository / JdbcListingRepository (@Primary)   │
│    • Spring JdbcTemplate & HikariCP Connection Pool         │
└──────────────────────────────┬──────────────────────────────┘
                               │ SQL Queries & Updates
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ PERSISTENCE LAYER (MySQL 8.0 Database on Port 3306)         │
│    • Database: LoopIn                                       │
│    • Tables: item_requests, item_listings                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Business Rules Implemented (Service Layer)

1. **Business Rule 1: Borrow Duration & Date Integrity**:
   - `startDate` and `endDate` are mandatory.
   - `endDate` cannot be earlier than `startDate`.
   - Maximum borrow duration cannot exceed **30 days** per campus circulation rules.
   - Throws `IllegalArgumentException` $\rightarrow$ Handled globally as **HTTP 400 Bad Request**.

2. **Business Rule 2: Campus Member Verification & Trust Radius Boundary**:
   - `itemName` cannot be blank.
   - `trustRadius` must be strictly between 1 and 3 ($1 = \text{Friends}$, $2 = \text{Mutuals}$, $3 = \text{Campus-wide}$).
   - `requesterEmail` must belong to an academic institutional domain (`.edu`, `.ac.in`).
   - Throws `IllegalArgumentException` $\rightarrow$ Handled globally as **HTTP 400 Bad Request**.

3. **Business Rule 3: State Transition Integrity**:
   - Only requests with status `OPEN` can be updated to `FULFILLED` or `CANCELLED`.
   - Completed or cancelled requests cannot be modified.
   - Throws `IllegalStateException` $\rightarrow$ Handled globally as **HTTP 409 Conflict**.

---

## 4. REST API Documentation

Base URL: `http://localhost:8080`

| Method | Endpoint | Description | Request Body / Param | Response Code |
|---|---|---|---|---|
| `GET` | `/api/requests` | Retrieve all borrow requests | None | `200 OK` |
| `GET` | `/api/requests/{id}` | Retrieve request by ID | Path variable `id` | `200 OK` / `404 Not Found` |
| `POST` | `/api/requests` | Create a new borrow request | JSON `ItemRequest` | `201 CREATED` / `400 Bad Request` |
| `PUT` | `/api/requests/{id}/status` | Update request status | Query param `status=FULFILLED` | `200 OK` / `409 Conflict` |
| `GET` | `/api/requests/{id}/matches` | Match available listings | Path variable `id` | `200 OK` |
| `GET` | `/api/listings` | Retrieve gear listings | Optional query param `category` | `200 OK` |

### Sample JSON Request Body (`POST /api/requests`):
> **Note**: Always include header `Content-Type: application/json`.
```json
{
  "itemName": "Anker USB-C Multi-Port Hub",
  "category": "Electronics",
  "requesterName": "Jordan Lee",
  "requesterEmail": "jordan.l@campus.edu",
  "startDate": "2026-10-18",
  "endDate": "2026-10-22",
  "trustRadius": 2,
  "note": "Presenting my capstone demo"
}
```

---

## 5. Prerequisites & Getting Started

### Prerequisites:
- **Java**: JDK 17+ (JDK 23 verified)
- **Node.js**: v18+ (v20+ recommended)
- **MySQL**: MySQL Server 8.0 running locally on port 3306

---

### Step 1: Database Setup
Make sure MySQL is running on port 3306. The database credentials in `backend/.env` are:
```properties
DB_HOST=localhost
DB_USER=root
DB_PASS=Mysql@0108
DB_NAME=LoopIn
DB_PORT=3306
```
*(The application will automatically execute `CREATE DATABASE IF NOT EXISTS LoopIn` and seed `schema.sql` and `data.sql` on startup).*

---

### Step 2: Running the Backend (Spring Boot)

Open a terminal and navigate to `backend`:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
The server will start at **`http://localhost:8080`**.

#### Running Backend Automated Tests:
```powershell
cd backend
.\mvnw.cmd test
```
All **10 tests** (Controller, Service, Context) will execute and pass:
```text
[INFO] Results:
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### Step 3: Running the Frontend (React + Vite)

Open a second terminal and navigate to `frontend`:
```powershell
cd frontend
npm install
npm run dev
```
The client app will launch at **`http://localhost:5173`**.

---

## 6. Activity Submission Checklist

- [x] **1. Development Environment**: Java 23, Spring Boot 4.0.0, Maven wrapper (`mvnw.cmd`).
- [x] **2. Skeleton Web Application**: Spring Boot REST application with embedded Tomcat on port 8080.
- [x] **3. Auto-Configuration**: Demonstrated through `@SpringBootApplication`, Jackson, HikariCP, and DispatcherServlet.
- [x] **4. Three-Tier Architecture**: Controller $\rightarrow$ Service $\rightarrow$ Repository $\rightarrow$ MySQL.
- [x] **5. Domain Modeling**: `ItemRequest` and `ItemListing` entities.
- [x] **6. Business Logic**: 3 distinct business rules implemented in `RequestServiceImpl`.
- [x] **7. Presentation Layer**: REST endpoints returning structured JSON and standard HTTP statuses.
- [x] **8. Testing Basics**: Identified test cases with Input, Expected Output, Actual Output, PASS.
- [x] **9. Automated Testing in Spring Boot**: JUnit 5 and Mockito tests passing (`mvnw test`).
- [x] **10. Application Flow**: Documented flow from Client to MySQL database.
- [x] **11. Clean Folder Structure**: Separated into `frontend/` and `backend/`.
- [x] **12. Screenshot 12**: Console showing Spring Boot banner, Tomcat port 8080, and JDBC success.
- [x] **13. Screenshot 13**: Browser/Postman showing JSON output of `GET /api/requests`.
- [x] **14. Auto-Configuration Explanation**: Documented in `spring_boot_activity_submission.md`.
