import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from '@/App.vue';
import router from '@/router';
import Vue3Lottie from 'vue3-lottie';
import '@/assets/css/global.less';
import 'element-plus/dist/index.css';

const app = createApp(App);

const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(Vue3Lottie);

app.mount('#app');
