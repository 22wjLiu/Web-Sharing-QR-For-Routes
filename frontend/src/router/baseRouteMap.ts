import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Default',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: {
          title: '首页',
        },
      },
      {
        path: '/routes',
        name: 'Routes',
        component: () => import('@/views/Home.vue'),
        meta: {
          title: '路线广场',
        },
      },
      {
        path: '/tutorial',
        name: 'Tutorial',
        component: () => import('@/views/Home.vue'),
        meta: {
          title: '教程',
        },
      },
    ],
  },
  {
    path: '/loginRegister',
    name: 'LoginRegister',
    component: () => import('@/layouts/LoginRegisterLayout.vue'),
    meta: {
      title: '登录',
    },
    redirect: '/login',
    children: [
      {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: {
          title: '登录',
        },
      },
      {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: {
          title: '注册',
        },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '404',
    },
  },
];

export default routes;
