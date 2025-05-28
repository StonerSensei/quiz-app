import api from './api';
import {
  RegisterRequest,
  AuthenticationRequest,
  AuthenticationResponse,
} from '../types/auth';

export const register = async (data: Omit<RegisterRequest, 'id'>): Promise<AuthenticationResponse> => {
  const response = await api.post<AuthenticationResponse>('/auth/register', data);
  return response.data;
};
export const login = async (data: AuthenticationRequest) => {
  const response = await api.post<AuthenticationResponse>('/auth/login', data);
  return response.data;
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};