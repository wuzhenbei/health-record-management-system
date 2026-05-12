<template>
  <div>
    <div class="page-header">
      <h2>我的健康档案</h2>
    </div>

    <el-empty v-if="!resident && !loading" description="暂无档案，请联系医生建档" :image-size="120" />

    <template v-if="resident">
      <!-- 健康摘要卡片 -->
      <el-row :gutter="16" style="margin-bottom:16px">
        <el-col :span="6" v-for="card in summaryCards" :key="card.label">
          <div class="summary-card" :style="{ borderLeft: `4px solid ${card.color}` }">
            <div class="s-label">{{ card.label }}</div>
            <div class="s-value" :style="{ color: card.color }">{{ card.value }}</div>
            <div class="s-status">{{ card.status }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 基本信息 -->
      <el-card style="margin-bottom:16px">
        <template #header>
          <div style="display:flex;align-items:center;justify-content:space-between">
            <div style="display:flex;align-items:center;gap:12px">
              <el-avatar :size="44" style="background:#409EFF;font-size:18px">{{ resident.name?.charAt(0) }}</el-avatar>
              <div>
                <div style="font-size:17px;font-weight:600">{{ resident.name }}</div>
                <div style="font-size:12px;color:#909399">{{ resident.gender === 1 ? '男' : '女' }} · {{ age }}岁 · {{ resident.bloodType }}型血</div>
              </div>
            </div>
            <el-button size="small" @click="openContactDialog">修改联系方式</el-button>
          </div>
        </template>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="身份证号">{{ resident.idCard }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ resident.phone }}</el-descriptions-item>
          <el-descriptions-item label="婚姻状况">{{ resident.marriage }}</el-descriptions-item>
          <el-descriptions-item label="职业">{{ resident.occupation }}</el-descriptions-item>
          <el-descriptions-item label="过敏史">{{ resident.allergy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ resident.birthDate }}</el-descriptions-item>
          <el-descriptions-item label="住址" :span="3">{{ resident.address }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-tabs v-model="activeTab">
        <!-- 体检记录 + 趋势图 -->
        <el-tab-pane label="我的体检记录" name="health">
          <div style="margin-bottom:10px;text-align:right">
            <el-button size="small" @click="toggleTrend">{{ showTrend ? '隐藏趋势图' : '查看趋势图' }}</el-button>
          </div>
          <el-card v-show="showTrend" shadow="never" style="margin-bottom:12px">
            <div ref="trendRef" style="height:230px"></div>
          </el-card>
          <el-table :data="healthList" stripe size="small" v-loading="loading">
            <el-table-column prop="measureDate" label="检测日期" width="110" />
            <el-table-column label="血压(mmHg)" width="120">
              <template #default="{ row }">
                <el-text :type="(row.systolic >= 140 || row.diastolic >= 90) ? 'danger' : 'success'">
                  {{ row.systolic }}/{{ row.diastolic }}
                </el-text>
              </template>
            </el-table-column>
            <el-table-column prop="heartRate" label="心率" width="80" />
            <el-table-column prop="height" label="身高" width="80" />
            <el-table-column prop="weight" label="体重" width="80" />
            <el-table-column prop="bmi" label="BMI" width="70">
              <template #default="{ row }">
                <el-tag size="small" :type="bmiTag(row.bmi)">{{ row.bmi }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="bloodSugar" label="血糖" width="90">
              <template #default="{ row }">
                <el-text :type="row.bloodSugar >= 7 ? 'danger' : 'success'">{{ row.bloodSugar }}</el-text>
              </template>
            </el-table-column>
            <el-table-column prop="cholesterol" label="胆固醇" min-width="90" />
            <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <!-- 病史 -->
        <el-tab-pane label="我的病史" name="medical">
          <el-table :data="medList" stripe size="small">
            <el-table-column prop="visitDate" label="就诊日期" width="110" />
            <el-table-column prop="disease" label="疾病名称" width="140" />
            <el-table-column prop="diagnosis" label="诊断描述" min-width="180" show-overflow-tooltip />
            <el-table-column prop="treatment" label="治疗方案" min-width="180" show-overflow-tooltip />
            <el-table-column prop="doctorName" label="接诊医生" width="90" />
          </el-table>
        </el-tab-pane>

        <!-- 随访 -->
        <el-tab-pane label="我的随访" name="followup">
          <el-table :data="followupList" stripe size="small">
            <el-table-column prop="followupDate" label="随访日期" width="110" />
            <el-table-column prop="followupType" label="随访方式" width="90" />
            <el-table-column prop="doctorName" label="随访医生" width="90" />
            <el-table-column prop="bp" label="血压" width="90" />
            <el-table-column prop="bloodSugar" label="血糖" width="80" />
            <el-table-column prop="symptom" label="症状" min-width="130" show-overflow-tooltip />
            <el-table-column prop="advice" label="医嘱" min-width="150" show-overflow-tooltip />
            <el-table-column prop="nextDate" label="下次随访" width="110">
              <template #default="{ row }">
                <el-tag v-if="row.nextDate" size="small" :type="nextDateTag(row.nextDate)">{{ row.nextDate }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </template>

    <!-- 修改联系方式弹窗 -->
    <el-dialog v-model="contactDialog" title="修改联系方式" width="400px">
      <el-form :model="contactForm" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="contactForm.phone" />
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="contactForm.address" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactDialog = false">取消</el-button>
        <el-button type="primary" :loading="contactSaving" @click="saveContact">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getMyResident, getHealthIndicators, getMedicalHistories } from '@/api/resident'
import { getFollowupByResident } from '@/api/followup'
import request from '@/utils/request'

const resident = ref<any>(null)
const healthList = ref<any[]>([])
const medList = ref<any[]>([])
const followupList = ref<any[]>([])
const activeTab = ref('health')
const loading = ref(false)
const trendRef = ref<HTMLElement>()
const showTrend = ref(false)
let trendChart: echarts.ECharts | null = null

const contactDialog = ref(false)
const contactSaving = ref(false)
const contactForm = ref({ phone: '', address: '' })

const age = computed(() => {
  if (!resident.value?.birthDate) return '-'
  return new Date().getFullYear() - new Date(resident.value.birthDate).getFullYear()
})

const summaryCards = computed(() => {
  const latest = healthList.value[0]
  if (!latest) return []
  const bpOk = latest.systolic < 140 && latest.diastolic < 90
  const sugarOk = !latest.bloodSugar || latest.bloodSugar < 7
  const bmiVal = Number(latest.bmi)
  const bmiOk = bmiVal >= 18.5 && bmiVal < 28
  return [
    {
      label: '血压', color: bpOk ? '#67c23a' : '#f56c6c',
      value: `${latest.systolic}/${latest.diastolic}`,
      status: bpOk ? '正常' : '偏高'
    },
    {
      label: '血糖(mmol/L)', color: sugarOk ? '#67c23a' : '#f56c6c',
      value: latest.bloodSugar ?? '-',
      status: sugarOk ? '正常' : '偏高'
    },
    {
      label: 'BMI', color: bmiOk ? '#67c23a' : '#e6a23c',
      value: latest.bmi ?? '-',
      status: bmiVal < 18.5 ? '偏低' : bmiVal < 28 ? '正常' : '超重'
    },
    {
      label: '检测日期', color: '#409EFF',
      value: latest.measureDate,
      status: '最近一次'
    }
  ]
})

function bmiTag(bmi: number) {
  if (!bmi) return 'info'
  if (bmi < 18.5) return 'warning'
  if (bmi < 24) return 'success'
  if (bmi < 28) return 'warning'
  return 'danger'
}

function nextDateTag(date: string) {
  const diff = (new Date(date).getTime() - Date.now()) / 86400000
  return diff < 0 ? 'danger' : diff <= 7 ? 'warning' : 'success'
}

function openContactDialog() {
  contactForm.value = { phone: resident.value?.phone || '', address: resident.value?.address || '' }
  contactDialog.value = true
}

async function saveContact() {
  contactSaving.value = true
  try {
    await request.put('/residents/my/contact', contactForm.value)
    resident.value.phone = contactForm.value.phone
    resident.value.address = contactForm.value.address
    ElMessage.success('联系方式已更新')
    contactDialog.value = false
  } finally { contactSaving.value = false }
}

async function toggleTrend() {
  showTrend.value = !showTrend.value
  if (!showTrend.value || !resident.value) return
  await nextTick()
  if (!trendRef.value) return
  const trendData = await request.get(`/health-indicators/resident/${resident.value.id}/trend`) as any[]
  if (!trendChart) trendChart = echarts.init(trendRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['收缩压', '舒张压', '血糖', 'BMI'], bottom: 0 },
    grid: { left: 40, right: 20, top: 16, bottom: 40 },
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

onMounted(async () => {
  loading.value = true
  try {
    resident.value = await getMyResident() as any
    const rid = resident.value.id
    healthList.value = await getHealthIndicators(rid) as any[]
    medList.value = await getMedicalHistories(rid) as any[]
    followupList.value = await getFollowupByResident(rid) as any[]
  } catch {
    resident.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.summary-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 18px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.07);
}
.s-label { font-size: 12px; color: #909399; margin-bottom: 4px; }
.s-value { font-size: 26px; font-weight: 700; line-height: 1.2; }
.s-status { font-size: 12px; color: #606266; margin-top: 4px; }
</style>
