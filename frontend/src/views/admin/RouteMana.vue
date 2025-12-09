<template>
  <div class="content-card">
    <div class="toolbar">
      <h2 class="toolbar-title">二维码路线管理</h2>
      <div class="toolbar-actions">
        <el-input
          v-model="searchQR"
          placeholder="搜索标题"
          :prefix-icon="Search"
          class="search-input"
          clearable
        />
        <el-input v-model="creatorIdFilter" placeholder="创建人ID" class="id-input" clearable />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          range-separator="至"
          class="date-picker"
          clearable
        />
        <el-button type="danger" :disabled="!selectedQRs.length" @click="handleBatchDeleteQR">
          批量删除
        </el-button>
      </div>
    </div>

    <el-table
      v-loading="tableLoading"
      :data="qrList"
      style="width: 100%"
      class="custom-table"
      @selection-change="onQRSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" sortable />
      <el-table-column label="封面" width="120" align="center">
        <template #default="scope">
          <el-image
            v-if="scope.row.coverUrl"
            :src="buildAvatarUrl(scope.row.coverUrl)"
            :preview-src-list="[buildAvatarUrl(scope.row.coverUrl)]"
            fit="cover"
            class="cover-img"
            :lazy="true"
          />
          <span v-else class="text-secondary">—</span>
        </template>
      </el-table-column>
      <el-table-column label="路线标题" min-width="150">
        <template #default="scope">
          <div class="qr-title">{{ scope.row.title }}</div>
          <div class="qr-desc">{{ scope.row.description }}</div>
        </template>
      </el-table-column>
      <el-table-column label="二维码" min-width="200">
        <template #default="scope">
          <a :href="scope.row.qrUrl" target="_blank" class="qr-link">
            <el-image
              :src="buildAvatarUrl(scope.row.qrUrl)"
              :preview-src-list="[buildAvatarUrl(scope.row.qrUrl)]"
              fit="cover"
              class="qr-img"
              :lazy="true"
            />
          </a>
        </template>
      </el-table-column>
      <el-table-column prop="userId" label="创建人ID" width="100" align="center">
        <template #default="scope">
          <el-tag type="info" size="small">{{ scope.row.userId }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="scope">
          <el-tag :type="statusTypeMap[scope.row.status ?? 0]">
            {{ statusTextMap[scope.row.status ?? 0] ?? '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" sortable>
        <template #default="scope">
          <div class="text-secondary">{{ formatDateTime(scope.row.createTime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="success" size="small" @click="handleAudit(scope.row, 1)"
            >审核通过</el-button
          >
          <el-button link type="warning" size="small" @click="handleAudit(scope.row, 2)"
            >驳回</el-button
          >
          <el-button link type="danger" size="small" @click="handleDeleteQR(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <div class="table-footer">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :current-page="currentQRPage"
        :page-size="qrPageSize"
        :page-sizes="qrPageSizes"
        :total="totalRoutes"
        @current-change="handleQRPageChange"
        @size-change="handleQRPageSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { getRouteList, updateRoute, deleteRoutes } from '@/api/route';
import type { RouteRecord } from '@/types/route';
import { useUserStore } from '@/store/user';
import { buildAvatarUrl } from '@/utils/avatar';

const userStore = useUserStore();

const searchQR = ref<string>('');
const creatorIdFilter = ref<string>('');
const dateRange = ref<[Date, Date] | null>(null);
const selectedQRs = ref<RouteRecord[]>([]);
const currentQRPage = ref(1);
const qrPageSize = ref(10);
const qrPageSizes = [5, 10, 20, 50];
const totalRoutes = ref(0);
const tableLoading = ref(false);
const qrList = ref<RouteRecord[]>([]);
const statusTextMap: Record<number, string> = {
  0: '待审核',
  1: '已通过',
  2: '已驳回',
};
const statusTypeMap: Record<number, 'info' | 'warning' | 'success' | 'danger'> = {
  0: 'warning',
  1: 'success',
  2: 'danger',
};

const formatDateTime = (date?: Date | string | null) => {
  if (!date) return '';
  const target = typeof date === 'string' ? new Date(date.replace(/-/g, '/')) : date;
  if (Number.isNaN(target.getTime())) return '';
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${target.getFullYear()}-${pad(target.getMonth() + 1)}-${pad(target.getDate())} ${pad(target.getHours())}:${pad(target.getMinutes())}:${pad(target.getSeconds())}`;
};

const fetchRoutes = async () => {
  tableLoading.value = true;
  try {
    const [start, end] = (dateRange.value ?? []) as [Date | undefined, Date | undefined];
    const userIdFilter = creatorIdFilter.value.trim();
    const parsedUserId = userIdFilter ? Number(userIdFilter) : undefined;
    const { data } = await getRouteList({
      page: currentQRPage.value,
      pageSize: qrPageSize.value,
      keyword: searchQR.value.trim() || undefined,
      userId: Number.isNaN(parsedUserId ?? NaN) ? undefined : parsedUserId,
      startTime: start ? formatDateTime(start) : undefined,
      endTime: end ? formatDateTime(end) : undefined,
    });
    const payload = data.data;
    const records = payload?.records ?? [];
    qrList.value = records.map((record) => ({
      id: record.id ?? null,
      title: record.title,
      description: record.description ?? '',
      qrUrl: record.qrUrl || '',
      coverUrl: record.coverUrl || '',
      createTime: record.createTime || '',
      isDeleted: record.isDeleted,
      userId: record.userId,
      status: record.status,
    }));
    totalRoutes.value = payload?.total ?? 0;
    selectedQRs.value = [];
  } catch {
  } finally {
    tableLoading.value = false;
  }
};

const handleDeleteQR = (row: RouteRecord) => {
  if (row.id == null) return;
  ElMessageBox.confirm(`确定要删除路线 "${row.title}" 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteRoutes({ ids: [row.id].filter((id): id is number => typeof id === 'number') });
      ElMessage.success('已删除');
      await fetchRoutes();
    } catch {}
  });
};

const onQRSelectionChange = (selection: RouteRecord[]) => {
  selectedQRs.value = selection;
};

const handleBatchDeleteQR = () => {
  if (!selectedQRs.value.length) return;
  ElMessageBox.confirm(`确定删除选中的 ${selectedQRs.value.length} 条路线吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const ids = selectedQRs.value
      .map((record) => record.id)
      .filter((id): id is number => typeof id === 'number');
    if (!ids.length) return;
    try {
      await deleteRoutes({ ids });
      selectedQRs.value = [];
      ElMessage.success('已删除所选路线');
      await fetchRoutes();
    } catch {}
  });
};

const handleQRPageChange = (page: number) => {
  currentQRPage.value = page;
  fetchRoutes();
};

const handleQRPageSizeChange = (size: number) => {
  qrPageSize.value = size;
  currentQRPage.value = 1;
  fetchRoutes();
};

const handleAudit = async (row: RouteRecord, status: number) => {
  if (row.id == null) {
    ElMessage.error('缺少路线 ID');
    return;
  }
  try {
    await updateRoute({
      id: row.id,
      title: row.title,
      qrUrl: row.qrUrl,
      coverUrl: row.coverUrl,
      description: row.description,
      status,
    });
    ElMessage.success(status === 1 ? '审核通过' : '已驳回');
    await fetchRoutes();
  } catch {}
};

watch([searchQR, creatorIdFilter, dateRange], () => {
  currentQRPage.value = 1;
  fetchRoutes();
});

onMounted(() => {
  fetchRoutes();
});
</script>

<style scoped>
.content-card {
  background-color: #ffffff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}
.toolbar-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.toolbar-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}
.search-input {
  width: 240px;
}
.id-input {
  width: 150px;
}
.date-picker {
  width: 260px;
}
.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.qr-title {
  font-weight: 500;
  color: #303133;
}
.qr-desc {
  font-size: 12px;
  color: #909399;
}
.qr-link {
  display: inline-block;
}
.qr-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}
.cover-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}
.text-secondary {
  color: #909399;
  font-size: 14px;
}
.mr-1 {
  margin-right: 4px;
}
</style>
