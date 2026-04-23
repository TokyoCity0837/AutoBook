// ─── User ───────────────────────────────────────────────

export interface ProfilePostItem {
  type: 'POST' | 'REPOST';
  post: Post;
  repostedBy: UserCard | null;
  repostedAt: string | null;
  activityAt: string;
}

export interface UserCard {
  id: number;
  visibleName: string;
  username: string;
  profileImage: string | null;
  role: string;
  isFriend: boolean;
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
  posts: ProfilePostItem[];
  isFriend?: boolean;
  isPrivate?: boolean;
}

export interface UserUpdateRequest {
  visibleName?: string;
  bio?: string;
  privacyType?: 'PUBLIC' | 'PRIVATE';
  profileImage?: string;
  username?: string;
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
