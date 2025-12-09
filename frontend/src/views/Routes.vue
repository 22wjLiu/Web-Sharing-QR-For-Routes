<template>
  <div
    class="square-container"
    v-loading.fullscreen.lock="loading"
    element-loading-text="加载中..."
  >
    <main class="main-wrapper">
      <div class="header-section">
        <div class="search-wrapper">
          <el-input
            v-model="searchQuery"
            placeholder="搜索路线标题关键词..."
            class="custom-search"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <div v-if="paginatedList.length === 0" class="empty-state">
        <el-empty description="没有找到相关路线，换个词试试？" />
      </div>

      <div v-else class="route-grid" v-loading="loading">
        <div
          v-for="(route, index) in paginatedList"
          :key="route.id ?? `route-${index}`"
          class="route-card"
          @click="openRouteDetail(route)"
        >
          <div class="cover-wrapper">
            <img :src="route.coverUrl" class="cover-img" alt="Cover" />
          </div>

          <div class="card-body">
            <h3 class="card-title">{{ route.title }}</h3>

            <div class="card-footer">
              <div class="meta">
                <div class="author-info">
                  <el-avatar :size="18" :src="route.authorAvatar" />
                  <span class="author-name">{{ route.authorName }}</span>
                </div>
                <span class="date-text">{{ formatDate(route.createTime) }}</span>
              </div>

              <div class="fav-stat" :class="{ 'is-active': route.favorited }">
                <HeartButton
                  :class="{ liked: route.favorited }"
                  @click.stop="toggleFavorite(route)"
                />
                <span class="fav-count">{{ route.favoritesCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="totalRoutes"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="handlePageChange"
          hide-on-single-page
        />
      </div>
    </main>

    <RouteDetailDialog v-model="routeDialogVisible" :route="currentRoute" :show-status="false">
      <template #actions>
        <div class="action-btn favor-btn" @click.stop="handleDialogFavorite">
          <HeartButton :class="{ liked: currentRoute?.favorited }" />
          <span class="favor-count">{{ currentRoute?.favoritesCount ?? 0 }}</span>
        </div>
      </template>
    </RouteDetailDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { getRouteExploreList } from '@/api/route';
import { addFavorite, removeFavorite } from '@/api/favor';
import { useUserStore } from '@/store/user';
import type { RouteOverview } from '@/types/route';
import { buildAvatarUrl } from '@/utils/avatar';
import RouteDetailDialog from '@/components/RouteDetailDialog.vue';
import HeartButton from '@/components/HeartButton.vue';

type RouteItem = RouteOverview;

const userStore = useUserStore();

// --- 状态数据 ---
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(12);
const totalRoutes = ref(0);
const routeDialogVisible = ref(false);
const currentRoute = ref<RouteItem | null>(null);
const routes = ref<RouteItem[]>([]);
const loading = ref(false);

const paginatedList = computed(() => routes.value);

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return dateStr.split(' ')[0];
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchRoutes();
};

const handlePageChange = (val: number) => {
  currentPage.value = val;
  fetchRoutes();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const fetchRoutes = async () => {
  loading.value = true;
  try {
    const { data } = await getRouteExploreList(
      currentPage.value,
      pageSize.value,
      searchQuery.value.trim() || undefined,
    );
    const payload = data.data;
    const records = payload?.records ?? [];
    routes.value = records.map((item) => ({
      ...item,
      coverUrl: buildAvatarUrl(item.coverUrl),
      qrUrl: buildAvatarUrl(item.qrUrl),
      authorAvatar: buildAvatarUrl(item.authorAvatar),
      favoritesCount: Number(item.favoritesCount || 0),
      favorited: Boolean(item.favorited),
    }));
    totalRoutes.value = payload?.total ?? 0;
  } catch {
  } finally {
    loading.value = false;
  }
};

const openRouteDetail = (route: RouteItem) => {
  currentRoute.value = route;
  routeDialogVisible.value = true;
};

const toggleFavorite = async (route: RouteItem) => {
  if (!route.id) return;
  if (!userStore.token) {
    ElMessage.warning('请先登录后再收藏');
    return;
  }
  try {
    if (route.favorited) {
      await removeFavorite(route.id);
      route.favorited = false;
      route.favoritesCount = Math.max(0, (route.favoritesCount || 0) - 1);
      if (currentRoute.value?.id === route.id) {
        currentRoute.value.favorited = false;
        currentRoute.value.favoritesCount = route.favoritesCount;
      }
    } else {
      await addFavorite(route.id);
      route.favorited = true;
      route.favoritesCount = (route.favoritesCount || 0) + 1;
      if (currentRoute.value?.id === route.id) {
        currentRoute.value.favorited = true;
        currentRoute.value.favoritesCount = route.favoritesCount;
      }
    }
  } catch (error) {
    console.error(error);
  }
};

const handleDialogFavorite = async () => {
  if (!currentRoute.value) return;
  if (!userStore.token) {
    ElMessage.warning('请先登录后再收藏');
    return;
  }
  await toggleFavorite(currentRoute.value);
};

watch(
  () => userStore.token,
  () => {
    fetchRoutes();
  },
);

onMounted(() => {
  fetchRoutes();
});
</script>

<style scoped lang="less">
// 变量定义
@primary-color: #ff2442;
@text-main: #1f2937;
@text-secondary: #6b7280;

// 通用容器
.square-container {
  min-height: 100vh;
  padding-bottom: 40px;
  display: flex;
  flex-direction: column;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.main-wrapper {
  max-width: 1152px; // max-w-6xl
  margin: 0 auto;
  width: 100%;
  padding: 0 16px;
  flex: 1;
  padding-top: 48px;
}

// 头部区域
.header-section {
  text-align: center;
  margin-bottom: 40px;

  .search-wrapper {
    width: 100%;
    max-width: 576px; // max-w-xl
    margin: 0 auto;
    position: relative;
  }
}

// 搜索框自定义 (覆盖 Element Plus)
.custom-search {
  :deep(.el-input__wrapper) {
    border-radius: 9999px;
    background-color: #fff;
    box-shadow:
      0 4px 6px -1px rgba(0, 0, 0, 0.05),
      0 2px 4px -1px rgba(0, 0, 0, 0.03) !important;
    padding-left: 20px;
    padding-right: 20px;
    height: 48px;

    &.is-focus {
      box-shadow: 0 0 0 2px @primary-color inset !important;
    }
  }

  :deep(.el-input__inner) {
    font-size: 16px;
  }

  .search-icon {
    color: #9ca3af;
    font-size: 18px;
  }
}

// 空状态
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 0;
}

// 瀑布流网格
.route-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  @media (min-width: 768px) {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
  @media (min-width: 1024px) {
    grid-template-columns: repeat(4, 1fr);
  }
}

// 卡片样式
.route-card {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #f3f4f6;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow:
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -2px rgba(0, 0, 0, 0.05);

    .cover-img {
      transform: scale(1.05);
    }
  }

  .cover-wrapper {
    position: relative;
    aspect-ratio: 3 / 4;
    overflow: hidden;
    background-color: #f3f4f6;

    .cover-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s duration;
    }
  }

  .card-body {
    padding: 12px;
    display: flex;
    flex-direction: column;
    flex: 1;

    .card-title {
      font-weight: 700;
      color: @text-main;
      font-size: 14px;
      margin: 0 0 4px 0;
      line-height: 1.25;
      display: -webkit-box;
      line-clamp: 2;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      margin-top: auto;
      padding-top: 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .meta {
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .author-info {
        display: flex;
        align-items: center;
        gap: 6px;
        overflow: hidden;

        .author-name {
          font-size: 11px;
          color: @text-secondary;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .date-text {
        font-size: 10px;
        color: #d1d5db;
      }

      .fav-stat {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #9ca3af;
        transition: color 0.2s;
        cursor: pointer;

        &:hover {
          color: #4b5563;
        }

        &.is-active {
          color: #ef4444;
        }

        .heart-icon {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          width: 20px;
          height: 20px;
        }

        .fav-count {
          font-size: 11px;
        }
      }
    }
  }
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 48px;
  margin-bottom: 32px;

  :deep(.el-pagination.is-background .el-pager li.is-active) {
    background-color: @primary-color;
  }
}

// 弹窗样式
:deep(.detail-dialog .el-dialog__body) {
  padding: 0;
  height: 550px;
}
:deep(.detail-dialog .el-dialog__header) {
  display: none;
}

.detail-container {
  display: flex;
  flex-direction: column;
  height: 100%;

  @media (min-width: 768px) {
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

  @media (min-width: 768px) {
    width: 60%;
    height: 100%;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    background-color: rgba(0, 0, 0, 0.05);
    @media (min-width: 768px) {
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
    @media (min-width: 768px) {
      display: none;
    }
  }
}

.detail-right {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fff;

  @media (min-width: 768px) {
    width: 40%;
  }

  .detail-header {
    padding: 16px;
    border-bottom: 1px solid #f3f4f6;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;

    .header-user {
      display: flex;
      align-items: center;
      gap: 8px;
      .user-info {
        display: flex;
        flex-direction: column;
        .user-name {
          font-size: 14px;
          font-weight: 600;
          color: @text-main;
          line-height: 1;
        }
        .publish-time {
          font-size: 10px;
          color: #9ca3af;
          margin-top: 4px;
        }
      }
    }

    .close-btn {
      cursor: pointer;
      padding: 4px;
      border-radius: 50%;
      transition: background 0.3s;
      &:hover {
        background-color: #f3f4f6;
      }
    }
  }

  .detail-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    // 自定义滚动条
    &::-webkit-scrollbar {
      width: 4px;
    }
    &::-webkit-scrollbar-thumb {
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

    .detail-desc {
      color: #4b5563;
      font-size: 14px;
      white-space: pre-wrap;
      line-height: 1.6;
    }
  }

  .detail-footer {
    padding: 8px;
    border-top: 1px solid #f3f4f6;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .footer-actions {
      display: flex;
      gap: 16px;
      color: #4b5563;

      .action-btn {
        display: flex;
        align-items: center;
        gap: 4px;
        cursor: pointer;
        transition: transform 0.2s;

        &:hover {
          transform: scale(1.05);
        }

        &.fav-active {
          color: #ef4444;
          .btn-text {
            font-weight: 700;
          }
        }
        &.favor-btn:hover {
          color: #ef4444;
        }

        &.favor-btn {
          position: relative;
          padding: 6px 6px 6px 2px;
          border-radius: 999px;
          border: none;
          background: transparent;
          overflow: visible;

          .particle {
            position: absolute;
            top: 50%;
            left: 50%;
            width: 6px;
            height: 6px;
            background: #ef4444;
            border-radius: 999px;
            transform: translate(-50%, -50%) scale(0.2);
            opacity: 0;
            pointer-events: none;
          }

          &.bursting {
            .particle {
              animation: heart-burst 0.6s ease-out forwards;
            }
            .particle:nth-child(1) {
              --tx: -6px;
              --ty: -22px;
            }
            .particle:nth-child(2) {
              --tx: 8px;
              --ty: -20px;
            }
            .particle:nth-child(3) {
              --tx: -16px;
              --ty: -8px;
            }
            .particle:nth-child(4) {
              --tx: 16px;
              --ty: -10px;
            }
            .particle:nth-child(5) {
              --tx: -4px;
              --ty: 8px;
            }
            .particle:nth-child(6) {
              --tx: 10px;
              --ty: 10px;
            }
          }
        }

        .btn-text {
          font-size: 12px;
        }
        .heart-icon {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          width: 24px;
          height: 24px;
          opacity: 0.9;
        }
        .favor-count {
          font-size: 12px;
        }
      }
    }

    .qr-btn {
      padding: 0 20px;
      font-weight: 700;
      color: #4b5563;
      background-color: #f3f4f6;
      border: 1px solid transparent;
      &:hover {
        background-color: #e5e7eb;
      }
    }
  }
}

.qr-box {
  text-align: center;
  padding: 8px;
  img {
    width: 100%;
    aspect-ratio: 1/1;
    margin-bottom: 8px;
    background-color: #f9fafb;
  }
  p {
    font-size: 12px;
    color: #6b7280;
  }
}

@keyframes heart-burst {
  0% {
    transform: translate(-50%, -50%) scale(0.4);
    opacity: 0.9;
  }
  70% {
    transform: translate(calc(-50% + var(--tx, 0px)), calc(-50% + var(--ty, 0px))) scale(1);
    opacity: 0.7;
  }
  100% {
    transform: translate(calc(-50% + var(--tx, 0px)), calc(-50% + var(--ty, 0px))) scale(0.9);
    opacity: 0;
  }
}
</style>
