import apiClient from '../api/apiClient';
import type { FollowResponse } from '../../domain/models';

export const followRepository = {
  async getStatus(userId: number): Promise<boolean> {
    const r = await apiClient.get(`/follows/status/${userId}`);
    return r.data;
  },

  async follow(userId: number): Promise<FollowResponse> {
    const r = await apiClient.post(`/follows/direct/${userId}`);
    return r.data;
  },

  async unfollow(userId: number): Promise<void> {
    await apiClient.delete(`/follows/unfollow/${userId}`);
  },

  async removeFriend(userId: number): Promise<void> {
      await apiClient.delete(`/follows/remove/${userId}`);
  },

  async getFriends(): Promise<FollowResponse[]> {
    const r = await apiClient.get('/follows/friends/me');
    return r.data;
  },

  async getFollowing(): Promise<FollowResponse[]> {
    const r = await apiClient.get('/follows/following');
    return r.data;
  },

  async getFollowers(): Promise<FollowResponse[]> {
    const r = await apiClient.get('/follows/followers');
    return r.data;
  },
};
