from decimal import Decimal

import pytest
from pydantic import ValidationError

from app.schemas.chat_schema import ChatRequest
from app.schemas.recommendation_schema import RoomFeature
from app.services.chatbot_service import find_rooms


def test_find_rooms_extracts_filters_and_returns_matching_rooms():
    response = find_rooms(
        ChatRequest(
            message="I want a cheap spa hotel in Antwerp with a double room",
            availableRooms=[
                room(1, "Antwerp", "DOUBLE", Decimal("135"), True),
                room(2, "Paris", "SUITE", Decimal("300"), True),
            ],
        )
    )

    assert response.filters.city == "Antwerp"
    assert response.filters.maxPrice == Decimal("150")
    assert response.filters.roomType == "DOUBLE"
    assert response.rooms[0].roomId == 1


def test_find_rooms_answers_greeting_without_searching():
    response = find_rooms(ChatRequest(message="hello", availableRooms=[]))

    assert response.reply.startswith("Hi! I can help you choose a room.")
    assert response.rooms == []


def test_find_rooms_asks_follow_up_when_request_is_too_vague():
    response = find_rooms(ChatRequest(message="I need a place to stay", availableRooms=[]))

    assert response.reply == "I can help with that. Which city or hotel area should I search, and do you have a budget or room type in mind?"
    assert response.rooms == []


def test_chat_request_rejects_blank_message():
    with pytest.raises(ValidationError):
        ChatRequest(message="   ", availableRooms=[])


def room(room_id: int, city: str, room_type: str, price: Decimal, has_spa: bool) -> RoomFeature:
    return RoomFeature(
        roomId=room_id,
        hotelId=f"hotel-{room_id}",
        hotelName=f"Hotel {room_id}",
        roomNumber=100 + room_id,
        city=city,
        stars=4,
        hasSpa=has_spa,
        roomType=room_type,
        pricePerNight=price,
        seaView=False,
        numberOfNights=None,
        discountPercentage=Decimal("0"),
    )