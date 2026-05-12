# Java Developer 2 - Role & Work Report

This document details the responsibilities, files, and specific implementations handled under the **Java Developer 2** role for the Tool-41 Vendor Risk Assessment capstone project.

## 1. Role Overview
As per the project specification, **Java Developer 2** is responsible for the foundational data architecture and backend orchestration. 
**Key Responsibilities Include:**
- Database schema management via **Flyway** migrations.
- Implementation of the **Repository Layer** (Spring Data JPA) with custom optimized queries.
- Authentication endpoints and **Role-Based Access Control (RBAC)** mechanisms.
- Dockerizing the application via **Docker Compose**.
- Audit logging, scheduled jobs, and backend performance tuning.

---

## 2. Key Implementations & File Locations

### A. Database Migrations (Flyway)
**Role Goal:** Manage all schema changes safely using versioned SQL files.
* **File Location:** `backend/src/main/resources/db/migration/V3__roles.sql` (and V1/V2).
* **Work Done:** Designed the relational schema. Created the `users`, `roles`, and `users_roles` tables. Seeded default master and viewer credentials directly into PostgreSQL so the app works out-of-the-box.

### B. Repository Layer & Custom Queries
**Role Goal:** Handle database interactions securely, including custom search capabilities.
* **File Locations:** 
  - `backend/src/main/java/com/internship/tool/repository/VendorRepository.java`
  - `backend/src/main/java/com/internship/tool/repository/RoleRepository.java`
  - `backend/src/main/java/com/internship/tool/repository/UserRepository.java`
* **Work Done:** Implemented Spring Data `JpaRepository` interfaces. Wrote a custom `@Query` in `VendorRepository` to execute high-performance paginated searches with `LOWER()` string matching for vendor names, contact persons, and emails, ensuring resilient `NULL` handling.

### C. Authentication & Role-Based Access Control (RBAC)
**Role Goal:** Secure the application using JWTs and strict authorization rules.
* **File Locations:**
  - `backend/src/main/java/com/internship/tool/controller/AuthController.java`
  - `backend/src/main/java/com/internship/tool/entity/Role.java` & `User.java`
* **Work Done:** Built the `AuthController` with `/login` and `/register` endpoints. Configured entities for Many-To-Many user-role relationships. Ensured that `ADMIN` users can delete records, while `VIEWER` users are restricted to read-only capabilities via Spring Security's `@PreAuthorize` annotations.

### D. Docker Compose & Container Orchestration
**Role Goal:** Run all 5 microservices synchronously with a single command.
* **File Location:** `docker-compose.yml` (Root Directory)
* **Work Done:** Defined the entire network topology. Linked the React frontend, Spring Boot backend, Flask AI service, PostgreSQL database, and Redis cache. Set up environment variable pass-through, health checks, and strict `depends_on` startup ordering to prevent race conditions.

### E. Backend Logic & Services
**Role Goal:** Handle complex business logic, CRUD interactions, and API integrity.
* **File Location:** `backend/src/main/java/com/internship/tool/service/VendorService.java`
* **Work Done:** Wrote the core service logic for creating, fetching, updating, and soft-deleting vendors. Included defensive programming to capture Data Integrity Violations (e.g., duplicate emails) and return structured, human-readable 400 Bad Request HTTP errors to the frontend.

---

## 3. Core Functions Built

| Function Name | Location | Description |
|---|---|---|
| `searchVendors()` | `VendorRepository.java` | Executes a custom native JPQL query to filter vendors by status and dynamic string queries while bypassing deleted records. |
| `authenticateUser()` | `AuthController.java` | Validates Spring Security credentials, generates a stateless JWT, and returns user roles to the frontend for UI rendering. |
| `createVendor()` / `updateVendor()` | `VendorService.java` | Core business logic that saves to the Postgres database while enforcing `@NotNull` and `@Email` domain constraints. |
| `handleDataIntegrityViolation()` | `VendorController.java` | An `@ExceptionHandler` function that safely catches duplicate database keys (PostgreSQL unique constraint violations) and prevents 500 server crashes. |

---

## 4. Overall Project Context
The work done by Java Developer 2 is the "glue" of the architecture. By defining the database schema, building the repository queries, and orchestrating `docker-compose`, this role allowed Java Developer 1 to safely build the business logic, and the Frontend/AI developers to interact with a reliable, scalable, and fully containerized system.

---

## 5. Technology Stack Deep Dive (Beginner Friendly)

To help you understand the core technologies and explain them to your mentor, here is a detailed breakdown of the tools used in this role, what they actually are, and how they were used in plain English.

### 1. Java 17 & Spring Boot 3.x
* **What it is:** Java is the programming language. Spring Boot is a massive framework built on top of Java that provides pre-written code for common tasks (like starting a web server or connecting to a database) so you don't have to write everything from scratch.
* **How it was used:** It acts as the "brain" of the backend. Instead of writing low-level server code, we used Spring Boot annotations (like `@RestController` or `@Service`) to tell Java "this class handles web requests" or "this class handles business logic". It automatically starts a Tomcat web server on port 8080 to listen for frontend requests.

### 2. PostgreSQL (Database)
* **What it is:** A powerful, open-source relational database. Think of it as a highly structured Excel spreadsheet where data is stored in tables (Rows and Columns) that relate to each other.
* **How it was used:** We used PostgreSQL to permanently save all the Application data. We created tables like `users`, `roles`, and `vendors`. When a user types in a new vendor on the frontend, Spring Boot takes that data and securely inserts it as a new row in the PostgreSQL `vendors` table.

### 3. Flyway (Database Migration)
* **What it is:** A version control system for your database. Just like GitHub tracks changes to your code, Flyway tracks changes to your database schema (tables and columns).
* **How it was used:** Instead of manually opening a database tool and clicking "Create Table", we wrote SQL scripts (`V1__init.sql`, `V3__roles.sql`). When the Spring Boot app starts, Flyway automatically reads these files and updates the PostgreSQL database to the correct structure. This ensures every developer on the team has the exact same database structure automatically.

### 4. Spring Data JPA & Hibernate
* **What it is:** "JPA" stands for Java Persistence API. It is a tool that translates Java Objects (like a `Vendor` class) into Database Rows (like a `vendors` table), and vice-versa. This is called Object-Relational Mapping (ORM).
* **How it was used:** We used it to avoid writing complex, repetitive SQL queries. By creating an interface like `VendorRepository extends JpaRepository`, Spring Boot automatically generated the code to Save, Find, Update, and Delete vendors. For custom searches (like filtering by name), we just wrote a simple `@Query` and Spring Data JPA handled the heavy lifting of talking to PostgreSQL.

### 5. Spring Security & JWT (JSON Web Tokens)
* **What it is:** Spring Security is a guard that stands in front of the application checking IDs. JWT is a secure, encrypted digital "ID badge" given to a user after they log in.
* **How it was used:** We used Spring Security to lock down the backend. When a user logs in via `/api/auth/login`, we check their password against the database. If correct, we generate a JWT string and send it back to the browser. The browser then attaches this JWT "badge" to every future request. We used `@PreAuthorize("hasRole('ADMIN')")` to ensure that if a VIEWER tries to delete a vendor, Spring Security intercepts the request and blocks it with a 403 Forbidden error.

### 6. Docker & Docker Compose
* **What it is:** Docker packages an application and all its dependencies (like Java, Python, or Node.js) into a standardized unit called a "Container". Docker Compose is a tool to run multiple containers at the same time using a single configuration file.
* **How it was used:** Since our app has 5 different moving parts (React Frontend, Java Backend, Python AI Service, Postgres DB, Redis Cache), asking a developer to install all of those manually would be a nightmare. We wrote a `docker-compose.yml` file that downloads and connects all 5 parts automatically. By just running `docker-compose up`, the entire fully-functional ecosystem starts up on any computer flawlessly.
