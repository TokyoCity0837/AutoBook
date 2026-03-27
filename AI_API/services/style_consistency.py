"""
StyleConsistencyAnalyzer v4.0

Analyzes how closely a new chapter matches the author's defined World and Voice.
Calculates similarity based on:
  1. Semantic Similarity (Word Embeddings / spaCy vectors) [35%]
  2. Lexical Set Overlap (Exact vocabulary) [25%]
  3. Tone Match (Polarity/Subjectivity) [20%]
  4. Complexity Match (Sentence Rhythms) [10%]
  5. Entity Coverage (Are the right characters here?) [10%]
"""

import math
from collections import Counter
from typing import List, Dict, Tuple
from services.style_analyzer import StyleAnalyzer, TextBlob

class StyleConsistencyAnalyzer:
    def __init__(self):
        self._analyzer = StyleAnalyzer()

    def analyze_chapters(
        self,
        profile: Dict,
        chapters: List[Dict],
    ) -> Dict:
        if not profile or not chapters:
            raise ValueError("Author profile and at least one chapter are required")

        per_chapter = []
        for ch in chapters:
            if not ch.get("text", "").strip():
                continue
            result = self._analyze_single_chapter(profile, ch)
            per_chapter.append(result)

        if not per_chapter:
            raise ValueError("All chapters are empty")

        scores = [ch["similarity_score"] for ch in per_chapter]
        overall = round(sum(scores) / len(scores), 3)

        return {
            "overall_score":    overall,
            "overall_label":    self._score_label(overall),
            "per_chapter":      per_chapter,
        }

    def _analyze_single_chapter(self, profile: Dict, chapter: Dict) -> Dict:
        text = chapter["text"]
        
        doc = self._analyzer.nlp(text)
        
        tokens_for_lexical = [t.lemma_.lower() for t in doc if not t.is_stop and not t.is_punct and not t.is_space and len(t.lemma_) >= 3]
        
        if len(tokens_for_lexical) < 10:
            return {
                "id":               chapter.get("id", ""),
                "title":            chapter.get("title", ""),
                "similarity_score": 0.0,
                "label":            "⚠ Not enough text",
                "missing_signatures": [],
                "outlier_words":    [],
                "missing_characters": [],
                "tone_deviation": 0.0,
                "complexity_deviation": 0.0
            }

        # 1 & 2. Lexical & Semantic score (combined into one overlap measure for now if vectors aren't fully robust)
        author_vocab = profile.get("style_words", []) + profile.get("thematic_words", [])
        
        # Exact Lexical Match
        lex_score = self._lexical_similarity(author_vocab, tokens_for_lexical)
        
        # Phrases 
        tokens_for_phrases = [t.text.lower() for t in doc if not t.is_punct and not t.is_space]
        ch_phrases = self._analyzer._extract_phrases(tokens_for_phrases)
        sig_score, missing_phrases = self._phrase_coverage(profile.get("signature_phrases", []), ch_phrases)

        # 3. Tone Match
        blob = TextBlob(text)
        author_tone = profile.get("tone_metrics", {"polarity": 0, "subjectivity": 0.5})
        tone_diff_pol = abs(author_tone["polarity"] - blob.sentiment.polarity)
        tone_diff_sub = abs(author_tone["subjectivity"] - blob.sentiment.subjectivity)
        
        # Max deviation is ~2.0 for polarity, ~1.0 for subjectivity
        tone_deviation = (tone_diff_pol + tone_diff_sub) / 2.0
        tone_score = max(0.0, 1.0 - tone_deviation)

        # 4. Complexity Match
        sentence_lengths = []
        total_content_words = 0
        total_words = 0
        for sent in doc.sents:
            slen = len([t for t in sent if not t.is_punct and not t.is_space])
            if slen > 0: sentence_lengths.append(slen)
        
        for token in doc:
            if not token.is_punct and not token.is_space: total_words += 1
            if not token.is_stop and not token.is_punct and not token.is_space and len(token.lemma_) >= 3:
                total_content_words += 1

        avg_sent_len = sum(sentence_lengths) / max(1, len(sentence_lengths))
        lex_density = total_content_words / max(1, total_words)

        author_comp = profile.get("complexity_metrics", {"avg_sentence_len": 15.0, "lexical_density": 0.5, "sentence_variance": 5.0})
        len_diff = abs(author_comp["avg_sentence_len"] - avg_sent_len) / max(1.0, author_comp["avg_sentence_len"])
        dens_diff = abs(author_comp["lexical_density"] - lex_density) / max(0.1, author_comp["lexical_density"])
        
        complexity_deviation = (min(1.0, len_diff) + min(1.0, dens_diff)) / 2.0
        comp_score = max(0.0, 1.0 - complexity_deviation)

        # 5. Entity Coverage
        ch_propans = {t.text for t in doc if t.pos_ == "PROPN"}
        author_chars = profile.get("characters", [])
        missing_chars = [c for c in author_chars[:5] if c not in text] # crude check
        char_score = 1.0 if not author_chars else (len(author_chars[:5]) - len(missing_chars)) / min(5, len(author_chars))

        # We substitute semantic score with phrase score for now until we load large spaCy models confidently
        semantic_score = sig_score 

        # Final Formula
        # 35% Semantics/Phrases + 25% Lexical Overlap + 20% Tone Match + 10% Complexity Match + 10% Entity Coverage
        similarity = round(0.35 * semantic_score + 0.25 * lex_score + 0.20 * tone_score + 0.10 * comp_score + 0.10 * char_score, 3)

        outliers = self._find_outlier_words(author_vocab, tokens_for_lexical, top_n=8)

        return {
            "id":               chapter.get("id", ""),
            "title":            chapter.get("title", f"Chapter {chapter.get('id','')}"),
            "similarity_score": similarity,
            "label":            self._score_label(similarity),
            "missing_signatures": missing_phrases[:10],
            "outlier_words":      outliers,
            "missing_characters": missing_chars,
            "tone_deviation": round(tone_deviation, 3),
            "complexity_deviation": round(complexity_deviation, 3)
        }

    def _lexical_similarity(self, author_vocab: List[Dict], chapter_tokens: List[str]) -> float:
        profile_scores = {e["word"]: e["score"] for e in author_vocab}
        if not profile_scores:
            return 0.0

        ch_freq = Counter(chapter_tokens)
        total = max(1, len(chapter_tokens))
        ch_tf = {w: n / total for w, n in ch_freq.items()}

        common = set(profile_scores) & set(ch_tf)
        dot = sum(profile_scores[w] * ch_tf[w] for w in common)
        norm_p = math.sqrt(sum(v ** 2 for v in profile_scores.values()))
        norm_c = math.sqrt(sum(v ** 2 for v in ch_tf.values()))

        if norm_p == 0 or norm_c == 0:
            return 0.0

        raw = dot / (norm_p * norm_c)
        return min(raw / 0.15, 1.0)

    def _phrase_coverage(
        self, author_phrases: List[Dict], chapter_phrases: List[Dict]
    ) -> Tuple[float, List[str]]:
        top_signatures = [e["phrase"] for e in author_phrases[:20]]
        if not top_signatures:
            return 1.0, []

        chapter_set = set(p["phrase"] for p in chapter_phrases)
        present = [w for w in top_signatures if w in chapter_set]
        missing = [w for w in top_signatures if w not in chapter_set]

        coverage = len(present) / len(top_signatures)
        return round(coverage, 3), missing

    def _find_outlier_words(
        self, author_vocab: List[Dict], chapter_tokens: List[str], top_n: int = 8
    ) -> List[str]:
        profile_words = {e["word"] for e in author_vocab}
        ch_freq = Counter(chapter_tokens)

        outliers = []
        for word, count in ch_freq.most_common(50):
            if word in profile_words:
                continue
            if count < 2:
                continue
            outliers.append(word)

        return outliers[:top_n]

    def _score_label(self, score: float) -> str:
        if score >= 0.70:
            return "✅ Maintains Author's World and Voice"
        elif score >= 0.50:
            return "🟡 Good consistency"
        elif score >= 0.30:
            return "🟠 Notable deviations in tone or vocabulary"
        else:
            return "🔴 Tone and World differ significantly"