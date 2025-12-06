export interface User {
  id: number | null;
  name: string;
  email: string;
  avatarUrl: string;
  role: number | null;
}

export interface LoginPayload {
  emailOrName: string;
  password: string;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  code: string;
}
