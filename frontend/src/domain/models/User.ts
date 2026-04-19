// ─── User ───────────────────────────────────────────────

export interface UserCard {
  id: number;
  visibleName: string;
  username: string;
  profileImage: string | null;
  role: string;
}

export interface UserProfile {
  id: number;
  username: string; 
  visibleName: string;
  bio: string;
  profileImage: string | null;
  privacy: 'PUBLIC' | 'PRIVATE';
  createdAt: string;
  role: string;
  followers: number;
  friends: number;
  books: BookCard[];
  posts: Post[];
}

export interface UserUpdateRequest {
  visibleName?: string;
  bio?: string;
  privacyType?: 'PUBLIC' | 'PRIVATE';
  profileImage?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  visibleName: string;
}

// ─── Forward references (resolved via re-exports) ──────
// These are imported from their own model files:
import type { BookCard } from './Book';
import type { Post } from './Post';
