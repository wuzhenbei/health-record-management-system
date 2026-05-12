<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="用户名" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.realName" placeholder="真实姓名" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部" clearable style="width:110px">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="医生" value="DOCTOR" />
            <el-option label="居民" value="RESIDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:90px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="roleType(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑用户' : '新增用户'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名" v-if="!editId">
          <el-input v-model="form.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码" v-if="!editId">
          <el-input v-model="form.password" type="password" placeholder="初始密码" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="医生" value="DOCTOR" />
            <el-option label="居民" value="RESIDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getUserPage, createUser, updateUser, deleteUser, resetPassword, updateUserStatus } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10, username: '', realName: '', role: '', status: undefined as any })
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const form = ref<any>({})

function roleLabel(r: string) { return r === 'ADMIN' ? '管理员' : r === 'DOCTOR' ? '医生' : '居民' }
function roleType(r: string) { return r === 'ADMIN' ? 'danger' : r === 'DOCTOR' ? 'success' : 'info' }

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage(query.value) as any
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 10, username: '', realName: '', role: '', status: undefined }
  loadData()
}

function openDialog(row?: any) {
  editId.value = row?.id || null
  form.value = row ? { realName: row.realName, phone: row.phone, role: row.role } : { username: '', password: '123456', role: 'DOCTOR', realName: '', phone: '' }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editId.value) {
      await updateUser(editId.value, form.value)
    } else {
      await createUser(form.value)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function handleResetPwd(row: any) {
  await ElMessageBox.confirm(`确定重置【${row.username}】的密码为 123456？`, '提示', { type: 'warning' })
  await resetPassword(row.id, '123456')
  ElMessage.success('密码已重置为 123456')
}

async function toggleStatus(row: any) {
  const ns = row.status === 1 ? 0 : 1
  await updateUserStatus(row.id, ns)
  row.status = ns
  ElMessage.success(ns === 1 ? '已启用' : '已停用')
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除用户【${row.username}】？`, '警告', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
