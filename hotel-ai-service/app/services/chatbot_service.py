from app.models.recommendation_model import to_suggestion
from app.models.text_parser import extract_filters, has_search_intent, is_greeting
from app.schemas.chat_schema import ChatRequest, ChatResponse, SearchFilters
from app.schemas.recommendation_schema import RoomFeature, RoomSuggestion


def find_rooms(request: ChatRequest) -> ChatResponse:
    # Main chatbot workflow used by the Spring web UI
    # It avoids ML scoring when the user is only greeting the assistant
    # The chatbot first decides whether to greet, clarify, or search
    if is_greeting(request.message):
        return ChatResponse(
            reply="Hi! I can help you choose a room. Tell me the city, budget, room type, dates, or style of trip. For example: family room in Antwerp under €200, romantic suite with spa, or cheap double room.",
            filters=SearchFilters(),
            rooms=[],
        )

    filters = extract_filters(request.message)
    # If parsing finds no useful search signal, ask a follow-up instead of returning random rooms
    if not has_search_intent(filters):
        return ChatResponse(
            reply="I can help with that. Which city or hotel area should I search, and do you have a budget or room type in mind?",
            filters=filters,
            rooms=[],
        )

    # Only ranked and explainable room matches are returned to Spring
    ranked_rooms = rank_rooms(filters, request.availableRooms)
    reply = build_reply(ranked_rooms, filters)

    return ChatResponse(reply=reply, filters=filters, rooms=ranked_rooms[:5])


def build_reply(ranked_rooms: list[RoomSuggestion], filters: SearchFilters) -> str:
    # The reply explains the result and proposes the next refinement
    # This makes the bot feel conversational rather than only returning room cards
    if not ranked_rooms:
        missing_hint = next_missing_detail(filters)
        return f"I could not find an exact match yet. {missing_hint} I can also relax the budget or remove one preference."

    details = []
    # Build a short natural-language summary from the filters we actually understood
    if filters.city:
        details.append(filters.city)
    if filters.roomType:
        details.append(filters.roomType.lower())
    if filters.maxPrice:
        details.append(f"under €{filters.maxPrice}")
    if filters.hasSpa:
        details.append("with spa")
    if filters.seaView:
        details.append("with sea view")
    if filters.minStars:
        details.append("luxury style")

    summary = " for " + ", ".join(details) if details else ""
    top_room = ranked_rooms[0]
    # Mention the top room directly so the answer gives a useful next step
    return (
        f"I found {len(ranked_rooms)} good room option{'s' if len(ranked_rooms) != 1 else ''}{summary}. "
        f"My top pick is Room {top_room.roomNumber} at {top_room.hotelName} because it matches {top_room.reason}. "
        f"Would you like me to make it cheaper, add spa, focus on one city, or show only suites?"
    )


def next_missing_detail(filters: SearchFilters) -> str:
    # Follow-up questions ask for the most useful missing search signal first
    # City and budget usually reduce the hotel search space the most
    if not filters.city:
        return "Which city should I focus on?"
    if not filters.maxPrice:
        return "What maximum price per night should I use?"
    if not filters.roomType:
        return "Which room type do you prefer: single, double, or suite?"
    return "Try a wider budget, another city, or a different room type."


def rank_rooms(filters: SearchFilters, rooms: list[RoomFeature]) -> list[RoomSuggestion]:
    # Hard filters remove impossible matches, then score keeps the best rooms first
    # This is search ranking for chat, separate from personal recommendation ranking
    suggestions = []

    for room in rooms:
        # A zero score means the room failed a required user preference
        score, reason_parts = score_room(filters, room)
        if score > 0:
            reason = ", ".join(reason_parts) if reason_parts else "Relevant room match"
            suggestions.append(to_suggestion(room, round(score, 2), reason))

    return sorted(suggestions, key=lambda item: item.score, reverse=True)


def score_room(filters: SearchFilters, room: RoomFeature) -> tuple[float, list[str]]:
    # Start with a small base score so partial text searches can still return results
    # Each matched preference increases confidence and creates a user-facing reason
    score = 0.25
    reasons: list[str] = []

    # City is treated as a hard requirement because users rarely want another destination
    if filters.city:
        if room.city.lower() != filters.city.lower():
            return 0.0, []
        score += 0.25
        reasons.append(f"{room.city} location")

    # Room type is also a hard requirement because it changes the actual product
    if filters.roomType:
        if room.roomType.upper() != filters.roomType.upper():
            return 0.0, []
        score += 0.15
        reasons.append(f"{room.roomType.lower()} room")

    # Amenity filters are hard requirements once the user explicitly asks for them
    if filters.hasSpa is not None:
        if room.hasSpa != filters.hasSpa:
            return 0.0, []
        score += 0.15
        reasons.append("spa hotel")

    # View preference is treated as required when mentioned in the request
    if filters.seaView is not None:
        if room.seaView != filters.seaView:
            return 0.0, []
        score += 0.10
        reasons.append("sea view")

    # Budget is a hard upper limit because showing expensive rooms feels unhelpful
    if filters.maxPrice is not None:
        if room.pricePerNight > filters.maxPrice:
            return 0.0, []
        score += 0.15
        reasons.append("within budget")

    # Star rating captures luxury intent without needing a separate hotel class model
    if filters.minStars is not None:
        if room.stars < filters.minStars:
            return 0.0, []
        score += 0.10
        reasons.append(f"{room.stars} star hotel")

    return min(score, 1.0), reasons