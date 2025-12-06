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
        component: () => import('@/views/Routes.vue'),
        meta: {
          title: '路线广场',
        },
      },
      {
        path: '/tutorial',
        name: 'Tutorial',
        component: () => import('@/views/Tutorial.vue'),
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
    path: '/userCenter',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue'),
    meta: {
      title: '用户中心',
    },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/admin/UserMana.vue'),
        meta: {
          title: '用户管理',
          icon: '',
        },
      },
      {
        path: '/qr',
        name: 'QR',
        component: () => import('@/views/admin/QRMana.vue'),
        meta: {
          title: '二维码管理',
          icon: '',
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
