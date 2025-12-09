import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user';
import type { ApiResponse } from '@/types/api';
import type { AxiosResponse } from 'axios';

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 响应拦截器
service.interceptors.response.use(
  (
    response: AxiosResponse<ApiResponse<unknown>>,
  ): AxiosResponse<ApiResponse<unknown>> | Promise<AxiosResponse<ApiResponse<unknown>>> => {
    const payload = response.data;
    if (payload && typeof payload === 'object' && 'code' in payload) {
      if (payload.code === 200) {
        return response;
      }
      ElMessage.error(payload.message || '请求失败');
      return Promise.reject(response);
    }
    return response;
  },
  async (error) => {
    const userStore = useUserStore();
    const status = error?.response?.status;
    const message = error?.response?.data?.message || error?.message || '请求出错，请稍后重试';

    switch (status) {
      case 401:
        await userStore.logout();
        ElMessage.error(message);
        break;
      case 403:
        console.error(message);
        break;
      case 500:
        ElMessage.error('服务未找到，请联系管理员');
        break;
      default:
        ElMessage.error(message);
        break;
    }
    return Promise.reject(error);
  },
);

export default service;
