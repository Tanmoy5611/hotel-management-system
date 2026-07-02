from decimal import Decimal

import pytest
from pydantic import ValidationError

from app.schemas.recommendation_schema import RecommendationRequest, RoomFeature
from app.services.recommendation_service import recommend_rooms


def test_recommend_rooms_prefers_similar_city_and_type():
    past_booking = room(1, "Antwerp", "DOUBLE", Decimal("140"), True)
    antwerp_double = room(2, "Antwerp", "DOUBLE", Decimal("135"), True)
    paris_suite = room(3, "Paris", "SUITE", Decimal("310"), False)

    response = recommend_rooms(
        RecommendationRequest(
            customerId=5,
            pastBookings=[past_booking],
            candidateRooms=[paris_suite, antwerp_double],
        )
    )

    assert response.recommendations[0].roomId == 2
    assert response.recommendations[0].score > response.recommendations[1].score


def test_recommend_rooms_returns_empty_without_booking_history():
    response = recommend_rooms(
        RecommendationRequest(
            customerId=5,
            pastBookings=[],
            candidateRooms=[room(2, "Antwerp", "DOUBLE", Decimal("135"), True)],
        )
    )

    assert response.recommendations == []


def test_room_feature_rejects_invalid_room_type():
    with pytest.raises(ValidationError):
        room(9, "Antwerp", "PENTHOUSE", Decimal("900"), True)


def room(room_id: int, city: str, room_type: str, price: Decimal, has_spa: bool) -> RoomFeature:
    return RoomFeature(
        roomId=room_id,
        hotelId=f"hotel-{room_id}",
        hotelName=f"Hotel {room_id}",
        roomNumber=200 + room_id,
        city=city,
        stars=4,
        hasSpa=has_spa,
        roomType=room_type,
        pricePerNight=price,
        seaView=False,
        numberOfNights=2,
        discountPercentage=Decimal("0"),
    )