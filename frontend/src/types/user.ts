export interface User {
  id: number | null;
  name: string;
  email: string;
  avatarUrl: string;
  role: number | null;
  description?: string;
}

export interface LoginPayload {
  emailOrName: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  code: string;
}

export interface UserListPayload {
  page: number;
  pageSize?: number;
  keyword?: string;
}

export interface PageResult<T> {
  total: number;
  records: T[];
}

export interface AddUserPayload {
  name: string;
  email: string;
  password: string;
  avatarUrl?: string;
  role: number;
  description: string;
}

export interface UpdateUserPayload {
  id: number;
  name?: string;
  email?: string;
  avatarUrl?: string;
  role?: number | null;
  description?: string;
}

export interface DeleteUsersPayload {
  ids: number[];
}

export interface UpdatePasswordPayload {
  oldPassWord: string;
  newPassWord: string;
}
