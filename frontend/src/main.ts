import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from '@/App.vue';
import router from '@/router';
import Vue3Lottie from 'vue3-lottie';
import ElementPlus from 'element-plus';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import '@/assets/css/global.less';
import 'element-plus/dist/index.css';

const app = createApp(App);

const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(Vue3Lottie);
app.use(ElementPlus, { locale: zhCn });

app.mount('#app');
