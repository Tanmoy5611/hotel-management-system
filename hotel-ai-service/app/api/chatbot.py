from fastapi import APIRouter

from app.schemas.chat_schema import ChatRequest, ChatResponse
from app.services.chatbot_service import find_rooms

# Router prefix matches the Spring RestClient path
router = APIRouter(prefix="/ai", tags=["chatbot"])


@router.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest) -> ChatResponse:
    # Route stays thin and delegates all decision logic to the service layer
    # Pydantic validates and normalizes the message before this function runs
    return find_rooms(request)