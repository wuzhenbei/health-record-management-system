<template>
  <div>
    <div class="page-header">
      <h2>慢病管理</h2>
      <div style="display:flex;gap:8px">
        <el-button :icon="Download" @click="exportExcel">导出 Excel</el-button>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增慢病</el-button>
      </div>
    </div>

    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="居民姓名">
          <el-input v-model="query.residentName" placeholder="姓名关键字" clearable style="width:130px" />
        </el-form-item>
        <el-form-item label="病种">
          <el-select v-model="query.diseaseType" placeholder="全部" clearable style="width:130px">
            <el-option v-for="d in diseaseTypes" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:110px">
            <el-option v-for="s in statusList" :key="s" :label="s" :value="s" />
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
        <el-table-column prop="residentName" label="居民姓名" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="175" />
        <el-table-column prop="diseaseType" label="病种" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="warning">{{ row.diseaseType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="confirmDate" label="确诊日期" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === '管理中' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="分级" width="80" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="登记时间" width="160" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑慢病' : '新增慢病'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="选择居民" v-if="!editId">
          <el-select
            v-model="form.residentId"
            filterable
            remote
            :remote-method="remoteSearchResident"
            :loading="residentSearchLoading"
            placeholder="输入姓名搜索"
            style="width:100%"
          >
            <el-option
              v-for="r in residentOptions"
              :key="r.id"
              :label="`${r.name}（${r.idCard?.slice(-4)}）`"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="病种">
          <el-select v-model="form.diseaseType" style="width:100%">
            <el-option v-for="d in diseaseTypes" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="确诊日期">
          <el-date-picker v-model="form.confirmDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option v-for="s in statusList" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="分级">
          <el-input v-model="form.level" placeholder="如一级/二级/三级（高血压适用）" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { Plus, Search, Download } from '@element-plus/icons-vue'
import { getChronicPage, createChronic, updateChronic, deleteChronic } from '@/api/chronic'
import { searchResidents } from '@/api/resident'

const diseaseTypes = ['高血压', '糖尿病', '冠心病', '脑卒中', 'COPD', '其他']
const statusList = ['管理中', '已治愈', '死亡', '迁出']

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10, residentName: '', diseaseType: '', status: '' })
const residentOptions = ref<any[]>([])
const residentSearchLoading = ref(false)

async function remoteSearchResident(name: string) {
  if (!name) return
  residentSearchLoading.value = true
  try { residentOptions.value = await searchResidents(name) as any[] }
  finally { residentSearchLoading.value = false }
}
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const form = ref<any>({})

async function loadData() {
  loading.value = true
  try {
    const res = await getChronicPage(query.value) as any
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 10, residentName: '', diseaseType: '', status: '' }
  loadData()
}

function openDialog(row?: any) {
  editId.value = row?.id || null
  form.value = row
    ? { diseaseType: row.diseaseType, confirmDate: row.confirmDate, status: row.status, level: row.level, remark: row.remark }
    : { residentId: undefined, diseaseType: '高血压', confirmDate: '', status: '管理中', level: '', remark: '' }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editId.value) { await updateChronic(editId.value, form.value) }
    else { await createChronic(form.value) }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除【${row.residentName}】的${row.diseaseType}记录？`, '警告', { type: 'warning' })
  await deleteChronic(row.id)
  ElMessage.success('删除成功')
  loadData()
}

function exportExcel() {
  const params = new URLSearchParams()
  if (query.value.diseaseType) params.append('diseaseType', query.value.diseaseType)
  if (query.value.status) params.append('status', query.value.status)
  const a = document.createElement('a')
  a.href = `/api/chronic-diseases/export?${params.toString()}`
  a.click()
}

onMounted(loadData)
</script>
