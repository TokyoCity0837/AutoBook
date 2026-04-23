import apiClient from '../api/apiClient';
import type { Chapter, ChapterCreateRequest, ChapterUpdateRequest } from '../../domain/models';

export const chapterRepository = {
  async getByBook(bookId: number): Promise<Chapter[]> {
    const r = await apiClient.get(`/chapters/book/${bookId}`);
    return r.data;
  },

  async getById(id: number): Promise<Chapter> {
    const r = await apiClient.get(`/chapters/${id}`);
    return r.data;
  },

  async create(bookId: number, data: ChapterCreateRequest): Promise<Chapter> {
    const r = await apiClient.post(`/chapters/book/${bookId}`, data);
    return r.data;
  },

  async update(id: number, data: ChapterUpdateRequest): Promise<Chapter> {
    const r = await apiClient.put(`/chapters/${id}`, data);
    return r.data; 
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/chapters/${id}`);
  },
};
