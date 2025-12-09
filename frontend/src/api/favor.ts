import service from '@/api/index';
import type { ApiResponse } from '@/types/api';

export const addFavorite = (routeId: number) => {
  return service.post<ApiResponse<null>>(`/favor/${routeId}`);
};

export const removeFavorite = (routeId: number) => {
  return service.delete<ApiResponse<null>>(`/favor/${routeId}`);
};
