import service from '@/api/index';
import type { ApiResponse } from '@/types/api';
import type {
  RouteRecord,
  RouteListPayload,
  AddRoutePayload,
  UpdateRoutePayload,
  DeleteRoutesPayload,
  PageResult,
  RouteOverview,
} from '@/types/route';

export const getRouteList = (payload: RouteListPayload) => {
  return service.get<ApiResponse<PageResult<RouteRecord>>>('/qr/list', {
    params: payload,
  });
};

export const getRouteExploreList = (page = 1, pageSize = 12, keyword?: string) => {
  return service.get<ApiResponse<PageResult<RouteOverview>>>('/qr/explore', {
    params: { page, pageSize, keyword },
  });
};

export const getRoutesByUser = (userId: number, page = 1, pageSize = 10) => {
  return service.get<ApiResponse<PageResult<RouteOverview>>>(`/qr/user/${userId}`, {
    params: { page, pageSize },
  });
};

export const getFavoriteRoutes = (userId: number, page = 1, pageSize = 10) => {
  return service.get<
    ApiResponse<PageResult<RouteRecord & { favoritesCount?: number; favorited?: boolean }>>
  >(`/qr/favorite/${userId}`, { params: { page, pageSize } });
};

export const addRoute = (payload: AddRoutePayload) => {
  return service.post<ApiResponse<null>>('/qr/add', payload);
};

export const updateRoute = (payload: UpdateRoutePayload) => {
  return service.put<ApiResponse<null>>(`/qr/${payload.id}`, payload);
};

export const deleteRoutes = (payload: DeleteRoutesPayload) => {
  return service.delete<ApiResponse<null>>('/qr', {
    data: payload,
  });
};
