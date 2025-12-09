<template>
  <div class="content-card">
    <div class="toolbar">
      <h2 class="toolbar-title">用户列表</h2>
      <div class="toolbar-actions">
        <el-input
          v-model="searchUser"
          placeholder="搜索用户名/邮箱"
          :prefix-icon="Search"
          class="search-input"
          clearable
        />
        <el-button type="primary" @click="openUserDialog('add')">
          <el-icon class="mr-1"><Plus /></el-icon> 新增用户
        </el-button>
        <el-button type="danger" :disabled="!selectedUsers.length" @click="handleBatchDeleteUser">
          批量删除
        </el-button>
      </div>
    </div>

    <el-table
      v-loading="tableLoading"
      :data="users"
      style="width: 100%"
      class="custom-table"
      @selection-change="onUserSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" sortable />
      <el-table-column label="用户" width="220">
        <template #default="scope">
          <div class="user-info">
            <el-avatar :size="36" :src="buildAvatarUrl(scope.row.avatarUrl)" />
            <div class="user-detail">
              <div class="user-name">{{ scope.row.name }}</div>
              <div class="user-email">{{ scope.row.email }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.role === 1 ? 'danger' : 'success'" effect="plain" round>
            {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="openUserDialog('edit', scope.row)"
            >编辑</el-button
          >
          <el-button link type="danger" size="small" @click="handleDeleteUser(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <div class="table-footer">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :current-page="currentUserPage"
        :page-size="userPageSize"
        :page-sizes="userPageSizes"
        :total="totalUsers"
        @current-change="handleUserPageChange"
        @size-change="handleUserPageSizeChange"
      />
    </div>

    <el-dialog
      v-model="userDialog.visible"
      :title="userDialog.type === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item v-if="userDialog.type === 'add'" label="密码">
          <el-input v-model="userForm.password" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" placeholder="选择角色" class="w-full">
            <el-option label="普通用户" :value="0" />
            <el-option label="管理员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="自我介绍">
          <el-input
            v-model="userForm.description"
            type="textarea"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-upload">
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
                    v-if="userForm.avatarUrl"
                    :src="buildAvatarUrl(userForm.avatarUrl)"
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
            <el-button v-if="userForm.avatarUrl" link type="danger" @click="handleAvatarRemove"
              >移除</el-button
            >
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue';
import type { UploadProps, UploadRequestOptions } from 'element-plus';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';
import type { User } from '@/types/user';
import { addUser, getUserList, updateUser, deleteUserByIds } from '@/api/user';
import { uploadStaticResource } from '@/api/resource';
import { buildAvatarUrl } from '@/utils/avatar';

type UserDialogType = 'add' | 'edit';
type UserFormModel = User & { password?: string };

const searchUser = ref<string>('');
const selectedUsers = ref<User[]>([]);
const currentUserPage = ref(1);
const userPageSize = ref(10);
const userPageSizes = [5, 10, 20, 50];
const totalUsers = ref(0);
const tableLoading = ref(false);
const users = ref<User[]>([]);

const userDialog = reactive<{ visible: boolean; type: UserDialogType }>({
  visible: false,
  type: 'add',
});
const avatarUploading = ref(false);
type UploadError = Error & { status?: number; method?: string; url?: string };
const createEmptyUserForm = (): UserFormModel => ({
  id: null,
  name: '',
  email: '',
  role: 0,
  description: '',
  avatarUrl: '',
  password: '',
});
const userForm = reactive<UserFormModel>(createEmptyUserForm());

const imageAccept = 'image/png, image/jpeg, image/jpg, image/gif, image/webp';

const openUserDialog = (type: UserDialogType, row?: User) => {
  userDialog.type = type;
  userDialog.visible = true;
  if (type === 'edit' && row) {
    Object.assign(userForm, row, { password: '' });
  } else {
    Object.assign(userForm, createEmptyUserForm());
  }
};

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
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

const handleAvatarUpload = async (options: UploadRequestOptions) => {
  if (!options.file) return;
  avatarUploading.value = true;
  try {
    const { data } = await uploadStaticResource(options.file as File, 'avatar');
    const path = data.data?.path || '';
    if (path) {
      userForm.avatarUrl = path;
      ElMessage.success('头像上传成功');
      options.onSuccess?.(data);
    } else {
      throw new Error('上传失败');
    }
  } catch (error) {
    ElMessage.error('头像上传失败');
    const uploadError: UploadError = Object.assign(
      new Error((error as Error)?.message || '上传失败'),
      {
        name: 'UploadError',
        status: 500,
        method: 'POST',
        url: '/resource/upload',
      },
    );
    options.onError?.(uploadError as never);
  } finally {
    avatarUploading.value = false;
  }
};

const handleAvatarRemove = () => {
  userForm.avatarUrl = '';
};

const fetchUsers = async () => {
  tableLoading.value = true;
  try {
    const { data } = await getUserList({
      page: currentUserPage.value,
      pageSize: userPageSize.value,
      keyword: searchUser.value.trim() || undefined,
    });
    const payload = data.data;
    const records = (payload?.records ?? []) as User[];
    users.value = records.map((record) => ({
      id: record.id ?? null,
      name: record.name,
      email: record.email,
      role: record.role ?? 0,
      avatarUrl: record.avatarUrl || '',
      description: record.description ?? '',
    }));
    totalUsers.value = payload?.total ?? 0;
  } catch {
  } finally {
    tableLoading.value = false;
  }
};

const saveUser = async () => {
  const name = userForm.name?.trim();
  const email = userForm.email?.trim();
  const description = userForm.description?.trim() ?? '';
  const avatarUrl = userForm.avatarUrl?.trim() || undefined;

  if (!name) {
    ElMessage.error('请输入用户名');
    return;
  }
  if (!email) {
    ElMessage.error('请输入邮箱');
    return;
  }
  if (!description) {
    ElMessage.error('请输入描述');
    return;
  }

  if (userDialog.type === 'add') {
    const password = userForm.password?.trim() || '';
    if (!password) {
      ElMessage.error('请输入密码');
      return;
    }
    try {
      await addUser({
        name,
        email,
        password,
        avatarUrl,
        description,
        role: userForm.role ?? 0,
      });
      ElMessage.success('用户新增成功');
      userDialog.visible = false;
      currentUserPage.value = 1;
      await fetchUsers();
    } catch {}
    return;
  }

  if (!userForm.id) {
    ElMessage.error('缺少用户 ID');
    return;
  }
  try {
    await updateUser({
      id: userForm.id,
      name,
      email,
      avatarUrl,
      description,
      role: userForm.role,
    });
    ElMessage.success('用户更新成功');
    userDialog.visible = false;
    await fetchUsers();
  } catch {}
};

const handleDeleteUser = (row: User) => {
  if (!row.id) return;
  ElMessageBox.confirm(`确定要删除用户 "${row.name}" 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteUserByIds({ ids: [row.id as number] });
      ElMessage.success('删除成功');
      selectedUsers.value = selectedUsers.value.filter((user) => user.id !== row.id);
      await fetchUsers();
    } catch {}
  });
};

const onUserSelectionChange = (selection: User[]) => {
  selectedUsers.value = selection;
};

const handleBatchDeleteUser = () => {
  if (!selectedUsers.value.length) return;
  ElMessageBox.confirm(`确定删除选中的 ${selectedUsers.value.length} 个用户吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const ids = selectedUsers.value
      .map((user) => user.id)
      .filter((id): id is number => typeof id === 'number');
    if (!ids.length) return;
    try {
      await deleteUserByIds({ ids });
      selectedUsers.value = [];
      ElMessage.success('批量删除成功');
      await fetchUsers();
    } catch {}
  });
};

const handleUserPageChange = (page: number) => {
  currentUserPage.value = page;
  fetchUsers();
};

const handleUserPageSizeChange = (size: number) => {
  userPageSize.value = size;
  currentUserPage.value = 1;
  fetchUsers();
};

watch(searchUser, () => {
  currentUserPage.value = 1;
  fetchUsers();
});

onMounted(() => {
  fetchUsers();
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
}
.toolbar-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}
.search-input {
  width: 240px;
}
.w-full {
  width: 100%;
}
.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}
.user-detail {
  margin-left: 10px;
}
.user-name {
  font-weight: 500;
  color: #303133;
}
.user-email {
  font-size: 12px;
  color: #909399;
}

.status-badge {
  display: flex;
  align-items: center;
  font-size: 12px;
}
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}
.status-dot.active {
  background-color: #67c23a;
}
.status-dot.deleted {
  background-color: #f56c6c;
}
.text-active {
  color: #67c23a;
}
.text-deleted {
  color: #f56c6c;
}
.mr-1 {
  margin-right: 4px;
}
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.2s;
}
.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
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
