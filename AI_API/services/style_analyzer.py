"""
StyleAnalyzer v4.0 — Semantic, Tone, and Rhythmic Analysis

1. THEMATIC (World): Setting/topic words (Nouns, Verbs, Adjectives).
2. STYLE (Voice): Abstract connectives (Adverbs, Conjunctions).
3. SIGNATURE PHRASES: N-grams.
4. TONE & SENTIMENT: Text polarity and subjectivity.
5. COMPLEXITY: Sentence lengths, lexical density, variance.
6. ENTITIES: Character names extraction.
"""

import math
import string
from collections import Counter
from typing import List, Dict

try:
    import spacy
except ImportError:
    raise ImportError("Please install spacy: pip install spacy && python -m spacy download en_core_web_sm")

try:
    from textblob import TextBlob
except ImportError:
    # Minimal fallback if textblob isn't installed
    class TextBlob:
        def __init__(self, text):
            self.sentiment = type('obj', (object,), {'polarity': 0.0, 'subjectivity': 0.5})

class StyleAnalyzer:
    def __init__(self):
        try:
            # Recommend upgrading to en_core_web_md later for vectors
            self.nlp = spacy.load("en_core_web_sm", disable=["parser"]) 
            # We must re-enable 'parser' or 'senter' to get sentence boundaries `doc.sents`
            self.nlp.enable_pipe("parser")
        except OSError:
            raise OSError("Please run: python -m spacy download en_core_web_sm")

        # Hardcode generic fiction noise
        self.GENERIC_FICTION_VERBS = {
            "say", "look", "tell", "ask", "go", "come", "get", "take", "make",
            "think", "see", "know", "leave", "give", "find", "turn", "begin",
            "stand", "sit", "hear", "fall", "keep", "feel", "try", "walk", "call", 
            "seem", "mean", "reply", "answer", "return", "continue", "exclaim", 
            "pass", "bring", "want", "run", "hold", "enter", "lie", "live", "stay", "talk",
            "die", "set", "bear", "grow", "laugh", "send", "rise", "care", "hope", "draw",
            "suppose", "like", "read", "follow", "bid", "strike", "repeat", "visit", "wait", 
            "reach", "observe", "desire", "believe"
        }
        self.GENERIC_FICTION_NOUNS = {
            "man", "woman", "boy", "girl", "child", "hand", "eye", "face", "head", 
            "door", "room", "table", "chair", "wall", "voice", "word", "way", 
            "day", "night", "morning", "evening", "life", "time", "year", "moment",
            "thing", "friend", "father", "mother", "brother", "sister", "wife", "husband",
            "sir", "lady", "person", "people", "house", "home", "car", "street", "foot", "arm", "shoulder",
            "smile", "nod", "step", "back", "master", "mistress", "papa", "cousin", "bed", "kitchen",
            "book", "half", "servant", "minute", "light", "companion", "seat", "hour"
        }
        self.GENERIC_FICTION_ADJ = {
            "good", "bad", "young", "old", "little", "great", "small", "large", "long", "short",
            "high", "low", "new", "early", "late", "first", "last", "own", "other", "same", "such",
            "sure", "ill", "poor"
        }
        self.COMMON_ADVERBS = {
            "so", "very", "too", "just", "now", "then", "well", "right", "away", 
            "back", "out", "up", "long", "here", "there", "again", "almost", "much", 
            "more", "even", "still", "only", "always", "never", "ever", "really",
            "quite", "rather", "already", "enough", "yes", "no", "perhaps", "maybe",
            "down", "off", "in", "about", "till", "instead"
        }
        self.PHRASE_EDGE_STOPWORDS = {
            "the", "a", "an", "and", "but", "or", "for", "nor", "on", "at", "to", "from", 
            "by", "with", "about", "as", "into", "like", "through", "after", "over", 
            "between", "out", "against", "during", "without", "before", "under", "around",
            "my", "your", "his", "her", "its", "our", "their", "this", "that", "these", "those",
            "he", "she", "it", "they", "i", "we", "you", "me", "him", "them", "us",
            "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
            "all", "any", "some", "every", "no", "not", "could", "would", "should", "will", "can", "shall",
            "of", "what", "which", "who", "whom", "where", "when", "why", "how", "if", "then", "than"
        }
        self.PHRASE_DISCARD_ONLY = {
            "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
            "can", "could", "shall", "should", "will", "would", "may", "might", "must",
            "not", "n't", "no", "yes",
            "he", "she", "it", "they", "i", "we", "you", "me", "him", "her", "them", "us",
            "this", "that", "these", "those", "'s", "'ll", "'re", "'ve", "'d", "’s", "’ll", "’re", "’ve", "’d", "’"
        }

    def analyze(self, texts: List[str]) -> Dict:
        all_text = "\n".join([t.strip() for t in texts if t.strip()])
        if not all_text:
            raise ValueError("Not enough text for analysis")

        # Basic sentiment
        blob = TextBlob(all_text)
        sentiment = blob.sentiment

        # Chunking
        words_raw = all_text.split()
        chunk_size = 500  # slightly bigger to preserve sentence boundaries better
        words_raw_chunks = [" ".join(words_raw[i:i+chunk_size]) for i in range(0, len(words_raw), chunk_size)]
        
        N = len(words_raw_chunks)
        if N == 0:
            raise ValueError("Text is too short.")

        STYLE_POS = {"ADV", "SCONJ", "CCONJ"}
        THEMATIC_POS = {"NOUN", "ADJ", "VERB"}

        global_freq = Counter()
        capitalized_freq = Counter()
        propn_freq = Counter()
        word_categories = {}
        doc_freqs = Counter()

        all_lemmas_for_phrases = [] 
        
        # Complexity metrics accumulators
        sentence_lengths = []
        total_content_words = 0
        total_words = 0

        for chunk_text in words_raw_chunks:
            # Increase length limit to 2 million characters safely inside the chunk loop
            # though our chunk is only 500 words
            doc = self.nlp(chunk_text)
            seen_in_this_chunk = set()

            # Record sentence lengths
            for sent in doc.sents:
                slen = len([t for t in sent if not t.is_punct and not t.is_space])
                if slen > 0:
                    sentence_lengths.append(slen)

            for token in doc:
                text_clean = token.text.strip(string.punctuation)
                if not token.is_punct and not token.is_space and text_clean:
                    all_lemmas_for_phrases.append(text_clean.lower())
                    total_words += 1

                if token.is_stop or token.is_punct or token.is_space:
                    continue

                if len(token.lemma_) >= 3:
                    total_content_words += 1

                # Track Proper Nouns for Characters List
                if token.pos_ == "PROPN" and len(token.text) >= 2:
                    propn_freq[token.text] += 1
                    continue # Skip PROPN from normal vocabulary
                
                if token.text.istitle() and not token.is_sent_start:
                    capitalized_freq[token.lemma_.lower()] += 1

                lemma = token.lemma_.lower()
                pos = token.pos_

                if lemma in self.GENERIC_FICTION_VERBS or lemma in self.GENERIC_FICTION_NOUNS or lemma in self.GENERIC_FICTION_ADJ:
                    continue
                if lemma in self.COMMON_ADVERBS:
                    continue

                if pos in STYLE_POS:
                    cat = "STYLE"
                elif pos in THEMATIC_POS:
                    cat = "THEMATIC"
                else:
                    continue 

                global_freq[lemma] += 1
                word_categories[lemma] = cat
                seen_in_this_chunk.add(lemma)

            for w in seen_in_this_chunk:
                doc_freqs[w] += 1

        # Complexity Stats
        avg_sent_len = sum(sentence_lengths) / max(1, len(sentence_lengths))
        sent_variance = 0.0
        if len(sentence_lengths) > 1:
            variance = sum((x - avg_sent_len)**2 for x in sentence_lengths) / len(sentence_lengths)
            sent_variance = math.sqrt(variance) # technically Standard Deviation
        
        lexical_density = total_content_words / max(1, total_words)

        # Characters List
        # Filter PROPNs that are actual names (freq >= 3 ideally, or top 20)
        characters = [name for name, count in propn_freq.most_common(20) if count >= max(2, min(5, N))]

        valid_words = []
        for w, count in global_freq.items():
            cap_ratio = capitalized_freq[w] / count if count > 0 else 0
            if cap_ratio > 0.5:
                # Add it to characters if it slipped through PROPN detection
                if count >= 3 and w.title() not in characters:
                    characters.append(w.title())
                continue 
            valid_words.append(w)

        # TF-IDF Calculation
        word_scores = []
        for w in valid_words:
            count = global_freq[w]
            if count < 2: 
                continue 
            
            tf = count / max(1, len(words_raw))
            idf = math.log((N + 1) / (doc_freqs[w] + 1)) + 1.0
            
            score = tf * idf
            if score > 0:
                word_scores.append({
                    "word": w,
                    "score": round(score, 6),
                    "freq": count,
                    "category": word_categories[w]
                })

        word_scores.sort(key=lambda x: x["score"], reverse=True)
        style_words = [w for w in word_scores if w["category"] == "STYLE"][:40]
        thematic_words = [w for w in word_scores if w["category"] == "THEMATIC"][:80]

        signature_phrases = self._extract_phrases(all_lemmas_for_phrases)

        return {
            "style_words": style_words,
            "thematic_words": thematic_words,
            "signature_phrases": signature_phrases,
            "vocabulary": thematic_words[:50],
            "characters": characters[:20],
            "tone_metrics": {
                "polarity": round(sentiment.polarity, 3),
                "subjectivity": round(sentiment.subjectivity, 3),
            },
            "complexity_metrics": {
                "avg_sentence_len": round(avg_sent_len, 2),
                "lexical_density": round(lexical_density, 3),
                "sentence_variance": round(sent_variance, 2), # StdDev
            }
        }

    def _extract_phrases(self, tokens: List[str]) -> List[Dict]:
        bi = Counter()
        tri = Counter()

        for i in range(len(tokens) - 2):
            g2 = (tokens[i], tokens[i+1])
            g3 = (tokens[i], tokens[i+1], tokens[i+2])
            
            if g2[0] not in self.PHRASE_EDGE_STOPWORDS and g2[-1] not in self.PHRASE_EDGE_STOPWORDS:
                if not (g2[0].isnumeric() or g2[1].isnumeric()):
                    if not all(w in self.PHRASE_DISCARD_ONLY for w in g2):
                        bi[" ".join(g2)] += 1

            if g3[0] not in self.PHRASE_EDGE_STOPWORDS and g3[-1] not in self.PHRASE_EDGE_STOPWORDS:
                if not (g3[0].isnumeric() or g3[1].isnumeric() or g3[2].isnumeric()):
                    if not all(w in self.PHRASE_DISCARD_ONLY for w in g3):
                        tri[" ".join(g3)] += 1

        result = []
        trigram_strings = set()
        
        for phrase, freq in tri.most_common(50):
            if "nt" in phrase.split() or "n't" in phrase.split() or "s" in phrase.split() or "'s" in phrase.split() or "’s" in phrase.split():
                continue
            if freq >= 2: 
                result.append({"phrase": phrase, "freq": freq})
                trigram_strings.add(phrase)

        for phrase, freq in bi.most_common(80):
            if "nt" in phrase.split() or "n't" in phrase.split() or "s" in phrase.split() or "'s" in phrase.split() or "’s" in phrase.split():
                continue
            if freq >= 2: 
                is_subset = False
                for t in trigram_strings:
                    if phrase in t:
                        is_subset = True
                        break
                if not is_subset:
                    result.append({"phrase": phrase, "freq": freq})

        result.sort(key=lambda x: x["freq"], reverse=True)
        return result[:30]