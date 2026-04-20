import apiClient from '../api/apiClient';
import type { SavedItem } from '../../domain/models';

export const libraryRepository = {
  getMySaved(): Promise<SavedItem[]> {
    return apiClient.get('/library/my').then(r => r.data);
  },

  getBookStatus(bookId: number): Promise<boolean> {
    return apiClient.get(`/library/book/${bookId}/status`).then(r => r.data);
  },

  toggleBook(bookId: number): Promise<void> {
    return apiClient.post(`/library/book/${bookId}`).then(() => {});
  },
};
