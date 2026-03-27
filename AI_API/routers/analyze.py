from fastapi import APIRouter, HTTPException
from models.schemas import (
    AnalyzeRequest, AnalyzeResponse,
    WordEntry, PhraseEntry
)
from services.style_analyzer import StyleAnalyzer
from services.profile_store import ProfileStore

router = APIRouter()
analyzer = StyleAnalyzer()

@router.post("", response_model=AnalyzeResponse)
def analyze_style(req: AnalyzeRequest):
    if not req.texts or all(len(t.strip()) < 100 for t in req.texts):
        raise HTTPException(status_code=400, detail="Text is too short (minimum 100 characters)")

    try:
        profile = analyzer.analyze(req.texts)
    except Exception as e:
        raise HTTPException(status_code=422, detail=str(e))

    # Saving directly as dictionaries matching the Pydantic schema easily
    ProfileStore.save(req.author_id, profile)

    return AnalyzeResponse(
        author_id=req.author_id,
        style_words=[WordEntry(**w) for w in profile["style_words"]],
        thematic_words=[WordEntry(**w) for w in profile["thematic_words"]],
        signature_phrases=[PhraseEntry(**p) for p in profile["signature_phrases"]],
        vocabulary=[WordEntry(**w) for w in profile["vocabulary"]],
        characters=profile.get("characters", []),
        tone_metrics=profile.get("tone_metrics", {"polarity": 0, "subjectivity": 0.5}),
        complexity_metrics=profile.get("complexity_metrics", {"avg_sentence_len": 15, "lexical_density": 0.5, "sentence_variance": 5.0})
    )

@router.get("/{author_id}/exists")
def profile_exists(author_id: str):
    return {"author_id": author_id, "has_profile": ProfileStore.exists(author_id)}