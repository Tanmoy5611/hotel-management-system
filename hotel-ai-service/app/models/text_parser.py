from decimal import Decimal
import re
from typing import Optional

from app.schemas.chat_schema import SearchFilters


ROOM_TYPES = {
    # Text aliases map customer language to the Spring RoomType enum
    "single": "SINGLE",
    "double": "DOUBLE",
    "suite": "SUITE",
    "family": "DOUBLE",
    "couple": "DOUBLE",
    "couples": "DOUBLE",
}

KNOWN_CITIES = {
    # Keep city extraction limited to destinations available in this application
    "antwerp": "Antwerp",
    "brussels": "Brussels",
    "bruges": "Bruges",
    "ghent": "Ghent",
    "gent": "Ghent",
    "paris": "Paris",
    "london": "London",
    "stockholm": "Stockholm",
    "singapore": "Singapore",
}

GREETINGS = {"hi", "hello", "hey", "good morning", "good afternoon", "good evening"}


def extract_filters(message: str) -> SearchFilters:
    # Rule-based parsing is explainable and enough for the current dataset size
    text = message.lower()
    max_price = extract_price_limit(text)
    min_stars = None

    # Budget words are translated into a practical default price ceiling
    if max_price is None and any(word in text for word in ["cheap", "budget", "affordable", "low price", "cheaper"]):
        max_price = Decimal("150")

    # Luxury intent becomes a minimum star filter instead of a vague keyword score
    if any(word in text for word in ["luxury", "premium", "high end", "five star", "best hotel", "top hotel"]):
        min_stars = 4

    # Return structured filters so downstream scoring never parses raw text again
    return SearchFilters(
        city=extract_city(text),
        maxPrice=max_price,
        roomType=extract_room_type(text),
        hasSpa=True if any(term in text for term in ["spa", "wellness", "relax", "relaxing"]) else None,
        seaView=True if any(term in text for term in ["sea view", "beach", "ocean", "waterfront"]) else None,
        minStars=min_stars,
    )


def is_greeting(message: str) -> bool:
    # Greeting detection prevents the bot from pretending every message is a search
    text = message.lower().strip()
    return text in GREETINGS or any(text.startswith(greeting + " ") for greeting in GREETINGS)


def has_search_intent(filters: SearchFilters) -> bool:
    # At least one extracted filter means we can attempt a room search
    return any([
        filters.city,
        filters.maxPrice,
        filters.roomType,
        filters.hasSpa is not None,
        filters.seaView is not None,
        filters.minStars,
    ])


def extract_price_limit(text: str) -> Optional[Decimal]:
    # Match natural budget phrases like under 200 or up to €250
    # Decimal is used because the value is compared with currency fields
    match = re.search(r"(?:under|below|less than|max|maximum|up to)\s*€?\s*(\d{2,4})", text)
    if match:
        return Decimal(match.group(1))

    # Support short messages like €180 even when no budget word is present
    euro_match = re.search(r"€\s*(\d{2,4})", text)
    if euro_match:
        return Decimal(euro_match.group(1))

    return None


def extract_city(text: str) -> Optional[str]:
    # Cities are limited to destinations that exist in the hotel catalog
    for key, city in KNOWN_CITIES.items():
        if key in text:
            return city
    return None


def extract_room_type(text: str) -> Optional[str]:
    # User-friendly words are mapped to the RoomType enum used by Spring
    for key, room_type in ROOM_TYPES.items():
        if key in text:
            return room_type
    return None