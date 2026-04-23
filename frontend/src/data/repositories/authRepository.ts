import apiClient from '../api/apiClient';

export const authRepository = {
  async login(email: string, password: string) {
    const r = await apiClient.post('/auth/login', { email, password });
    return r.data;
  },

  async register( username: string, visibleName: string, email: string, password: string ) {
    const r = await apiClient.post('/auth/register', { username, visibleName, email, password,});
    return r.data;
  },

  async logout() {
    await apiClient.post('/auth/logout');
  },
};
