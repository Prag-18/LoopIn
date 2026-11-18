# LoopIn - Campus Peer-to-Peer Item Borrowing & Lending Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-23-orange.svg)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-19.0-blue.svg)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-6.1-purple.svg)](https://vitejs.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![H2](https://img.shields.io/badge/H2-In--Memory-lightgrey.svg)](https://www.h2database.com/)
[![Build Status](https://img.shields.io/badge/Tests-15%2F15%20Passed-success.svg)]()

> **LoopIn** is a full-stack campus community platform that enables students to borrow and lend essential gear (graphing calculators, lab coats, textbooks, electronics, and cameras) through a tiered trust radius (1st-degree friends, 2nd-degree mutual connections, and campus-wide). It is built as a **microservices architecture** with two independent Spring Boot services and a React frontend.

---

## 1. Project Structure

The project is organized into three independent layers — **frontend**, **backend (Core Service)**, and **trust-service (Trust & Safety Microservice)**:

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
│       │   ├── Navbar.jsx                 # Navigation component with Circle badge & hosted count
│       │   ├── Hero.jsx                   # Hero banner component with Explore/Hosted switcher
│       │   ├── Footer.jsx                 # Footer component
│       │   ├── ProtectedRoute.jsx         # Route authentication guard
│       │   ├── HostItemModal.jsx          # Modal dialog to host gear for the campus network
│       │   └── CircleModal.jsx            # Campus friend circle management modal
│       └── pages/
│           ├── Home.jsx                   # Landing page
│           ├── Listings.jsx               # Explore campus gear feed (community items)
│           ├── HostedItems.jsx            # Dedicated view for user's hosted gear & requests
│           ├── Request.jsx                # Item borrow request form
│           ├── SignIn.jsx                 # Student login page
│           └── SignUp.jsx                 # Student registration page
│
├── backend/                               # Microservice 1 — Core Borrowing Service
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
├── trust-service/                         # Microservice 2 — Trust & Safety Verification Service
│   ├── pom.xml                            # Maven project object model & dependencies
│   ├── mvnw / mvnw.cmd                    # Maven wrapper executable scripts
│   └── src/
│       ├── main/
│       │   ├── java/com/loopin/trust/
│       │   │   ├── TrustServiceApplication.java     # Application entry point (port 8081)
│       │   │   ├── DatabaseInitializer.java         # H2 seed data initializer
│       │   │   ├── config/
│       │   │   │   └── H2ConsoleConfig.java         # H2 web console configuration
│       │   │   ├── controller/
│       │   │   │   └── TrustController.java         # Trust REST Controller (/api/trust/*)
│       │   │   ├── dto/
│       │   │   │   ├── TrustVerificationRequest.java  # Inter-service request DTO
│       │   │   │   └── TrustVerificationResponse.java # Verification result DTO
│       │   │   ├── model/
│       │   │   │   └── TrustProfile.java            # JPA Entity (trust_profiles table)
│       │   │   ├── repository/
│       │   │   │   └── TrustProfileRepository.java  # Spring Data JPA repository
│       │   │   └── service/
│       │   │       ├── TrustService.java            # Business logic interface
│       │   │       └── TrustServiceImpl.java        # Service implementation (Rules 1 & 2)
│       │   └── resources/
│       │       └── application.properties           # H2 datasource & JPA settings
│       └── test/
│           └── java/com/loopin/trust/
│               ├── TrustServiceApplicationTests.java  # Context load test
│               ├── controller/
│               │   └── TrustControllerTest.java     # MockMvc controller test (1 test)
│               └── service/
│                   └── TrustServiceTest.java        # JUnit 5 service unit tests (3 tests)
│
├── .gitignore                             # Root git ignore
└── README.md                              # Main documentation (this file)
```

---

## 2. Microservices Architecture & Data Flow

LoopIn runs as two independent Spring Boot microservices communicating over HTTP:

```
[ Client / Frontend (Vite/React — Port 5173) ]
                        │
                        │ HTTP / JSON
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│          MICROSERVICE 1 — Core Borrowing Service (Port 8080)    │
│                                                                 │
│  1. PRESENTATION:  RequestController, ListingController         │
│                    GlobalExceptionHandler                       │
│                         │                                       │
│  2. BUSINESS:      RequestServiceImpl                           │
│                    (Rules: Dates, Campus email, State machine)  │
│                         │                          │            │
│  3. DATA ACCESS:   JdbcRequestRepository      ─────┘            │
│                    JdbcListingRepository                        │
└─────────────┬───────────────────────────────────────────────────┘
              │ HTTP POST /api/trust/verify (inter-service call)
              ▼
┌─────────────────────────────────────────────────────────────────┐
│       MICROSERVICE 2 — Trust & Safety Service (Port 8081)       │
│                                                                 │
│  1. PRESENTATION:  TrustController (/api/trust/*)               │
│                         │                                       │
│  2. BUSINESS:      TrustServiceImpl                             │
│                    (Rules: Radius eligibility, Auto-provision)  │
│                         │                                       │
│  3. DATA ACCESS:   TrustProfileRepository (Spring Data JPA)     │
└─────────────────────────────┬───────────────────────────────────┘
                              │ SQL (H2 In-Memory)
                              ▼
                  ┌───────────────────────────┐
                  │  H2 In-Memory Database    │
                  │  Table: trust_profiles    │
                  └───────────────────────────┘

              Microservice 1 also writes to:
┌─────────────────────────────────────────────────────────────────┐
│          PERSISTENCE — MySQL 8.0 (Port 3306)                    │
│    Database: LoopIn                                             │
│    Tables: item_requests, item_listings                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Business Rules Implemented

### Microservice 1 — Core Service (`RequestServiceImpl`)

1. **Business Rule 1: Borrow Duration & Date Integrity**
   - `startDate` and `endDate` are mandatory.
   - `endDate` cannot be earlier than `startDate`.
   - Maximum borrow duration cannot exceed **30 days** per campus circulation rules.
   - Throws `IllegalArgumentException` → Handled globally as **HTTP 400 Bad Request**.

2. **Business Rule 2: Campus Member Verification & Trust Radius Boundary**
   - `itemName` cannot be blank.
   - `trustRadius` must be strictly between 1 and 3 (1 = Friends, 2 = Mutuals, 3 = Campus-wide).
   - `requesterEmail` must belong to an academic institutional domain (`.edu`, `.ac.in`).
   - Throws `IllegalArgumentException` → Handled globally as **HTTP 400 Bad Request**.

3. **Business Rule 3: State Transition Integrity**
   - Only requests with status `OPEN` can be updated to `FULFILLED` or `CANCELLED`.
   - Completed or cancelled requests cannot be modified.
   - Throws `IllegalStateException` → Handled globally as **HTTP 409 Conflict**.

### Microservice 2 — Trust Service (`TrustServiceImpl`)

4. **Business Rule 4: Trust Radius Eligibility**
   - Campus-wide search (`radius = 3`) requires a minimum trust score of **60** and **zero violations**.
   - If ineligible, the radius is capped at 2 (2nd-degree mutuals) and `RADIUS_RESTRICTED_LOW_TRUST_SCORE` is returned.

5. **Business Rule 5: Auto-Provisioning of Trust Profiles**
   - If a student has no existing profile, one is automatically created on first verification.
   - Academic domain emails (`.edu`, `.ac.in`) receive an initial score of **75 (SILVER badge)**.
   - Non-academic emails receive **50 (BRONZE badge)**.

---

## 4. REST API Documentation

### Microservice 1 — Core Service (`http://localhost:8080`)

| Method | Endpoint | Description | Request Body / Param | Response Code |
|---|---|---|---|---|
| `GET` | `/api/requests` | Retrieve all borrow requests | None | `200 OK` |
| `GET` | `/api/requests/{id}` | Retrieve request by ID | Path variable `id` | `200 OK` / `404 Not Found` |
| `POST` | `/api/requests` | Create a new borrow request | JSON `ItemRequest` | `201 CREATED` / `400 Bad Request` |
| `PUT` | `/api/requests/{id}/status` | Update request status | Query param `status=FULFILLED` | `200 OK` / `409 Conflict` |
| `GET` | `/api/requests/{id}/matches` | Match available listings | Path variable `id` | `200 OK` |
| `GET` | `/api/listings` | Retrieve gear listings | Optional query param `category` | `200 OK` |

#### Sample JSON — `POST /api/requests`
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

### Microservice 2 — Trust Service (`http://localhost:8081`)

| Method | Endpoint | Description | Request Body / Param | Response Code |
|---|---|---|---|---|
| `POST` | `/api/trust/verify` | Verify a student's trust eligibility for a borrow attempt | JSON `TrustVerificationRequest` | `200 OK` |
| `GET` | `/api/trust/profile` | Retrieve a student's trust profile | Query param `email` | `200 OK` / `404 Not Found` |
| `GET` | `/api/trust/profiles` | Retrieve all community trust profiles | None | `200 OK` |
| `GET` | `/api/trust/stats` | Retrieve aggregate trust statistics | None | `200 OK` |
| `GET` | `/h2-console` | H2 web console (development only) | — | — |

#### Sample JSON — `POST /api/trust/verify`
```json
{
  "studentEmail": "alex.m@campus.edu",
  "requestedRadius": 3,
  "itemName": "Graphing Calculator"
}
```

#### Sample Response
```json
{
  "studentEmail": "alex.m@campus.edu",
  "verified": true,
  "trustScore": 75,
  "statusMessage": "VERIFIED_CAMPUS_MEMBER",
  "maxAllowedRadius": 3
}
```

---

## 5. Trust Profile Model

The `TrustProfile` JPA entity stored in the H2 in-memory database (`trust_profiles` table):

| Field | Type | Description |
|---|---|---|
| `id` | Long | Auto-generated primary key |
| `studentEmail` | String | Unique student identifier |
| `trustScore` | int | Community standing score (0–100) |
| `campusVerified` | boolean | Academic domain badge |
| `completedLoans` | int | Successful loan completions |
| `violationCount` | int | Policy violations |
| `badgeLevel` | String | `GOLD` (≥85) / `SILVER` (≥70) / `BRONZE` (<70) |
| `lastEvaluatedAt` | LocalDateTime | Timestamp of last update |

---

## 6. Prerequisites & Getting Started

### Prerequisites
- **Java**: JDK 17+ (JDK 23 verified)
- **Node.js**: v18+ (v20+ recommended)
- **MySQL**: MySQL Server 8.0 running locally on port 3306

---

### Step 1: Database Setup

Make sure MySQL is running on port 3306. The database credentials in `backend/.env` are:
```properties
DB_HOST=localhost
DB_USER=root
DB_PASS=your_mysql_password
DB_NAME=LoopIn
DB_PORT=3306
```
*(The application will automatically execute `CREATE DATABASE IF NOT EXISTS LoopIn` and seed `schema.sql` and `data.sql` on startup.)*

The Trust Service uses an **H2 in-memory database** — no additional setup required.

---

### Step 2: Running the Core Backend (Microservice 1 — Port 8080)

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The server will start at **`http://localhost:8080`**.

#### Running Core Service Tests:
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

### Step 3: Running the Trust Service (Microservice 2 — Port 8081)

Open a second terminal:
```powershell
cd trust-service
.\mvnw.cmd spring-boot:run
```

The service will start at **`http://localhost:8081`**.  
H2 console available at **`http://localhost:8081/h2-console`** (JDBC URL: `jdbc:h2:mem:trustdb`).

#### Running Trust Service Tests:
```powershell
cd trust-service
.\mvnw.cmd test
```
All **5 tests** (Controller, Service, Context) will execute and pass:
```text
[INFO] Results:
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### Step 4: Running the Frontend (React + Vite — Port 5173)

Open a third terminal:
```powershell
cd frontend
npm install
npm run dev
```

The client app will launch at **`http://localhost:5173`**.

---

## 7. Activity Submission Checklist

- [x] **1. Development Environment**: Java 23, Spring Boot 4.0.0, Maven wrapper (`mvnw.cmd`).
- [x] **2. Skeleton Web Application**: Two Spring Boot REST services with embedded Tomcat (ports 8080 & 8081).
- [x] **3. Auto-Configuration**: Demonstrated through `@SpringBootApplication`, Jackson, HikariCP, Spring Data JPA, and DispatcherServlet.
- [x] **4. Three-Tier Architecture**: Controller → Service → Repository → MySQL (Service 1) / H2 (Service 2).
- [x] **5. Domain Modeling**: `ItemRequest`, `ItemListing` (Service 1) and `TrustProfile` JPA Entity (Service 2).
- [x] **6. Business Logic**: 5 distinct business rules across both microservices.
- [x] **7. Presentation Layer**: REST endpoints returning structured JSON and standard HTTP statuses.
- [x] **8. Testing Basics**: Identified test cases with Input, Expected Output, Actual Output, PASS.
- [x] **9. Automated Testing**: JUnit 5 and Mockito tests passing in both services (`mvnw test`).
- [x] **10. Application Flow**: Documented flow from Client → Microservice 1 → Microservice 2 → Databases.
- [x] **11. Clean Folder Structure**: Separated into `frontend/`, `backend/`, and `trust-service/`.
- [x] **12. Microservices**: Two independent Spring Boot services with inter-service HTTP communication.
- [x] **13. Screenshot**: Console showing Spring Boot banner, Tomcat ports 8080/8081, and JDBC/H2 success.
- [x] **14. Auto-Configuration Explanation**: Documented in `spring_boot_activity_submission.md`.
