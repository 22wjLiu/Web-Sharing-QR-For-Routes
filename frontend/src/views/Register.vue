<template>
  <main class="main-conatiner-register">
    <section class="setps-container">
      <section class="top">
        <span class="title">戏曲地图</span>
        <el-steps direction="vertical" :active="registerStatus" finish-status="success">
          <el-step :icon="Postcard" title="基本信息" description="提供用户名、邮箱和密码" />
          <el-step :icon="Message" title="验证邮箱" description="输入您的邮箱验证码" />
          <el-step :icon="CircleCheckFilled" title="注册成功" description="欢迎来到戏曲地图" />
        </el-steps>
      </section>
      <section class="bottom">
        <button @click="$router.push('/')">
          <el-icon><Back /></el-icon> <span>返回首页</span>
        </button>
        <button @click="$router.push('/login')">登录</button>
      </section>
    </section>

    <section class="input-container-wrapper">
      <div class="input-container" :style="translateStyle">
        <section class="first">
          <section class="tip">
            <h2>创建一个新账号</h2>
            <span>请提供您的用户名、邮箱和密码来进行账号注册</span>
          </section>
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
            label-width="auto"
          >
            <!-- 用户名 -->
            <el-form-item label="用户名" prop="name">
              <el-input v-model="registerForm.name" placeholder="请输入用户名" />
            </el-form-item>
            <!-- 邮箱 -->
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <!-- 密码 -->
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
            <div class="strength-bars" :class="`level-${strengthLevel}`">
              <div
                v-for="n in 4"
                :key="n"
                class="bar"
                div
                :class="{ active: strengthLevel >= n }"
              ></div>
              <div
                class="strength-text"
                :class="`level-${strengthLevel}`"
                v-if="registerForm.password"
              >
                强度：{{ strengthLabel }}
              </div>
            </div>
            <el-form-item>
              <el-button type="success" @click="handleFirstContinue(registerFormRef)"
                >继续</el-button
              >
            </el-form-item>
          </el-form>
        </section>

        <section class="second">
          <section class="tip">
            <h2>验证邮箱</h2>
            <span>我们发送了一个验证码到 {{ registerForm.email }}</span>
          </section>
          <el-form ref="vertifyFormRef" :model="vertifyForm" :rules="vertifyRules">
            <el-form-item prop="code">
              <OtpInput v-model="vertifyForm.code" :length="4" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleSecondContinue(vertifyFormRef)"
                >继续</el-button
              >
            </el-form-item>
          </el-form>
        </section>

        <section class="third">
          <section class="tip">
            <h1 style="font-size: 45px; color: #67c23a">注册成功</h1>
            <span>将在{{ jumpCountDown }}秒后跳转到<b>登录页面</b></span>
          </section>
        </section>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { ElMessage, ElLoading } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { Back, Postcard, Message, CircleCheckFilled } from '@element-plus/icons-vue';
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import OtpInput from '@/components/OtpInput.vue';
import { sendCode } from '@/api/email';
import { register } from '@/api/user';

// 全局路由
const router = useRouter();

// 注册状态
const registerStatus = ref<number>(0);

// 注册表单
interface RegisterForm {
  name: string;
  email: string;
  password: string;
}
const registerFormRef = ref<FormInstance>();
const registerForm = reactive<RegisterForm>({
  name: '',
  email: '',
  password: '',
});
const registerRules = reactive<FormRules<RegisterForm>>({
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2~20 个字符', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: ['blur', 'change'] },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入密码'));
          return;
        }

        const level = calcPasswordStrength(value);

        if (level < 2) {
          callback(new Error('密码太弱：至少 6 位，包含字母和数字，建议加入特殊符号或加长长度'));
        } else {
          callback();
        }
      },
      trigger: ['blur', 'change'],
    },
  ],
});

// 邮箱验证码表单
interface VertifyFrom {
  code: string;
}
const vertifyFormRef = ref<FormInstance>();
const vertifyForm = reactive<VertifyFrom>({
  code: '',
});
const vertifyRules = reactive<FormRules<VertifyFrom>>({
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!/^\d{4}$/.test(value)) {
          callback(new Error('验证码为 4 位数字'));
        } else {
          callback();
        }
      },
      trigger: 'change',
    },
  ],
});

// 当前注册步骤索引
const currentIndex = ref(0);
const translateStyle = computed(() => ({
  transform: `translateX(-${currentIndex.value * 33.333}%)`,
}));

// 密码强度
const calcPasswordStrength = (val: string): number => {
  if (!val) return 0;

  let level = 0;
  if (val.length >= 6) level++;
  if (/[A-Za-z]/.test(val) && /\d/.test(val)) level++;
  if (/[^A-Za-z0-9]/.test(val)) level++;
  if (val.length >= 12) level++;

  return Math.min(level, 4);
};

const strengthLevel = computed(() => calcPasswordStrength(registerForm.password));

const strengthLabel = computed(() => {
  switch (strengthLevel.value) {
    case 0:
      return '空';
    case 1:
      return '弱';
    case 2:
      return '一般';
    case 3:
      return '强';
    case 4:
      return '非常强';
    default:
      return '';
  }
});

// 处理第一次点击继续
const handleFirstContinue = async (registerFormEl: FormInstance | undefined) => {
  if (!registerFormEl) return;

  const isValid = await registerFormEl.validate().catch(() => false);
  if (!isValid) return;

  const loadingInstance = ElLoading.service({
    lock: true,
    text: '验证码发送中...',
  });

  try {
    await sendCode({ name: registerForm.name, email: registerForm.email, type: 'REGISTER' });
    ElMessage.success('验证码已发送，请前往邮箱查收');
    currentIndex.value = 1;
    registerStatus.value = 1;
  } catch {
  } finally {
    loadingInstance.close();
  }
};

// 跳转首页倒计时
const jumpCountDown = ref<number>(3);

// 处理第二次点击继续
const handleSecondContinue = async (vertifyFormEl: FormInstance | undefined) => {
  if (!vertifyFormEl) return;

  const isValid = await vertifyFormEl.validate().catch(() => false);
  if (!isValid) return;

  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在注册帐号...',
  });

  try {
    await register({
      name: registerForm.name,
      email: registerForm.email,
      password: registerForm.password,
      code: vertifyForm.code,
    });
    ElMessage.success('注册成功');
    currentIndex.value = 2;
    registerStatus.value = 2;

    const timer = setInterval(() => {
      jumpCountDown.value -= 1;
      if (jumpCountDown.value === 0) {
        clearInterval(timer);
        router.push('/login');
      }
    }, 1000);
  } catch {
  } finally {
    loadingInstance.close();
  }
};
</script>

<style scoped lang="less">
.main-conatiner-register {
  display: flex;
  width: 100%;
  height: 100%;

  & > .setps-container {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    width: 30%;
    padding: 0 20px;
    padding-top: 24px;
    padding-bottom: 16px;
    border-right: 1px dashed #d8d8d8;

    & > .top {
      flex: 1 1 auto;
      display: flex;
      flex-direction: column;
      gap: 10%;
      max-height: 60%;

      & > .title {
        text-align: center;
        padding: 2px 10px;
        margin-bottom: 8px;
        border-radius: 999px;
        font-size: 16px;
        letter-spacing: 0.2em;
        text-transform: uppercase;
        color: #b91c1c;
        background: rgba(185, 28, 28, 0.06);
      }

      & > .el-steps {
        & :deep(.el-step__icon) {
          background: rgb(244, 244, 244);
        }

        & :deep(.el-step__title) {
          font-size: 14px;
        }

        & :deep(.el-step__description) {
          font-size: 10px;
        }
      }
    }

    & > .bottom {
      display: flex;
      justify-content: space-between;

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
  }

  & > .input-container-wrapper {
    flex: 1 1 auto;
    display: flex;
    overflow: hidden;
    align-items: center;
    position: relative;
    & > .input-container {
      display: flex;
      width: 300%;
      flex-shrink: 0;
      transition: transform 0.3s ease;

      & > .first {
        & > .el-form {
          width: 61.8%;

          /* 横条区域 */
          & > .strength-bars {
            position: relative;
            display: flex;
            gap: 24px;

            &.level-1 .bar.active {
              background-color: var(--el-color-danger);
            }

            &.level-2 .bar.active {
              background-color: var(--el-color-warning);
            }

            &.level-3 .bar.active {
              background-color: var(--el-color-primary);
            }

            &.level-4 .bar.active {
              background-color: var(--el-color-success);
            }

            & > .bar {
              flex: 1;
              height: 6px;
              border-radius: 999px;
              background-color: #e5e7eb;
            }

            & > .strength-text {
              position: absolute;
              top: 0;
              right: -16px;
              transform: translateX(100%) translateY(-40%);
              font-size: 12px;
              color: #6b7280;
            }
          }
        }
      }

      & > .first,
      & > .second,
      & > .third {
        width: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        gap: 32px;

        & > .tip {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 10px;
          & > span {
            font-size: 12px;
          }
        }

        & .el-button {
          display: block;
          width: 100%;
          padding: 6px 24px;
          margin: 0 auto;
          margin-top: 40px;
          color: #fff;
          border-radius: 10px;
        }
      }

      & > .second {
        & :deep(.el-form-item__error) {
          margin-top: 16px;
          left: 50%;
          transform: translateX(-50%);
        }
        & .el-button {
          margin-top: 32px;
        }
      }
    }
  }
}
</style>
