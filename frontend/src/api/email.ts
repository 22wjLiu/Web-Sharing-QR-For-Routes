import service from '@/api/index';
import type { sendCodePayload } from '@/types/email';
import type { ApiResponse } from '@/types/api';

export const sendCode = (payload: sendCodePayload) => {
  return service.get<ApiResponse<null>>('/auth/sendCode', {
    params: payload,
  });
};
