import service from '@/api/index';
import type { User, LoginPayload, RegisterPayload } from '@/types/user';
import type { ApiResponse } from '@/types/api';

interface LoginResponse {
  token: string;
}

export const getUserInfo = () => {
  return service.get<ApiResponse<User>>('/auth/getUserInfo');
};

export const register = (payload: RegisterPayload) => {
  return service.post<ApiResponse<null>>('/auth/register', payload);
};

export const userLogin = (payload: LoginPayload) => {
  return service.post<ApiResponse<LoginResponse>>('/auth/login', payload);
};

export const userLogOut = () => {
  return service.post<ApiResponse<null>>('/auth/logout');
};
