from fastapi import FastAPI

from app.api import chatbot, recommendations

# FastAPI application kept separate from Spring Boot
# This keeps ML dependencies out of the Java build and makes the AI service independently testable
app = FastAPI(title="Hotel AI Service", version="0.1.0")

# Register each AI capability as its own router
# Personal recommendations and chat search have different request shapes
app.include_router(recommendations.router)
app.include_router(chatbot.router)


@app.get("/health")
def health() -> dict[str, str]:
    # Used by Spring to check whether the Python service is running
    # Keep this endpoint lightweight so startup polling is cheap
    return {"status": "ok"}