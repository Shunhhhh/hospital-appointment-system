<template>
  <div class="records-page">
    <div class="page-shell">
      <SidebarNav />
      <main class="main-area">
        <div class="page-header">
          <h1 class="page-title">门诊记录</h1>
          <span class="page-tip">查看历史就诊病历和处方</span>
        </div>

        <div class="records-list" v-loading="loading">
          <div
            v-for="item in records"
            :key="item.recordID"
            class="record-card"
            @click="goToDetail(item.recordID)"
          >
            <div class="card-left">
              <div class="record-date">
                <span class="date-day">{{ formatDay(item.createTime) }}</span>
                <span class="date-month">{{ formatMonth(item.createTime) }}</span>
              </div>
            </div>
            <div class="card-center">
              <div class="record-doctor">{{ item.doctorName || '未知医生' }}</div>
              <div class="record-dept">{{ item.departmentName || '未知科室' }}</div>
              <div class="record-complaint" v-if="item.chiefComplaint">
                主诉：{{ item.chiefComplaint.length > 30 ? item.chiefComplaint.slice(0, 30) + '...' : item.chiefComplaint }}
              </div>
            </div>
            <div class="card-right">
              <div class="record-diagnosis" v-if="item.finalDiagnosis || item.preliminaryDiagnosis">
                <el-tag type="warning" effect="light" size="small">诊断</el-tag>
                <span>{{ item.finalDiagnosis || item.preliminaryDiagnosis }}</span>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
          </div>

          <el-empty v-if="!loading && records.length === 0" description="暂无门诊记录">
            <el-button type="primary" @click="$router.push('/hospital/home')">去预约挂号</el-button>
          </el-empty>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import SidebarNav from '@/components/hospital/SidebarNav.vue'
import { medicalRecordAPI, type MedicalRecord } from '@/api/hospital/medicalRecord'

const router = useRouter()
const loading = ref(false)
const records = ref<MedicalRecord[]>([])

const formatDay = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.getDate().toString().padStart(2, '0')
}

const formatMonth = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}/${d.getMonth() + 1}`
}

const getCurrentPatientId = () => {
  const raw = localStorage.getItem('hospital_user')
  if (!raw) return null
  try {
    const user = JSON.parse(raw)
    return user.patientID ? Number(user.patientID) : null
  } catch {
    return null
  }
}

const loadRecords = async () => {
  const patientId = getCurrentPatientId()
  if (!patientId) {
    ElMessage.warning('请先登录')
    router.push('/hospital/login')
    return
  }
  loading.value = true
  try {
    const res = await medicalRecordAPI.getByPatient(patientId)
    if (res.code === 200) {
      records.value = res.data || []
    }
  } catch {
    ElMessage.error('加载门诊记录失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (recordId: number) => {
  router.push(`/hospital/medical-record/${recordId}`)
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.records-page {
  min-height: 100vh;
  background: #f5f8fc;
}

.page-shell {
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px;
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 16px;
}

.main-area {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.page-tip {
  color: #9ca3af;
  font-size: 13px;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(31, 41, 55, 0.04);
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(31, 41, 55, 0.08);
}

.card-left {
  flex: 0 0 auto;
}

.record-date {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #1677ff, #63b3ff);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.date-day {
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}

.date-month {
  font-size: 11px;
  opacity: 0.85;
  margin-top: 2px;
}

.card-center {
  flex: 1;
  min-width: 0;
}

.record-doctor {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}

.record-dept {
  font-size: 13px;
  color: #6b7280;
  margin-top: 2px;
}

.record-complaint {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 6px;
}

.card-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.record-diagnosis {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #374151;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  color: #d1d5db;
  font-size: 18px;
}

@media (max-width: 960px) {
  .page-shell {
    grid-template-columns: 1fr;
  }

  .card-right {
    display: none;
  }
}
</style>
