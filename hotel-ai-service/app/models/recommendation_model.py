from collections import Counter
from decimal import Decimal

from sklearn.feature_extraction import DictVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from app.schemas.recommendation_schema import RoomFeature, RoomSuggestion

PRICE_SCALE = 600.0
STAR_SCALE = 5.0


def rank_recommendations(
    past_bookings: list[RoomFeature],
    candidate_rooms: list[RoomFeature],
    limit: int = 5,
) -> list[RoomSuggestion]:
    # Entry point used by the recommendation API
    # It keeps the ML scoring pure so the service layer only handles request and response objects
    # Personal recommendations need both user history and rooms to rank
    if not past_bookings or not candidate_rooms:
        return []

    # Convert past bookings into one customer profile before comparing against candidate rooms
    profile = build_customer_profile(past_bookings)
    scores = cosine_scores(profile, candidate_rooms)
    scored_rooms = []

    # Combine each model score with an explanation that can be shown in the UI
    for room, score in zip(candidate_rooms, scores):
        scored_rooms.append(to_suggestion(room, score, build_reason(profile, room)))

    return sorted(scored_rooms, key=lambda item: item.score, reverse=True)[:limit]


def build_customer_profile(past_bookings: list[RoomFeature]) -> dict:
    # The profile is an aggregate of what the customer actually booked before
    # Most common categorical values represent repeated user preference
    cities = Counter(room.city.lower() for room in past_bookings if room.city)
    room_types = Counter(room.roomType.upper() for room in past_bookings if room.roomType)

    # Numeric preferences use averages to keep one unusual booking from dominating
    average_price = sum(float(room.pricePerNight) for room in past_bookings) / len(past_bookings)
    average_stars = sum(room.stars for room in past_bookings) / len(past_bookings)

    return {
        "city": cities.most_common(1)[0][0] if cities else None,
        "room_type": room_types.most_common(1)[0][0] if room_types else None,
        "price": average_price,
        "stars": average_stars,
        "has_spa": any(room.hasSpa for room in past_bookings),
        "sea_view": any(room.seaView for room in past_bookings),
    }


def cosine_scores(profile: dict, rooms: list[RoomFeature]) -> list[float]:
    # DictVectorizer handles categorical one-hot features and numeric features together
    # The customer profile is placed first so its vector can be compared with every room vector
    vectorizer = DictVectorizer(sparse=False)
    rows = [profile_features(profile)] + [room_features(room) for room in rooms]
    vectors = vectorizer.fit_transform(rows)

    # Cosine similarity is simple, deterministic, and good for mixed sparse feature vectors
    similarities = cosine_similarity(vectors[0:1], vectors[1:]).flatten()

    return [round(float(score), 2) for score in similarities]


def profile_features(profile: dict) -> dict:
    # Numeric values are scaled so price does not dominate categorical matches
    # Feature keys must match room_features so vector columns line up correctly
    return {
        f"city={profile['city']}": 1.0,
        f"room_type={profile['room_type']}": 1.0,
        "has_spa": 1.0 if profile["has_spa"] else 0.0,
        "sea_view": 1.0 if profile["sea_view"] else 0.0,
        "price": profile["price"] / PRICE_SCALE,
        "stars": profile["stars"] / STAR_SCALE,
    }


def room_features(room: RoomFeature) -> dict:
    # Candidate rooms are encoded with the same feature names as the profile
    # Boolean features stay numeric because DictVectorizer expects numeric values
    return {
        f"city={room.city.lower()}": 1.0,
        f"room_type={room.roomType.upper()}": 1.0,
        "has_spa": 1.0 if room.hasSpa else 0.0,
        "sea_view": 1.0 if room.seaView else 0.0,
        "price": float(room.pricePerNight) / PRICE_SCALE,
        "stars": room.stars / STAR_SCALE,
    }


def build_reason(profile: dict, room: RoomFeature) -> str:
    # Reasons expose why the score is high enough for an examiner or user to understand
    # Keep this separate from scoring so the model can change without breaking UI copy
    matches: list[str] = []

    if profile["city"] and room.city.lower() == profile["city"]:
        matches.append("preferred city")
    if profile["room_type"] and room.roomType.upper() == profile["room_type"]:
        matches.append("room type")
    if room.hasSpa == profile["has_spa"]:
        matches.append("spa preference")
    if room.seaView == profile["sea_view"]:
        matches.append("view preference")

    if not matches:
        return "Balanced match based on price and hotel quality"

    return "Matches your " + ", ".join(matches)


def to_suggestion(room: RoomFeature, score: float, reason: str) -> RoomSuggestion:
    # Keep the response model independent from the internal scoring helpers
    # Decimal is preserved for money values to avoid floating point currency output
    return RoomSuggestion(
        roomId=room.roomId,
        hotelId=room.hotelId,
        hotelName=room.hotelName,
        roomNumber=room.roomNumber,
        city=room.city,
        roomType=room.roomType,
        pricePerNight=Decimal(room.pricePerNight),
        score=score,
        reason=reason,
    )