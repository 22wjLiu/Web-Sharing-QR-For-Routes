<template>
  <el-dialog
    :model-value="visible"
    width="850px"
    class="detail-dialog"
    :show-close="false"
    align-center
    destroy-on-close
    @close="emitClose"
  >
    <div class="detail-container" v-if="route">
      <div class="detail-left">
        <img :src="route.coverUrl" />
        <div class="mobile-close" @click="emitClose">
          <el-icon color="#fff" :size="20"><ArrowLeft /></el-icon>
        </div>
      </div>

      <div class="detail-right">
        <div class="detail-header">
          <div class="header-user">
            <el-avatar :size="32" :src="route.authorAvatar || ''" />
            <div class="user-info">
              <span class="user-name">{{ route.authorName || '未知作者' }}</span>
              <span class="publish-time">发布于 {{ formatDate(route.createTime) }}</span>
            </div>
          </div>
          <div class="close-btn" @click="emitClose">
            <el-icon :size="20" color="#999"><Close /></el-icon>
          </div>
        </div>

        <div class="detail-content custom-scroll">
          <h2 class="detail-title">{{ route.title }}</h2>
          <div v-if="showStatus && route.status != null" class="detail-status">
            <el-tag size="small" :type="statusType(route.status)" effect="plain">
              {{ statusText(route.status) }}
            </el-tag>
          </div>
          <p class="detail-desc">{{ route.description }}</p>
        </div>

        <div class="detail-footer">
          <div class="footer-actions">
            <slot name="actions" />
          </div>

          <el-popover placement="top-end" :width="200" trigger="hover">
            <template #reference>
              <el-button round :icon="Grid" class="qr-btn">查看二维码</el-button>
            </template>
            <div class="qr-box">
              <img :src="route.qrUrl" />
              <p>微信扫码查看详情</p>
            </div>
          </el-popover>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ArrowLeft, Close, Grid } from '@element-plus/icons-vue';
import type { PropType } from 'vue';
import type { RouteOverview } from '@/types/route';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  route: { type: Object as PropType<RouteOverview | null>, default: null },
  showStatus: { type: Boolean, default: false },
});

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void;
}>();

const visible = computed(() => props.modelValue);

const emitClose = () => emit('update:modelValue', false);

const statusText = (status?: number | null) => {
  const map: Record<number, string> = { 0: '审核中', 1: '已发布', 2: '已驳回' };
  return status != null && map[status] ? map[status] : '未知';
};

const statusType = (status?: number | null) => {
  if (status === 0) return 'primary';
  if (status === 1) return 'success';
  if (status === 2) return 'danger';
  return 'info';
};

const formatDate = (date?: string) => {
  if (!date) return '';
  return date.split(' ')[0] || date;
};
</script>

<style scoped>
:deep(.el-dialog__body) {
  padding: 0;
  height: 550px;
}
:deep(.el-dialog__header) {
  display: none;
}

.detail-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

@media (min-width: 768px) {
  .detail-container {
    flex-direction: row;
  }
}

.detail-left {
  width: 100%;
  height: 250px;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-radius: 4px;
  overflow: hidden;
}

@media (min-width: 768px) {
  .detail-left {
    width: 60%;
    height: 100%;
  }
}

.detail-left img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

@media (min-width: 768px) {
  .detail-left img {
    object-fit: cover;
  }
}

.mobile-close {
  position: absolute;
  top: 16px;
  left: 16px;
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 50%;
  padding: 4px;
  cursor: pointer;
}

@media (min-width: 768px) {
  .mobile-close {
    display: none;
  }
}

.detail-right {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

@media (min-width: 768px) {
  .detail-right {
    width: 40%;
  }
}

.detail-header {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  line-height: 1;
}

.publish-time {
  font-size: 10px;
  color: #9ca3af;
  margin-top: 4px;
}

.close-btn {
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
}

.close-btn:hover {
  background-color: #f3f4f6;
}

.detail-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.detail-content::-webkit-scrollbar {
  width: 4px;
}

.detail-content::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 4px;
}

.detail-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 12px;
  line-height: 1.4;
}

.detail-status {
  margin-bottom: 12px;
}

.detail-desc {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  white-space: pre-wrap;
}

.detail-footer {
  padding: 8px;
  border-top: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.footer-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.qr-box {
  text-align: center;
}

.qr-box img {
  width: 120px;
  height: 120px;
}

.qr-btn {
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #1f2937;
}
</style>
