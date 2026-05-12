<template>
  <div>
    <div class="page-header">
      <h2>操作日志</h2>
    </div>

    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="操作人">
          <el-input v-model="query.username" placeholder="账号" clearable style="width:120px" />
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="query.module" placeholder="模块名" clearable style="width:120px" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="query.startDate" type="date" value-format="YYYY-MM-DD" style="width:140px" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="query.endDate" type="date" value-format="YYYY-MM-DD" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" stripe v-loading="loading" size="small">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="操作人账号" width="110" />
        <el-table-column prop="realName" label="操作人姓名" width="100" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="opType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="opTypeColor(row.opType)">{{ row.opType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="requestUri" label="请求路径" min-width="160" show-overflow-tooltip />
        <el-table-column prop="requestMethod" label="方法" width="70" />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="costTime" label="耗时(ms)" width="90" />
        <el-table-column prop="createTime" label="操作时间" width="160" />
      </el-table>
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getOplogPage } from '@/api/oplog'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({
  pageNum: 1, pageSize: 20,
  username: '', module: '',
  startDate: '', endDate: ''
})

function opTypeColor(t: string) {
  return t === 'CREATE' ? 'success' : t === 'UPDATE' ? 'warning' : t === 'DELETE' ? 'danger' : 'info'
}

async function loadData() {
  loading.value = true
  try {
    const params: any = { ...query.value }
    if (!params.username) delete params.username
    if (!params.module) delete params.module
    if (!params.startDate) delete params.startDate
    if (!params.endDate) delete params.endDate
    const res = await getOplogPage(params) as any
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}

function resetQuery() {
  query.value = { pageNum: 1, pageSize: 20, username: '', module: '', startDate: '', endDate: '' }
  loadData()
}

onMounted(loadData)
</script>
