from decimal import Decimal
from typing import Optional

from pydantic import BaseModel, Field, field_validator

from app.schemas.recommendation_schema import RoomFeature, RoomSuggestion


class ChatRequest(BaseModel):
    # Natural-language message from the chatbot UI
    message: str = Field(min_length=1)
    availableRooms: list[RoomFeature] = Field(default_factory=list)

    @field_validator("message")
    @classmethod
    def normalize_message(cls, value: str) -> str:
        # Reject whitespace-only messages before the service layer runs
        cleaned = value.strip()
        if not cleaned:
            raise ValueError("message is required")
        return cleaned


class SearchFilters(BaseModel):
    # Structured filters extracted from free text
    city: Optional[str] = None
    maxPrice: Optional[Decimal] = None
    roomType: Optional[str] = None
    hasSpa: Optional[bool] = None
    seaView: Optional[bool] = None
    minStars: Optional[int] = None


class ChatResponse(BaseModel):
    # Text answer plus optional ranked room cards
    reply: str
    filters: SearchFilters
    rooms: list[RoomSuggestion]