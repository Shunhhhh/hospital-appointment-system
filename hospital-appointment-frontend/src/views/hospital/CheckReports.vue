<template>
  <div class="reports-page">
    <div class="page-shell">
      <SidebarNav />
      <main class="main-area">
        <section class="reports-head">
          <div class="title-line">
            <h1 class="page-title">检查报告</h1>
            <span class="head-note">查看化验、影像与体检报告</span>
          </div>
        </section>

        <div class="filter-bar">
          <el-select v-model="reportType" placeholder="报告类型" clearable>
            <el-option label="全部" value="" />
            <el-option label="化验报告" value="lab" />
            <el-option label="影像报告" value="image" />
            <el-option label="体检报告" value="physical" />
          </el-select>
        </div>

        <div class="report-list" v-loading="loading">
          <div v-for="report in filteredReports" :key="report.reportID" class="report-card">
            <div class="report-icon">
              <el-icon size="28"><Document /></el-icon>
            </div>
            <div class="report-main">
              <div class="report-name">{{ report.reportName }}</div>
              <div class="report-meta">
                <span>{{ report.departmentName }}</span>
                <span>{{ report.doctorName }}</span>
                <span>{{ report.checkDate }}</span>
              </div>
            </div>
            <div class="report-status">
              <el-tag :type="report.reportStatus === 2 ? 'success' : 'warning'" size="small">
                {{ report.reportStatus === 2 ? '已完成' : '处理中' }}
              </el-tag>
            </div>
            <div class="report-action">
              <el-button type="primary" size="small" @click="viewReport(report)">查看报告</el-button>
            </div>
          </div>
          <el-empty v-if="!loading && filteredReports.length === 0" description="暂无检查报告" />
        </div>

        <el-dialog v-model="dialogVisible" title="报告详情" width="700px">
          <div v-if="currentReport" class="report-detail">
            <div class="detail-item"><strong>报告名称：</strong>{{ currentReport.reportName }}</div>
            <div class="detail-item"><strong>检查科室：</strong>{{ currentReport.departmentName }}</div>
            <div class="detail-item"><strong>检查医生：</strong>{{ currentReport.doctorName }}</div>
            <div class="detail-item"><strong>检查日期：</strong>{{ currentReport.checkDate }}</div>
            <div class="detail-item"><strong>报告状态：</strong>{{ currentReport.reportStatus === 2 ? '已完成' : '处理中' }}</div>
            <div class="detail-content">
              <strong>检查结果：</strong>
              <p>{{ currentReport.reportContent || '暂无' }}</p>
            </div>
            <div class="detail-content">
              <strong>医生建议：</strong>
              <p>{{ currentReport.doctorAdvice || '暂无' }}</p>
            </div>
          </div>
        </el-dialog>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import SidebarNav from '@/components/hospital/SidebarNav.vue'
import { Document } from '@element-plus/icons-vue'
import { checkReportAPI } from '@/api/hospital/checkReport'
import type { CheckReport } from '@/api/hospital/checkReport'

const loading = ref(false)
const reportType = ref('')
const dialogVisible = ref(false)
const currentReport = ref<CheckReport | null>(null)
const reports = ref<CheckReport[]>([])

const getPatientId = () => {
  const raw = localStorage.getItem('hospital_user')
  if (!raw) return null
  const user = JSON.parse(raw)
  return user.patientID || null
}

const loadReports = async () => {
  const patientId = getPatientId()
  if (!patientId) return
  loading.value = true
  try {
    const res = await checkReportAPI.getByPatient(patientId, reportType.value || undefined)
    if (res.code === 200) {
      reports.value = res.data || []
    }
  } catch {
    ElMessage.error('加载报告失败')
  } finally {
    loading.value = false
  }
}

const filteredReports = computed(() => reports.value)

const viewReport = (report: CheckReport) => {
  currentReport.value = report
  dialogVisible.value = true
}

watch(reportType, () => loadReports())

onMounted(() => loadReports())
</script>

<style scoped>
.reports-page {
  min-height: 100vh;
  background: #f5f8fc;
  color: #1f2937;
}

.top-header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 20;
}

.brand { display: flex; align-items: center; gap: 12px; }
.brand-logo {
  width: 42px; height: 42px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; background: linear-gradient(135deg, #1677ff 0%, #63b3ff 100%);
  font-weight: 700; font-size: 14px;
}
.brand-title { font-size: 16px; font-weight: 700; }
.brand-subtitle { font-size: 12px; color: #6b7280; }

.page-shell {
  max-width: 1440px; margin: 0 auto; padding: 16px;
  display: grid; grid-template-columns: 220px minmax(0, 1fr); gap: 16px;
}

.main-area { min-width: 0; display: flex; flex-direction: column; gap: 16px; }

.reports-head {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 18px 20px;
}

.title-line { display: flex; align-items: baseline; gap: 12px; }
.page-title { margin: 0; font-size: 22px; }
.head-note { color: #6b7280; font-size: 13px; }

.filter-bar {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px 16px;
}

.report-list {
  display: flex; flex-direction: column; gap: 12px;
}

.report-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px 20px;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto auto;
  gap: 16px;
  align-items: center;
  transition: box-shadow 0.2s;
}

.report-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.report-icon {
  width: 48px; height: 48px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(22,119,255,0.08); color: #1677ff;
  border-radius: 10px;
}

.report-name { font-size: 16px; font-weight: 700; color: #1f2937; }
.report-meta { margin-top: 4px; display: flex; gap: 16px; color: #6b7280; font-size: 13px; }

.report-detail .detail-item { margin-bottom: 12px; }
.report-detail .detail-content { margin-bottom: 16px; line-height: 1.8; }
.report-detail .detail-content p { color: #4b5563; margin-top: 4px; }

@media (max-width: 1180px) {
  .page-shell { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .report-card { grid-template-columns: 1fr; }
}
</style>
