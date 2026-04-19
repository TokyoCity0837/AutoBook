import apiClient from '../api/apiClient';

export const storageRepository = {
  upload(file: File | Blob, filename?: string): Promise<string> {
    const formData = new FormData();
    formData.append('file', file, filename || 'file');
    return apiClient
      .post('/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(r => r.data);
  },
};
