from app.models.recommendation_model import rank_recommendations
from app.schemas.recommendation_schema import RecommendationRequest, RecommendationResponse


def recommend_rooms(request: RecommendationRequest) -> RecommendationResponse:
    # Service layer keeps API routing separate from model scoring
    # The model receives only validated feature DTOs, not web or database objects
    recommendations = rank_recommendations(
        request.pastBookings,
        request.candidateRooms,
    )
    return RecommendationResponse(recommendations=recommendations)