from decimal import Decimal
from typing import Optional

from pydantic import BaseModel, Field, field_validator


VALID_ROOM_TYPES = {"SINGLE", "DOUBLE", "SUITE"}


class RoomFeature(BaseModel):
    # DTO received from Spring, not a database entity
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

    @field_validator("roomType")
    @classmethod
    def normalize_room_type(cls, value: str) -> str:
        # Keep categorical values stable for vector encoding
        normalized = value.upper()
        if normalized not in VALID_ROOM_TYPES:
            raise ValueError("roomType must be SINGLE, DOUBLE, or SUITE")
        return normalized


class RecommendationRequest(BaseModel):
    # Spring sends booking history and the current candidate room catalog
    customerId: Optional[int] = None
    pastBookings: list[RoomFeature] = Field(default_factory=list)
    candidateRooms: list[RoomFeature] = Field(default_factory=list)


class RoomSuggestion(BaseModel):
    # Compact recommendation payload returned to Spring and the UI
    roomId: int
    hotelId: str
    hotelName: str
    roomNumber: int
    city: str
    roomType: str
    pricePerNight: Decimal
    score: float
    reason: str


class RecommendationResponse(BaseModel):
    # Empty list means there is no valid personalization signal yet
    recommendations: list[RoomSuggestion]