# AutoBook AI API

![Python](https://img.shields.io/badge/Python-3.10%2B-blue)
![FastAPI](https://img.shields.io/badge/FastAPI-0.100%2B-green)
![spaCy](https://img.shields.io/badge/spaCy-en__core__web__sm-purple)
![Ollama](https://img.shields.io/badge/Ollama-llama3.2-orange)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

An AI microservice for analyzing author writing style and assisting during the writing process.  
Built with **Python / FastAPI + spaCy + TextBlob + Ollama**, it works alongside a Spring Boot backend to provide real-time style analysis, vocabulary building, consistency checking, and LLM-powered text generation.

---

## Architecture

```
Spring Boot (Java)  ←→  AutoBook AI API (Python/FastAPI)  ←→  Ollama (LLM)
```

| Component | Role |
|:---|:---|
| **Spring Boot** | Main backend — authentication, book storage, user management, vocabulary DB |
| **AutoBook AI API** | This service — all NLP/AI logic |
| **Ollama (llama3.2)** | Local LLM — text generation and vocabulary enrichment |

---

## Core Concept — Author's Style Passport

Instead of abstract numeric metrics (sentence length counts, comma rates, etc.), the system builds a human-readable **Style Passport** — a structured vocabulary fingerprint of the author, composed of:

| Layer | POS Tags | What it captures |
|:---|:---:|:---|
| **Thematic Palette** | `NOUN`, `VERB`, `ADJ` | The author's *world* — atmosphere, setting, topics |
| **Voice Connectives** | `ADV`, `SCONJ`, `CCONJ` | The author's *voice* — how thoughts flow and connect |
| **Signature Phrases** | bigrams + trigrams | The author's *fingerprint* — recurring idioms and collocations |

Additionally, since v4.0:

| Metric | Description |
|:---|:---|
| **Tone Metrics** | `polarity` [-1 → +1] and `subjectivity` [0 → 1] via TextBlob |
| **Complexity Metrics** | `avg_sentence_len`, `lexical_density`, `sentence_variance` (StdDev) |
| **Characters** | Extracted proper nouns (character names, up to 20) |

---

## How It Works

### 1. Style Analysis — `POST /api/v1/analyze`

Analyzes the author's texts and builds a Style Passport.

**Pipeline:**
1. Joins all texts → splits into 500-word chunks
2. Runs `spaCy en_core_web_sm` with sentence boundary detection
3. Filters generic fiction noise (`GENERIC_FICTION_VERBS`, `GENERIC_FICTION_NOUNS`, `GENERIC_FICTION_ADJ`, `COMMON_ADVERBS`)
4. Separates proper nouns → stored as `characters[]`, excluded from vocabulary
5. Computes **TF-IDF** per word across chunks, splits result into `style_words` (≤40) and `thematic_words` (≤80)
6. Extracts **Signature Phrases**: bigrams and trigrams that don't start/end with stopwords (≤30)
7. Computes **tone** via TextBlob and **complexity** from sentence length statistics

---

### 2. Style Consistency — `POST /api/v1/consistency`

Compares a set of chapters against the author's profile. Returns an overall score and per-chapter breakdown.

**Similarity Formula (5 components):**

| Component | Weight | How it's calculated |
|:---|:---:|:---|
| Semantic / Phrase Coverage | **35%** | Coverage of top-20 author signature phrases in the chapter |
| Lexical Overlap (Cosine) | **25%** | Cosine similarity between author TF-IDF profile and chapter TF |
| Tone Match | **20%** | Difference in `polarity` + `subjectivity` vs. author's baseline |
| Complexity Match | **10%** | Deviation in `avg_sentence_len` and `lexical_density` |
| Entity Coverage | **10%** | Presence of top-5 author characters in the chapter |

**Score labels:**

| Score | Label |
|:---:|:---|
| ≥ 0.70 | ✅ Maintains Author's World and Voice |
| ≥ 0.50 | 🟡 Good consistency |
| ≥ 0.30 | 🟠 Notable deviations in tone or vocabulary |
| < 0.30 | 🔴 Tone and World differ significantly |

Each chapter result also includes: `missing_signatures`, `outlier_words`, `missing_characters`, `tone_deviation`, `complexity_deviation`.

---

### 3. Suggestions — `POST /api/v1/suggest`

Real-time word and phrase suggestions while the author writes (inline assistant, < 50ms).

**Scoring formula:**
```
score = 0.50 × tfidf_score        (how characteristic the word is for the author)
      + 0.35 × context_score      (1.0 = exact match in cursor context, 0.5 = prefix match)
      - 0.15 × overuse_penalty    (penalizes words already used 1-3+ times in current text)
```

Returns: up to **12 word suggestions** + up to **8 phrase suggestions** (only phrases not yet present in the current text).

---

### 4. Text Generation — `POST /api/v1/generate`

Generates a text continuation in the author's style via **Ollama (llama3.2)**.

The system prompt is built from the Style Passport — three vocabulary blocks:
```
═══ AUTHOR'S VOCABULARY PROFILE ═══
Voice Markers (adverbs, connectives): presently, moreover, indeed, ...
World Palette (nouns, verbs, adjectives): darkness, sword, whisper, ...
Signature Phrases: cold wind, looked at him, ...

═══ STRICT RULES ═══
1. Integrate Voice Markers to match author's flow.
2. Use World Palette naturally in descriptions.
3. Continue seamlessly — no meta-comments.
4. Write ONLY the continuation text.
5. Stay in the exact same language as context.
```

> The LLM receives **real author vocabulary**, not numeric metrics. It absorbs the author's rhythm through examples, not rules.

**Ollama parameters:** `temperature=0.8`, `top_p=0.92`, `num_predict = max_sentences × 60`.

---

### 5. Vocabulary Builder — `/api/v1/vocabulary`

Manages the approval workflow for the author's public vocabulary.

| Word Status | Meaning |
|:---:|:---|
| `PENDING` | Suggested by AI, awaiting author decision |
| `APPROVED` | Confirmed — shown on the author's public profile |
| `REJECTED` | Dismissed — will not be suggested again |

| Word Source | Meaning |
|:---:|:---|
| `AI_SUGGESTED` | Extracted from author's texts by the analyzer |
| `MANUAL` | Added by the author directly (auto-approved) |

The `enrich` endpoint sends top vocabulary words to Ollama to generate a short `meaning` and a creative fiction `usage_example` for each word.

---

## API Reference

| Method | Endpoint | Description |
|:---:|:---|:---|
| `POST` | `/api/v1/analyze` | Analyze author texts → build Style Passport |
| `POST` | `/api/v1/suggest` | Get word/phrase suggestions while writing |
| `POST` | `/api/v1/generate` | Generate text continuation in author's style |
| `POST` | `/api/v1/consistency` | Check how closely chapters match the author's profile |
| `GET` | `/api/v1/vocabulary/{author_id}` | Get the author's public vocabulary |
| `POST` | `/api/v1/vocabulary/{author_id}/enrich` | Enrich vocabulary with AI meanings via Ollama |
| `GET` | `/health` | Health check |

Full interactive documentation: `http://localhost:8000/docs`

---

## Project Structure

```
AutoBook_ai_v2/
├── main.py                      # FastAPI app entry point (v2.1.0)
├── models/
│   └── schemas.py               # Pydantic v2 request/response models
├── routers/
│   ├── analyze.py               # POST /api/v1/analyze
│   ├── suggest.py               # POST /api/v1/suggest
│   ├── generate.py              # POST /api/v1/generate
│   ├── consistency.py           # POST /api/v1/consistency
│   └── vocabulary.py            # GET + POST /api/v1/vocabulary
├── services/
│   ├── style_analyzer.py        # StyleAnalyzer v4.0 — core NLP pipeline
│   ├── style_consistency.py     # StyleConsistencyAnalyzer v4.0 — 5-component scoring
│   ├── suggestion_engine.py     # SuggestionEngine v3 — context-aware suggestions
│   ├── text_generator.py        # TextGenerator v3 — Ollama prompt builder
│   ├── profile_store.py         # JSON profile persistence (data/profiles/)
│   └── vocabulary_approval.py  # VocabularyApprovalService — approval queue logic
├── data/
│   └── profiles/                # Saved author profiles (gitignored)
├── static/                      # Static UI assets
├── test_local.py                # Unit tests (internal dev tool)
├── test_compare.py              # Poe vs Austen comparison test
├── test_real_books.py           # 6-author cross-matrix accuracy test
├── AI_DOCUMENTATION.md          # Internal architecture documentation (Ukrainian)
└── README.md                    # This file
```

---

## Tech Stack

| Technology | Version | Purpose |
|:---|:---:|:---|
| Python | 3.10+ | Core language |
| FastAPI | 0.100+ | Web framework |
| Pydantic v2 | 2.x | Data validation and schemas |
| spaCy | 3.x + `en_core_web_sm` | POS tagging, lemmatization, NER, sentence boundaries |
| TextBlob | any | Sentiment (polarity + subjectivity) |
| httpx | any | Async HTTP client for Ollama calls |
| Ollama + llama3.2 | any | Local LLM for generation and vocabulary enrichment |

---

## Quick Start

### 1. Install Python dependencies

```bash
pip install fastapi uvicorn spacy textblob httpx pydantic
python -m spacy download en_core_web_sm
python -m textblob.download_corpora
```

### 2. Install and start Ollama

```bash
# Install Ollama: https://ollama.com
ollama pull llama3.2
ollama serve
```

> Ollama is only required for `/generate` and `/vocabulary/enrich` endpoints.  
> All analysis and suggestion endpoints work without it.

### 3. Run the API

```bash
uvicorn main:app --reload --port 8000
```

- API: `http://localhost:8000`
- Swagger UI: `http://localhost:8000/docs`
- Health check: `http://localhost:8000/health`

---

## Testing

Three test scripts are provided as internal development tools:

| File | Type | Description |
|:---|:---:|:---|
| `test_local.py` | Unit | Tests individual analysis components with sample texts |
| `test_compare.py` | Comparison | Poe vs Austen — verifies two authors produce distinct profiles |
| `test_real_books.py` | Integration | 6 classic authors from Project Gutenberg, full cross-author similarity matrix |

```bash
python test_local.py
python test_compare.py
python test_real_books.py
```

---

## Design Decisions

### Why spaCy + TF-IDF + TextBlob instead of deep learning?

| Criterion | This approach | Heavy ML (Embeddings) |
|:---|:---|:---|
| **Speed** | ✅ < 100ms for full analysis | ❌ Slow (network or GPU required) |
| **Interpretability** | ✅ Author sees exact words causing low score | ❌ Black box (returns 0.85 with no explanation) |
| **New dimensions** | ✅ Tone + Complexity via TextBlob + counters | ❌ Requires separate specialized models |
| **DB integration** | ✅ Clean JSON → SQL (`word`, `score`, `category`) | ❌ Vectors require pgvector extension |
| **Infrastructure** | ✅ CPU-only, simple pip install | ❌ GPU or paid API, heavy packages (1–3 GB) |
| **LLM (creative tasks)** | ✅ Ollama locally for generation only | — |

**Strategy:** Fast classical NLP for analysis (< 100ms) + LLM power only where creativity is needed (text generation, vocabulary enrichment).
