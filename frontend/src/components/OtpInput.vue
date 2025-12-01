<template>
  <!-- 整个验证码区域 -->
  <div class="otp-input" @paste.prevent="handlePaste">
    <input
      v-for="(_, index) in props.length"
      :key="index"
      :ref="(el) => setInputRef(el as HTMLInputElement | null, index)"
      class="otp-input-item"
      type="text"
      inputmode="numeric"
      autocomplete="one-time-code"
      maxlength="1"
      :disabled="props.disabled"
      :value="values[index]"
      @input="(event) => handleInput(index, event)"
      @keydown="(event) => handleKeydown(index, event as KeyboardEvent)"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';

const props = withDefaults(
  defineProps<{
    modelValue: string;
    length?: number;
    autoFocus?: boolean;
    disabled?: boolean;
  }>(),
  {
    length: 4,
    autoFocus: false,
    disabled: false,
  },
);

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'complete', value: string): void;
}>();

// 每一格的值
const values = ref<string[]>(Array(props.length).fill(''));

// 把外部的 modelValue 同步到内部数组
const syncFromModel = (value: string) => {
  const chars = (value ?? '').split('').slice(0, props.length);
  values.value = Array.from({ length: props.length }, (_, i) => chars[i] ?? '');
};

syncFromModel(props.modelValue);

watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal !== values.value.join('')) {
      syncFromModel(newVal ?? '');
    }
  },
);

// 更新 v-model，并在填满时触发 complete
const updateModel = () => {
  const joined = values.value.join('');
  if (joined !== props.modelValue) {
    emit('update:modelValue', joined);
  }
  if (joined.length === props.length) {
    emit('complete', joined);
  }
};

// 管理每个 input 的 ref，方便控制焦点
const inputRefs = ref<HTMLInputElement[]>([]);

const setInputRef = (el: HTMLInputElement | null, index: number) => {
  if (el) {
    inputRefs.value[index] = el;
  }
};

const focusInput = (index: number) => {
  const el = inputRefs.value[index];
  if (el) {
    el.focus();
    el.select();
  }
};

// 输入事件：只保留最后一个数字，自动跳到下一格
const handleInput = (index: number, event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value;

  // 只保留数字
  value = value.replace(/\D/g, '');

  // 多输入时只取最后一个字符
  if (value.length > 1) {
    value = value.slice(-1);
  }

  values.value[index] = value;
  updateModel();

  // 有值就跳到下一格
  if (value && index < props.length - 1) {
    focusInput(index + 1);
  }
};

// 键盘事件：退格 / 左右移动
const handleKeydown = (index: number, event: KeyboardEvent) => {
  if (event.key === 'Backspace' || event.key === 'Delete') {
    event.preventDefault();
    if (values.value[index]) {
      // 当前格有值，先清空当前格
      values.value[index] = '';
      updateModel();
    } else if (index > 0) {
      // 当前格没值，跳回上一格并清空上一格
      focusInput(index - 1);
      values.value[index - 1] = '';
      updateModel();
    }
    return;
  }

  if (event.key === 'ArrowLeft' && index > 0) {
    event.preventDefault();
    focusInput(index - 1);
    return;
  }

  if (event.key === 'ArrowRight' && index < props.length - 1) {
    event.preventDefault();
    focusInput(index + 1);
  }
};

// 粘贴事件：一次性填充到各个格子
const handlePaste = (event: ClipboardEvent) => {
  const text = event.clipboardData?.getData('text') ?? '';
  const digits = text.replace(/\D/g, '').slice(0, props.length);
  const chars = digits.split('');

  values.value = Array.from({ length: props.length }, (_, i) => chars[i] ?? '');
  updateModel();

  // 焦点跳到最后一个有值的格子
  const lastIndex = Math.min(chars.length, props.length) - 1;
  if (lastIndex >= 0) {
    focusInput(lastIndex);
  }
};

onMounted(() => {
  if (props.autoFocus) {
    focusInput(0);
  }
});
</script>

<style scoped lang="less">
.otp-input {
  display: flex;
  gap: 12px;
}

.otp-input-item {
  width: 56px;
  height: 56px;
  text-align: center;
  font-size: 24px;
  border-radius: 12px;
  border: 1px solid var(--el-border-color, #dcdfe6);
  outline: none;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background-color 0.2s ease;
}

.otp-input-item:focus {
  border-color: var(--el-color-primary, #409eff);
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.2);
  background-color: #f5fbff;
}
</style>
