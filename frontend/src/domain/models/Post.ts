import type { UserCard } from './User';

// Post

export interface Post {
  id: number;
  content: string;
  author: UserCard;
  postType: 'FEED' | 'REVIEW' | 'ANNOUNCEMENT';
  imageUrl: string | null;
  hasImage: boolean;
  createdAt: string;
  updatedAt: string;
  likeCount: number;
  commentCount: number;
  repostCount: number;
  likedByMe: boolean;
  repostedByMe: boolean;
}

export interface PostDetails extends Post {
  comments: Comment[];
}

export interface CreatePostRequest {
  content: string;
  postType: string;
  imageUrl: string | null;
}

// Comment

export interface Comment {
  id: number;
  content: string;
  author: UserCard;
  createdAt: string;
  updatedAt: string;
  parentId: number | null;
  replies: Comment[];
  likes: number;
}

export interface CreateCommentRequest {
  content: string;
  parentId: number | null;
}
