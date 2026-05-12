<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-box">
        <el-icon class="logo-icon"><FirstAid /></el-icon>
        <span v-if="!isCollapse" class="logo-text">健康档案系统</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#1a2332"
          text-color="#b0bac7"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>统计仪表盘</template>
          </el-menu-item>

          <template v-if="isAdmin || isDoctor">
            <el-menu-item index="/residents">
              <el-icon><User /></el-icon>
              <template #title>居民档案</template>
            </el-menu-item>
            <el-menu-item index="/chronic">
              <el-icon><MedicalKit /></el-icon>
              <template #title>慢病管理</template>
            </el-menu-item>
            <el-menu-item index="/followup">
              <el-icon><Calendar /></el-icon>
              <template #title>随访记录</template>
            </el-menu-item>
          </template>

          <template v-if="isAdmin || isDoctor">
            <el-menu-item index="/alerts">
              <el-icon><Bell /></el-icon>
              <template #title>
                健康预警
                <el-badge v-if="pendingAlertCount > 0" :value="pendingAlertCount" :max="99" style="margin-left:8px" />
              </template>
            </el-menu-item>
          </template>

          <el-menu-item v-if="isAdmin" index="/users">
            <el-icon><Setting /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>

          <el-menu-item v-if="isAdmin" index="/oplog">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>

          <el-menu-item v-if="isResident" index="/my-profile">
            <el-icon><UserFilled /></el-icon>
            <template #title>我的档案</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 主区域 -->
    <el-container>
      <!-- 顶部 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" style="background:#409EFF">
                {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
              <el-tag size="small" :type="roleTagType" style="margin-left:6px">{{ roleLabel }}</el-tag>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="changePwd">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <!-- 修改密码对话框 -->
  <el-dialog v-model="pwdDialogVisible" title="修改密码" width="400px">
    <el-form :model="pwdForm" label-width="90px">
      <el-form-item label="原密码">
        <el-input v-model="pwdForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="pwdForm.newPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="pwdLoading" @click="submitChangePwd">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/auth'
import { getPendingAlertCount } from '@/api/alert'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => (route.meta.title as string) || '')

const isAdmin = computed(() => userStore.isAdmin())
const isDoctor = computed(() => userStore.isDoctor())
const isResident = computed(() => userStore.isResident())

const pendingAlertCount = ref(0)
onMounted(async () => {
  if (isAdmin.value || isDoctor.value) {
    try {
      pendingAlertCount.value = (await getPendingAlertCount() as any) || 0
    } catch {}
  }
})

const roleLabel = computed(() => {
  const r = userStore.userInfo?.role
  return r === 'ADMIN' ? '管理员' : r === 'DOCTOR' ? '医生' : '居民'
})
const roleTagType = computed(() => {
  const r = userStore.userInfo?.role
  return r === 'ADMIN' ? 'danger' : r === 'DOCTOR' ? 'success' : 'info'
})

const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '' })

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  } else if (cmd === 'changePwd') {
    pwdForm.value = { oldPassword: '', newPassword: '' }
    pwdDialogVisible.value = true
  }
}

async function submitChangePwd() {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  pwdLoading.value = true
  try {
    await changePassword(pwdForm.value)
    ElMessage.success('密码修改成功，请重新登录')
    pwdDialogVisible.value = false
    userStore.logout()
    router.push('/login')
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}
.layout-aside {
  background: #1a2332;
  transition: width 0.25s;
  overflow: hidden;
  flex-shrink: 0;
}
.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid #2a3547;
  overflow: hidden;
}
.logo-icon {
  font-size: 24px;
  color: #409EFF;
  flex-shrink: 0;
}
.logo-text {
  margin-left: 10px;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
}
.layout-header {
  background: #fff;
  border-bottom: 1px solid #e8eaed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.collapse-btn:hover { color: #409EFF; }
.header-right { display: flex; align-items: center; }
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background .2s;
}
.user-info:hover { background: #f5f7fa; }
.user-name { font-size: 14px; color: #303133; }
.layout-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
:deep(.el-menu) { border-right: none; }
</style>
