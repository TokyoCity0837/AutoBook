"""
POST /api/v1/vocabulary/enrich  — enriches words with meaning and example via LLM
GET  /api/v1/vocabulary/{author_id} — returns the ready public vocabulary

Spring Boot stores the result in the vocabulary table (word, category, meaning, usage_example).
"""

from fastapi import APIRouter, HTTPException
from models.schemas import VocabularyResponse, VocabularyWord
from services.profile_store import ProfileStore
from services.text_generator import TextGenerator
import httpx
import json

router = APIRouter()
generator = TextGenerator()

@router.get("/{author_id}", response_model=VocabularyResponse)
def get_vocabulary(author_id: str, limit: int = 50):
    profile = ProfileStore.get(author_id)
    if not profile:
        raise HTTPException(status_code=404, detail=f"Profile '{author_id}' not found")

    words = [
        VocabularyWord(
            word=w["word"],
            category=w.get("category", "THEMATIC"),
            score=w["score"],
            freq=w["freq"],
            meaning=None,       
            usage_example=None,
            ai_generated=False,
        )
        for w in profile.get("vocabulary", [])[:limit]
    ]

    return VocabularyResponse(author_id=author_id, words=words)


@router.post("/{author_id}/enrich", response_model=VocabularyResponse)
async def enrich_vocabulary(author_id: str, limit: int = 20):
    profile = ProfileStore.get(author_id)
    if not profile:
        raise HTTPException(status_code=404, detail=f"Profile '{author_id}' not found")

    top_words = profile.get("vocabulary", [])[:limit]
    words_list = [w["word"] for w in top_words]

    prompt = f"""For each word in this list, provide a JSON response with a short meaning and a usage example in a creative fiction context.

Words: {', '.join(words_list)}

Respond with a JSON array only, no other text:
[
  {{"word": "word1", "meaning": "brief definition", "example": "short example sentence"}},
  ...
]"""

    enriched_map = {}
    try:
        payload = {
            "model": generator.model,
            "messages": [{"role": "user", "content": prompt}],
            "stream": False,
            "options": {"temperature": 0.3}
        }
        async with httpx.AsyncClient(timeout=60.0) as client:
            r = await client.post("http://localhost:11434/api/chat", json=payload)
            r.raise_for_status()
            content = r.json()["message"]["content"].strip()

        start = content.find("[")
        end = content.rfind("]") + 1
        if start != -1 and end > start:
            enriched_list = json.loads(content[start:end])
            enriched_map = {item["word"]: item for item in enriched_list}

    except Exception:
        pass  # if Ollama is unavailable — return without enrichment

    result_words = []
    for w in top_words:
        enriched = enriched_map.get(w["word"], {})
        result_words.append(VocabularyWord(
            word=w["word"],
            category=w.get("category", "THEMATIC"),
            score=w["score"],
            freq=w["freq"],
            meaning=enriched.get("meaning"),
            usage_example=enriched.get("example"),
            ai_generated=bool(enriched),
        ))

    return VocabularyResponse(author_id=author_id, words=result_words)