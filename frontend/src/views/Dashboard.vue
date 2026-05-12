<template>
  <div>
    <div class="page-header">
      <h2>统计仪表盘</h2>
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <div class="stat-card" :style="{ background: card.color }">
          <div class="stat-value">{{ overview[card.key] ?? '-' }}</div>
          <div class="stat-label">{{ card.label }}</div>
          <el-icon class="stat-bg-icon"><component :is="card.icon" /></el-icon>
        </div>
      </el-col>
    </el-row>

    <!-- 第一行图表：慢病分布 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="10">
        <div class="chart-card">
          <div class="chart-title">慢病病种分布</div>
          <div ref="pieRef" style="height:260px"></div>
        </div>
      </el-col>
      <el-col :span="7">
        <div class="chart-card">
          <div class="chart-title">性别分布</div>
          <div ref="genderRef" style="height:260px"></div>
        </div>
      </el-col>
      <el-col :span="7">
        <div class="chart-card">
          <div class="chart-title">年龄段分布</div>
          <div ref="ageRef" style="height:260px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 第二行图表：趋势 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-title">近6个月随访趋势</div>
          <div ref="followupTrendRef" style="height:220px"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-title">近6个月居民新增趋势</div>
          <div ref="residentTrendRef" style="height:220px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 随访到期提醒 -->
    <el-card v-if="pendingList.length > 0" shadow="never" style="border:1px solid #ffd591">
      <template #header>
        <div style="display:flex;align-items:center;gap:8px;color:#e6a23c">
          <el-icon><Bell /></el-icon>
          <span style="font-weight:600">近7天随访到期提醒（{{ pendingList.length }} 条）</span>
        </div>
      </template>
      <el-table :data="pendingList" size="small" :show-header="false">
        <el-table-column prop="residentName" label="居民" width="90" />
        <el-table-column prop="followupType" label="方式" width="70" />
        <el-table-column prop="doctorName" label="医生" width="80" />
        <el-table-column label="应随访日期" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="nextDateTag(row.nextDate)">{{ row.nextDate }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push('/residents/' + row.residentId)">查看档案</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getChronicByType, getChronicByGender, getChronicByAge, getFollowupTrend, getResidentTrend, getPendingFollowup } from '@/api/stats'

const overview = ref<any>({})
const pieRef = ref<HTMLElement>()
const genderRef = ref<HTMLElement>()
const ageRef = ref<HTMLElement>()
const followupTrendRef = ref<HTMLElement>()
const residentTrendRef = ref<HTMLElement>()
const pendingList = ref<any[]>([])

const cards = [
  { key: 'totalResidents', label: '居民总人数', color: 'linear-gradient(135deg,#409EFF,#2d7cf6)', icon: 'User' },
  { key: 'totalChronic', label: '慢病管理人数', color: 'linear-gradient(135deg,#e6a23c,#f0a020)', icon: 'MedicalKit' },
  { key: 'totalFollowup', label: '累计随访次数', color: 'linear-gradient(135deg,#67c23a,#4db32e)', icon: 'Calendar' },
  { key: 'monthNewResidents', label: '本月新增居民', color: 'linear-gradient(135deg,#909399,#78808a)', icon: 'Plus' }
]

function nextDateTag(date: string) {
  const diff = (new Date(date).getTime() - Date.now()) / 86400000
  return diff < 0 ? 'danger' : diff <= 3 ? 'warning' : 'success'
}

function initLineChart(el: HTMLElement, data: any[], color: string) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: data.map((d: any) => d.name), axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'line', data: data.map((d: any) => d.value),
      smooth: true, symbol: 'circle', symbolSize: 6,
      itemStyle: { color },
      areaStyle: { color: color + '22' },
      lineStyle: { width: 2, color }
    }]
  })
}

onMounted(async () => {
  overview.value = await getOverview() as any
  pendingList.value = await getPendingFollowup() as any[]

  const typeData = await getChronicByType() as any[]
  const pie = echarts.init(pieRef.value!)
  pie.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{ type: 'pie', radius: ['40%', '65%'], data: typeData.map((d: any) => ({ name: d.name, value: d.value })), label: { show: true, formatter: '{b}: {c}' } }]
  })

  const genderData = await getChronicByGender() as any[]
  echarts.init(genderRef.value!).setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人' },
    series: [{ type: 'pie', radius: '60%', data: genderData.map((d: any) => ({ name: d.name, value: d.value })), color: ['#409EFF', '#f56c6c'] }]
  })

  const ageData = await getChronicByAge() as any[]
  echarts.init(ageRef.value!).setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ageData.map((d: any) => d.name), axisLabel: { interval: 0, rotate: 20, fontSize: 11 } },
    yAxis: { type: 'value', name: '人数' },
    series: [{ type: 'bar', data: ageData.map((d: any) => d.value), itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] } }]
  })

  const followupData = await getFollowupTrend() as any[]
  initLineChart(followupTrendRef.value!, followupData, '#67c23a')

  const residentData = await getResidentTrend() as any[]
  initLineChart(residentTrendRef.value!, residentData, '#409EFF')
})
</script>

<style scoped>
.stat-card {
  border-radius: 12px;
  padding: 24px 20px;
  color: #fff;
  position: relative;
  overflow: hidden;
  min-height: 100px;
}
.stat-value { font-size: 38px; font-weight: 700; line-height: 1; }
.stat-label { font-size: 14px; opacity: 0.9; margin-top: 8px; }
.stat-bg-icon {
  position: absolute;
  right: 16px;
  bottom: 12px;
  font-size: 52px;
  opacity: 0.15;
}
</style>
