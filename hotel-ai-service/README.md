# Hotel AI Service

This folder contains the Python AI microservice for the hotel project. The main Spring Boot application still owns the database, security, booking rules, and web pages. This FastAPI service only receives clean room feature DTOs from Spring and returns ranked, explainable AI results.

The AI service has two main responsibilities:

- Recommend rooms for a logged-in customer based on previous bookings
- Understand a chatbot message and return matching rooms with a short explanation

The implementation is intentionally simple and explainable. It does not use a black-box LLM. Instead, it uses rule-based text parsing for chat search and cosine similarity for personalized recommendations.

## Why This Is a Separate Service

The project uses Java and Spring Boot for the main hotel application. Python is better suited for small AI and machine learning code, so the AI part is separated into a FastAPI microservice.

This gives a few advantages:

- Spring Boot can stay focused on the hotel domain and database
- Python dependencies such as `scikit-learn`, `FastAPI`, and `Pydantic` do not enter the Java build
- AI logic is independently testable with `pytest`
- The recommendation model can be changed later without changing the main Spring controllers

## High Level Architecture

```text
Browser
  |
  | calls Spring endpoints
  v
Spring Boot webapi/controller/ai
  |
  | loads rooms, bookings, and current customer from database
  v
Spring AI business services
  |
  | maps JPA entities into flat AI DTOs
  v
PythonAiClient
  |
  | HTTP POST to FastAPI on port 8001
  v
Hotel AI Service
  |
  | parses text or ranks room features
  v
JSON response back to Spring
  |
  v
Browser renders recommendations, room cards, booking choices
```

## Main Spring Boot Integration Files

The Python service is not called directly from the browser. The browser calls Spring, and Spring calls Python.

| Responsibility | Spring file |
| :-- | :-- |
| Recommendation endpoint for browser | `src/main/java/be/kdg/prog5/hotels/webapi/controller/ai/AiRecommendationApiController.java` |
| Chat endpoint for browser | `src/main/java/be/kdg/prog5/hotels/webapi/controller/ai/AiChatApiController.java` |
| AI booking endpoint for quote, confirm, cancel | `src/main/java/be/kdg/prog5/hotels/webapi/controller/ai/AiBookingApiController.java` |
| Recommendation business flow | `src/main/java/be/kdg/prog5/hotels/business/ai/AiRecommendationServiceImpl.java` |
| Chat business flow | `src/main/java/be/kdg/prog5/hotels/business/ai/AiChatServiceImpl.java` |
| Entity to AI feature mapping | `src/main/java/be/kdg/prog5/hotels/business/ai/AiDataMapper.java` |
| HTTP client to Python | `src/main/java/be/kdg/prog5/hotels/infrastructure/ai/PythonAiClient.java` |
| Auto-start Python process | `src/main/java/be/kdg/prog5/hotels/infrastructure/ai/PythonAiProcessManager.java` |

## Python Project Structure

```text
hotel-ai-service
  app
    api
      chatbot.py
      recommendations.py
    models
      recommendation_model.py
      text_parser.py
    schemas
      chat_schema.py
      recommendation_schema.py
    services
      chatbot_service.py
      recommendation_service.py
    main.py
  tests
    test_chatbot_service.py
    test_recommendation_service.py
  requirements.txt
```

### `app/main.py`

Creates the FastAPI application and registers the routers.

Available routes:

- `GET /health`
- `POST /ai/recommendations`
- `POST /ai/chat`

Spring uses `/health` to check if the Python service is already running before starting a new process.

### `app/api`

The API layer is intentionally thin.

- `recommendations.py` receives `RecommendationRequest` and returns `RecommendationResponse`
- `chatbot.py` receives `ChatRequest` and returns `ChatResponse`

The API layer does not contain model logic. It only delegates to the service layer.

### `app/schemas`

The schemas are Pydantic models. They validate the request and response shape.

Important schema:

```python
class RoomFeature(BaseModel):
    roomId: int
    hotelId: str
    hotelName: str
    roomNumber: int
    city: str
    stars: int
    hasSpa: bool
    roomType: str
    pricePerNight: Decimal
    seaView: bool
    numberOfNights: Optional[int] = None
    discountPercentage: Decimal = Decimal("0")
```

This is a flat DTO. It is not a JPA entity and it does not contain nested hotel or room objects. Spring creates this DTO in `AiDataMapper`.

Room type validation only allows:

- `SINGLE`
- `DOUBLE`
- `SUITE`

This matches the Java `RoomType` enum.

## Recommendation Model

The recommendation model is implemented in:

```text
app/models/recommendation_model.py
```

The goal is to recommend rooms similar to what the customer booked before.

### Input

Spring sends:

- `pastBookings`: rooms the current customer booked before
- `candidateRooms`: all rooms that can be ranked now

If the customer has no booking history, the model returns an empty list. This is better than pretending to personalize without data.

### Customer Profile Building

The model builds one customer profile from past bookings.

It uses:

- Most common city
- Most common room type
- Average price per night
- Average hotel stars
- Whether the customer ever booked a spa hotel
- Whether the customer ever booked a sea view room

Example:

```text
Past bookings:
- Antwerp DOUBLE room, 4 stars, spa, 140 euro
- Antwerp DOUBLE room, 4 stars, spa, 160 euro

Customer profile:
- city = antwerp
- room_type = DOUBLE
- average price = 150
- average stars = 4
- has_spa = true
- sea_view = false
```

### Vector Encoding

The model uses `DictVectorizer` from scikit-learn.

Categorical values become one-hot features:

```text
city=antwerp
room_type=DOUBLE
```

Boolean values become numeric values:

```text
has_spa = 1.0
sea_view = 0.0
```

Numeric values are scaled:

```text
price / 600
stars / 5
```

Scaling is important because price is much larger than star rating. Without scaling, price would dominate the similarity score.

### Similarity Algorithm

The recommendation model uses cosine similarity.

```python
cosine_similarity(customer_profile_vector, room_vectors)
```

The closer a candidate room is to the customer profile, the higher the score.

Each result is converted into a `RoomSuggestion` with:

- room id
- hotel id
- hotel name
- room number
- city
- room type
- price per night
- score
- reason

### Explanation Reason

The model also returns a reason so the UI can explain the recommendation.

Examples:

- `Matches your preferred city, room type, spa preference`
- `Balanced match based on price and hotel quality`

This makes the recommendation explainable for the user and for project evaluation.

## Chatbot Room Search

The chatbot search is implemented in:

```text
app/services/chatbot_service.py
app/models/text_parser.py
```

The chatbot does not use an LLM. It uses rule-based parsing and deterministic room ranking. This keeps the behavior easy to test and explain.

### Chat Flow

```text
User message
  |
  v
Spring AiChatApiController
  |
  v
AiChatServiceImpl loads all rooms with hotel data
  |
  v
AiDataMapper converts rooms into RoomFeature DTOs
  |
  v
Python /ai/chat
  |
  v
text_parser extracts filters
  |
  v
chatbot_service ranks matching rooms
  |
  v
Spring returns reply + room cards to the browser
```

### Text Parsing

The parser extracts structured filters from natural text.

Supported filters:

| User text example | Extracted filter |
| :-- | :-- |
| `Antwerp`, `Brussels`, `Ghent` | `city` |
| `cheap`, `budget`, `affordable` | `maxPrice = 150` |
| `under 200`, `up to €250` | `maxPrice` |
| `single`, `double`, `suite` | `roomType` |
| `family`, `couple` | `DOUBLE` |
| `spa`, `wellness`, `relaxing` | `hasSpa = true` |
| `sea view`, `beach`, `waterfront` | `seaView = true` |
| `luxury`, `premium`, `five star` | `minStars = 4` |

Known cities are limited to the cities available in this hotel catalog. This prevents the model from searching for places that do not exist in the data.

### Greeting and Vague Messages

The chatbot first checks if the user is greeting.

Example:

```text
hello
```

Response:

```text
Hi! I can help you choose a room...
```

If the message has no useful search filters, the bot asks a follow-up question instead of returning random rooms.

Example:

```text
I need a place to stay
```

Response:

```text
Which city or hotel area should I search, and do you have a budget or room type in mind?
```

### Chat Ranking

The chat ranking is different from the personal recommendation model.

Chat search starts with a base score of `0.25`.

Then it adds points for matched filters:

| Match | Score increase |
| :-- | --: |
| City | `+0.25` |
| Room type | `+0.15` |
| Spa | `+0.15` |
| Budget | `+0.15` |
| Sea view | `+0.10` |
| Star rating | `+0.10` |

Some filters are hard requirements. If the user asks for Antwerp, a room in Paris is removed. If the user asks for a suite, non-suite rooms are removed. If the user asks for a maximum price, rooms above that price are removed.

The top 5 room matches are returned.

## AI Booking Support

The Python AI service does not create bookings.

Booking remains inside Spring Boot because booking needs:

- current authenticated customer
- room availability check
- customer discount
- database transaction
- ownership and security rules

The chatbot can guide booking through Spring endpoints:

- `GET /api/ai/bookings/session`
- `POST /api/ai/bookings/quote`
- `POST /api/ai/bookings/confirm`
- `GET /api/ai/bookings`
- `POST /api/ai/bookings/cancel`

This means the chatbot can help the user book or cancel, but Spring still protects the actual business operation.

## API Endpoints

### Health

```http
GET /health
```

Response:

```json
{
  "status": "ok"
}
```

Used by `PythonAiProcessManager` to check if the service is alive.

### Recommendations

```http
POST /ai/recommendations
```

Request:

```json
{
  "customerId": 5,
  "pastBookings": [
    {
      "roomId": 1,
      "hotelId": "hilton-antwerp",
      "hotelName": "Hilton Antwerp",
      "roomNumber": 204,
      "city": "Antwerp",
      "stars": 4,
      "hasSpa": true,
      "roomType": "DOUBLE",
      "pricePerNight": 140,
      "seaView": false,
      "numberOfNights": 2,
      "discountPercentage": 10
    }
  ],
  "candidateRooms": []
}
```

Response:

```json
{
  "recommendations": [
    {
      "roomId": 2,
      "hotelId": "hilton-antwerp",
      "hotelName": "Hilton Antwerp",
      "roomNumber": 205,
      "city": "Antwerp",
      "roomType": "DOUBLE",
      "pricePerNight": 135,
      "score": 0.91,
      "reason": "Matches your preferred city, room type, spa preference"
    }
  ]
}
```

### Chat

```http
POST /ai/chat
```

Request:

```json
{
  "message": "cheap spa hotel in Antwerp with a double room",
  "availableRooms": []
}
```

Response:

```json
{
  "reply": "I found 1 good room option for Antwerp, double, under €150, with spa...",
  "filters": {
    "city": "Antwerp",
    "maxPrice": 150,
    "roomType": "DOUBLE",
    "hasSpa": true,
    "seaView": null,
    "minStars": null
  },
  "rooms": []
}
```

## Spring Auto Start

The Spring project can automatically start this Python service.

Configuration is in:

```text
src/main/resources/application.properties
```

Important properties:

```properties
hotel.ai.service.base-url=http://localhost:8001
hotel.ai.service.auto-start=true
hotel.ai.service.working-directory=hotel-ai-service
hotel.ai.service.python-executable=hotel-ai-service/.venv/bin/python3
hotel.ai.service.startup-timeout-seconds=20
```

When the Spring application is ready, `PythonAiProcessManager` does this:

1. Checks `/health`
2. If Python is already running, it reuses it
3. If not running, it starts `uvicorn app.main:app --host 127.0.0.1 --port 8001`
4. It streams Python logs into the Spring log
5. It stops the child Python process when Spring shuts down

For tests, auto-start is disabled in:

```text
src/test/resources/application-test.properties
```

## Manual Run

From the project root:

```bash
cd hotel-ai-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8001
```

Then check:

```bash
curl http://localhost:8001/health
```

Expected:

```json
{"status":"ok"}
```

## Testing

Run Python tests:

```bash
cd hotel-ai-service
source .venv/bin/activate
pytest
```

Current test coverage checks:

- Chat parser extracts city, budget, spa, and room type
- Chat greeting does not trigger room search
- Vague chat messages ask follow-up questions
- Blank chat messages are rejected by Pydantic
- Recommendation model ranks similar city and room type higher
- Recommendation model returns empty recommendations without booking history
- Invalid room type is rejected

Run Spring AI-focused tests:

```bash
./gradlew test --tests '*Ai*UnitTest'
```

## Error Handling

If Python is unavailable, Spring catches the `RestClientException` inside `PythonAiClient` and throws `AiServiceUnavailableException`.

The web API returns a clear unavailable response instead of exposing low-level HTTP client errors.

The frontend also shows a friendly message:

```text
I cannot reach the AI service right now. Please start the Python AI service on port 8001 and try again.
```

## Limitations

This AI implementation is designed for this project dataset and for explainability.

Current limitations:

- It does not learn over time automatically
- It does not train a persisted model file
- Chat parsing supports known cities and keywords only
- Recommendation quality depends on previous bookings
- New customers do not receive personalized recommendations yet
- It does not use room availability for recommendations, only room features

These limitations are acceptable for this project because the goal is a clear, working, explainable AI feature integrated with Spring Boot.

## Summary

The AI part of the project is built as a small Python FastAPI service connected to Spring Boot.

Spring is responsible for:

- database access
- authentication
- booking rules
- DTO mapping
- calling Python
- returning results to the browser

Python is responsible for:

- validating AI request payloads
- extracting filters from chat text
- ranking room matches
- building customer preference profiles
- calculating cosine similarity recommendations
- returning explainable room suggestions

This separation keeps the project architecture clean and makes the AI implementation easier to understand, test, and extend.