<template>
  <div>
    <div class="page-header">
      <h2>居民档案</h2>
      <div style="display:flex;gap:8px">
        <el-button :icon="Download" @click="exportExcel">导出 Excel</el-button>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增档案</el-button>
      </div>
    </div>

    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="居民姓名" clearable style="width:130px" />
        </el-form-item>
        <el-form-item label="身份证">
          <el-input v-model="query.idCard" placeholder="身份证号" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="query.phone" placeholder="手机号" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="query.gender" placeholder="全部" clearable style="width:90px">
            <el-option label="男" :value="1" /><el-option label="女" :value="0" />
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
        <el-table-column prop="name" label="姓名" width="90" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="110" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="address" label="住址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="bloodType" label="血型" width="70" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">查看档案</el-button>
            <el-button link type="warning" @click="openDialog(row)">编辑</el-button>
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
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑档案' : '新增档案'" width="600px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio><el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血型">
              <el-select v-model="form.bloodType" style="width:100%">
                <el-option v-for="t in ['A','B','O','AB']" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号"><el-input v-model="form.idCard" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="婚姻状况">
              <el-select v-model="form.marriage" style="width:100%">
                <el-option v-for="s in ['未婚','已婚','离异','丧偶']" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职业"><el-input v-model="form.occupation" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="住址"><el-input v-model="form.address" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过敏史"><el-input v-model="form.allergy" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
          </el-col>
        </el-row>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Download } from '@element-plus/icons-vue'
import { getResidentPage, createResident, updateResident, deleteResident } from '@/api/resident'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10, name: '', idCard: '', phone: '', gender: undefined as any })
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const form = ref<any>({})

async function loadData() {
  loading.value = true
  try {
    const res = await getResidentPage(query.value) as any
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 10, name: '', idCard: '', phone: '', gender: undefined }
  loadData()
}

function goDetail(id: number) { router.push(`/residents/${id}`) }

function openDialog(row?: any) {
  editId.value = row?.id || null
  form.value = row ? { ...row } : { name: '', gender: 1, birthDate: '', idCard: '', phone: '', address: '', bloodType: 'A', marriage: '', occupation: '', allergy: '', remark: '' }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editId.value) { await updateResident(editId.value, form.value) }
    else { await createResident(form.value) }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

function exportExcel() {
  const params = new URLSearchParams()
  if (query.value.name) params.append('name', query.value.name)
  if (query.value.idCard) params.append('idCard', query.value.idCard)
  if (query.value.phone) params.append('phone', query.value.phone)
  const token = localStorage.getItem('token')
  const url = `/api/residents/export?${params.toString()}`
  const a = document.createElement('a')
  a.href = url
  a.click()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除【${row.name}】的档案？`, '警告', { type: 'warning' })
  await deleteResident(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
