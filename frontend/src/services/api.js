import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const login = (username, password) => {
  return api.post('/auth/login', { username, password });
};

export const getVendors = (params) => {
  return api.get('/vendors', { params });
};

export const getVendorById = (id) => {
  return api.get(`/vendors/${id}`);
};

export const createVendor = (vendor) => {
  return api.post('/vendors', vendor);
};

export const updateVendor = (id, vendor) => {
  return api.put(`/vendors/${id}`, vendor);
};

export const deleteVendor = (id) => {
  return api.delete(`/vendors/${id}`);
};

export default api;
