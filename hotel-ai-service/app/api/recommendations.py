from fastapi import APIRouter

from app.schemas.recommendation_schema import RecommendationRequest, RecommendationResponse
from app.services.recommendation_service import recommend_rooms

# Router prefix matches the Spring RestClient path
router = APIRouter(prefix="/ai", tags=["recommendations"])


@router.post("/recommendations", response_model=RecommendationResponse)
def recommendations(request: RecommendationRequest) -> RecommendationResponse:
    # Route stays thin and delegates all ML scoring to the service layer
    # Pydantic validates room feature shape before scoring starts
    return recommend_rooms(request)