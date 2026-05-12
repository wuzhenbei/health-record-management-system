<template>
  <div>
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="router.back()">返回</el-button>
      <h2>{{ resident.name }} — 居民档案详情</h2>
    </div>

    <!-- 基本信息 -->
    <el-card style="margin-bottom:16px">
      <template #header><span style="font-weight:600">基本信息</span></template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="姓名">{{ resident.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ resident.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ resident.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ resident.idCard }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ resident.phone }}</el-descriptions-item>
        <el-descriptions-item label="血型">{{ resident.bloodType }}</el-descriptions-item>
        <el-descriptions-item label="婚姻状况">{{ resident.marriage }}</el-descriptions-item>
        <el-descriptions-item label="职业">{{ resident.occupation }}</el-descriptions-item>
        <el-descriptions-item label="过敏史">{{ resident.allergy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="住址" :span="3">{{ resident.address }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ resident.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab">
      <!-- 健康指标 -->
      <el-tab-pane label="健康指标" name="health">
        <div style="margin-bottom:12px;display:flex;justify-content:flex-end;gap:8px">
          <el-button size="small" @click="toggleTrend">{{ showTrend ? '隐藏趋势图' : '显示趋势图' }}</el-button>
          <el-button type="primary" size="small" :icon="Plus" @click="openHealthDialog()">新增体检</el-button>
        </div>
        <!-- 体检趋势折线图 -->
        <el-card v-show="showTrend" shadow="never" style="margin-bottom:12px;padding:4px">
          <div ref="trendRef" style="height:240px"></div>
        </el-card>
        <el-table :data="healthList" stripe size="small">
          <el-table-column prop="measureDate" label="检测日期" width="110" />
          <el-table-column prop="height" label="身高(cm)" width="90" />
          <el-table-column prop="weight" label="体重(kg)" width="90" />
          <el-table-column prop="bmi" label="BMI" width="70" />
          <el-table-column label="血压(mmHg)" width="120">
            <template #default="{ row }">{{ row.systolic }}/{{ row.diastolic }}</template>
          </el-table-column>
          <el-table-column prop="heartRate" label="心率" width="70" />
          <el-table-column prop="bloodSugar" label="血糖" width="80" />
          <el-table-column prop="cholesterol" label="胆固醇" width="80" />
          <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openHealthDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="delHealth(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 病史 -->
      <el-tab-pane label="病史记录" name="medical">
        <div style="margin-bottom:12px;display:flex;justify-content:flex-end">
          <el-button type="primary" size="small" :icon="Plus" @click="openMedDialog()">新增病史</el-button>
        </div>
        <el-table :data="medList" stripe size="small">
          <el-table-column prop="visitDate" label="就诊日期" width="110" />
          <el-table-column prop="disease" label="疾病名称" width="140" />
          <el-table-column prop="diagnosis" label="诊断描述" min-width="160" show-overflow-tooltip />
          <el-table-column prop="treatment" label="治疗方案" min-width="160" show-overflow-tooltip />
          <el-table-column prop="doctorName" label="接诊医生" width="90" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openMedDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="delMed(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 慢病 -->
      <el-tab-pane label="慢病登记" name="chronic">
        <div style="margin-bottom:12px;display:flex;justify-content:flex-end">
          <el-button type="primary" size="small" :icon="Plus" @click="openChronicDialog()">登记慢病</el-button>
        </div>
        <el-table :data="chronicList" stripe size="small">
          <el-table-column prop="diseaseType" label="病种" width="100" />
          <el-table-column prop="confirmDate" label="确诊日期" width="110" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag size="small" :type="row.status === '管理中' ? 'warning' : 'info'">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="分级" width="80" />
          <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openChronicDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="delChronic(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 随访记录 -->
      <el-tab-pane label="随访记录" name="followup">
        <el-table :data="followupList" stripe size="small">
          <el-table-column prop="followupDate" label="随访日期" width="110" />
          <el-table-column prop="diseaseType" label="病种" width="90" />
          <el-table-column prop="followupType" label="随访方式" width="90" />
          <el-table-column prop="doctorName" label="随访医生" width="90" />
          <el-table-column prop="bp" label="血压" width="90" />
          <el-table-column prop="bloodSugar" label="血糖" width="80" />
          <el-table-column prop="symptom" label="症状" min-width="120" show-overflow-tooltip />
          <el-table-column prop="nextDate" label="下次随访" width="110" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 健康指标弹窗 -->
    <el-dialog v-model="healthDialog" :title="healthEditId ? '编辑体检' : '新增体检'" width="540px">
      <el-form :model="healthForm" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="检测日期"><el-date-picker v-model="healthForm.measureDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="身高(cm)"><el-input-number v-model="healthForm.height" :precision="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="体重(kg)"><el-input-number v-model="healthForm.weight" :precision="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="收缩压"><el-input-number v-model="healthForm.systolic" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="舒张压"><el-input-number v-model="healthForm.diastolic" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="心率"><el-input-number v-model="healthForm.heartRate" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="血糖"><el-input-number v-model="healthForm.bloodSugar" :precision="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="胆固醇"><el-input-number v-model="healthForm.cholesterol" :precision="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="healthForm.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="healthDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHealth">确认</el-button>
      </template>
    </el-dialog>

    <!-- 病史弹窗 -->
    <el-dialog v-model="medDialog" :title="medEditId ? '编辑病史' : '新增病史'" width="540px">
      <el-form :model="medForm" label-width="90px">
        <el-form-item label="就诊日期"><el-date-picker v-model="medForm.visitDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="疾病名称"><el-input v-model="medForm.disease" /></el-form-item>
        <el-form-item label="诊断描述"><el-input v-model="medForm.diagnosis" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="治疗方案"><el-input v-model="medForm.treatment" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="接诊医生"><el-input v-model="medForm.doctorName" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="medForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="medDialog = false">取消</el-button>
        <el-button type="primary" @click="submitMed">确认</el-button>
      </template>
    </el-dialog>

    <!-- 慢病弹窗 -->
    <el-dialog v-model="chronicDialog" :title="chronicEditId ? '编辑慢病' : '登记慢病'" width="480px">
      <el-form :model="chronicForm" label-width="90px">
        <el-form-item label="病种">
          <el-select v-model="chronicForm.diseaseType" style="width:100%">
            <el-option v-for="d in ['高血压','糖尿病','冠心病','脑卒中','COPD','其他']" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="确诊日期"><el-date-picker v-model="chronicForm.confirmDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="chronicForm.status" style="width:100%">
            <el-option v-for="s in ['管理中','已治愈','死亡','迁出']" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="分级"><el-input v-model="chronicForm.level" placeholder="如一级/二级/三级" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="chronicForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chronicDialog = false">取消</el-button>
        <el-button type="primary" @click="submitChronic">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowLeft } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getResidentById, getHealthIndicators, createHealthIndicator, updateHealthIndicator, deleteHealthIndicator,
  getMedicalHistories, createMedicalHistory, updateMedicalHistory, deleteMedicalHistory,
  getChronicByResident
} from '@/api/resident'
import { createChronic, updateChronic, deleteChronic } from '@/api/chronic'
import { getFollowupByResident } from '@/api/followup'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const residentId = Number(route.params.id)

const resident = ref<any>({})
const activeTab = ref('health')
const healthList = ref<any[]>([])
const medList = ref<any[]>([])
const chronicList = ref<any[]>([])
const followupList = ref<any[]>([])

const trendRef = ref<HTMLElement>()
const showTrend = ref(false)
let trendChart: echarts.ECharts | null = null

const healthDialog = ref(false)
const healthEditId = ref<number|null>(null)
const healthForm = ref<any>({})

const medDialog = ref(false)
const medEditId = ref<number|null>(null)
const medForm = ref<any>({})

const chronicDialog = ref(false)
const chronicEditId = ref<number|null>(null)
const chronicForm = ref<any>({})

async function loadAll() {
  resident.value = await getResidentById(residentId) as any
  healthList.value = await getHealthIndicators(residentId) as any[]
  medList.value = await getMedicalHistories(residentId) as any[]
  chronicList.value = await getChronicByResident(residentId) as any[]
  followupList.value = await getFollowupByResident(residentId) as any[]
}

async function toggleTrend() {
  showTrend.value = !showTrend.value
  if (!showTrend.value) return
  await nextTick()
  if (!trendRef.value) return
  const trendData = await request.get(`/health-indicators/resident/${residentId}/trend`) as any[]
  if (!trendChart) trendChart = echarts.init(trendRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['收缩压', '舒张压', '血糖', 'BMI'], bottom: 0 },
    grid: { left: 40, right: 20, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: trendData.map((d: any) => d.measureDate) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '收缩压', type: 'line', data: trendData.map((d: any) => d.systolic), smooth: true, itemStyle: { color: '#f56c6c' } },
      { name: '舒张压', type: 'line', data: trendData.map((d: any) => d.diastolic), smooth: true, itemStyle: { color: '#e6a23c' } },
      { name: '血糖', type: 'line', data: trendData.map((d: any) => d.bloodSugar), smooth: true, itemStyle: { color: '#67c23a' } },
      { name: 'BMI', type: 'line', data: trendData.map((d: any) => d.bmi), smooth: true, itemStyle: { color: '#409EFF' } }
    ]
  })
}

function openHealthDialog(row?: any) {
  healthEditId.value = row?.id || null
  healthForm.value = row ? { ...row } : { residentId, measureDate: '', height: undefined, weight: undefined, systolic: undefined, diastolic: undefined, heartRate: undefined, bloodSugar: undefined, cholesterol: undefined, remark: '' }
  healthDialog.value = true
}
async function submitHealth() {
  if (healthEditId.value) await updateHealthIndicator(healthEditId.value, healthForm.value)
  else await createHealthIndicator({ ...healthForm.value, residentId })
  ElMessage.success('操作成功')
  healthDialog.value = false
  healthList.value = await getHealthIndicators(residentId) as any[]
}
async function delHealth(row: any) {
  await ElMessageBox.confirm('确定删除此体检记录？', '提示', { type: 'warning' })
  await deleteHealthIndicator(row.id)
  ElMessage.success('已删除')
  healthList.value = await getHealthIndicators(residentId) as any[]
}

function openMedDialog(row?: any) {
  medEditId.value = row?.id || null
  medForm.value = row ? { ...row } : { residentId, visitDate: '', disease: '', diagnosis: '', treatment: '', doctorName: '', remark: '' }
  medDialog.value = true
}
async function submitMed() {
  if (medEditId.value) await updateMedicalHistory(medEditId.value, medForm.value)
  else await createMedicalHistory({ ...medForm.value, residentId })
  ElMessage.success('操作成功')
  medDialog.value = false
  medList.value = await getMedicalHistories(residentId) as any[]
}
async function delMed(row: any) {
  await ElMessageBox.confirm('确定删除此病史记录？', '提示', { type: 'warning' })
  await deleteMedicalHistory(row.id)
  ElMessage.success('已删除')
  medList.value = await getMedicalHistories(residentId) as any[]
}

function openChronicDialog(row?: any) {
  chronicEditId.value = row?.id || null
  chronicForm.value = row ? { ...row } : { residentId, diseaseType: '', confirmDate: '', status: '管理中', level: '', remark: '' }
  chronicDialog.value = true
}
async function submitChronic() {
  if (chronicEditId.value) await updateChronic(chronicEditId.value, chronicForm.value)
  else await createChronic({ ...chronicForm.value, residentId })
  ElMessage.success('操作成功')
  chronicDialog.value = false
  chronicList.value = await getChronicByResident(residentId) as any[]
}
async function delChronic(row: any) {
  await ElMessageBox.confirm('确定删除此慢病记录？', '提示', { type: 'warning' })
  await deleteChronic(row.id)
  ElMessage.success('已删除')
  chronicList.value = await getChronicByResident(residentId) as any[]
}

onMounted(loadAll)
</script>
