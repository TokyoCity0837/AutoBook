import apiClient from '../api/apiClient';
import type { FollowResponse } from '../../domain/models';

export const followRepository = {
  getStatus(userId: number): Promise<boolean> {
    return apiClient.get(`/follows/status/${userId}`).then(r => r.data);
  },

  follow(userId: number): Promise<FollowResponse> {
    return apiClient.post(`/follows/direct/${userId}`).then(r => r.data);
  },

  unfollow(userId: number): Promise<void> {
    return apiClient.delete(`/follows/remove/${userId}`).then(() => {});
  },

  getFriends(): Promise<FollowResponse[]> {
    return apiClient.get('/follows/friends/me').then(r => r.data);
  },

  getFollowing(): Promise<FollowResponse[]> {
    return apiClient.get('/follows/following').then(r => r.data);
  },

  getFollowers(): Promise<FollowResponse[]> {
    return apiClient.get('/follows/followers').then(r => r.data);
  },
};
