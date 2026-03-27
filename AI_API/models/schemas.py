from pydantic import BaseModel, Field
from typing import List, Optional

# ── Base Entities ─────────────────────────────────────────────────────────────

class WordEntry(BaseModel):
    word:        str
    score:       float
    freq:        int
    category:    str   # "STYLE" (voice/flow) or "THEMATIC" (world/topic)

class PhraseEntry(BaseModel):
    phrase: str
    freq:   int

# ── Analyze API ───────────────────────────────────────────────────────────────

class ToneMetrics(BaseModel):
    polarity:     float
    subjectivity: float

class ComplexityMetrics(BaseModel):
    avg_sentence_len:  float
    lexical_density:   float
    sentence_variance: float

class AnalyzeRequest(BaseModel):
    author_id: str
    texts: List[str] = Field(..., description="List of texts — books or chapters")

class AnalyzeResponse(BaseModel):
    author_id:         str
    style_words:       List[WordEntry]     # Core connectives, adverbs (Voice)
    thematic_words:    List[WordEntry]     # Nouns, verbs, adjectives (World)
    signature_phrases: List[PhraseEntry]   # Frequent 2-3 word combinations
    vocabulary:        List[WordEntry]     # Top thematic words combined
    characters:        List[str]           # Extracted PROPN entities
    tone_metrics:      ToneMetrics
    complexity_metrics: ComplexityMetrics

# ── Suggest API ───────────────────────────────────────────────────────────────

class SuggestRequest(BaseModel):
    author_id:      str
    current_text:   str
    cursor_context: Optional[str] = None

class SuggestResponse(BaseModel):
    word_suggestions:   List[WordEntry]
    phrase_suggestions: List[PhraseEntry]

# ── Generate API (Continuation) ───────────────────────────────────────────────

class GenerateRequest(BaseModel):
    author_id:     str
    context:       str
    max_sentences: int   = Field(3, ge=1, le=10)
    temperature:   float = Field(0.8, ge=0.1, le=2.0)

class GenerateResponse(BaseModel):
    generated_text: str
    model_used:     str

# ── Consistency API (Matching style) ──────────────────────────────────────────

class ChapterInput(BaseModel):
    id:    str
    title: str
    text:  str

class ConsistencyRequest(BaseModel):
    author_id: str
    chapters:  List[ChapterInput]

class ChapterConsistency(BaseModel):
    id:                 str
    title:              str
    similarity_score:   float
    label:              str
    missing_signatures: List[str]
    outlier_words:      List[str]   # Words not typical for the author
    missing_characters: List[str]   # Characters expected but missing
    tone_deviation:     float       # absolute difference from author baseline
    complexity_deviation: float     # absolute difference from author baseline

class ConsistencyResponse(BaseModel):
    author_id:     str
    overall_score: float
    overall_label: str
    per_chapter:   List[ChapterConsistency]

# ── Vocabulary Public API ─────────────────────────────────────────────────────

class VocabularyWord(BaseModel):
    word:          str
    category:      str
    meaning:       Optional[str] = None
    usage_example: Optional[str] = None
    score:         float
    freq:          int
    ai_generated:  bool = False

class VocabularyResponse(BaseModel):
    author_id: str
    words:     List[VocabularyWord]