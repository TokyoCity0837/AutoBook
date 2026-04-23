import apiClient from '../api/apiClient';
import type { Comment, CreateCommentRequest } from '../../domain/models';

export const commentRepository = {
  // ─── Post comments ────────────────────────────────────
  async getByPost(postId: number): Promise<Comment[]> {
    const r = await apiClient.get(`/comments/post/${postId}`);
    return r.data;
  },

  async createForPost(postId: number, data: CreateCommentRequest): Promise<Comment> {
    const r = await apiClient.post(`/comments/post/${postId}`, data);
    return r.data;
  },

  async likePostComment(commentId: number): Promise<void> {
    await apiClient.put(`/comments/${commentId}/like`);
  },

  // ─── Book comments ────────────────────────────────────
  async getByBook(bookId: number): Promise<Comment[]> {
    const r = await apiClient.get(`/book-comments/book/${bookId}`);
    return r.data;
  },

  async createForBook(bookId: number, data: CreateCommentRequest): Promise<Comment> {
    const r = await apiClient.post(`/book-comments/book/${bookId}`, data);
    return r.data;
  },

  async likeBookComment(commentId: number): Promise<void> {
    await apiClient.put(`/book-comments/${commentId}/like`);
  },
};
