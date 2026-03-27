"""
POST /api/v1/generate

Spring Boot calls this endpoint when the author clicked "Suggest continuation".
This is a slower endpoint (LLM generation ~3-10 sec).
"""

from fastapi import APIRouter, HTTPException
from models.schemas import GenerateRequest, GenerateResponse
from services.text_generator import TextGenerator
from services.profile_store import ProfileStore

router = APIRouter()
generator = TextGenerator()


@router.post("", response_model=GenerateResponse)
async def generate_continuation(req: GenerateRequest):
    profile = ProfileStore.get(req.author_id)
    if not profile:
        raise HTTPException(
            status_code=404,
            detail=f"Author profile '{req.author_id}' not found. Call /analyze first"
        )

    if len(req.context.strip()) < 50:
        raise HTTPException(
            status_code=400,
            detail="Context is too short. Minimum 50 characters required."
        )

    try:
        result = await generator.generate(
            profile=profile,
            context=req.context,
            max_sentences=req.max_sentences,
            temperature=req.temperature,
        )
    except Exception as e:
        raise HTTPException(
            status_code=503,
            detail=f"Ollama unavailable: {str(e)}. Make sure ollama is running on localhost:11434"
        )

    return GenerateResponse(**result)


@router.get("/models")
async def list_available_models():
    """List of models available in Ollama."""
    import httpx
    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            r = await client.get("http://localhost:11434/api/tags")
            r.raise_for_status()
            data = r.json()
            models = [m["name"] for m in data.get("models", [])]
            return {"models": models}
    except Exception as e:
        return {"models": [], "error": str(e)}