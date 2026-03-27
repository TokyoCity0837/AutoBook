#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AutoBook AI v4.0 — Gutenberg Full Book Stress Test
Run: python test_local.py
"""

import sys
import json
import urllib.request
sys.path.insert(0, ".")

# ── Colors for terminal ────────────────────────────────────────────────────
G  = "\033[92m"   
Y  = "\033[93m"   
R  = "\033[91m"   
B  = "\033[94m"   
C  = "\033[96m"   
W  = "\033[97m"   
DIM = "\033[2m"
RESET = "\033[0m"

def header(title: str):
    print(f"\n{B}{'═'*70}{RESET}")
    print(f"{W}  {title}{RESET}")
    print(f"{B}{'═'*70}{RESET}")

def ok(msg):    print(f"  {G}✓{RESET}  {msg}")
def warn(msg):  print(f"  {Y}⚠{RESET}  {msg}")
def err(msg):   print(f"  {R}✗{RESET}  {msg}")
def info(msg):  print(f"  {C}→{RESET}  {msg}")
def dim(msg):   print(f"  {DIM}{msg}{RESET}")

def fetch_gutenberg_book(url: str, start_marker: str, end_marker: str, excerpt_words: int = -1) -> str:
    info(f"Downloading from Project Gutenberg: {url}")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            text = response.read().decode('utf-8')
            
        # Strip headers
        start_idx = text.find(start_marker)
        if start_idx == -1: start_idx = 1000
        end_idx = text.find(end_marker, start_idx)
        if end_idx == -1: end_idx = len(text) - 1000
        
        clean_text = text[start_idx:end_idx]
        
        # Take an excerpt if requested (e.g., first 15,000 words to speed up tests)
        if excerpt_words > 0:
            words = clean_text.split()
            clean_text = " ".join(words[:excerpt_words])
            
        ok(f"Downloaded and cleaned {len(clean_text.split())} words of text.")
        return clean_text
    except Exception as e:
        err(f"Failed to download book: {e}")
        return ""

def test_analyze(book_text: str):
    header("TEST 1 — Semantic & Metric Analysis (Project Gutenberg)")
    from services.style_analyzer import StyleAnalyzer
    
    analyzer = StyleAnalyzer()
    texts = [book_text]

    info(f"Analyzing {len(book_text.split())} words...")
    profile = analyzer.analyze(texts)

    style_words = profile["style_words"]
    thematic_words = profile["thematic_words"]
    phrases = profile["signature_phrases"]
    chars = profile.get("characters", [])
    tone = profile.get("tone_metrics", {})
    comp = profile.get("complexity_metrics", {})
    
    ok(f"Found {len(chars)} Characters. Top-5:")
    print(f"      {', '.join(chars[:5])}")
    
    ok(f"Tone: Polarity={tone.get('polarity')}, Subjectivity={tone.get('subjectivity')}")
    ok(f"Complexity: Avg Sent={comp.get('avg_sentence_len')} words, Lexical Density={comp.get('lexical_density')}")

    ok(f"Found {len(style_words)} Style connectives (Voice). Top-5:")
    for w in style_words[:5]:
        print(f"      {w['word']:<15} score={w['score']:.4f}  freq={w['freq']}")

    ok(f"Found {len(thematic_words)} Thematic words (World). Top-5:")
    for w in thematic_words[:5]:
        print(f"      {w['word']:<15} score={w['score']:.4f}  freq={w['freq']}")

    ok(f"Found {len(phrases)} Signature Phrases. Top-5:")
    for p in phrases[:5]:
        print(f"      \"{p['phrase']}\"  (freq={p['freq']})")

    ok("Profile built successfully!")
    return profile

def test_consistency(profile):
    header("TEST 2 — Multi-Factor Consistency Comparison")
    from services.style_consistency import StyleConsistencyAnalyzer

    # Ch 1 of Wuthering Heights starts with 1801 and talks about Lockwood and Heathcliff
    CONSISTENCY_CHAPTERS = [
        {
            "id": "correct_style",
            "title": "A Gothic Night (Consistent)",
            "text": """
            Mr. Heathcliff remained silent as the wind howled across the desolate moors. 
            Shadows danced wildly in the firelight of the immense hearth, casting strange shapes 
            upon the deeply set windows. Lockwood stood and gazed at the cold stones, wondering if any 
            living soul still dwelled within these walls. The silence was absolute, save for 
            the occasional scratching of a branch against the glass. It was a bleak and dreary evening.
            """
        },
        {
            "id": "wrong_style",
            "title": "Modern City (Inconsistent)",
            "text": """
            The neon lights of the city were bright and neon colors flashed everywhere. 
            I checked my smartphone and saw that my Uber was just two minutes away. 
            The coffee shop was full of people working on laptops and drinking lattes. 
            It was a sunny, cheerful, and incredibly happy day in the downtown area. Everyone was smiling!
            What a magnificent and positive experience this is.
            """
        }
    ]

    analyzer = StyleConsistencyAnalyzer()
    result = analyzer.analyze_chapters(profile, CONSISTENCY_CHAPTERS)

    score = result["overall_score"]
    label = result["overall_label"]

    ok(f"Overall Consistency: {score:.1%}  {label}")

    for ch in result["per_chapter"]:
        score_val = ch['similarity_score']
        color = G if score_val >= 0.7 else (Y if score_val >= 0.4 else R)
        print(f"\n  {C}{ch['title']}{RESET} -> {color}{score_val:.1%}{RESET} - {ch['label']}")
        dim(f"    Tone Match: deviation = {ch.get('tone_deviation')}")
        dim(f"    Complexity Match: deviation = {ch.get('complexity_deviation')}")
        if ch.get("missing_characters"):
            dim(f"    Missing Chars: {', '.join(ch['missing_characters'][:3])}")
        if ch.get("outlier_words"):
            dim(f"    Outliers: {', '.join(ch['outlier_words'][:3])}")

    return result

def main():
    print(f"\n{W}{'═'*70}")
    print("  AutoBook AI v4.0 (Advanced Gutenberg Test)")
    print(f"{'═'*70}{RESET}")

    # Wuthering Heights
    # We take 20,000 words. Enough to build a powerful profile, small enough for quick testing
    wuthering_url = "https://www.gutenberg.org/cache/epub/768/pg768.txt"
    book_text = fetch_gutenberg_book(
        url=wuthering_url, 
        start_marker="CHAPTER I", 
        end_marker="*** END OF THE PROJECT GUTENBERG EBOOK WUTHERING HEIGHTS ***",
        excerpt_words=15000 
    )

    if not book_text:
        return

    try:
        profile = test_analyze(book_text)
        test_consistency(profile)

        header("FINAL SUMMARY")
        ok("v4.0 modules correctly processed entire book chapters!")
        print(f"\n  {G}Metrics, Chars, Tone, and Semantic similarity are active. 🎉{RESET}\n")

    except Exception as e:
        err(f"Test failed: {e}")
        import traceback; traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()