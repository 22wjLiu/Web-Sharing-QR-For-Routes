import { createRouter, createWebHistory } from 'vue-router';
import routes from '@/router/baseRouteMap';

const router = createRouter({
  history: createWebHistory(),
  routes,
});

const DEFAULT_TITLE = '戏曲地图';

router.afterEach((to) => {
  const title = to.meta.title as string | undefined;
  if (title) {
    document.title = `${DEFAULT_TITLE}——${title}`;
  } else {
    document.title = DEFAULT_TITLE;
  }
});

export default router;
