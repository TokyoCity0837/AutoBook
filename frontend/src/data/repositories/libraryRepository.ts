import apiClient from '../api/apiClient';
import type { SavedItem } from '../../domain/models';

export const libraryRepository = {
  async getMySaved(): Promise<SavedItem[]> {
    const r = await apiClient.get('/library/my');
    return r.data;
  },

  async getBookStatus(bookId: number): Promise<boolean> {
    const r = await apiClient.get(`/library/book/${bookId}/status`);
    return r.data;
  },

  async toggleBook(bookId: number): Promise<void> {
    await apiClient.post(`/library/book/${bookId}`);
  },
};
