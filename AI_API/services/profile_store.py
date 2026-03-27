import json
import os
from typing import Dict, Optional
from models.schemas import WordEntry, PhraseEntry

class ProfileStore:
    
    _PROFILES_DIR = "data/profiles"

    @classmethod
    def _get_path(cls, author_id: str) -> str:
        os.makedirs(cls._PROFILES_DIR, exist_ok=True)
        return os.path.join(cls._PROFILES_DIR, f"{author_id}_v2.json")

    @classmethod
    def save(cls, author_id: str, profile_data: Dict):
        """
        Saves the flattened profile to JSON. This simulates a relational DB:
        Table: AuthorVocab (author_id, word, score, freq, category)
        """
        with open(cls._get_path(author_id), "w", encoding="utf-8") as f:
            json.dump(profile_data, f, ensure_ascii=False, indent=2)

    @classmethod
    def get(cls, author_id: str) -> Optional[Dict]:
        path = cls._get_path(author_id)
        if not os.path.exists(path):
            return None
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)

    @classmethod
    def exists(cls, author_id: str) -> bool:
        return os.path.exists(cls._get_path(author_id))