<template>
  <main class="main-container-login">
    <section class="top-button-container">
      <button @click="$router.push('/')">
        <el-icon><Back /></el-icon> <span>返回首页</span>
      </button>
      <button @click="$router.push('/register')">注册</button>
    </section>
    <section class="input-container">
      <header class="card-header">
        <span class="card-tag">戏曲地图</span>
        <h1 class="card-title">登录</h1>
        <p class="card-desc">让戏曲从舞台走进旅途，跟着地图寻戏曲，让每一次出行都有戏韵相伴。</p>
      </header>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        label-width="auto"
        @keydown.enter="handleLogin(loginFormRef)"
      >
        <!-- 用户名或者邮箱 -->
        <el-form-item label="帐号" prop="emailOrName">
          <el-input v-model="loginForm.emailOrName" placeholder="请输入用户名或者邮箱" />
        </el-form-item>
        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin(loginFormRef)">登录</el-button>
        </el-form-item>
      </el-form>
    </section>
  </main>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus';
import { ElLoading } from 'element-plus';
import { Back } from '@element-plus/icons-vue';
import { ref, reactive } from 'vue';
import { useUserStore } from '@/store/user';
import router from '@/router';

const { login } = useUserStore();

// 登录表单
interface LoginForm {
  emailOrName: string;
  password: string;
}
const loginFormRef = ref<FormInstance>();
const loginForm = reactive<LoginForm>({
  emailOrName: '',
  password: '',
});
const loginRules = reactive<FormRules<LoginForm>>({
  emailOrName: { required: true, message: '请输入用户名或者邮箱', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
});
const handleLogin = async (loginFormEl: FormInstance | undefined) => {
  if (!loginFormEl) return;

  const isValid = await loginFormEl.validate().catch(() => false);
  if (!isValid) return;

  const loadingInstance = ElLoading.service({
    lock: true,
    text: '登录中...',
  });

  try {
    await login({ emailOrName: loginForm.emailOrName, password: loginForm.password });
    router.push('/home');
  } catch {
  } finally {
    loadingInstance.close();
  }
};
</script>

<style scoped lang="less">
.main-container-login {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  width: 100%;
  height: 100%;

  & > .top-button-container {
    position: absolute;
    top: 0;
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 10px;

    & > button {
      padding: 3px 10px;
      font-weight: bold;
      border: 2px solid transparent;
      border-radius: 9999px;
      transition: border 0.2s ease-out;
    }

    & > button:hover {
      border-color: #2c2c2c;
    }
  }

  & > .input-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    /* 顶部小签名：像卷首题签 */
    & > .card-header {
      margin-bottom: 24px;
      text-align: left;

      & > .card-tag {
        display: inline-block;
        padding: 2px 10px;
        margin-bottom: 8px;
        border-radius: 999px;
        font-size: 12px;
        letter-spacing: 0.2em;
        text-transform: uppercase;
        color: #b91c1c;
        background: rgba(185, 28, 28, 0.06);
      }

      /* 主标题：用一点“戏曲感”的字体风格 */
      & > .card-title {
        margin: 0 0 8px;
        font-size: 24px;
        font-weight: 600;
        color: #111827;
      }

      /* 副标题：低饱和说明文字 */
      & > .card-desc {
        margin: 0;
        max-width: 420px;
        font-size: 13px;
        line-height: 1.8;
        color: #6b7280;
      }
    }

    /* 覆盖当前卡片里的按钮样式 */
    & .el-button--primary {
      width: 100%;
      border-radius: 999px;
      background-image: linear-gradient(90deg, #2563eb, #4f46e5);
      border: none;
    }

    /* 输入框聚焦时用同一个蓝色 */
    & .el-input__inner:focus {
      border-color: #2563eb;
      box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.18);
    }

    & > .el-form {
      width: 100%;

      & .el-button {
        display: block;
        width: 100%;
        padding: 6px 24px;
        margin: 0 auto;
        margin-top: 20px;
        color: #fff;
        border-radius: 10px;
      }
    }
  }
}
</style>
