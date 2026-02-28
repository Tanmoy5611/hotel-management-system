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

### 5. Stay (Link Entity – Room ↔ Guest)
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

---

## Architecture Overview

The application follows a layered architecture:

Controller → Service → Repository → Database

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
* **Failure (404):** An error message is displayed to the user via a notification or alert.

---



> <h2 align="center"> Author: <span style="color:#9d0dfd;"><em>Tanmoy Das</em></span> </h2>
<p align="center">
  <i>Bachelor of Applied Computer Science</i><br>
  <strong>KdG - Antwerp</strong>
</p>
