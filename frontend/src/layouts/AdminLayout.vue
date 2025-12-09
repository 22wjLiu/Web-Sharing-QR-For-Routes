<template>
  <div class="admin-container">
    <el-container class="layout-container">
      <el-aside width="220px" class="sidebar">
        <div class="logo-box">
          <el-icon class="logo-icon" :size="24"><ElementPlus /></el-icon>
          <span>后台管理</span>
        </div>

        <el-menu
          :default-active="route.path"
          class="sidebar-menu"
          text-color="#606266"
          active-text-color="#409EFF"
          :router="true"
        >
          <el-menu-item v-for="item in adminMenuItems" :key="item.index" :index="item.index">
            <el-icon v-if="item.icon">
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <span class="location-label">当前位置:</span>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>后台管理</el-breadcrumb-item>
              <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="user-dropdown">
                <el-avatar :size="32" :src="profile?.avatarUrl" class="user-avatar" />
                {{ profile?.name }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goHome">首页</el-dropdown-item>
                  <el-dropdown-item @click="goUserCenter">个人中心</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue';
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useRoute, useRouter } from 'vue-router';
import { ElementPlus, User, PictureRounded, ArrowDown } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const { profile } = storeToRefs(userStore);

const adminIconMap: Record<string, Component> = {
  User,
  PictureRounded,
};

const adminMenuItems = computed(() => {
  const adminRoute = router.getRoutes().find((r) => r.name === 'Admin');
  if (!adminRoute || !adminRoute.children) {
    return [];
  }
  const basePath = adminRoute.path?.replace(/\/$/, '') || '';
  return adminRoute.children.map((child) => {
    const fullPath = child.path.startsWith('/') ? child.path : `${basePath}/${child.path}`;
    const title = child.meta?.title as string;
    const iconKey = child.meta?.icon as string;
    return {
      index: fullPath,
      title,
      icon: adminIconMap[iconKey] ?? null,
    };
  });
});

const goHome = () => {
  router.push('/home');
};

const goUserCenter = () => {
  const url = router.resolve('/userCenter').href;
  window.open(url, '_blank');
};

const handleLogout = async () => {
  await userStore.logout();
};
</script>

<style scoped>
/* 布局容器 */
.admin-container {
  height: 100vh;
  width: 100%;
}
.layout-container {
  height: 100%;
}

/* 侧边栏样式 */
.sidebar {
  background-color: #ffffff;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px 0 rgba(29, 35, 41, 0.05);
  z-index: 10;
}
.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  color: #409eff;
  border-bottom: 1px solid #e6e6e6;
}
.logo-icon {
  margin-right: 8px;
}
.sidebar-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

/* 顶部导航栏样式 */
.header {
  background-color: #ffffff;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
}
.header-left {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}
.location-label {
  margin-right: 8px;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}
.user-dropdown:hover {
  color: #409eff;
}
.user-avatar {
  margin-right: 8px;
}

/* 主内容背景 */
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
