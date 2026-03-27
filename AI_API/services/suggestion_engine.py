"""
SuggestionEngine v3 — Context-aware completion based on Author's World and Voice.

Logic:
  1. Combine style_words and thematic_words to form the prediction pool.
  2. Analyze cursor context (last 2-3 sentences).
  3. Calculate relevance based on previous TF-IDF + matching context.
  4. Return top phrase and word suggestions.
"""

import math
from collections import Counter
from typing import List, Dict, Optional
import spacy

class SuggestionEngine:
    def __init__(self):
        # Local lightweight tokenization if needed, but we can reuse the global one.
        # Fallback to simple split if spacy isn't loaded here.
        pass

    def suggest(
        self,
        profile: Dict,
        current_text: str,
        cursor_context: Optional[str],
        top_n: int = 12,
    ) -> Dict:
        context = cursor_context or current_text[-500:] 
        
        # Simple tokenization for suggesting logic
        context_tokens = [w.lower() for w in context.replace(".", " ").replace(",", " ").split() if len(w) >= 3]
        context_freq = Counter(context_tokens)
        
        cur_tokens = [w.lower() for w in current_text.replace(".", " ").replace(",", " ").split() if len(w) >= 3]
        cur_freq = Counter(cur_tokens)

        # ── Words ────────────────────────────────────────────────────────────
        author_vocab = profile.get("style_words", []) + profile.get("thematic_words", [])
        
        word_candidates = []
        for entry in author_vocab:
            w = entry["word"]

            # penalty if already used too frequently in current text
            overuse_penalty = 0.15 * min(cur_freq.get(w, 0), 3)

            # context relevance: if a similar word is nearby
            context_score = 0
            if w in context_freq:
                context_score = 1.0
            else:
                prefix = w[:4]
                if any(t.startswith(prefix) for t in context_tokens):
                    context_score = 0.5 

            score = (
                0.5 * entry["score"]
                + 0.35 * context_score
                - overuse_penalty
            )

            word_candidates.append({"word": w, "score": round(score, 6), "freq": entry["freq"], "category": entry.get("category", "")})

        word_candidates.sort(key=lambda x: x["score"], reverse=True)

        # ── Phrases ───────────────────────────────────────────────────────────
        cur_text_lower = current_text.lower()
        phrase_candidates = []
        for entry in profile.get("signature_phrases", []):
            phr = entry["phrase"].lower()
            if phr in cur_text_lower:
                continue

            score = 0.6 * entry["freq"]
            phrase_candidates.append({
                "phrase": entry["phrase"],
                "score": round(score, 4),
                "freq": entry["freq"],
            })

        phrase_candidates.sort(key=lambda x: x["score"], reverse=True)

        return {
            "word_suggestions": word_candidates[:top_n],
            "phrase_suggestions": phrase_candidates[:8],
        }