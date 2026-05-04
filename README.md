# Programming 5 - Hotels Management System

## Course Information
- **Course Name:** Programming 5
- **Academic Year:** 2025–2026
- **Group:** ACS201

## Student Information
- **Name:** Tanmoy Das
- **KdG Email:** tanmoy.das@student.kdg.be
- **Student ID:** 0166044-77
---

## Project Description

This project is a Spring Boot application for managing hotels, rooms, guests, and stays.

The system follows:
- Layered Architecture (Controller → Service → Repository)
- Aggregate Root principles (DDD style)
- Spring Data JPA
- BigDecimal for monetary values
- Proper cascading and orphan removal for aggregates
- JPA best practices
- N+1 problems solved
---

## Domain Entities

The domain model consists of the following entities:

### 1. Hotel (Aggregate Root)
- id (database ID)
- `hotelId` (business identifier: slug)
- name
- city
- country
- stars
- openedOn
- hasSpa
- imageUrl
- description
- OneToMany → Rooms

### 2. Room (Aggregate Root inside Hotel)
- id (database ID)
- number
- type
- pricePerNight (BigDecimal)
- seaView
- photoUrl
- description
- ManyToOne → Hotel
- OneToMany → Stay (cascade = ALL, orphanRemoval = true)

#### RoomType enum:
```bash
- SINGLE, DOUBLE, SUITE
```

### 3. Guest (Independent Aggregate)
- id
- fullName
- email
- dob
- avatarUrl
- OneToMany → Stay

### 4. VIPGuest (Inheritance)
- Extends Guest
- discountPercentage (BigDecimal)
- Uses discountPercentage > 0

### 5. Stay (Link Entity – Room <-> Guest)
Represents a booking.

- id
- checkInDate
- checkOutDate
- ManyToOne → Room
- ManyToOne → Guest

Business logic:
- `getNumberOfNights()`
- `getTotalPrice()`
- `getFinalPrice()` (applies discount)

### 6. ApplicationUser (Authentication & Ownership)

Represents a user that can log into the system.
- id (database ID)
- email (unique, used for login)
- password (encrypted)
- role (RoleType enum)
- OneToMany → Guests (user owns guests)

### RoleType enum:
```bash
ADMIN,
USER
```

### 7. ActivityLog (Audit / System Tracking)

Represents system activities performed by users (admin dashboard).

- id (database ID)
- action (ActivityType enum)
- description
- timestamp (LocalDateTime)
- ManyToOne → ApplicationUser (who performed the action)

### ActivityType enum:

```bash
CREATE_HOTEL,
UPDATE_HOTEL,
DELETE_HOTEL,
CREATE_ROOM,
UPDATE_ROOM,
DELETE_ROOM,
BOOK_ROOM,
CREATE_GUEST,
DELETE_GUEST,
CREATE_USER,
DELETE_USER,
UPDATE_USER_ROLE
```
---

## Architecture Overview

The application follows a layered architecture: `Controller → Service → Repository → Database`

```bash
┌─────────────────────────────┐
│        Presentation Layer   │
│      (Spring MVC + View)    │
│  Controllers + Thymeleaf UI │
└───────────────┬─────────────┘
                │
                ▼
┌─────────────────────────────┐
│        Service Layer        │
│     Business Logic & Rules  │
│   Aggregate Coordination    │
└───────────────┬─────────────┘
                │
                ▼
┌─────────────────────────────┐
│       Repository Layer      │
│      Spring Data JPA        │
│   JPQL Queries & Fetching   │
└───────────────┬─────────────┘
                │
                ▼
┌─────────────────────────────┐
│         Database            │
│        PostgreSQL           │
└─────────────────────────────┘
```

### Controller Layer
- Handles HTTP requests.
- Maps URLs to methods.
- Sends data to the view (Thymeleaf).
- Delegates all business logic to the service layer.

### Service Layer
- Contains business logic.
- Enforces domain rules.
- Coordinates aggregates.
- Ensures clean separation between web and persistence.

### Repository Layer
- Responsible for database access.
- Uses Spring Data JPA.
- Contains no business logic.
- Executes JPQL queries and entity loading strategies.

### Domain Layer (Entities)
- Represents core business concepts.
- Encapsulates domain behavior.
- Aggregates follow DDD principles.
- `Room` owns `Stay` (cascade + orphan removal).
- `Guest` is an independent aggregate.

### Monetary Handling
- All money values use `BigDecimal`.
- Calculations are performed in the domain layer.
- Formatting is done **only in the view layer (Thymeleaf)**.
- No floating-point types are used for prices.
---

## Build & Run Instructions (CLI)

### 1. Requirements

Make sure the following tools are installed:

- Java 21 (or version specified in build.gradle)
- Docker & Docker Desktop
- Git

### 2. Clone the Repository

```bash
git clone <repository-url>
cd <project-folder>
```

### 3. Start the database

The application requires a PostgreSQL database.
Use the provided docker-compose.yml file in the project root:
```bash
docker compose up -d
```

### 4. Build the project

Use the Gradle Wrapper (included in the repository):
```bash
# macOS / Linux
./gradlew clean build

# Windows
gradlew.bat clean build
```
### 5. Run the Application
```bash
# macOS / Linux
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

### 6. Open in browser

After seeing:
- Started HotelsApplication
```bash
http://localhost:8080
```
---

### Notes
	•	The application uses Spring Data JPA.
	•	The database schema is generated automatically.
	•	Monetary values use BigDecimal.
	•	Aggregate boundaries are respected (Room owns Stay).
	•	Deletions follow proper cascading rules.
---

### Features Implemented
	•	Hotel management (CRUD)
	•	Room management (CRUD + filtering)
	•	Guest management (CRUD + VIP logic)
	•	Booking system (Stay entity)
	•	Discount calculation
	•	Top-picked rooms query
	•	Filtering & sorting
	•	Add Hotel, Room & Guest via UI (with descriptions)
	•	Real-world booking Home page
	•	Multi-language support (i18n) - EN, NL, FR, DE, BN
	•	Thymeleaf UI with Bootstrap
    •	Dark / Light theme
---

# Week 2 - REST API (Room)

## Overview
In Week 2, a robust **REST API** was implemented for the **Room** entity. The implementation strictly adheres to REST architectural principles to ensure a clean, scalable interface between the client and server.

### Key API Principles
* **Base Path:** `/api/rooms`
* **Methods:** Proper use of HTTP verbs (`GET`, `DELETE`).
* **Status Codes:** Implementation of specific response codes:
    * `200 OK`: Successful retrieval.
    * `204 No Content`: Successful deletion.
    * `404 Not Found`: Resource not found.
* **Data Format:** Standard **JSON** responses.
* **Architecture:**
    * **DTO Usage:** Implementation of `RoomDto` for decoupled data transfer.
    * **Global Exception Handling:** Managed via `@RestControllerAdvice` for consistent error structures.
* **Integration:** * Tested and verified via `rooms-api.http`.
    * Fully integrated with **JavaScript (AJAX)** to enable dynamic, asynchronous deletion without page refreshes.
---

## Controller Implementation

The API logic is encapsulated within the `RoomApiController`.

### Configuration
* **Controller Class:** `RoomApiController`
* **Base Mapping:** `@RequestMapping("/api/rooms")`

### Endpoints Implemented

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/rooms` | Retrieve a list of all rooms. |
| **GET** | `/api/rooms/{id}` | Retrieve details for a specific room by ID. |
| **DELETE** | `/api/rooms/{id}` | Permanently delete a room record. |

---

### 1. GET – Retrieve All Rooms (200 OK)

This endpoint fetches the complete list of available rooms from the database, transformed into DTOs for client-side consumption.

#### HTTP Request
* **URL:** `http://localhost:8080/api/rooms`
* **Method:** `GET`
* **Accept:** `application/json`

#### Response
* **Status:** `200 OK`
* **Body:** `List<RoomDto>`

**Example Response Body:**
```json
{
    "id": 1,
    "number": 101,
    "pricePerNight": 120.00,
    "hotelName": "Hilton"
}
```

### Internal Implementation Flow
1. **Service Layer:** The controller invokes roomService.getAllRooms().
2. **Data Mapping:** Room entities are converted to RoomDto using the RoomMapper component to prevent exposing internal entity structures.
3. **Response Wrapper:** Results are wrapped in a ResponseEntity.ok(...) to ensure the correct HTTP status is sent to the client.

### 2. GET - Retrieve Single Room (200 OK)

This endpoint fetches the details of a specific room based on its unique identifier.

#### HTTP Request
* **URL:** `GET http://localhost:8080/api/rooms/1`
* **Method:** `GET`
* **Accept:** `application/json`

#### Response
* **Status:** `200 OK`
* **Body:** `RoomDto`

**Example Response Body:**
```json
{
  "id": 1,
  "number": 101,
  "pricePerNight": 120.00,
  "hotelName": "Hilton"
}
```
### Internal Implementation Flow
1. **Service Layer:** Invokes roomService.getRoomById(id) to locate the record.
2. **Data Mapping:** Uses the RoomMapper to transform the entity into a RoomDto.
3. **Response Wrapper:** Returns the mapped object via ResponseEntity.ok(...).

### 3. GET - Room Not Found (404)

This scenario occurs when a client requests a resource using an identifier that does not exist in the database.

#### HTTP Request
* **URL:** `GET http://localhost:8080/api/rooms/99999`
* **Method:** `GET`
* **Accept:** `application/json`

#### Response
* **Status:** `404 Not Found`
* **Body:** `ApiError`

**Example Response Body:**
```json
{
  "timestamp": "2026-02-28T12:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Room with id 99999 not found",
  "path": "/api/rooms/99999"
}
```
### Internal Implementation Flow
1. **Exception Trigger:** When the service layer cannot find the record, it throws a custom RoomNotFoundException.
2. **Global Catch:** The request is intercepted by the Global Exception Handler.
3. **Class:** ApiExceptionHandler
4. **Annotation:** @RestControllerAdvice
5. **Response Wrapper:** The handler maps the exception details into a standardized ApiError object and returns it with a 404 status code.

### 4. DELETE - Delete Room (204 No Content)

This endpoint allows for the permanent removal of a room record from the system.

#### HTTP Request
* **URL:** `DELETE http://localhost:8080/api/rooms/1`
* **Method:** `DELETE`
* **Accept:** `application/json`

#### Response
* **Status:** `204 No Content`
* **Body:** *None (The response body is empty by design).*

#### Internal Implementation Flow
1. **Service Layer:** The controller calls the deletion logic, ensuring the room is removed from the database.
2. **Response Wrapper:** Instead of returning data, the controller returns `ResponseEntity.noContent().build()`.
3. **Frontend Impact:** The client receives a success confirmation (204) and can then update the UI (e.g., removing the row from a table via AJAX).
---

### 5. DELETE - Room Not Found (404)

Attempts to delete a non-existent resource are handled gracefully to inform the client of the invalid request.

#### HTTP Request
* **URL:** `DELETE http://localhost:8080/api/rooms/99999`
* **Method:** `DELETE`
* **Accept:** `application/json`

#### Response
* **Status:** `404 Not Found`
* **Body:** `ApiError` (Standardized JSON error structure).
---

## DTO & Mapping

To maintain a clean separation between the database layer and the API layer, the application utilizes the **Data Transfer Object (DTO)** pattern.

* **DTO Class:** `RoomDto`
* **Fields:** `id`, `number`, `pricePerNight`, `hotelName`
* **Mapping Framework:** MapStruct (`@Mapper(componentModel = "spring")`)
* **Custom Logic:** `@Mapping(source = "hotel.name", target = "hotelName")`

> **Note:** This approach prevents the internal `Room` entity from being exposed directly, keeping the API responses clean and specifically tailored for the frontend.
---

## Exception Handling

Centralized error management is implemented using a **Global Exception Handler** to ensure consistent API responses across the entire application.

* **Annotation:** `@RestControllerAdvice`
* **Target Class:** `ApiExceptionHandler`
* **Handled Exceptions:** `RoomNotFoundException`
* **Result:** Returns a structured JSON object (`ApiError`) containing a timestamp, status code, and descriptive message.
---

## HTTP Test File

The API was rigorously tested using the `rooms-api.http` file included in the project. This allows for rapid verification of the following scenarios:

1. **GET all rooms** - Returns `200 OK`.
2. **GET one room** - Returns `200 OK`.
3. **GET one room (Invalid ID)** - Returns `404 Not Found`.
4. **DELETE room** - Returns `204 No Content`.
5. **DELETE room (Invalid ID)** - Returns `404 Not Found`.
---

## AJAX Integration

The `DELETE` functionality is fully integrated with the frontend using the JavaScript **Fetch API**. This fulfills the requirement for dynamic UI updates without page reloads.
* **Success (204):** The room is removed from the DOM immediately.
* **Failure (404):** An error message is displayed to the applicationUser via a notification or alert.
---

# Week 3

During week 3, two additional REST operations were implemented for the Room API. The new endpoints allow creating a room (**POST**) and updating the room description (**PATCH**).

All endpoints were tested using the `rooms-api.http` file included in the project.
---

### Creating a room - Created (201)

#### Request
```http
POST http://localhost:8080/api/rooms
Content-Type: application/json
Accept: application/json

{
  "number": 501,
  "pricePerNight": 199.99,
  "hotelId": "hilton-old-town"
}
```

### Response
- 201 Created

### Creating a room - Bad Request (400)

#### Request
```http
POST http://localhost:8080/api/rooms
Content-Type: application/json
Accept: application/json

{
  "number": -5,
  "pricePerNight": -100,
  "hotelId": null
}
```
### Response
- 400 Bad Request

### Creating a room - Conflict (409)

#### Request
```http
POST http://localhost:8080/api/rooms
Content-Type: application/json
Accept: application/json

{
  "number": 101,
  "pricePerNight": 150,
  "hotelId": "hilton-old-town"
}
```
### Response
- 409 Conflict

### Updating room description - No Content (204)

#### Request
```http
PATCH http://localhost:8080/api/rooms/1/description
Content-Type: application/json
Accept: application/json

{
  "description": "Updated modern deluxe room"
}
```
### Response
- 204 No Content


### Updating room description - Bad Request (400)

#### Request
```http
PATCH http://localhost:8080/api/rooms/1/description
Content-Type: application/json
Accept: application/json

{
  "description": ""
}
```
### Response
- 400 Bad Request

### Updating room description - Not Found (404)

#### Request
```http
PATCH http://localhost:8080/api/rooms/99999/description
Content-Type: application/json
Accept: application/json

{
  "description": "This room does not exist"
}
```
### Response
- 404 Not Found

# Week 4 - Spring Security

## Overview

In Week 4, Spring Security was integrated into the Hotels application to add authentication and authorization.

**The application now supports:**
* **User login and logout**
* **Role-based authorization**
* **Persisted users** in the database
* **Password hashing**
* A **custom login page**
* **Dynamic UI behavior** based on applicationUser status (Anonymous, Staff, or Administrator)
* **REST API & Ajax support** maintained from previous weeks

> The Hotels application models **hotel management staff**, not customers.
---

## Roles used in the system
| Role | Meaning |
| :--- | :--- |
| **Anonymous** | Public visitor browsing hotels and rooms |
| **USER** | Hotel staff performing operational tasks |
| **ADMIN** | Hotel manager with full administrative privileges |


## Authentication

A custom login page was implemented using Spring Security.

**Login URL:** `/login`

**Users authenticate using:**
* email
* password

After successful login, the applicationUser is redirected to: `/home`

The navigation bar dynamically updates to show the current login status and provides a logout option.

> **Example:** > Logged in as: `applicationUser@hotelapp.com`
---

## Persisted Users

Users are implemented as a persisted entity in the database, ensuring that accounts are not lost when the application restarts.

* **Entity:** `User`
* **Repository:** `SpringDataUserRepository`

**The applicationUser entity stores:**
* `id`
* `email`
* `password` (hashed)
* `role` (ADMIN/USER)

This satisfies the requirement that users must be stored and managed via the database.

## Password Hashing

- Passwords are never stored in plaintext to ensure system security.
- The application utilizes **BCrypt password hashing** via Spring Security's `PasswordEncoder`. 
- Passwords are salted and encoded before being persisted to the database.
---

## Default Seeded Users

To facilitate testing, a applicationUser seeding routine is implemented using `CommandLineRunner`. When the application starts and the applicationUser table is empty, two default users are automatically created.

| Role | Email                | Password   |
| :--- |:---------------------|:-----------|
| **ADMIN** | `admin@hotelapp.com` | `admin123` |
| **USER** | `user@hotelapp.com`  | `user123`  |

> These credentials are displayed on the login page during the development phase for easier testing.

## Authorization Model

### Anonymous Users

Anonymous visitors can access public pages such as:
* `/home`
* `/hotels`
* `/rooms`
* `/hotels/{id}`
* `/rooms/{id}`
They can browse the application but cannot perform operational tasks.

---

### USER (Hotel Staff)

The **USER** role represents hotel staff.

Staff members can perform operational tasks such as:
* view guests
* add a guest
* book a room
* access guest management pages

**Example staff pages:**
* `/guests`
* `/guests/add`
* `/rooms/{id}/book`

These operations represent normal hotel front-desk activities.

### ADMIN (Hotel Manager)

The **ADMIN** role represents a hotel administrator or manager.

Admins have all staff permissions plus management functionality.

**Admins can:**
* add a hotel
* add a room
* add guests
* book rooms
* delete hotels, rooms, and guests
* manage application users

**Admin pages include:**
* `/admin/users`
* `/hotels/add`
* `/rooms/add`

**The admin panel allows:**
* viewing all users
* creating new users
* deleting users
* switching roles (**USER ↔ ADMIN**)

> The main admin account cannot be deleted.

### Different Behavior for Logged-In Users

The application shows different functionality depending on the authentication state.

**Anonymous visitors**
* can browse hotels and rooms

**Staff users**
* can manage guests
* can book rooms

**Admin users**
* can manage hotels, rooms, and users
This ensures authenticated users see more application-specific functionality than anonymous visitors.

---

### REST API Compatibility

The REST API implemented in previous weeks continues to work seamlessly with the new security layer.

**Examples:**
* `GET /api/rooms`
* `POST /api/rooms`
* `PATCH /api/rooms/{id}/description`
* `DELETE /api/rooms/{id}`

**Security rules applied:**

| Method | Access |
| :--- | :--- |
| **GET** | public |
| **POST** | authenticated |
| **PATCH** | authenticated |
| **DELETE** | authenticated |

> **CSRF protection** was temporarily disabled so Ajax requests continue to function, as required by the assignment.

## Example Links

### Public Page
Accessible without authentication: 
[http://localhost:8080/home](http://localhost:8080/home)

### Page requiring authentication
**Example staff page:**
[http://localhost:8080/guests/add](http://localhost:8080/guests/add)

**Example admin page:**
[http://localhost:8080/admin/users](http://localhost:8080/admin/users)
---

## Summary

Week 4 introduced a complete **Spring Security** setup into the Hotels application.

**The implementation includes:**
* custom login page
* persisted users
* **BCrypt** password hashing
* default seeded users
* role-based authorization
* staff operations (add guest, book room)
* admin operations (manage hotels, rooms, users)
* continued REST API and Ajax functionality

This creates a realistic security model for a hotel management system where **anonymous visitors**, **staff users**, and **administrators** have different levels of access.


## Week 5 - Security & Authorization

In Week 5, Spring Security was implemented to secure the Hotel Booking application.

The main goal was to introduce:
- authentication (login system)
- role-based authorization (USER / ADMIN)
- ownership-based access control (users linked to their own data)
- CSRF protection for secure requests

---

## Seeded Users

The application seeds the following users automatically:

| Email              | Password  | Role              |
|--------------------|-----------|-------------------|
| admin@hotelapp.com | admin123  | ADMIN (PROTECTED) |
| user@hotelapp.com  | user123   | USER              |
| tanmoy@gmail.com   | tanmoy123 | ADMIN             |

Passwords are stored securely using **BCrypt hashing**.

---

## Roles in the Application

There are three types of users:

---

### 1. Unauthenticated Users (Anonymous)

Users who are not logged in.

**Can:**
- View home page
- View hotels
- View rooms

**Cannot:**
- Create guests
- Delete guests
- Access admin or staff features

Example: http://localhost:8080/home

---

### 2. USER Role (Staff)

Represents staff users of the system.

**Can:**
- View guests
- Create new guests
- View guest details
- Interact with the system (rooms, bookings, etc.)

**Ownership rule:**
- When a USER creates a guest -> that guest is linked to that user
- USER can **only delete their own guests**

Example: http://localhost:8080/guests/add

---

### 3. ADMIN Role

Administrators have full access.

**Can:**
- Manage hotels
- Manage rooms
- Manage guests
- Manage users
- Delete any guest (even if not owner)
- Switch between USER and ADMIN roles
- Admin Dashboard to manage users and view recent activity logs

Example: http://localhost:8080/admin/users

---

## User–Guest Association

Each guest is linked to a user (owner).

Relationship:
ApplicationUser (1) –– (many) Guest

When a guest is created:
- the logged-in user becomes the owner
- ownership is enforced during delete/update

**Access rules:**
- Owner -> can modify/delete
- ADMIN -> can modify/delete all
- Other users → cannot modify/delete

---

## UI Access Control

The UI hides actions that users are not allowed to perform.

Examples:
- "Add Guest" button hidden for anonymous users
- Admin menu visible only to ADMIN
- Delete button shown only for:
  - owner
  - ADMIN

Important: UI hiding is not enough -> backend also validates.

---

## Server-Side Authorization

All security rules are enforced in the backend using Spring Security.

Examples:
- `/admin/**` -> ADMIN only
- `/api/**` (POST, PATCH, DELETE) -> authenticated users only
- Ownership checks enforced in service layer

This prevents users from bypassing restrictions using tools like Postman.

---

## REST API Security

The REST API continues to work with security rules:

- `GET /api/**` -> public
- `POST /api/**` -> authenticated
- `PATCH /api/**` -> authenticated
- `DELETE /api/**` -> authenticated

Example protected endpoint:
POST /api/rooms

---

## CSRF Protection

CSRF protection is **enabled** using Spring Security (default behavior).

Implementation includes:
- CSRF token in HTML meta tags
- Token added to all AJAX requests via headers
- Shared `csrf.js` helper for reuse

This ensures:
- Only valid requests from the application are accepted
- External/malicious requests are blocked

---

## Consistency of Security Model

The system follows consistent security rules:

- Anonymous users -> read-only access
- Authenticated users -> can create data
- Users -> can only modify their own data
- Admin -> full control

This matches real-world application behavior and assignment requirements.

---

## Summary

Week 5 introduces a complete and secure system:

- Authentication with Spring Security
- Role-based access (USER / ADMIN)
- Ownership-based authorization
- Protected REST API
- CSRF protection for all state-changing requests
- UI + backend validation

This results in a realistic hotel management system where:
- users manage their own data
- administrators manage the entire system securely
---


# Week 6 - Testing

## Overview

In this week, I implemented tests for both the repository layer and the service layer.

The goal was to:
* **Verify database constraints and mappings**
* **Validate business logic** in the service layer
* **Ensure tests are isolated** and reproducible
* **Follow best practices** from the course (Arrange–Act–Assert, multiple scenarios)

---

##  Test Configuration (Spring Profile)

All tests run with a separate profile:
`@ActiveProfiles("test")`

**This ensures:**
* Tests use a separate environment
* Tests connect to a separate PostgreSQL test database: `hotels_test`
* No interference with development or production data
* The normal application seeder does not run during tests because `UserSeeder` is disabled for the `test` profile

## Test Data Setup Strategy

In every test class, I used:
`@BeforeEach`

### What happens in setup:

**All repositories are cleaned:**
* `repository.deleteAll();`

**Required entities are created manually:**
* `ApplicationUser` (mandatory for Guest owner FK)
* `Hotel`
* `Guest` (when needed)

**Why this is important:**
* **Each test starts with a clean database:** Prevents data leaking from previous runs.
* **Tests are independent:** A failure in one test does not affect the others.
* **No hidden dependencies:** All state is explicitly defined within the setup method.
---

## Repository Layer Tests

### 1. GuestRepository Tests

**What was tested:**

#### Delete operations
* `deleteById()` removes a guest correctly
* `deleteAll()` clears the table

#### Validation constraints
1. **Email cannot be null**
  * `ConstraintViolationException` → Bean Validation (Hibernate Validator)

2. **Email must be unique**
  * `DataIntegrityViolationException` → Database constraint

3. **Owner is required (NOT NULL FK)**
  * `DataIntegrityViolationException`

**Important understanding:**

| Type | Where enforced |
| :--- | :--- |
| **@NotNull** | Validation layer |
| **UNIQUE / FK** | Database |

### 2. RoomRepository Tests

**What was tested:**

#### Aggregate behavior
**Room → Stay (cascade + orphanRemoval)**

* Deleting a `Room` automatically deletes all related `Stay` entities.
* `roomRepository.deleteById(roomId);`

**Ensures:**
* No orphan records in the database.
* Correct aggregate design and data consistency.

#### Unique constraint
**Same room number in same hotel → NOT allowed**

* `DataIntegrityViolationException`

**Enforced by database** (not Hibernate) to prevent duplicate room entries within a single hotel.

#### Lazy loading (performance)
**`Room.stays` is LAZY**

* `entityManagerFactory.getPersistenceUnitUtil().isLoaded(foundRoom, "stays") == false`

**Prevents unnecessary queries** by ensuring related stays are only loaded when explicitly accessed.

#### Eager loading
**`Stay.guest` is EAGER**

* `stay.getGuest() != null`

**Guest is loaded immediately** whenever a stay is retrieved, optimizing for common access patterns.

---

## Service Layer Tests (Integration Tests)

### Configuration

`@SpringBootTest`
`@ActiveProfiles("test")`

**These are integration tests, not unit tests:**
* Use a **real PostgreSQL test database**
* Use **real repositories**
* Test the **full flow** (Service → Repository → DB)
---

### Tested Service: `RoomService`

#### 1. `createRoom()`
*  **Success** → room created and linked to hotel
*  **Duplicate** → `RoomAlreadyExistsException`

#### 2. `getRoomById()`
*  Returns correct room
*  Throws `RoomNotFoundException`

#### 3. `deleteRoom()`
*  Room removed from database

#### 4. `updateRoomDescription()`
*  Uses **JPA dirty checking** (no explicit `.save()` needed)

#### 5. `bookRoom()` (Aggregate logic)
*  Creates a `Stay` (`Room` acts as the aggregate root)
*  Guest not found → `GuestNotFoundException`
*  Room not found → `RoomNotFoundException`

#### 6. `findRooms()` (Filtering)
* Uses **Optional** parameters:
  * `Optional<RoomType>`
  * `Optional<Boolean>`
  * `Optional<BigDecimal>`
* **Allows flexible queries** without tedious null checks.
---

### Important Fix (Logging + Security)

**Problem:**
* Services rely on the **logged-in user** for activity logging.
* Tests run in a background context with **no authentication**, causing null pointer exceptions or test failures.

**Solution:**

- Created a safe retrieval method:
`securityService.getLoggedInUserSafe()`

- Centralized the null-safe activity logging in `SafeActivityLogger`:
```
safeActivityLogger.log(ActivityType.UPDATE_ROOM, "Updated room description");
```

###  Result:

* **Tests run without authentication**
* **Logging still works** in the real application
* **Business logic is independent** of security context
---

### How to Run Tests

**From terminal:**
```bash
docker compose up -d postgres_hotels_test_db
./gradlew test
```

**Or via IntelliJ:**
- Start the `postgres_hotels_test_db` Docker container first
- Right click → Run Tests
---

### What Makes These Tests Good

These tests follow course best practices:

* **Independent:** A clean database is ensured for each test run.
* **Repeatable:** Results are consistent across different environments.
* **Clear AAA structure:** Every test follows the **Arrange–Act–Assert** pattern.
* **Comprehensive:** Both success and failure scenarios are covered.
* **Realistic:** They validate actual database behavior, including constraints and mappings.
* **Logic-focused:** They verify the core business logic within the service layer.
---

**Chosen because:**
* Service logic is heavily dependent on data persistence.
* It provides more realistic testing of how the application functions in production.
--- 

### Summary of Week 6:

In this week, I implemented a robust testing suite that ensures the reliability of the core application layers:

* **Repository tests** for validating database constraints, entity mappings, and specific loading behaviors (Lazy vs. Eager).
* **Service integration tests** to verify complex business logic and aggregate roots.
* **Proper test isolation** using dedicated Spring profiles (`@ActiveProfiles("test")`) to keep environments separate.
* **PostgreSQL test database** setup so tests do not touch development or production data.
* **Safe activity logging** through `SafeActivityLogger`, allowing business logic to function even when no security context or authenticated user is present.
* **Result:** A reliable and realistic testing setup that is fully aligned with course requirements and industry best practices. ###
----

# Week 8 - Controller Testing & Security Verification

## Overview

In this week, I implemented integration tests for the presentation layer and security authorization rules.

The goal was to:
* **Verify MVC controllers** return the correct Thymeleaf views and model attributes
* **Verify REST API controllers** return correct HTTP status codes
* **Test Spring Security with security filters enabled**
* **Verify owner/admin authorization rules**
* **Run all tests with the `test` profile and a separate PostgreSQL test database**
* **Keep all tests executable with one command**

---

## Test Configuration

All Week 8 tests use the test profile:

```java
@ActiveProfiles("test")
```

The controller tests also use:

```java
@SpringBootTest
@AutoConfigureMockMvc
```

**This means:**
* The full Spring context is loaded.
* Spring Security filters are active during tests.
* Tests use the separate PostgreSQL database `hotels_test`.
* The normal application seed data does not interfere with test data.
* Tests behave closer to the real application than simple unit tests.

---

## Why MockMvc Was Used

`MockMvc` allows controller testing without starting a real web server.

I used it to:
* Send HTTP requests such as `GET` and `PATCH`
* Add query parameters and path variables
* Send JSON request bodies
* Simulate authenticated users with roles
* Add CSRF tokens for modifying requests
* Verify HTTP status codes
* Verify returned view names
* Verify model attributes

---

## MVC Integration Tests

### Test Class

`HotelControllerMvcTest`

### Purpose

This class tests the MVC part of the presentation layer.
It verifies that `HotelController` returns the correct Thymeleaf pages and places the expected data in the model.

### Tested Scenarios

#### 1. Hotels overview page

```http
GET /hotels
```

**Verified:**
* HTTP `200 OK`
* View name is `hotels`
* Model contains `hotels`
* Model contains `total`

#### 2. Search hotels by name

```http
GET /hotels?name=Grand
```

**Verified:**
* HTTP `200 OK`
* View name is `hotels`
* Model contains the filtered `hotels` attribute

#### 3. Hotel detail page

```http
GET /hotels/{hotelId}
```

**Verified:**
* HTTP `200 OK`
* View name is `hotel-detail`
* Model contains `hotel`
* Model contains `rooms`
* Model contains `guestsPerRoom`
* Model contains `totalGuests`

---

## API Integration Tests

### Test Class

`RoomApiControllerTest`

### Purpose

This class tests the REST API part of the presentation layer.
It verifies that `RoomApiController` returns the correct HTTP responses and respects Spring Security rules.

### Test Data Strategy

For the API tests I used SQL scripts:

```java
@Sql(scripts = "/sql/room-api-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
```

**Why this is useful:**
* Every API test starts with known data.
* The database is cleaned after every test.
* Tests are repeatable and independent.
* There are no hidden dependencies on development seed data.

### Tested Scenarios

#### 1. Get all rooms

```http
GET /api/rooms
```

**Verified:**
* HTTP `200 OK`

#### 2. Admin updates room description

```http
PATCH /api/rooms/1/description
```

**Security setup:**

```java
@WithMockUser(roles = "ADMIN")
```

**Verified:**
* HTTP `204 No Content`

#### 3. Normal user cannot update room description

```http
PATCH /api/rooms/1/description
```

**Security setup:**

```java
@WithMockUser(roles = "USER")
```

**Verified:**
* HTTP `403 Forbidden`

---

## CSRF Protection

Spring Security is enabled during tests.
Therefore, state-changing requests such as `PATCH`, `POST`, and `DELETE` require a CSRF token.

For the `PATCH` API tests I used:

```java
.with(csrf())
```

This is important because the test should fail or pass because of authorization, not because of a missing CSRF token.

---

## Role Verification Tests

### Test Class

`SecurityAuthorizationTest`

### Purpose

This class tests the owner/admin authorization rule for deleting guests.

The authorization is implemented in the service layer with:

```java
@PreAuthorize("@guestAuthorizationService.canDeleteGuest(#guestId, authentication)")
```

Because the security rule is on the service method, the test also calls the service method directly.
This matches the Week 8 instruction: if authorization is implemented in the service layer with `@PreAuthorize`, it must be tested on the service.

### Tested Scenarios

#### 1. Owner may delete own guest

**Verified:**
* Delete succeeds
* Guest is removed from the database

#### 2. Other normal user may not delete guest

**Verified:**
* `AccessDeniedException`
* Guest still exists in the database

#### 3. Admin may delete any guest

**Verified:**
* Delete succeeds
* Guest is removed from the database

#### 4. Anonymous user may not delete guest

**Verified:**
* `AccessDeniedException`
* Guest still exists in the database

---

## How To Run All Tests

Start the PostgreSQL test database:

```bash
docker compose up -d postgres_hotels_test_db
```

Then run all tests:

```bash
./gradlew test
```

This runs repository tests, service tests, MVC integration tests, API integration tests, and security authorization tests together.

---

## Code Coverage

The following screenshots show IntelliJ IDEA coverage results after executing all tests:

<p align="center">
<img src="images/test-screenshots/test_coverage1.png" width="800">
</p>

<p align="center">
<img src="images/test-screenshots/test_coverage2.png" width="800">
</p>

<p align="center">
<img src="images/test-screenshots/test_coverage3.png" width="800">
</p>

<p align="center">
<img src="images/test-screenshots/test_coverage4.png" width="800">
</p>

---

## Test Classes Required By Week 8

| Requirement | Test class |
| :--- | :--- |
| MVC integration tests | `HotelControllerMvcTest` |
| API integration tests | `RoomApiControllerTest` |
| Role verification tests | `SecurityAuthorizationTest` |

---

## What Makes These Tests Good

These tests follow the course best practices:

* **Independent:** Test data is cleaned and recreated for predictable results.
* **Repeatable:** Tests can run many times with the same outcome.
* **Clear:** Test method names describe the expected behavior.
* **Realistic:** Tests load the Spring context and use a real PostgreSQL test database.
* **Security-aware:** Spring Security is enabled and CSRF is included where needed.
* **Complete scenarios:** Both allowed and forbidden actions are tested.
* **Single command:** All tests run together using `./gradlew test`.

---

## Summary of Week 8

In this week, I added presentation-layer and security-focused integration tests:

* MVC tests for Thymeleaf hotel pages.
* REST API tests for room endpoints.
* Security tests for owner/admin guest deletion.
* SQL-based API test setup and cleanup.
* PostgreSQL test database with the `test` profile.
* CSRF-aware tests for modifying requests.

**Result:** A realistic Week 8 testing setup that verifies controller behavior, API behavior, and Spring Security authorization rules without touching development or production data.

----

# Week 9 - Unit Testing With Mocking & Continuous Integration

## Overview

In this week, I added mock-based unit tests and a GitLab CI pipeline.

The goal was to:
* **Unit test one REST API endpoint** with mocked controller dependencies
* **Unit test business-layer methods** with mocked repositories and logging
* **Use `verify`** to prove that important dependency methods are called with the correct arguments
* **Keep all tests executable with one command**
* **Run build and test automatically in GitLab CI**
* **Run CI tests against a PostgreSQL service**
* **Publish a JUnit test report in the pipeline**

---

## Mocking Tests

### API Unit Test Class

`RoomApiControllerUnitTest`

### Tested Endpoint

```http
POST /api/rooms
```

This endpoint was chosen because it has meaningful behavior:
* It validates request data.
* It converts a DTO to a domain object.
* It calls the service layer.
* It converts the saved entity back to a DTO.
* It can return different HTTP responses.

### Mocked Dependencies

In this test class, the controller is real, but its dependencies are mocked:
* `RoomService`
* `RoomMapper`

### Tested Scenarios

* Valid request returns `201 Created`
* Missing required field returns `400 Bad Request`
* Duplicate room number returns `409 Conflict`

---

## Business Layer Unit Tests

### Test Class

`RoomServiceUnitTest`

### Tested Service Methods

#### 1. `createRoom(...)`

**Tested scenarios:**
* Room is created successfully
* Duplicate room number throws `RoomAlreadyExistsException`
* Missing hotel throws `IllegalArgumentException`

#### 2. `searchAvailableRooms(...)`

**Tested scenarios:**
* Query is cleaned and repository filtering is called
* Invalid date range is rejected before repository access
* Rooms with overlapping stays are filtered out

### Mocked Dependencies

The service is tested with mocked dependencies:
* `SpringDataRoomRepository`
* `SpringDataHotelRepository`
* `SpringDataGuestRepository`
* `SafeActivityLogger`

---

## Verify Tests

The Week 9 tests use `verify` to check interactions with mocked dependencies.

Examples:
* `RoomApiControllerUnitTest` verifies that `roomService.createRoom(...)` is called with the expected room and hotel id.
* `RoomServiceUnitTest` verifies that `roomRepo.searchRooms(...)` receives the cleaned query.
* `RoomServiceUnitTest` verifies that activity logging is called after successful room creation.

## Code Coverage after Week 9

The following screenshot shows IntelliJ IDEA coverage results after executing all tests:
<p align="center">
<img src="images/test-screenshots/test_coverage5.png" width="800">
</p>

---

## Continuous Integration

### CI File

`.gitlab-ci.yml`

### Pipeline Stages

The pipeline has two stages:

| Stage | Purpose |
| :--- | :--- |
| `build` | Compiles and builds the application without running tests |
| `test` | Runs all tests and publishes the JUnit report |

### PostgreSQL Service In CI

The test stage starts a PostgreSQL service:

```yaml
services:
  - name: postgres:18.1-alpine
    alias: postgres
```

Inside the pipeline, tests connect to:

```properties
jdbc:postgresql://postgres:5432/hotels_test
```

Locally, tests still use the Docker Compose test database:

```properties
jdbc:postgresql://localhost:5051/hotels_test
```

---

## CI Cache And Reports

The pipeline caches Gradle files:
* `.gradle/caches/`
* `.gradle/wrapper/`
* `build/`

The test stage publishes the JUnit report from:

```text
build/test-results/test/TEST-*.xml
```

Recent pipeline test report:
[GitLab latest pipeline test report](https://gitlab.com/kdg-ti/programming-5/projects-25-26/acs201/tanmoy.das/spring-backend/-/pipelines/latest/test_report?ref=main)

---

## How To Run All Tests

Start the PostgreSQL test database:

```bash
docker compose up -d postgres_hotels_test_db
```

Run all tests:

```bash
./gradlew test
```

This runs repository tests, service integration tests, controller integration tests, security tests, and Week 9 unit tests together.

---

## Test Classes Required By Week 9

| Requirement | Test class |
| :--- | :--- |
| Mocking tests for web API endpoint | `RoomApiControllerUnitTest` |
| Mocking tests for business layer | `RoomServiceUnitTest` |
| Tests using `verify` | `RoomApiControllerUnitTest`, `RoomServiceUnitTest` |

---

## Summary of Week 9

In this week, I added unit tests with mocks and continuous integration:

* API unit tests for `POST /api/rooms`
* Business-layer unit tests for room creation and room availability search
* Mockito `verify` checks for important method calls
* GitLab CI with separate build and test stages
* PostgreSQL service for CI tests
* JUnit test report publishing in GitLab

**Result:** The project now has both realistic integration tests and focused unit tests, and all tests can run locally or in GitLab CI with PostgreSQL.

----

> <h2 align="center"> Author: <span style="color:#9d0dfd;"><em>Tanmoy Das</em></span> </h2>
<p align="center">
  <i>Bachelor of Applied Computer Science</i><br>
  <strong>KdG - Antwerp</strong>
</p>
