import apiClient from '../api/apiClient';

export const authRepository = {
  login(email: string, password: string) {
    return apiClient.post('/auth/login', { email, password }).then(r => r.data);
  },

  register(username: string, password: string, visibleName: string) {
    return apiClient.post('/auth/register', { username, password, visibleName }).then(r => r.data);
  },

  logout() {
    return apiClient.post('/auth/logout').then(() => {});
  },
};
