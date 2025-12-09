import service from '@/api/index';
import type {
  User,
  LoginPayload,
  LoginResponse,
  RegisterPayload,
  UserListPayload,
  DeleteUsersPayload,
  PageResult,
  UpdateUserPayload,
  AddUserPayload,
  UpdatePasswordPayload,
} from '@/types/user';
import type { ApiResponse } from '@/types/api';

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

export const getUserList = (payload: UserListPayload) => {
  return service.get<ApiResponse<PageResult<User>>>('/user/list', {
    params: payload,
  });
};

export const addUser = (payload: AddUserPayload) => {
  return service.post<ApiResponse<null>>('/user/add', payload);
};

export const updateUser = (payload: UpdateUserPayload) => {
  return service.put<ApiResponse<null>>(`/user/${payload.id}`, payload);
};

export const deleteUserByIds = (payload: DeleteUsersPayload) => {
  return service.delete<ApiResponse<null>>('/user', {
    data: payload,
  });
};

export const updatePassword = (payload: UpdatePasswordPayload) => {
  return service.put<ApiResponse<null>>(`/user/password`, {
    oldPassWord: payload.oldPassWord,
    newPassWord: payload.newPassWord,
  });
};
