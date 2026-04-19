import apiClient from '../api/apiClient';
import type { Post, PostDetails, CreatePostRequest } from '../../domain/models';

export const postRepository = {
  getFeed(): Promise<Post[]> {
    return apiClient.get('/posts/feed').then(r => r.data);
  },

  getById(id: number): Promise<PostDetails> {
    return apiClient.get(`/posts/${id}`).then(r => r.data);
  },

  create(data: CreatePostRequest): Promise<PostDetails> {
    return apiClient.post('/posts', data).then(r => r.data);
  },

  like(id: number): Promise<void> {
    return apiClient.put(`/posts/${id}/like`).then(() => {});
  },

  unlike(id: number): Promise<void> {
    return apiClient.delete(`/posts/${id}/like`).then(() => {});
  },

  repost(id: number): Promise<void> {
    return apiClient.put(`/posts/${id}/repost`).then(() => {});
  },
};
