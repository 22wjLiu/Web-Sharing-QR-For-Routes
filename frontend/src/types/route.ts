export interface RouteRecord {
  id: number | null;
  title: string;
  description: string;
  qrUrl: string;
  coverUrl?: string;
  createTime?: string;
  isDeleted?: number;
  userId?: number;
  status?: number | null;
}

export interface RouteOverview extends RouteRecord {
  authorName: string;
  authorAvatar: string;
  favoritesCount: number;
  favorited: boolean;
}

export interface RouteListPayload {
  page: number;
  pageSize?: number;
  keyword?: string;
  userId?: number;
  startTime?: string;
  endTime?: string;
}

export interface AddRoutePayload {
  title: string;
  description: string;
  qrUrl: string;
  coverUrl?: string;
  userId: number;
}

export interface UpdateRoutePayload {
  id: number;
  title?: string;
  description?: string;
  qrUrl?: string;
  coverUrl?: string;
  status?: number | null;
}

export interface DeleteRoutesPayload {
  ids: number[];
}

export interface PageResult<T> {
  total: number;
  records: T[];
}
