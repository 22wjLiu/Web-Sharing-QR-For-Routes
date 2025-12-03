<template>
  <el-container>
    <el-header style="padding: 0"><Header :nav-items="navItems" /></el-header>
    <el-main style="padding: 0"><RouterView /></el-main>
    <el-footer style="padding: 0"><Footer :nav-items="navItems" /></el-footer>
  </el-container>
</template>

<script setup lang="ts">
import type { NavItem } from '@/types/layout';
import { useRouter } from 'vue-router';
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';

const router = useRouter();

const navItems: NavItem[] =
  router
    .getRoutes()
    .find((r) => r.path === '/')
    ?.children.map((child) => {
      return {
        label: child.meta?.title,
        path: child.path,
      } as NavItem;
    }) || [];
</script>

<style scoped lang="less"></style>
