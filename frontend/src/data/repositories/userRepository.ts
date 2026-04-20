import apiClient from '../api/apiClient';
import type { UserProfile, UserUpdateRequest } from '../../domain/models';

export const userRepository = {
  async getMe(): Promise<UserProfile> {
    const r = await apiClient.get('/users/profile/me');
    return r.data;
  },

  async getById(id: number): Promise<UserProfile> {
    const r = await apiClient.get(`/users/profile/${id}`);
    return r.data;
  },

  async update(id: number, data: UserUpdateRequest): Promise<void> {
    await apiClient.put(`/users/${id}`, data);
  },

  async search(query: string): Promise<any[]> {
    const r = await apiClient.get(`/users/search?username=${query}`);
    return r.data;
  },
};
