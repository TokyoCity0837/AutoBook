"""
VocabularyApprovalService — manages the word approval process by the author.

How it works in the UI:
  1. AI analyzes the book → generates candidates (words + phrases)
  2. A "Vocabulary Builder" panel appears on the side of the editor
  3. Notifications appear: "AI recommends adding word X" with context where it is used
  4. The author clicks ✓ (approve) or ✗ (reject) — or ignores
  5. Approved words go to the public Vocabulary on the author's profile
  6. Rejected words are no longer suggested and do not affect suggest

Spring stores the status of each word in the vocabulary table:
  status: PENDING | APPROVED | REJECTED
  source: AI_SUGGESTED | MANUAL (added by the author manually)
"""

from typing import List, Dict, Optional
from enum import Enum


class WordStatus(str, Enum):
    PENDING  = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"


class WordSource(str, Enum):
    AI_SUGGESTED = "AI_SUGGESTED"
    MANUAL       = "MANUAL"


class VocabularyApprovalService:

    def build_approval_queue(
        self,
        profile: Dict,
        already_processed: List[Dict],   # words already in Spring DB (any status)
        batch_size: int = 20,
    ) -> List[Dict]:
        """
        Builds the notification queue for the Vocabulary Builder panel.

        Returns batch_size new candidates not yet in already_processed.
        Each candidate contains:
          - word / phrase
          - score and freq (for sorting)
          - usage example from the author's text (context_snippet)
          - type: WORD or PHRASE
          - status: PENDING (just created)
          - source: AI_SUGGESTED
        """
        processed_set = {
            item["word"].lower()
            for item in already_processed
        }

        candidates = []

        # First words (higher priority — TF-IDF score)
        for entry in profile.get("signature_words", []):
            if entry["word"].lower() in processed_set:
                continue
            candidates.append({
                "word":    entry["word"],
                "type":    "WORD",
                "score":   entry["score"],
                "freq":    entry["freq"],
                "status":  WordStatus.PENDING,
                "source":  WordSource.AI_SUGGESTED,
                "context_snippet": None,   # filled via enrich_with_context()
                "meaning":         None,
            })

        # Then phrases
        for entry in profile.get("signature_phrases", []):
            if entry["phrase"].lower() in processed_set:
                continue
            candidates.append({
                "word":    entry["phrase"],
                "type":    "PHRASE",
                "score":   float(entry["freq"]),
                "freq":    entry["freq"],
                "status":  WordStatus.PENDING,
                "source":  WordSource.AI_SUGGESTED,
                "context_snippet": None,
                "meaning":         None,
            })

        # Sort: most characteristic first
        candidates.sort(key=lambda x: x["score"], reverse=True)
        return candidates[:batch_size]

    def enrich_with_context(
        self,
        candidates: List[Dict],
        original_texts: List[str],
        snippet_len: int = 120,
    ) -> List[Dict]:
        """
        For each candidate, finds a real line from the author's text
        where the word/phrase is used.

        This is important for the UI — the author sees not just a word,
        but a live example from their own book: "...the cold whispered shadows..."
        """
        full_text = "\n".join(original_texts)
        lines = [l.strip() for l in full_text.split(".") if l.strip()]

        for candidate in candidates:
            word_lower = candidate["word"].lower()
            snippet = None

            for line in lines:
                if word_lower in line.lower() and len(line) >= 20:
                    # Trim to snippet_len with word highlighted
                    start = max(0, line.lower().find(word_lower) - 40)
                    chunk = line[start:start + snippet_len].strip()
                    if len(chunk) > 20:
                        snippet = f"...{chunk}..."
                        break

            candidate["context_snippet"] = snippet

        return candidates

    def apply_decision(
        self,
        candidates: List[Dict],
        word: str,
        approve: bool,
    ) -> List[Dict]:
        """
        Applies the author's decision to a specific word.
        Spring stores the result in the DB.

        approve=True  → APPROVED → word goes to public Vocabulary
        approve=False → REJECTED → word will not be suggested again
        """
        new_status = WordStatus.APPROVED if approve else WordStatus.REJECTED
        for c in candidates:
            if c["word"].lower() == word.lower():
                c["status"] = new_status
                break
        return candidates

    def get_public_vocabulary(self, candidates: List[Dict]) -> List[Dict]:
        """Only approved words — for the author's public profile."""
        return [c for c in candidates if c["status"] == WordStatus.APPROVED]

    def get_pending(self, candidates: List[Dict]) -> List[Dict]:
        """Words awaiting decision — for the Vocabulary Builder panel."""
        return [c for c in candidates if c["status"] == WordStatus.PENDING]

    def add_manual_word(
        self,
        candidates: List[Dict],
        word: str,
        meaning: Optional[str] = None,
    ) -> List[Dict]:
        """
        The author manually adds a word (without AI recommendation).
        Immediately APPROVED.
        """
        # No duplicates
        existing = {c["word"].lower() for c in candidates}
        if word.lower() in existing:
            return candidates

        candidates.append({
            "word":             word,
            "type":             "WORD",
            "score":            0.0,
            "freq":             0,
            "status":           WordStatus.APPROVED,
            "source":           WordSource.MANUAL,
            "context_snippet":  None,
            "meaning":          meaning,
        })
        return candidates