"""
POST /api/v1/consistency

Перевіряє наскільки текст (розділ) відповідає стилю автора.
Використовує StyleConsistencyAnalyzer з повноцінним порівнянням метрик, тону, та семантики.
"""

from fastapi import APIRouter, HTTPException
from models.schemas import ConsistencyRequest, ConsistencyResponse, ChapterConsistency
from typing import List, Optional
from services.style_consistency import StyleConsistencyAnalyzer
from services.profile_store import ProfileStore

router = APIRouter()
analyzer = StyleConsistencyAnalyzer()


@router.post("", response_model=ConsistencyResponse)
def check_consistency(req: ConsistencyRequest):
    profile = ProfileStore.get(req.author_id)
    if not profile:
        raise HTTPException(
            status_code=404,
            detail=f"Author profile '{req.author_id}' not found. Call /analyze first."
        )

    # Re-use the multi-chapter architecture natively
    per_chapter = []
    
    for ch_in in req.chapters:
        if len(ch_in.text.strip()) < 100:
            continue
        
        chapter_dict = {
            "id": ch_in.id,
            "title": ch_in.title,
            "text": ch_in.text,
        }
        per_chapter.append(chapter_dict)

    if not per_chapter:
        raise HTTPException(
            status_code=400,
            detail="No valid chapters provided. Minimum 100 characters each."
        )

    result = analyzer.analyze_chapters(profile, per_chapter)

    return ConsistencyResponse(
        author_id=req.author_id,
        overall_score=result["overall_score"],
        overall_label=result["overall_label"],
        per_chapter=[ChapterConsistency(**ch) for ch in result["per_chapter"]]
    )