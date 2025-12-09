import type { User, LoginPayload } from '@/types/user';
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { userLogin, userLogOut, getUserInfo } from '@/api/user';
import router from '@/router';
import { ElLoading } from 'element-plus';
import { buildAvatarUrl } from '@/utils/avatar';

const USER_PROFILE_KEY = 'qr_user_profile';
const USER_TOKEN_KEY = 'qr_user_token';

const formatProfile = (payload: User | null): User | null => {
  if (!payload) {
    return null;
  }
  return {
    ...payload,
    avatarUrl: buildAvatarUrl(payload.avatarUrl),
  };
};

const readProfileFromStorage = (): User | null => {
  try {
    const raw = localStorage.getItem(USER_PROFILE_KEY);
    if (!raw) return null;
    return formatProfile(JSON.parse(raw) as User);
  } catch (error: unknown) {
    if (error instanceof Error) {
      console.error('解析存储用户信息出错:', error.message);
    }
    return null;
  }
};

const readTokenFromStorage = (): string | null => localStorage.getItem(USER_TOKEN_KEY);

const persistProfile = (profile: User | null) => {
  if (profile) {
    localStorage.setItem(USER_PROFILE_KEY, JSON.stringify(profile));
  } else {
    localStorage.removeItem(USER_PROFILE_KEY);
  }
};

const persistToken = (token: string | null) => {
  if (token) {
    localStorage.setItem(USER_TOKEN_KEY, token);
  } else {
    localStorage.removeItem(USER_TOKEN_KEY);
  }
};

export const useUserStore = defineStore('user', () => {
  const profile = ref<User | null>(readProfileFromStorage());
  const token = ref<string | null>(readTokenFromStorage());

  const isLoggedIn = computed(() => Boolean(token.value));

  const setProfile = (payload: User | null) => {
    const formatted = formatProfile(payload);
    profile.value = formatted;
    persistProfile(formatted);
  };

  const updateProfile = (payload: Partial<User>) => {
    const nextProfile: User = profile.value
      ? { ...profile.value, ...payload }
      : {
          id: payload.id ?? null,
          email: payload.email ?? '',
          name: payload.name ?? '',
          avatarUrl: payload.avatarUrl ?? '',
          role: payload.role ?? null,
          description: payload.description ?? '',
        };
    setProfile(nextProfile);
  };

  const fetchProfile = async () => {
    const { data: userResponse } = await getUserInfo();
    if (userResponse.data) {
      setProfile(userResponse.data);
      return userResponse.data;
    }
    return null;
  };

  const login = async (payload: LoginPayload) => {
    const { data: loginResponse } = await userLogin(payload);

    if (!loginResponse.data?.token) {
      throw new Error(loginResponse.message || '登录失败');
    }

    token.value = loginResponse.data.token;
    persistToken(token.value);

    try {
      await fetchProfile();
    } catch (error) {
      console.error('获取用户信息失败', error);
    }

    return loginResponse.data.token;
  };

  const logout = async () => {
    const loading = ElLoading.service({
      lock: true,
      text: '正在退出...',
      background: 'rgba(0, 0, 0, 0.25)',
    });
    try {
      await userLogOut();
    } finally {
      loading.close();
      token.value = null;
      persistToken(null);
      setProfile(null);
      router.push('/login');
    }
  };

  return {
    profile,
    token,
    isLoggedIn,
    login,
    logout,
    setProfile,
    updateProfile,
    fetchProfile,
  };
});
