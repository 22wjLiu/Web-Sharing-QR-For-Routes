import service from '@/api/index';
import type { ApiResponse } from '@/types/api';
import type { UploadStaticResponse } from '@/types/resource';

export type UploadStaticType = 'avatar' | 'qr' | 'routecover';

export const uploadStaticResource = (file: File, type: UploadStaticType) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('type', type);

  return service.post<ApiResponse<UploadStaticResponse>>('/resource/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};
