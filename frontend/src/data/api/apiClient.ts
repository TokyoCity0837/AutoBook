import axios from 'axios';
import { API_BASE_URL } from '../../shared/constants/config';
import { navigateTo } from '../../shared/utils/navigation';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // session cookies auto-sent
});

// Handle 401 → redirect to login
apiClient.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err?.response?.status === 401) {
      navigateTo('/login');
    }
    return Promise.reject(err);
  }
);

export default apiClient;
