<template>
  <div>
    <div class="page-header">
      <h2>随访记录</h2>
      <div style="display:flex;gap:8px">
        <el-button :icon="Download" @click="exportExcel">导出 Excel</el-button>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增随访</el-button>
      </div>
    </div>

    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="居民姓名">
          <el-input v-model="query.residentName" placeholder="姓名关键字" clearable style="width:130px" />
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="query.doctorName" placeholder="医生姓名" clearable style="width:120px" />
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
        <el-table-column prop="diseaseType" label="关联病种" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="warning" v-if="row.diseaseType">{{ row.diseaseType }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="followupDate" label="随访日期" width="110" />
        <el-table-column prop="doctorName" label="随访医生" width="90" />
        <el-table-column prop="followupType" label="随访方式" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="followupTypeTag(row.followupType)">{{ row.followupType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bp" label="血压" width="90" />
        <el-table-column prop="bloodSugar" label="血糖" width="80" />
        <el-table-column prop="symptom" label="症状描述" min-width="140" show-overflow-tooltip />
        <el-table-column prop="nextDate" label="下次随访" width="110" />
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

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑随访' : '新增随访'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="选择居民" v-if="!editId">
              <el-select
                v-model="form.residentId"
                filterable remote
                :remote-method="remoteSearchResident"
                :loading="residentSearchLoading"
                placeholder="输入姓名搜索"
                style="width:100%"
                @change="onResidentChange"
              >
                <el-option v-for="r in residentOptions" :key="r.id"
                  :label="`${r.name}（${r.idCard?.slice(-4)}）`" :value="r.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联慢病">
              <el-select v-model="form.chronicId" clearable placeholder="可不选" style="width:100%"
                :disabled="!form.residentId && !editId">
                <el-option v-for="c in chronicOptions" :key="c.id"
                  :label="`${c.diseaseType}（${c.confirmDate}）`" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="随访日期">
              <el-date-picker v-model="form.followupDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="随访方式">
              <el-select v-model="form.followupType" style="width:100%">
                <el-option v-for="t in ['门诊','电话','入户']" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生姓名">
              <el-input v-model="form.doctorName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血压">
              <el-input v-model="form.bp" placeholder="如 120/80" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血糖(mmol/L)">
              <el-input-number v-model="form.bloodSugar" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次随访">
              <el-date-picker v-model="form.nextDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="症状描述">
              <el-input v-model="form.symptom" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="医嘱建议">
              <el-input v-model="form.advice" type="textarea" :rows="2" />
            </el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Download } from '@element-plus/icons-vue'
import { getFollowupPage, createFollowup, updateFollowup, deleteFollowup } from '@/api/followup'
import { searchResidents, getChronicByResident } from '@/api/resident'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({ pageNum: 1, pageSize: 10, residentName: '', doctorName: '' })
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const form = ref<any>({})
const residentOptions = ref<any[]>([])
const residentSearchLoading = ref(false)
const chronicOptions = ref<any[]>([])

function followupTypeTag(t: string) {
  return t === '门诊' ? 'success' : t === '电话' ? 'info' : 'warning'
}

async function remoteSearchResident(name: string) {
  if (!name) return
  residentSearchLoading.value = true
  try { residentOptions.value = await searchResidents(name) as any[] }
  finally { residentSearchLoading.value = false }
}

async function onResidentChange(rid: number) {
  form.value.chronicId = undefined
  chronicOptions.value = rid ? await getChronicByResident(rid) as any[] : []
}

async function loadData() {
  loading.value = true
  try {
    const res = await getFollowupPage(query.value) as any
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 10, residentName: '', doctorName: '' }
  loadData()
}

function openDialog(row?: any) {
  editId.value = row?.id || null
  chronicOptions.value = []
  if (row) {
    form.value = { chronicId: row.chronicId, followupDate: row.followupDate, followupType: row.followupType, doctorName: row.doctorName, bp: row.bp, bloodSugar: row.bloodSugar, symptom: row.symptom, advice: row.advice, nextDate: row.nextDate }
    if (row.residentId) getChronicByResident(row.residentId).then((r: any) => { chronicOptions.value = r })
  } else {
    form.value = { residentId: undefined, chronicId: undefined, followupDate: '', followupType: '门诊', doctorName: '', bp: '', bloodSugar: undefined, symptom: '', advice: '', nextDate: '' }
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editId.value) { await updateFollowup(editId.value, form.value) }
    else { await createFollowup(form.value) }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除【${row.residentName}】的随访记录？`, '警告', { type: 'warning' })
  await deleteFollowup(row.id)
  ElMessage.success('删除成功')
  loadData()
}

function exportExcel() {
  const params = new URLSearchParams()
  if (query.value.residentName) params.append('residentName', query.value.residentName)
  const a = document.createElement('a')
  a.href = `/api/followup-records/export?${params.toString()}`
  a.click()
}

onMounted(loadData)
</script>
