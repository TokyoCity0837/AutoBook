import apiClient from '../api/apiClient';
import type { Comment, CreateCommentRequest } from '../../domain/models';

export const commentRepository = {
  // ─── Post comments ────────────────────────────────────
  getByPost(postId: number): Promise<Comment[]> {
    return apiClient.get(`/comments/post/${postId}`).then(r => r.data);
  },

  createForPost(postId: number, data: CreateCommentRequest): Promise<Comment> {
    return apiClient.post(`/comments/post/${postId}`, data).then(r => r.data);
  },

  likePostComment(commentId: number): Promise<void> {
    return apiClient.put(`/comments/${commentId}/like`).then(() => {});
  },

  // ─── Book comments ────────────────────────────────────
  getByBook(bookId: number): Promise<Comment[]> {
    return apiClient.get(`/book-comments/book/${bookId}`).then(r => r.data);
  },

  createForBook(bookId: number, data: CreateCommentRequest): Promise<Comment> {
    return apiClient.post(`/book-comments/book/${bookId}`, data).then(r => r.data);
  },

  likeBookComment(commentId: number): Promise<void> {
    return apiClient.put(`/book-comments/${commentId}/like`).then(() => {});
  },
};
