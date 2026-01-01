# Programming 3 – Hotels Project (Final Submission)

## Student Information

- **Name:** ``Tanmoy Das``
- **Course:** Programming 3
- **Group:** ACS 201
- **Project:** Hotels Management Application
- **Academic Year:** 2025–2026

---

## Domain Description

This project models a **hotel management domain**.  
The goal is to manage **hotels**, their **rooms**, and the **guests** staying in those rooms.

### Entities

#### Hotel

- Represents a hotel
- Main attributes: ``id, name, openedOn, stars, hasSpa, imageUrl``
- A hotel has **many rooms** (one-to-many)

#### Room

- Represents a hotel room
- Main attributes: ``number, type, pricePerNight, seaView, photoUrl``
- A room belongs to **one hotel**
- A room can have **many guests**

#### RoomType (enum)
- ``SINGLE, DOUBLE, SUITE``

#### Guest

- Represents a person staying in a hotel
- Main attributes: ``id, fullName, dob, email, vip, avatarUrl``
- A guest can stay in **many rooms**

---

## Relationships

- **Hotel → Room:** one-to-many
- **Room ↔ Guest:** many-to-many
    - Implemented using a cross table `rooms_guests`

---

## Inheritance

- `VIPGuest` is a **subclass of Guest**
- Extra attribute: `discountPercentage`
- Implemented using **JPA inheritance with a discriminator column**

---

## Application Evolution (Weeks 1–11)

The project was developed step-by-step following the weekly assignments:

- **Week 1:** Console application with datasets, filtering, streams, fluent programming style
- **Week 2:** Layered architecture (presentation, business, data), interfaces, dependency injection
- **Week 3:** Spring MVC web application
- **Week 4:** Thymeleaf templates, fragments, internationalization
- **Week 5:** Bootstrap styling, responsive layout, cards, client-side validation
- **Week 6:** ViewModels, custom converters, Bean Validation, session history
- **Week 7:** JDBC repositories using `JdbcClient` with H2 database
- **Week 8:** Database relationships and cross tables
- **Week 9:** JPA repository implementation and profiles
- **Week 10:** Subclass integration and Spring Data JPA
- **Week 11:** Exception handling and custom error pages

``All required features and specifications as defined in the course assignments from week 1 to 11 has been implemented on time.``

---

## Profiles & Configuration

The project uses **Spring Profiles** to switch between different persistence implementations.

---

### ▶ Active Profile

Configured in `application.properties`:

```properties
spring.profiles.active=jpa
```

---

### Available Profiles

| Profile        | Description                                                                        |
|----------------|------------------------------------------------------------------------------------|
| **inmemory**   | Java collections only, seeded via `DataFactory`. No database required.             |
| **jdbc**       | Spring JDBC (`JdbcClient`) with H2 database. Uses `schema.sql` and `data.sql`.     |
| **jpa**        | JPA with Hibernate and H2 database.                                                |
| **springdata** | Spring Data `JpaRepository`, method queries & custom queries.                      |
| `dev` | Development profile using H2 + JPA with SQL logging and SQL initialization enabled.         |
| **prod**       | PostgreSQL configuration for production environment.                               |

---

## Database Configuration

The application supports both **H2 (development)** and **PostgreSQL (production)** via Spring Profiles.

---

### H2 Database (Development / JDBC / JPA)

- Used for `inmemory`, `jdbc`, and `jpa` profiles
- In-memory database, automatically initialized
- Schema and data loaded via `schema.sql` and `data.sql` (JDBC / JPA)
  **H2 Console:**

```
http://localhost:8080/h2-console
```

---

### PostgreSQL (Production)

Used when running with the `prod` profile.

```properties
spring.datasource.url=jdbc:postgresql:pro3_db
spring.datasource.username=postgres
spring.datasource.password=Student_1234
```

⚠ **Important:** When running with the `prod` profile, the PostgreSQL database **must already exist**.

---

## ▶ How to Run the Project

### Requirements

- **Java 21**
- **Gradle**
- *(Optional)* PostgreSQL (only for `prod` profile)

---

### Steps

1. Clone the repository
2. Open the project in **IntelliJ IDEA**
3. Select **SpringHotelsApplication**
4. Run the application with the desired profile  
   *(default profile: `jpa`)*
- or
```bash
./gradlew bootRun
```

5. Open your browser at:

```
http://localhost:8080
```

---

### 🌐 Start URL (Web Application)

```
http://localhost:8080/
```
This redirects to the **Home page** of the application.

---

## Available Pages

| URL               | Description                       |
|-------------------|-----------------------------------|
| `/home`           | Landing page                      |
| `/hotels`         | List and filter hotels            |
| `/hotels/{id}`    | Hotel details with rooms & guests |
| `/rooms`          | List and filter rooms             |
| `/rooms/{number}` | Room details with guests          |
| `/guests`         | List, search & filter guests      |
| `/guests/{id}`    | Guest details with rooms          |
| `/hotels/add`     | Add hotel form                    |
| `/rooms/add`      | Add room form                     |
| `/guests/add`     | Add guest form                    |
| `/history`        | Session history                   |

---

## 🌍 Internationalization (i18n)

The application supports **multiple languages** using Spring message bundles:

- 🇬🇧 **English** (default)
- 🇳🇱 **Dutch**
- 🇫🇷 **French**
- 🇩🇪 **German**
- 🇧🇩 **Bengali**
  
Language can be changed dynamically using the `lang` parameter:

```
?lang=en
?lang=nl
?lang=fr
?lang=de
?lang=bn
```
A language selector is also available in the navigation bar.

---

## Features Implemented

- **Hotel, Room, and Guest management**
- **Filtering and searching** using different data types
- **One-to-many and many-to-many** relationships
- **VIP Guest subclass** with discount logic
- **Multiple repository implementations**  
  (InMemory, JDBC, JPA, Spring Data)
- **Profile-based configuration**
- **Layered architecture**
- **Thymeleaf fragments** (navbar & footer)
- **Bootstrap UI** with responsive cards
- **Internationalization (i18n)**
- **Session history tracking** using interceptors
- **Custom converters**
- **Bean Validation** with translated error messages
- **Custom exception handling**
- **Separate database and general error pages**
- **Logging using SLF4J**

---

### Home Page

The **Home page** is designed in the style of a **real hotel booking platform**, focusing on usability and visual
clarity.

- **Hero search-style layout** (redirects to ``All Hotels`` page)
- **Featured Hotels, Beach & Spa, Popular Cities** Sections
- **Best Value Rooms, Premium Rooms, Top Picks**
- **Clear navigation** to all major application pages

---

## What Makes This Project Unique

### Technical

- **Four repository implementations**  
  *(InMemory, JDBC, JPA, Spring Data JPA)*
- **Clean layered architecture** with loose coupling
- **Profile-based switching** without code changes
- **Open Session in View explicitly disabled**

### ⚙ Functional

- **Session-based visit history** per user
- **VIP Guest subclass** integrated across:
    - UI
    - Service layer
    - Persistence layer
- **Realistic dataset** with meaningful relationships

### Presentation

- **Bootstrap cards** instead of plain tables
- **Responsive layout** for different screen sizes
- **Consistent navigation and footer** using Thymeleaf fragments
- **Clean, readable, user-friendly UI**

---

## Application Flow & Architecture

The application uses a **layered architecture** for all domain entities (**Hotel, Room, Guest**).
Every web request follows the same execution path, independent of the active persistence profile.

- **Browser (HTTP Request)**
  ↓
- **Controller Layer**  
  *(HotelController / RoomController / GuestController)*
  ↓
- **Service Layer (Interfaces)**  
  *(HotelService / RoomService / GuestService)*
  ↓
- **Service Implementations**  
  *(HotelServiceImpl / RoomServiceImpl / GuestServiceImpl)*
  ↓
- **Repository Interfaces**  
  *(HotelRepository / RoomRepository / GuestRepository)*
  ↓
- **Repository Implementations (Profile-based)**  
  *(InMemory / JDBC / JPA / Spring Data)*
  ↓
- **Database or In-Memory Data Source**

### Explanation

- **Controllers** handle HTTP requests and prepare data for the views
- **Services** contain all business logic and validation
- **Repository interfaces** provide loose coupling
- **Repository implementations** are selected using Spring profiles
- No controller accesses the database directly
- The same flow applies to all entities (Hotel, Room, Guest)
  This design allows the application to switch persistence strategies without changing business or presentation logic.

---

## Code Quality

- **Obsolete code removed**
- **No commented-out dead code**
- **Clear and logical package structure**
- **Logging added** to complex or critical methods
- **Comments added** where logic is not immediately obvious

---

## Verification

Before final submission, the project was:

- **Cloned into a new directory**
- **Built and run successfully**
- **Tested with multiple Spring profiles**

---

## Final Notes

All assignments from **Week 1 to Week 11** were completed **on time** and fully implement
all required features and specifications defined in each weekly assignment.

The project demonstrates a **gradual evolution** from:

- a **console application**
- to a **full Spring Boot web application**
- with **multiple persistence strategies**
### Acknowledgements

Special thanks to ``Mr. de Rijke`` for the guidance and teaching throughout the **Programming 3** course.

**Final version tagged as `final` on the `main` branch**
---