import apiClient from '../api/apiClient';
import type { BookDetails, BookCard, BookCreateRequest, BookUpdateRequest } from '../../domain/models';

export const bookRepository = {
  getById(id: number): Promise<BookDetails> {
    return apiClient.get(`/books/${id}`).then(r => r.data);
  },

  getMyBooks(): Promise<BookCard[]> {
    return apiClient.get('/books/author/me').then(r => r.data);
  },

  create(data: BookCreateRequest): Promise<BookDetails> {
    return apiClient.post('/books', data).then(r => r.data);
  },

  update(id: number, data: BookUpdateRequest): Promise<BookDetails> {
    return apiClient.put(`/books/${id}`, data).then(r => r.data);
  },
};
