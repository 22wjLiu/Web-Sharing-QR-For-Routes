import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Default',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/index',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: {
          title: '戏曲地图——首页',
        },
      },
    ],
  },
  {
    path: '/loginRegister',
    name: 'LoginRegister',
    component: () => import('@/layouts/LoginRegisterLayout.vue'),
    meta: {
      title: '戏曲地图——登录',
    },
    redirect: '/login',
    children: [
      {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: {
          title: '戏曲地图——首页',
        },
      },
      {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: {
          title: '戏曲地图——注册',
        },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '戏曲地图',
    },
  },
];

export default routes;
