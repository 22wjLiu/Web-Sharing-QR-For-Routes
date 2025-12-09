<template>
  <div class="header">
    <div class="header__inner">
      <section class="left">
        <div class="logo" @click="router.push('/')">
          <span class="logo-text">戏曲地图</span>
        </div>

        <nav class="nav">
          <button
            v-for="item in navItems"
            :key="item.path"
            class="nav-item"
            :class="{ 'is-active': isActive(item) }"
            @click="go(item.path)"
          >
            {{ item.label }}
          </button>
        </nav>
      </section>
      <section class="right">
        <el-dropdown v-if="isLoggedIn">
          <span class="avatar-wrapper">
            <el-avatar :size="36" :src="profile?.avatarUrl" />
            <span class="avatar-name">{{ profile?.name }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goUserCenter">个人中心</el-dropdown-item>
              <el-dropdown-item v-if="profile?.role === 1" @click="goAdmin"
                >后台管理</el-dropdown-item
              >
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <button v-else class="login-btn" @click="router.push('/login')">登录</button>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import type { NavItem } from '@/types/layout';

const router = useRouter();
const route = useRoute();

const userStore = useUserStore();
const { profile, isLoggedIn } = storeToRefs(userStore);
const { logout } = userStore;

defineProps<{
  navItems: NavItem[];
}>();

const go = (path: string) => {
  if (path !== route.path) {
    router.push(path);
  }
};

const handleLogout = async () => {
  await logout();
};

const goUserCenter = () => {
  const url = router.resolve('/userCenter').href;
  window.open(url, '_blank');
};

const goAdmin = () => {
  router.push('/admin');
};

const isActive = (item: { path: string }) => {
  if (item.path === '/') return route.path === '/';
  return route.path.startsWith(item.path);
};
</script>

<style scoped lang="less">
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(12px);

  & > .header__inner {
    max-width: 1120px;
    margin: 0 auto;
    padding: 10px 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;

    & > .left {
      display: flex;
      align-items: center;
      gap: 24px;

      & > .logo {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;

        & > .logo-text {
          font-size: 20px;
          font-weight: 600;
          letter-spacing: 0.08em;
          color: #0f172a;
        }
      }

      & > .nav {
        display: flex;
        align-items: center;
        gap: 10px;

        & > .nav-item {
          border: none;
          background: transparent;
          padding: 6px 10px;
          border-radius: 999px;
          font-size: 16px;
          cursor: pointer;
          color: #64748b;
          position: relative;
          transition: all 0.2s ease;

          &::after {
            content: '';
            position: absolute;
            left: 12px;
            right: 12px;
            bottom: 3px;
            height: 2px;
            border-radius: 999px;
            background: linear-gradient(90deg, #ef4444, #3b82f6);
            transform: scaleX(0);
            transform-origin: center;
            transition: transform 0.2s ease;
          }

          &:hover {
            color: #0f172a;
            background: rgba(148, 163, 184, 0.06);
          }

          &.is-active {
            color: #0f172a;
            font-weight: 600;

            &::after {
              transform: scaleX(1);
            }
          }
        }
      }
    }

    & > .right {
      display: flex;
      align-items: center;
      gap: 8px;

      & > .login-btn {
        border: none;
        background: transparent;
        padding: 6px 10px;
        border-radius: 999px;
        font-size: 16px;
        cursor: pointer;
        color: #64748b;
        position: relative;
        transition: all 0.2s ease;

        &:hover {
          color: #0f172a;
          background: rgba(148, 163, 184, 0.06);
        }
      }

      & .avatar-wrapper {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
      }
    }
  }
}

/* 简单响应式 */
@media (max-width: 768px) {
  .logo-text {
    display: none;
  }

  .header__inner {
    padding-inline: 12px;
  }

  .nav {
    gap: 4px;
  }

  .nav-item {
    padding-inline: 8px;
  }

  .avatar-name {
    display: none;
  }
}
</style>
