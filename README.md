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

> <h2 align="center"> Author: <span style="color:#9d0dfd;"><em>Tanmoy Das</em></span> </h2>
<p align="center">
  <i>Bachelor of Applied Computer Science</i><br>
  <strong>KdG - Antwerp</strong>
</p>
