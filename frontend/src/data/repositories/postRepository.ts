import apiClient from '../api/apiClient';
import type { Post, PostDetails, CreatePostRequest } from '../../domain/models';

export const postRepository = {
  async getFeed(): Promise<Post[]> {
    const r = await apiClient.get('/posts/feed');
    return r.data;
  },

  async getById(id: number): Promise<PostDetails> {
    const r = await apiClient.get(`/posts/${id}`);
    return r.data;
  },

  async create(data: CreatePostRequest): Promise<PostDetails> {
    const r = await apiClient.post('/posts', data);
    return r.data;
  },

  async toggleLike(id: number): Promise<boolean> {
    const r = await apiClient.post(`/posts/${id}/like`);
    return r.data;
  },

  async toggleRepost(postId: number): Promise<boolean> {
    const { data } = await apiClient.post<boolean>(`/posts/${postId}/repost`);
    return data;
  }
};
