import { createRouter, createWebHistory } from 'vue-router';
import routes from '@/router/baseRouteMap';
import { useUserStore } from '@/store/user';
import { storeToRefs } from 'pinia';

const router = createRouter({
  history: createWebHistory(),
  routes,
});

const DEFAULT_TITLE = '戏曲地图';

router.beforeEach((to, _, next) => {
  if (to.path.startsWith('/admin')) {
    const userStore = useUserStore();
    const { profile } = storeToRefs(userStore);
    if (!profile.value) {
      next('/login');
      return;
    } else if (profile.value.role !== 1) {
      next('/notFound');
      return;
    }
  }
  next();
});

router.afterEach((to) => {
  const title = to.meta.title as string | undefined;
  if (title) {
    document.title = `${DEFAULT_TITLE}——${title}`;
  } else {
    document.title = DEFAULT_TITLE;
  }
});

export default router;
