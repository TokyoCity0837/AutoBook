import type { UserCard } from './User';

// ─── Book ───────────────────────────────────────────────

export interface BookCard {
  id: number;
  title: string;
  coverImage: string | null;
  author: UserCard;
}

export interface BookDetails {
  id: number;
  title: string;
  description: string;
  coverImage: string | null;
  author: UserCard;
  genre: string;
  privacy: 'PUBLIC' | 'PRIVATE';
  createdAt: string;
  updatedAt: string;
  likeCount: number;
  commentCount: number;
  // Editor-specific style fields
  font?: string;
  fontSize?: number;
  lineHeight?: number;
  paraStyle?: number;
}

export interface BookUpdateRequest {
  title?: string;
  description?: string;
  genre?: string;
  privacy?: string;
  coverImage?: string;
  font?: string;
  fontSize?: number;
  lineHeight?: number;
  paraStyle?: number;
}

export interface BookCreateRequest {
  title: string;
  privacy: string;
  description: string;
}
