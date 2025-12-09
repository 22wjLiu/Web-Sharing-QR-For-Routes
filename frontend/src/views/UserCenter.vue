<template>
  <div class="app-container">
    <div class="profile-header">
      <div class="header-inner">
        <div class="avatar-wrapper">
          <el-avatar :size="100" :src="user.avatarUrl" class="user-avatar">
            <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
          </el-avatar>
          <div class="role-badge">
            <el-icon v-if="user.role === 1" color="#E6A23C" :size="16"><Management /></el-icon>
            <el-icon v-else color="#409EFF" :size="16"><User /></el-icon>
          </div>
        </div>

        <div class="info-content">
          <div class="name-row">
            <h1 class="user-name">{{ user.name }}</h1>
            <el-tag size="small" :type="user.role === 1 ? 'warning' : ''" effect="light" round>
              {{ user.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </div>

          <p class="user-id">ID: {{ user.id }} | {{ user.email }}</p>
          <p class="user-desc">{{ user.description || '这个人很懒，什么都没有写~' }}</p>

          <div class="action-buttons">
            <el-button round size="small" @click="openEditProfile">编辑资料</el-button>
            <el-button round size="small" plain @click="openChangePassword">修改密码</el-button>
          </div>
        </div>

        <div class="stats-box">
          <div class="stat-item">
            <div class="stat-num">{{ myRoutes.length }}</div>
            <div class="stat-label">发布</div>
          </div>
          <div class="stat-item">
            <div class="stat-num">{{ favRoutes.length }}</div>
            <div class="stat-label">收藏</div>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="tabs-header">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="我的发布" name="published">
            <template #label>
              <div class="tab-label">
                <el-icon><Position /></el-icon><span>我的发布</span>
              </div>
            </template>
          </el-tab-pane>
          <el-tab-pane label="我的收藏" name="favorites">
            <template #label>
              <div class="tab-label">
                <el-icon><Star /></el-icon><span>收藏</span>
              </div>
            </template>
          </el-tab-pane>
        </el-tabs>

        <div v-if="activeTab === 'published'" class="add-btn-wrapper">
          <el-button type="primary" round :icon="Plus" size="small" @click="openAddRoute">
            发布新路线
          </el-button>
        </div>
      </div>

      <el-empty v-if="currentList.length === 0" description="暂无数据"></el-empty>

      <div v-else>
        <div class="route-grid">
          <div
            v-for="(route, index) in paginatedList"
            :key="route.id ?? `route-${index}`"
            class="route-card"
            @click="openRouteDetail(route)"
          >
            <div
              v-if="activeTab === 'published'"
              class="delete-overlay"
              @click.stop="confirmDelete(route)"
            >
              <el-icon :size="14"><Delete /></el-icon>
            </div>

            <div class="cover-wrapper">
              <img :src="route.coverUrl" class="cover-img" alt="Cover" />
              <div
                v-if="activeTab === 'published'"
                class="status-badge"
                :class="getStatusClass(route.status ?? 0)"
              >
                {{ getStatusText(route.status ?? 0) }}
              </div>
            </div>

            <div class="card-body">
              <h3 class="card-title">{{ route.title }}</h3>
              <p class="card-desc">{{ route.description }}</p>
              <div class="card-footer">
                <div class="author-box">
                  <el-avatar :size="16" :src="user.avatarUrl"></el-avatar>
                  <span class="author-name">{{ user.name }}</span>
                </div>
                <span class="date-text">{{ formatDate(route.createTime || '') }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="currentList.length"
            :page-size="pageSize"
            v-model:current-page="currentPage"
            @current-change="handlePageChange"
            hide-on-single-page
          />
        </div>
      </div>
    </div>

    <RouteDetailDialog
      v-model="routeDialogVisible"
      :route="currentRoute"
      :show-status="activeTab === 'published'"
    >
      <template #actions>
        <el-button
          v-if="activeTab === 'published' && currentRoute"
          class="edit-btn"
          type="info"
          plain
          size="small"
          @click="openEditRoute"
        >
          编辑
        </el-button>
        <div v-if="activeTab === 'favorites' && currentRoute" class="action-btn favor-btn">
          <HeartButton
            :class="{ liked: currentRoute?.favorited }"
            @click.stop="handleDialogFavorite"
          />
          <span class="favor-count">{{ currentRoute?.favoritesCount ?? 0 }}</span>
        </div>
        <div v-else-if="activeTab === 'published' && currentRoute" class="action-btn">
          <el-button
            class="edit-btn del-btn"
            type="info"
            plain
            size="small"
            @click="confirmDelete(currentRoute)"
          >
            删除
          </el-button>
        </div>
      </template>
    </RouteDetailDialog>

    <el-dialog v-model="editRouteDialogVisible" title="编辑路线" width="500px" class="form-dialog">
      <el-form :model="editRouteForm" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="editRouteForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editRouteForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="avatar-row">
            <el-upload
              class="upload-card"
              :show-file-list="false"
              :http-request="handleEditCoverUpload"
              :accept="imageAccept"
              :before-upload="beforeAvatarUpload"
              :disabled="editCoverUploading"
            >
              <template #trigger>
                <div class="upload-trigger" :class="{ uploading: editCoverUploading }">
                  <img
                    v-if="editRouteForm.coverUrl"
                    :src="buildAvatarUrl(editRouteForm.coverUrl)"
                    class="upload-img"
                    alt="cover"
                  />
                  <div v-else class="upload-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传封面</span>
                  </div>
                  <div v-if="editCoverUploading" class="avatar-loading">上传中...</div>
                </div>
              </template>
            </el-upload>
            <el-button
              v-if="editRouteForm.coverUrl"
              link
              type="danger"
              @click="editRouteForm.coverUrl = ''"
            >
              移除
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="二维码图片">
          <div class="avatar-row">
            <el-upload
              class="upload-card"
              :show-file-list="false"
              :http-request="handleEditQrUpload"
              :accept="imageAccept"
              :before-upload="beforeAvatarUpload"
              :disabled="editQrUploading"
            >
              <template #trigger>
                <div class="upload-trigger" :class="{ uploading: editQrUploading }">
                  <img
                    v-if="editRouteForm.qrUrl"
                    :src="buildAvatarUrl(editRouteForm.qrUrl)"
                    class="upload-img"
                    alt="qr"
                  />
                  <div v-else class="upload-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传二维码</span>
                  </div>
                  <div v-if="editQrUploading" class="avatar-loading">上传中...</div>
                </div>
              </template>
            </el-upload>
            <el-button
              v-if="editRouteForm.qrUrl"
              link
              type="danger"
              @click="editRouteForm.qrUrl = ''"
            >
              移除
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editRouteDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditRoute">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="addRouteDialogVisible" title="发布新路线" width="500px" class="form-dialog">
      <el-form :model="addRouteForm" label-position="top">
        <el-form-item label="标题">
          <el-input
            v-model="addRouteForm.title"
            placeholder="给你的路线起个吸引人的名字"
          ></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="addRouteForm.description"
            type="textarea"
            :rows="4"
            placeholder="分享你的戏曲路线故事..."
          ></el-input>
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="avatar-row">
            <el-upload
              class="upload-card"
              :show-file-list="false"
              :http-request="handleCoverUpload"
              :accept="imageAccept"
              :before-upload="beforeAvatarUpload"
              :disabled="coverUploading"
            >
              <template #trigger>
                <div class="upload-trigger" :class="{ uploading: coverUploading }">
                  <img
                    v-if="addRouteForm.coverUrl"
                    :src="buildAvatarUrl(addRouteForm.coverUrl)"
                    class="upload-img"
                    alt="cover"
                  />
                  <div v-else class="upload-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传封面</span>
                  </div>
                  <div v-if="coverUploading" class="avatar-loading">上传中...</div>
                </div>
              </template>
            </el-upload>
            <el-button
              v-if="addRouteForm.coverUrl"
              link
              type="danger"
              @click="addRouteForm.coverUrl = ''"
            >
              移除
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="二维码图片">
          <div class="avatar-row">
            <el-upload
              class="upload-card"
              :show-file-list="false"
              :http-request="handleQrUpload"
              :accept="imageAccept"
              :before-upload="beforeAvatarUpload"
              :disabled="qrUploading"
            >
              <template #trigger>
                <div class="upload-trigger" :class="{ uploading: qrUploading }">
                  <img
                    v-if="addRouteForm.qrUrl"
                    :src="buildAvatarUrl(addRouteForm.qrUrl)"
                    class="upload-img"
                    alt="qr"
                  />
                  <div v-else class="upload-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传二维码</span>
                  </div>
                  <div v-if="qrUploading" class="avatar-loading">上传中...</div>
                </div>
              </template>
            </el-upload>
            <el-button
              v-if="addRouteForm.qrUrl"
              link
              type="danger"
              @click="addRouteForm.qrUrl = ''"
            >
              移除
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addRouteDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewRoute">立即发布</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑个人信息" width="500px" class="form-dialog">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="头像">
          <div class="avatar-row">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :http-request="handleAvatarUpload"
              :accept="imageAccept"
              :before-upload="beforeAvatarUpload"
              :disabled="avatarUploading"
            >
              <template #trigger>
                <div class="avatar-trigger" :class="{ uploading: avatarUploading }">
                  <img
                    v-if="editForm.avatarUrl"
                    :src="buildAvatarUrl(editForm.avatarUrl)"
                    class="avatar-img"
                    alt="avatar"
                  />
                  <div v-else class="avatar-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传头像</span>
                  </div>
                  <div v-if="avatarUploading" class="avatar-loading">上传中...</div>
                </div>
              </template>
            </el-upload>
            <el-button v-if="editForm.avatarUrl" link type="danger" @click="handleAvatarRemove">
              移除
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.name" maxlength="20" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px" class="form-dialog">
      <el-form :model="passwordForm" label-position="top">
        <el-form-item label="旧密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePassword">确认修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Management, Position, Star, Delete, Plus } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';
import { buildAvatarUrl } from '@/utils/avatar';
import {
  getRoutesByUser,
  getFavoriteRoutes,
  addRoute,
  deleteRoutes,
  updateRoute,
} from '@/api/route';
import { updatePassword, updateUser } from '@/api/user';
import { removeFavorite } from '@/api/favor';
import { uploadStaticResource } from '@/api/resource';
import type { RouteOverview, RouteRecord } from '@/types/route';
import RouteDetailDialog from '@/components/RouteDetailDialog.vue';
import HeartButton from '@/components/HeartButton.vue';
import type { UploadRequestOptions } from 'element-plus';

type RouteItem = RouteOverview;

const userStore = useUserStore();
const user = computed(
  () =>
    userStore.profile || { id: 0, name: '', email: '', avatarUrl: '', description: '', role: 0 },
);

const myRoutes = ref<RouteItem[]>([]);
const favRoutes = ref<RouteItem[]>([]);
const avatarUploading = ref(false);
const coverUploading = ref(false);
const qrUploading = ref(false);
const editCoverUploading = ref(false);
const editQrUploading = ref(false);
const imageAccept = 'image/png, image/jpeg, image/jpg, image/gif, image/webp';
type UploadError = Error & { status?: number; method?: string; url?: string };

const activeTab = ref('published');
const editDialogVisible = ref(false);
const passwordDialogVisible = ref(false);
const routeDialogVisible = ref(false);
const addRouteDialogVisible = ref(false);
const editRouteDialogVisible = ref(false);
const currentRoute = ref<RouteItem | null>(null);

const currentPage = ref(1);
const pageSize = ref(8);

const editForm = reactive({
  avatarUrl: '',
  name: '',
  email: '',
  description: '',
});
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' });
const addRouteForm = reactive({ title: '', description: '', coverUrl: '', qrUrl: '' });
const editRouteForm = reactive({
  id: 0,
  title: '',
  description: '',
  coverUrl: '',
  qrUrl: '',
  status: 0,
});

const currentList = computed(() => {
  return activeTab.value === 'published' ? myRoutes.value : favRoutes.value;
});

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return currentList.value.slice(start, end);
});

watch(activeTab, () => {
  currentPage.value = 1;
  if (activeTab.value === 'favorites' && favRoutes.value.length === 0) {
    fetchFavoriteRoutes();
  }
  if (activeTab.value === 'published' && myRoutes.value.length === 0) {
    fetchMyRoutes();
  }
});

watch(
  () => userStore.profile,
  (profile) => {
    if (profile) {
      editForm.avatarUrl = profile.avatarUrl;
      editForm.name = profile.name;
      editForm.email = profile.email;
      editForm.description = profile.description || '';
    }
  },
  { immediate: true },
);

const handlePageChange = (val: number) => {
  currentPage.value = val;
};

const openRouteDetail = (route: RouteItem) => {
  currentRoute.value = { ...route };
  routeDialogVisible.value = true;
};

const openEditProfile = () => {
  if (userStore.profile) {
    Object.assign(editForm, {
      avatarUrl: userStore.profile.avatarUrl,
      name: userStore.profile.name,
      email: userStore.profile.email,
      description: userStore.profile.description || '',
    });
  }
  editDialogVisible.value = true;
};

const openChangePassword = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordDialogVisible.value = true;
};

const openAddRoute = () => {
  addRouteForm.title = '';
  addRouteForm.description = '';
  addRouteForm.coverUrl = '';
  addRouteForm.qrUrl = '';
  addRouteDialogVisible.value = true;
};

const openEditRoute = () => {
  if (!currentRoute.value) return;
  Object.assign(editRouteForm, {
    id: currentRoute.value.id,
    title: currentRoute.value.title,
    description: currentRoute.value.description,
    coverUrl: currentRoute.value.coverUrl,
    qrUrl: currentRoute.value.qrUrl,
    status: currentRoute.value.status ?? 0,
  });
  editRouteDialogVisible.value = true;
};

const submitNewRoute = async () => {
  if (!addRouteForm.title || !addRouteForm.description) {
    ElMessage.warning('标题和描述不能为空');
    return;
  }
  if (!addRouteForm.qrUrl) {
    ElMessage.warning('请上传二维码图片');
    return;
  }

  if (!user.value.id) {
    ElMessage.error('请先登录');
    return;
  }

  try {
    await addRoute({
      title: addRouteForm.title,
      description: addRouteForm.description,
      coverUrl: addRouteForm.coverUrl || undefined,
      qrUrl: addRouteForm.qrUrl,
      userId: user.value.id,
    });
    ElMessage.success('发布成功，等待审核');
    addRouteDialogVisible.value = false;
    await fetchMyRoutes();
  } catch {}
};

const submitEditRoute = async () => {
  if (!editRouteForm.id) {
    ElMessage.error('缺少路线 ID');
    return;
  }
  if (!editRouteForm.title || !editRouteForm.description || !editRouteForm.qrUrl) {
    ElMessage.warning('标题、描述和二维码不能为空');
    return;
  }
  try {
    await updateRoute({
      id: editRouteForm.id,
      title: editRouteForm.title,
      description: editRouteForm.description,
      coverUrl: editRouteForm.coverUrl || undefined,
      qrUrl: editRouteForm.qrUrl,
      status: editRouteForm.status ?? null,
    });
    ElMessage.success('路线更新成功');
    editRouteDialogVisible.value = false;
    await fetchMyRoutes();
    if (currentRoute.value && currentRoute.value.id === editRouteForm.id) {
      currentRoute.value = mapRoute({ ...currentRoute.value, ...editRouteForm });
    }
  } catch {}
};

const confirmDelete = (route: RouteItem) => {
  ElMessageBox.confirm('确定要删除这条路线吗？删除后无法恢复。', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      if (!route.id) return;
      await deleteRoutes({ ids: [route.id] });
      await fetchMyRoutes();
      routeDialogVisible.value = false;
      ElMessage.success('删除成功');
    })
    .catch(() => {});
};

const handleDialogFavorite = async () => {
  if (!currentRoute.value) return;
  if (!currentRoute.value.id) return;
  await removeFavorite(currentRoute.value.id);
  await fetchFavoriteRoutes();
  routeDialogVisible.value = false;
  ElMessage.success('已取消收藏');
};

const saveProfile = async () => {
  if (!user.value.id) {
    ElMessage.error('请先登录');
    return;
  }
  try {
    await updateUser({
      id: user.value.id,
      name: editForm.name,
      email: editForm.email,
      avatarUrl: editForm.avatarUrl,
      description: editForm.description,
    });
    userStore.updateProfile({
      name: editForm.name,
      email: editForm.email,
      avatarUrl: editForm.avatarUrl,
      description: editForm.description,
    });
    editDialogVisible.value = false;
    ElMessage.success('个人信息更新成功');
  } catch {
    ElMessage.error('个人信息更新失败');
  }
};

const savePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('密码不能为空');
    return;
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致');
    return;
  }
  if (!user.value.id) {
    ElMessage.error('请先登录');
    return;
  }
  try {
    await updatePassword({
      oldPassWord: passwordForm.oldPassword,
      newPassWord: passwordForm.newPassword,
    });
    passwordDialogVisible.value = false;
    ElMessage.success('密码修改成功');
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  } catch {
    ElMessage.error('密码修改失败');
  }
};

const getStatusText = (status?: number | null) => {
  const map: Record<number, string> = { 0: '审核中', 1: '已发布', 2: '已驳回' };
  return map[status ?? -1] ?? '未知';
};

const getStatusClass = (status?: number | null) => {
  if (status === 0) return 'status-blue';
  if (status === 1) return 'status-green';
  if (status === 2) return 'status-red';
  return 'status-gray';
};

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return dateStr.split(' ')[0];
};

const beforeAvatarUpload = (rawFile: File) => {
  const isImage = rawFile.type.startsWith('image/');
  const isLt2M = rawFile.size / 1024 / 1024 < 2;
  if (!isImage) {
    ElMessage.error('请上传图片文件');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB');
    return false;
  }
  return true;
};

const handleAvatarUpload = async (options: {
  file: File;
  onError?: (err: Error) => void;
  onSuccess?: (res: unknown) => void;
}) => {
  if (!options.file) return;
  avatarUploading.value = true;
  try {
    const { data } = await uploadStaticResource(options.file as File, 'avatar');
    const path = data.data?.path || '';
    if (path) {
      editForm.avatarUrl = path;
      ElMessage.success('头像上传成功');
      options.onSuccess?.(data);
    } else {
      throw new Error('上传失败');
    }
  } catch (error) {
    ElMessage.error('头像上传失败');
    options.onError?.(error as Error);
  } finally {
    avatarUploading.value = false;
  }
};

const handleAvatarRemove = () => {
  editForm.avatarUrl = '';
};

const handleCoverUpload = async (options: UploadRequestOptions) => {
  if (!options.file) return;
  coverUploading.value = true;
  try {
    const { data } = await uploadStaticResource(options.file as File, 'routecover');
    const path = data.data?.path || '';
    if (path) {
      addRouteForm.coverUrl = path;
      ElMessage.success('封面上传成功');
      options.onSuccess?.(data);
    } else {
      throw new Error('上传失败');
    }
  } catch (error) {
    ElMessage.error('封面上传失败');
    const uploadError: UploadError = Object.assign(
      new Error((error as Error)?.message || '上传失败'),
      { status: 500, method: 'POST', url: '/resource/upload' },
    );
    options.onError?.(uploadError as never);
  } finally {
    coverUploading.value = false;
  }
};

const handleQrUpload = async (options: UploadRequestOptions) => {
  if (!options.file) return;
  qrUploading.value = true;
  try {
    const { data } = await uploadStaticResource(options.file as File, 'qr');
    const path = data.data?.path || '';
    if (path) {
      addRouteForm.qrUrl = path;
      ElMessage.success('二维码上传成功');
      options.onSuccess?.(data);
    } else {
      throw new Error('上传失败');
    }
  } catch (error) {
    ElMessage.error('二维码上传失败');
    const uploadError: UploadError = Object.assign(
      new Error((error as Error)?.message || '上传失败'),
      { status: 500, method: 'POST', url: '/resource/upload' },
    );
    options.onError?.(uploadError as never);
  } finally {
    qrUploading.value = false;
  }
};

const handleEditCoverUpload = async (options: UploadRequestOptions) => {
  if (!options.file) return;
  editCoverUploading.value = true;
  try {
    const { data } = await uploadStaticResource(options.file as File, 'routecover');
    const path = data.data?.path || '';
    if (path) {
      editRouteForm.coverUrl = path;
      ElMessage.success('封面上传成功');
      options.onSuccess?.(data);
    } else {
      throw new Error('上传失败');
    }
  } catch (error) {
    ElMessage.error('封面上传失败');
    const uploadError: UploadError = Object.assign(
      new Error((error as Error)?.message || '上传失败'),
      { status: 500, method: 'POST', url: '/resource/upload' },
    );
    options.onError?.(uploadError as never);
  } finally {
    editCoverUploading.value = false;
  }
};

const handleEditQrUpload = async (options: UploadRequestOptions) => {
  if (!options.file) return;
  editQrUploading.value = true;
  try {
    const { data } = await uploadStaticResource(options.file as File, 'qr');
    const path = data.data?.path || '';
    if (path) {
      editRouteForm.qrUrl = path;
      ElMessage.success('二维码上传成功');
      options.onSuccess?.(data);
    } else {
      throw new Error('上传失败');
    }
  } catch (error) {
    ElMessage.error('二维码上传失败');
    const uploadError: UploadError = Object.assign(
      new Error((error as Error)?.message || '上传失败'),
      { status: 500, method: 'POST', url: '/resource/upload' },
    );
    options.onError?.(uploadError as never);
  } finally {
    editQrUploading.value = false;
  }
};

const mapRoute = (route: RouteRecord | RouteOverview): RouteItem => {
  const overview: RouteItem = {
    id: route.id ?? 0,
    title: route.title,
    description: route.description ?? '',
    coverUrl: buildAvatarUrl(route.coverUrl),
    qrUrl: buildAvatarUrl(route.qrUrl),
    createTime: route.createTime || '',
    status: route.status ?? 0,
    authorName: user.value.name,
    authorAvatar: user.value.avatarUrl,
    favoritesCount:
      'favoritesCount' in route && route.favoritesCount !== undefined
        ? Number(route.favoritesCount)
        : 0,
    favorited: 'favorited' in route ? Boolean(route.favorited) : false,
    userId: route.userId,
  };

  if ('authorName' in route && route.authorName) {
    overview.authorName = route.authorName;
  }
  if ('authorAvatar' in route && route.authorAvatar) {
    overview.authorAvatar = buildAvatarUrl(route.authorAvatar);
  }

  return overview;
};

const fetchMyRoutes = async () => {
  if (!user.value.id) return;
  const { data } = await getRoutesByUser(user.value.id, 1, 1000);
  const records = data.data?.records ?? [];
  myRoutes.value = records.map(mapRoute);
};

const fetchFavoriteRoutes = async () => {
  if (!user.value.id) return;
  const { data } = await getFavoriteRoutes(user.value.id, 1, 1000);
  const records = data.data?.records ?? [];
  favRoutes.value = records.map(mapRoute);
};

const ensureProfile = async () => {
  if (!userStore.profile) {
    await userStore.fetchProfile();
  }
};

onMounted(async () => {
  await ensureProfile();
  await Promise.all([fetchMyRoutes(), fetchFavoriteRoutes()]);
});
</script>

<style scoped lang="less">
// 变量定义
@primary-color: #ff2442;
@text-main: #1f2937;
@text-secondary: #6b7280;
@bg-page: #f8f8f8;

// 通用样式
.app-container {
  min-height: 100vh;
  padding-bottom: 40px;
  background-color: @bg-page;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

// 头部区域
.profile-header {
  background-color: #fff;
  padding: 48px 16px 32px;
  margin-bottom: 16px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);

  .header-inner {
    max-width: 896px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 24px;

    @media (min-width: 768px) {
      flex-direction: row;
      align-items: flex-start;
    }
  }

  .avatar-wrapper {
    position: relative;
    cursor: pointer;

    .user-avatar {
      border: 4px solid #fff;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }

    .role-badge {
      position: absolute;
      bottom: 0;
      right: 0;
      background-color: #fff;
      border-radius: 50%;
      padding: 4px;
      box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
      border: 1px solid #f3f4f6;
      display: flex;
    }
  }

  .info-content {
    flex: 1;
    text-align: center;

    @media (min-width: 768px) {
      text-align: left;
    }

    .name-row {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      justify-content: center;

      @media (min-width: 768px) {
        flex-direction: row;
        justify-content: flex-start;
      }

      .user-name {
        font-size: 24px;
        font-weight: 700;
        color: @text-main;
        margin: 0;
      }
    }

    .user-id {
      font-size: 14px;
      color: @text-secondary;
      font-family: monospace;
      margin-bottom: 8px;
    }

    .user-desc {
      color: #4b5563;
      font-size: 14px;
      margin-bottom: 16px;
      max-width: 500px;
      margin-left: auto;
      margin-right: auto;
      @media (min-width: 768px) {
        margin-left: 0;
      }
    }

    .action-buttons {
      display: flex;
      gap: 4px;
      justify-content: center;
      @media (min-width: 768px) {
        justify-content: flex-start;
      }
    }
  }

  .stats-box {
    display: flex;
    gap: 24px;
    text-align: center;
    color: #374151;

    .stat-num {
      font-weight: 700;
      font-size: 18px;
    }
    .stat-label {
      font-size: 12px;
      color: #9ca3af;
    }
  }
}

// 主内容区域
.main-content {
  max-width: 896px;
  margin: 0 auto;
  padding: 0 8px;
  position: relative;
}

.tabs-header {
  position: relative;
  margin-bottom: 16px;

  .add-btn-wrapper {
    position: absolute;
    right: 8px;
    top: 0;
    z-index: 10;
  }
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 4px;
}

// Element Plus 覆盖样式 (使用 :deep)
:deep(.el-tabs__nav-wrap::after) {
  background-color: transparent !important;
}
:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  color: #999;

  &.is-active {
    color: #333;
    font-weight: 700;
    font-size: 18px;
  }
}
:deep(.el-tabs__active-bar) {
  background-color: @primary-color;
  height: 3px;
  border-radius: 2px;
}
:deep(.el-button--primary) {
  --el-button-bg-color: @primary-color;
  --el-button-border-color: @primary-color;
  --el-button-hover-bg-color: darken(@primary-color, 10%);
  --el-button-hover-border-color: darken(@primary-color, 10%);
}

// 路线网格列表
.route-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  @media (min-width: 768px) {
    grid-template-columns: repeat(3, 1fr);
  }
  @media (min-width: 1024px) {
    grid-template-columns: repeat(4, 1fr);
  }
}

// 卡片样式
.route-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #f3f4f6;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);

    .delete-overlay {
      display: flex;
    }
    .cover-img {
      transform: scale(1.05);
    }
  }

  .delete-overlay {
    display: none;
    position: absolute;
    top: 8px;
    right: 8px;
    z-index: 20;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    backdrop-filter: blur(4px);
    align-items: center;
    justify-content: center;
    transition: background-color 0.2s;

    &:hover {
      background-color: #ef4444;
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
      transition: transform 0.5s;
    }

    .status-badge {
      position: absolute;
      top: 8px;
      left: 8px;
      font-size: 12px;
      padding: 2px 6px;
      border-radius: 4px;
      color: white;
      backdrop-filter: blur(12px);
      box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);

      &.status-blue {
        background-color: rgba(59, 130, 246, 0.8);
      }
      &.status-green {
        background-color: rgba(34, 197, 94, 0.8);
      }
      &.status-red {
        background-color: rgba(239, 68, 68, 0.8);
      }
      &.status-gray {
        background-color: #9ca3af;
      }
    }
  }

  .card-body {
    padding: 12px;

    .card-title {
      font-weight: 700;
      color: @text-main;
      font-size: 14px;
      margin: 0 0 4px 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .card-desc {
      font-size: 12px;
      color: @text-secondary;
      margin-bottom: 8px;
      display: -webkit-box;
      line-clamp: 2;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 2.5em;
      line-height: 1.6;
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 8px;

      .author-box {
        display: flex;
        align-items: center;
        gap: 4px;
        .author-name {
          font-size: 10px;
          color: #9ca3af;
          width: 48px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .date-text {
        font-size: 10px;
        color: #d1d5db;
      }
    }
  }
}

// 分页样式
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  margin-bottom: 16px;

  :deep(.el-pagination.is-background .el-pager li.is-active) {
    background-color: @primary-color;
  }
}

// 详情弹窗
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
        .user-text {
          display: flex;
          flex-direction: column;
          .name {
            font-size: 14px;
            font-weight: 600;
            color: @text-main;
            line-height: 1;
          }
          .sub {
            font-size: 10px;
            color: #9ca3af;
            margin-top: 4px;
          }
        }
      }

      .close-icon {
        cursor: pointer;
        padding: 4px;
        border-radius: 50%;
        &:hover {
          background-color: #f3f4f6;
        }
      }
    }

    .detail-scroll-content {
      flex: 1;
      overflow-y: auto;
      padding: 16px;

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
      .detail-tags {
        margin-bottom: 16px;
      }
      .detail-desc-text {
        color: #4b5563;
        font-size: 14px;
        white-space: pre-wrap;
        line-height: 1.6;
      }
      .detail-time {
        margin-top: 24px;
        font-size: 12px;
        color: #9ca3af;
      }
    }

    .detail-footer {
      padding: 8px;
      border-top: 1px solid #f3f4f6;
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .qr-btn {
        padding: 0 20px;
        font-weight: 700;
        color: #4b5563;
        background-color: #f3f4f6;
        border: 1px solid #e5e7eb;
        &:hover {
          background-color: #e5e7eb;
        }
      }

      .edit-btn {
        background: #f3f4f6;
        border: 1px solid #e5e7eb;
        color: #1f2937;
      }
      .edit-btn:hover {
        background: #e5e7eb;
        color: #111827;
      }

      .del-btn {
        color: #ef4444;
      }
      .del-btn:hover {
        color: #b91c1c;
      }
    }
  }
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: transform 0.2s;

  &:hover {
    transform: scale(1.05);
  }

  &.favor-btn {
    color: #ef4444;
  }
}

.favor-count {
  font-size: 12px;
  color: #4b5563;
  display: inline-flex;
  align-items: center;
  line-height: 1;
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

// 通用表单弹窗
.form-dialog {
  border-radius: 16px;
}
.avatar-row {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}
.img-preview {
  margin-top: 8px;
  height: 128px;
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f3f4f6;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.upload-card :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.2s;
}
.upload-card :deep(.el-upload:hover) {
  border-color: @primary-color;
}
.upload-trigger {
  width: 160px;
  height: 120px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.upload-trigger.uploading {
  opacity: 0.7;
  pointer-events: none;
}
.upload-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
  font-size: 12px;
  gap: 6px;
}
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.2s;
}
.avatar-uploader :deep(.el-upload:hover) {
  border-color: @primary-color;
}
.avatar-trigger {
  width: 120px;
  height: 120px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-trigger.uploading {
  opacity: 0.7;
  pointer-events: none;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}
.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
  font-size: 12px;
  gap: 6px;
}
.avatar-loading {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 12px;
  border-radius: 6px;
}
</style>
