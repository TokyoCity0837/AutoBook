import apiClient from '../api/apiClient';
import type { BookDetails, BookCard, BookCreateRequest, BookUpdateRequest } from '../../domain/models';

export const bookRepository = {
  async getById(id: number): Promise<BookDetails> {
    const r = await apiClient.get(`/books/${id}`);
    return r.data;
  },

  async getMyBooks(): Promise<BookCard[]> {
    const r = await apiClient.get('/books/author/me');
    return r.data;
  },

  async create(data: BookCreateRequest): Promise<BookDetails> {
    const r = await apiClient.post('/books', data);
    return r.data;
  },

  async update(id: number, data: BookUpdateRequest): Promise<BookDetails> {
    const r = await apiClient.put(`/books/${id}`, data);
    return r.data;
  },

  async delete(bookId: number): Promise<void> {
    await apiClient.delete(`/books/${bookId}`);
  },
};
