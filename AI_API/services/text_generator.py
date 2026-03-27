"""
TextGenerator v3 — Generates text continuation mapping to the author's Voice and World.

Powered by Ollama. Instead of confusing the LLM with sentence lengths and comma counts,
we provide exactly what writers need:
1. The thematic palette (world-building words)
2. Style connectives (author's voice and flow)
3. Signature phrases (idioms)

This guarantees the model writes IN the world, WITH the character of the author.
"""

import httpx
from typing import Dict, Optional

OLLAMA_BASE_URL = "http://localhost:11434"
DEFAULT_MODEL = "llama3.2"

class TextGenerator:
    def __init__(self, model: str = DEFAULT_MODEL, base_url: str = OLLAMA_BASE_URL):
        self.model = model
        self.base_url = base_url

    async def generate(
        self,
        profile: Dict,
        context: str,
        max_sentences: int = 3,
        temperature: float = 0.8,
    ) -> Dict:
        system_prompt = self._build_system_prompt(profile)
        user_prompt = self._build_user_prompt(context, max_sentences)

        payload = {
            "model": self.model,
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user",   "content": user_prompt},
            ],
            "stream": False,
            "options": {
                "temperature": temperature,
                "top_p": 0.92,
                "num_predict": max_sentences * 60,
            }
        }

        async with httpx.AsyncClient(timeout=60.0) as client:
            response = await client.post(
                f"{self.base_url}/api/chat",
                json=payload
            )
            response.raise_for_status()
            data = response.json()

        generated = data["message"]["content"].strip()

        return {
            "generated_text": generated,
            "model_used": self.model,
        }

    def _build_system_prompt(self, profile: Dict) -> str:
        style_words   = [e["word"] for e in profile.get("style_words", [])[:20]]
        thematic_words = [e["word"] for e in profile.get("thematic_words", [])[:20]]
        top_phrases = [e["phrase"] for e in profile.get("signature_phrases", [])[:10]]

        style_block    = f"Voice Markers (adverbs, connectives): {', '.join(style_words)}" if style_words else ""
        thematic_block = f"World Palette (nouns, verbs, adjectives): {', '.join(thematic_words)}" if thematic_words else ""
        phrases_block  = f"Signature Phrases: {', '.join(top_phrases)}" if top_phrases else ""

        prompt = f"""You are a creative writing assistant continuing text in a very specific author's style. You must immerse yourself in the author's Voice and World.

═══ AUTHOR'S VOCABULARY PROFILE ═══
{style_block}
{thematic_block}
{phrases_block}

═══ STRICT RULES ═══
1. Integrate the Author's Voice Markers to match their flow and transition style smoothly.
2. Use the World Palette naturally when describing scenes or actions.
3. Continue seamlessly — no meta-comments, no "Here is the continuation:".
4. Write ONLY the continuation text.
5. Stay in the exact same language (e.g., if the context is Ukrainian, write in Ukrainian)."""

        return prompt

    def _build_user_prompt(self, context: str, max_sentences: int) -> str:
        return f"""Continue this text naturally in the author's style. Write exactly {max_sentences} sentence(s).

CONTEXT (last paragraphs):
{context}

CONTINUATION:"""