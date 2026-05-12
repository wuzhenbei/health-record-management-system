<template>
  <div>
    <div class="page-header">
      <h2>健康预警</h2>
    </div>

    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="预警类型">
          <el-select v-model="query.alertType" placeholder="全部" clearable style="width:120px">
            <el-option v-for="t in alertTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:110px">
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="warning" :loading="scanning" @click="doScan">扫描历史数据</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="residentName" label="居民姓名" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="175" />
        <el-table-column prop="alertType" label="预警类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeColor(row.alertType)" size="small">{{ row.alertType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertValue" label="异常指标" min-width="160" />
        <el-table-column prop="measureDate" label="检测日期" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : 'success'" size="small">
              {{ row.status === 0 ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleNote" label="处理备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="预警时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push('/residents/' + row.residentId)">查看档案</el-button>
            <el-button v-if="row.status === 0" link type="warning" @click="openHandle(row)">标记处理</el-button>
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

    <el-dialog v-model="handleDialogVisible" title="标记处理" width="420px">
      <el-form label-width="80px">
        <el-form-item label="处理备注">
          <el-input v-model="handleNote" type="textarea" :rows="3" placeholder="请输入处理意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitHandle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAlertPage, handleAlert, batchScanAlerts } from '@/api/alert'

const alertTypes = ['高血压', '高血糖', '超重', '心率异常']

const loading = ref(false)
const submitting = ref(false)
const scanning = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10, alertType: '', status: undefined as any })
const handleDialogVisible = ref(false)
const handleNote = ref('')
const currentId = ref<number>(0)

async function doScan() {
  scanning.value = true
  try {
    const msg = await batchScanAlerts() as any
    ElMessage.success(typeof msg === 'string' ? msg : '扫描完成')
    loadData()
  } finally { scanning.value = false }
}

function typeColor(t: string) {
  if (t === '高血压') return 'danger'
  if (t === '高血糖') return 'warning'
  if (t === '超重') return ''
  return 'info'
}

async function loadData() {
  loading.value = true
  try {
    const params: any = { pageNum: query.value.pageNum, pageSize: query.value.pageSize }
    if (query.value.alertType) params.alertType = query.value.alertType
    if (query.value.status !== undefined && query.value.status !== '') params.status = query.value.status
    const res = await getAlertPage(params) as any
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 10, alertType: '', status: undefined }
  loadData()
}

function openHandle(row: any) {
  currentId.value = row.id
  handleNote.value = ''
  handleDialogVisible.value = true
}

async function submitHandle() {
  submitting.value = true
  try {
    await handleAlert(currentId.value, handleNote.value)
    ElMessage.success('已标记处理')
    handleDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

onMounted(loadData)
</script>
