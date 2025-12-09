<template>
  <div class="not-found-container">
    <!-- 背景装饰 -->
    <div class="bg-circle circle-blue"></div>
    <div class="bg-circle circle-purple"></div>

    <div class="content-wrapper">
      <!-- 视觉主体区域 -->
      <div class="visual-area">
        <!-- 404 大标题 -->
        <h1 class="title-404 floating-element">404</h1>

        <!-- 装饰性元素 (阴影) -->
        <div class="shadow-wrapper">
          <div class="shadow-element"></div>
        </div>
      </div>

      <!-- 文本信息 -->
      <div class="text-area">
        <h2 class="subtitle">哎呀，页面好像去火星探险了</h2>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-area">
        <el-button type="primary" size="large" class="custom-btn primary-btn" @click="goHome">
          <el-icon class="icon-margin"><House /></el-icon>
          返回首页
        </el-button>

        <el-button size="large" class="custom-btn secondary-btn" @click="goBack">
          <el-icon class="icon-margin"><Back /></el-icon>
          返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { House, Back } from '@element-plus/icons-vue';

// 使用 vue-router 进行路由跳转
const router = useRouter();

const goHome = () => {
  router.push('/home');
};

const goBack = () => {
  router.back();
};
</script>

<style scoped lang="less">
/* 容器布局 */
.not-found-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9fafb; /* gray-50 */
  position: relative;
  overflow: hidden;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.content-wrapper {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 0 1rem;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

/* 视觉区域 */
.visual-area {
  position: relative;
  display: inline-block;
  margin-bottom: 2rem;
}

.title-404 {
  font-size: 150px;
  font-weight: 900;
  line-height: 1;
  letter-spacing: -0.05em;
  user-select: none;
  margin: 0;

  /* 渐变文字背景 */
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  color: transparent; /* Fallback */
}

/* 响应式字体大小 */
@media (min-width: 768px) {
  .title-404 {
    font-size: 200px;
  }
}

/* 阴影装饰 */
.shadow-wrapper {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  z-index: -1;
}

.shadow-element {
  background-color: #000;
  width: 8rem; /* 32 * 0.25rem */
  height: 1rem; /* 4 * 0.25rem */
  margin: 8rem auto 0; /* mt-32 */
  border-radius: 9999px;
  filter: blur(24px); /* blur-xl */
  animation: shadow-pulse 6s ease-in-out infinite;
}

/* 文本区域 */
.text-area {
  margin-bottom: 2.5rem; /* mb-10 */
  position: relative;
  z-index: 10;
}

.subtitle {
  font-size: 1.5rem; /* text-2xl */
  font-weight: 700;
  color: #1f2937; /* gray-800 */
  margin: 0;
}

@media (min-width: 768px) {
  .subtitle {
    font-size: 1.875rem; /* text-3xl */
  }
}

/* 按钮区域 */
.action-area {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 10;
}

@media (min-width: 640px) {
  .action-area {
    flex-direction: row;
  }
}

.icon-margin {
  margin-right: 0.5rem;
}

/* 按钮自定义样式覆盖 */
.custom-btn {
  padding-left: 2rem !important;
  padding-right: 2rem !important;
  height: 3rem !important; /* h-12 */
  border-radius: 0.75rem !important; /* rounded-xl */
  font-size: 1rem !important;
  transition: all 0.3s ease !important;
}

.primary-btn {
  box-shadow:
    0 10px 15px -3px rgba(199, 210, 254, 0.5),
    0 4px 6px -2px rgba(199, 210, 254, 0.5); /* shadow-indigo-200 */
}

.primary-btn:hover {
  box-shadow:
    0 10px 15px -3px rgba(165, 180, 252, 0.8),
    0 4px 6px -2px rgba(165, 180, 252, 0.8); /* shadow-indigo-300 */
  transform: translateY(-1px);
}

.secondary-btn {
  background-color: #fff !important;
  border-color: #dcdfe6 !important;
  color: #606266 !important;
}

.secondary-btn:hover {
  color: #9333ea !important; /* text-purple-600 */
  border-color: #d8b4fe !important;
  background-color: #fff !important;
  transform: translateY(-1px);
}

/* 背景圆圈装饰 */
.bg-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.3;
}

.circle-blue {
  background-color: #bfdbfe; /* blue-200 */
  width: 24rem; /* w-96 */
  height: 24rem;
  top: -100px;
  left: -100px;
}

.circle-purple {
  background-color: #e9d5ff; /* purple-200 */
  width: 20rem; /* w-80 */
  height: 20rem;
  bottom: -50px;
  right: -50px;
}

/* 动画定义 */
@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
  100% {
    transform: translateY(0px);
  }
}

@keyframes shadow-pulse {
  0% {
    transform: scale(1);
    opacity: 0.2;
  }
  50% {
    transform: scale(0.85);
    opacity: 0.1;
  }
  100% {
    transform: scale(1);
    opacity: 0.2;
  }
}

.floating-element {
  animation: float 6s ease-in-out infinite;
}
</style>
