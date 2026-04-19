import apiClient from '../api/apiClient';
import type { Chapter, ChapterCreateRequest, ChapterUpdateRequest } from '../../domain/models';

export const chapterRepository = {
  getByBook(bookId: number): Promise<Chapter[]> {
    return apiClient.get(`/chapters/book/${bookId}`).then(r => r.data);
  },

  getById(id: number): Promise<Chapter> {
    return apiClient.get(`/chapters/${id}`).then(r => r.data);
  },

  create(bookId: number, data: ChapterCreateRequest): Promise<Chapter> {
    return apiClient.post(`/chapters/book/${bookId}`, data).then(r => r.data);
  },

  update(id: number, data: ChapterUpdateRequest): Promise<Chapter> {
    return apiClient.put(`/chapters/${id}`, data).then(r => r.data);
  },

  delete(id: number): Promise<void> {
    return apiClient.delete(`/chapters/${id}`).then(() => {});
  },
};
