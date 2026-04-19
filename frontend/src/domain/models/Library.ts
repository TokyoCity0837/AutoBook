import type { BookCard } from './Book';

// ─── Library (Saved Items) ──────────────────────────────

export interface SavedItem {
  id: number;
  book: BookCard;
  savedAt: string;
}
