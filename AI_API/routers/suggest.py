"""
POST /api/v1/suggest

Spring Boot calls this endpoint:
  - Every N seconds while the author is writing (debounce on the frontend ~1-2 sec)
  - Or when a hotkey is pressed

Fast endpoint — no ML, only statistics + cosine similarity.
Response < 50ms.
"""

from fastapi import APIRouter, HTTPException
from models.schemas import SuggestRequest, SuggestResponse, WordEntry, PhraseEntry
from services.suggestion_engine import SuggestionEngine
from services.profile_store import ProfileStore

router = APIRouter()
engine = SuggestionEngine()


@router.post("", response_model=SuggestResponse)
def get_suggestions(req: SuggestRequest):
    profile = ProfileStore.get(req.author_id)
    if not profile:
        raise HTTPException(
            status_code=404,
            detail=f"Author profile '{req.author_id}' not found. Call /analyze first"
        )

    if not req.current_text.strip():
        raise HTTPException(status_code=400, detail="current_text cannot be empty")

    result = engine.suggest(
        profile=profile,
        current_text=req.current_text,
        cursor_context=req.cursor_context,
    )

    return SuggestResponse(
        word_suggestions=[WordEntry(**w) for w in result["word_suggestions"]],
        phrase_suggestions=[PhraseEntry(**p) for p in result["phrase_suggestions"]],
    )