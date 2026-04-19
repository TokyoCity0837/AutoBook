import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true, // якщо у тебе cookies auth
});

export function navigateTo(path: string) {
    if (window.location.pathname !== path) {
      window.location.assign(path);
    }
  }

// приклад: якщо токен в localStorage
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status;

    if (status === 401) {
      // 1) прибираємо токен/сесію
      localStorage.removeItem('token');

      // 2) редірект на логін
      // (можеш додати ?next=... щоб після логіну повернутись)
      navigateTo('/login');
    }

    return Promise.reject(err);
  }
);

export default api;