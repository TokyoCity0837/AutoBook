import type { UserCard } from './User';

// ─── Follow ─────────────────────────────────────────────

export interface FollowResponse {
  id: number;
  follower: UserCard;
  following: UserCard;
  status: 'PENDING' | 'ACCEPTED';
  createdAt: string;
}
