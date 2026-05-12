<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <el-icon class="login-logo"><FirstAid /></el-icon>
        <h1>社区医疗健康档案管理系统</h1>
        <p>Community Health Archive Management System</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button
          type="primary"
          size="large"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >登 录</el-button>
      </el-form>
      <div class="login-tips">
        <span>默认账号：admin / doctor01 / resident01，密码均为 123456</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const data = await login(form.value) as any
    userStore.setToken(data.token)
    userStore.setUserInfo({
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      role: data.role,
      avatar: data.avatar
    })
    ElMessage.success('登录成功')
    const role = data.role
    if (role === 'RESIDENT') {
      router.push('/my-profile')
    } else {
      router.push('/dashboard')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a2332 0%, #2d4a7a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px 36px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.login-header {
  text-align: center;
  margin-bottom: 36px;
}
.login-logo {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 12px;
}
.login-header h1 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 6px;
}
.login-header p {
  font-size: 12px;
  color: #909399;
}
.login-form :deep(.el-input__inner) { height: 44px; }
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 8px;
  background: linear-gradient(135deg, #409EFF, #2d7cf6);
  border: none;
}
.login-tips {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
