<template>
  <div class="my-appointments">
    <section class="appointments-head">
      <div>
        <el-button text class="back-button" @click="router.push('/hospital/home')">← 返回首页</el-button>
        <div class="title-line">
          <h1 class="page-title">我的挂号</h1>
          <span class="head-note">查看待就诊、签到、完成与失效记录</span>
        </div>
      </div>
      <div class="head-stats">
        <div class="stat-pill primary">
          <span class="stat-value">{{ appointments.length }}</span>
          <span class="stat-label">当前记录</span>
        </div>
        <div class="stat-pill">
          <span class="stat-value">{{ activeCount }}</span>
          <span class="stat-label">待处理</span>
        </div>
      </div>
    </section>

    <!-- 状态筛选 -->
    <div class="status-tabs">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        type="button"
        class="status-tab"
        :class="{ active: statusFilter === tab.value }"
        @click="changeStatus(tab.value)"
      >
        <span>{{ tab.label }}</span>
        <strong>{{ tab.count }}</strong>
      </button>
    </div>

    <!-- 挂号列表 -->
    <div class="appointment-list" v-loading="loading">
      <div
        v-for="apt in appointments"
        :key="apt.appointmentID"
        class="appointment-card"
      >
        <div class="card-main">
          <div class="doctor-info">
            <el-avatar :size="56" class="doctor-avatar">
              {{ apt.doctorName?.charAt(0) }}
            </el-avatar>
            <div class="info">
              <div class="doctor-line">
                <div class="doctor-name">{{ apt.doctorName || '暂未分配医生' }}</div>
                <el-tag :type="getStatusType(apt.appointmentStatus)" effect="light">
                  {{ getStatusText(apt.appointmentStatus) }}
                </el-tag>
              </div>
              <div class="department">{{ apt.departmentName || '门诊科室' }}</div>
            </div>
          </div>

          <div class="appointment-meta">
            <div class="meta-cell">
              <span class="meta-label">就诊日期</span>
              <span class="meta-value">{{ apt.appointmentDate }}</span>
            </div>
            <div class="meta-cell">
              <span class="meta-label">时间段</span>
              <span class="meta-value">{{ getTimeSlotText(apt.timeSlot) }}</span>
            </div>
            <div class="meta-cell">
              <span class="meta-label">就诊序号</span>
              <span class="meta-value no-value">{{ apt.appointmentNumber }}</span>
            </div>
            <div class="meta-cell">
              <span class="meta-label">挂号费用</span>
              <span class="meta-value fee-value">¥{{ Number(apt.paymentAmount || 0).toFixed(2) }}</span>
            </div>
          </div>

          <div class="card-actions">
            <template v-if="apt.appointmentStatus === 1 && !isCheckInExpired(apt)">
              <el-button size="small" @click="handleCancel(apt.appointmentID)">取消预约</el-button>
              <el-button type="primary" size="small" @click="handleCheckIn(apt.appointmentID)">签到</el-button>
            </template>

            <template v-else-if="apt.appointmentStatus === 1 && isCheckInExpired(apt)">
              <el-button type="warning" size="small" disabled>签到已截止</el-button>
              <span class="waiting-hint danger">超过预约开始 5 分钟未签到</span>
            </template>

            <template v-else-if="apt.appointmentStatus === 2">
              <el-button type="success" size="small" disabled>已签到</el-button>
              <span class="waiting-hint">请等待叫号</span>
            </template>

            <template v-else-if="apt.appointmentStatus === 8">
              <el-button type="info" size="small" disabled>已失效</el-button>
              <span class="waiting-hint success">请重新预约其他时段</span>
            </template>

            <template v-else-if="apt.appointmentStatus === 4 && apt.isReviewed === 0">
              <el-button size="small" @click="goToReview(apt)">去评价</el-button>
            </template>

            <template v-else>
              <span class="waiting-hint muted">无需处理</span>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && appointments.length === 0" description="暂无挂号记录">
      <el-button type="primary" @click="$router.push('/hospital/home')">去预约</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { appointmentAPI } from '@/api/hospital/appointment'
import type { Appointment } from '@/api/hospital/appointment'

const router = useRouter()
const loading = ref(false)
const appointments = ref<Appointment[]>([])
const statusFilter = ref('')

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待就诊', value: '1' },
  { label: '已签到', value: '2' },
  { label: '已完成', value: '4' },
  { label: '已取消', value: '5' },
  { label: '已失效', value: '8' }
]

const activeCount = computed(() => {
  return appointments.value.filter(item => item.appointmentStatus === 1 || item.appointmentStatus === 2).length
})

const statusTabs = computed(() => {
  return statusOptions.map(item => ({
    ...item,
    count: item.value
      ? appointments.value.filter(apt => String(apt.appointmentStatus) === item.value).length
      : appointments.value.length
  }))
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'info', 1: 'primary', 2: 'warning', 3: 'warning', 4: 'success', 5: 'info', 6: 'info', 7: 'danger', 8: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待支付', 1: '已预约', 2: '已签到', 3: '就诊中', 4: '已完成', 5: '已取消', 6: '已退号', 7: '已爽约', 8: '已失效'
  }
  return texts[status] || '未知'
}

const getTimeSlotText = (timeSlot: number) => {
  if (timeSlot === 1) return '上午'
  if (timeSlot === 2) return '下午'
  return '夜诊'
}

const getAppointmentStartTime = (apt: Appointment) => {
  if (apt.scheduleStartTime) {
    return apt.scheduleStartTime
  }
  const fallbackTimes: Record<number, string> = {
    1: '08:00:00',
    2: '14:00:00',
    3: '19:00:00'
  }
  return fallbackTimes[apt.timeSlot] || '08:00:00'
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

const isCheckInExpired = (apt: Appointment) => {
  if (apt.appointmentStatus !== 1) {
    return false
  }

  const [year, month, day] = apt.appointmentDate.split('-').map(Number)
  const [hour, minute, second] = getAppointmentStartTime(apt).split(':').map(Number)
  const startTime = new Date(year, month - 1, day, hour, minute, second || 0)
  return Date.now() > startTime.getTime() + 5 * 60 * 1000
}

const loadAppointments = async () => {
  loading.value = true
  try {
    const patientID = getCurrentPatientId()
    if (!patientID) {
      appointments.value = []
      ElMessage.warning('请先登录患者账号')
      router.push('/hospital/login')
      return
    }
    const status = statusFilter.value ? Number(statusFilter.value) : undefined
    const res = await appointmentAPI.getByPatient(patientID, status)
    if (res.code === 200) {
      appointments.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载挂号记录失败')
  } finally {
    loading.value = false
  }
}

const changeStatus = (value: string) => {
  statusFilter.value = value
  loadAppointments()
}

const handleCancel = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await appointmentAPI.cancel(id, '用户主动取消')
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const handleCheckIn = async (id: string) => {
  try {
    const res = await appointmentAPI.checkIn(id)
    if (res.code === 200) {
      ElMessage.success('签到成功')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '签到失败')
    }
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

const goToReview = (apt: Appointment) => {
  router.push(`/hospital/review/${apt.appointmentID}?doctorId=${apt.doctorID}`)
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.my-appointments {
  min-height: 100vh;
  background: #f5f8fc;
  padding: 18px 20px 28px;
  max-width: 1440px;
  margin: 0 auto;
  color: #1f2937;
}

.appointments-head,
.status-tabs,
.appointment-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

.appointments-head {
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.back-button {
  color: #1677ff;
  padding-left: 0;
}

.title-line {
  margin-top: 4px;
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}

.page-title {
  margin: 0;
  font-size: 26px;
  line-height: 1.25;
  color: #111827;
  font-weight: 700;
}

.head-note {
  color: #6b7280;
  font-size: 13px;
}

.head-stats {
  display: flex;
  gap: 10px;
  flex: 0 0 auto;
}

.stat-pill {
  min-width: 92px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #e5e7eb;
}

.stat-pill.primary {
  background: rgba(22, 119, 255, 0.08);
  border-color: rgba(22, 119, 255, 0.18);
}

.stat-value {
  display: block;
  color: #1677ff;
  font-size: 22px;
  line-height: 1;
  font-weight: 700;
}

.stat-label {
  display: block;
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
}

.status-tabs {
  margin-bottom: 16px;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  overflow-x: auto;
}

.status-tab {
  height: 38px;
  padding: 0 14px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: transparent;
  color: #4b5563;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.status-tab strong {
  min-width: 22px;
  height: 22px;
  padding: 0 6px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f3f6fb;
  color: #6b7280;
  font-size: 12px;
}

.status-tab:hover,
.status-tab.active {
  color: #1677ff;
  background: rgba(22, 119, 255, 0.08);
  border-color: rgba(22, 119, 255, 0.14);
}

.status-tab.active strong {
  color: #ffffff;
  background: #1677ff;
}

.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appointment-card {
  padding: 16px 18px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.appointment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(31, 41, 55, 0.08);
}

.card-main {
  display: grid;
  grid-template-columns: minmax(260px, 1.05fr) minmax(480px, 1.7fr) minmax(180px, 0.55fr);
  gap: 18px;
  align-items: center;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.doctor-avatar {
  flex: 0 0 auto;
  background: linear-gradient(135deg, #1677ff, #74b9ff);
}

.info {
  min-width: 0;
}

.doctor-line {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.doctor-name {
  font-size: 20px;
  line-height: 1.25;
  font-weight: 700;
  color: #111827;
}

.department {
  color: #6b7280;
  font-size: 13px;
  margin-top: 4px;
}

.appointment-meta {
  min-width: 0;
  display: grid;
  grid-template-columns: 1.2fr 0.7fr 0.7fr 0.7fr;
  gap: 10px;
}

.meta-cell {
  min-height: 54px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fbff;
  border: 1px solid #eef2f7;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.meta-label {
  color: #6b7280;
  font-size: 12px;
}

.meta-value {
  margin-top: 4px;
  color: #1f2937;
  font-size: 15px;
  font-weight: 600;
}

.no-value {
  color: #1677ff;
}

.fee-value {
  color: #ef4444;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.waiting-hint {
  color: #22c55e;
  font-size: 13px;
  white-space: nowrap;
}

.waiting-hint.danger {
  color: #ef4444;
}

.waiting-hint.success {
  color: #16a34a;
}

.waiting-hint.muted {
  color: #9ca3af;
}

@media (max-width: 1180px) {
  .card-main {
    grid-template-columns: 1fr;
    align-items: stretch;
  }

  .card-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .appointments-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .head-stats {
    width: 100%;
  }

  .stat-pill {
    flex: 1;
  }

  .appointment-meta {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 520px) {
  .appointment-meta {
    grid-template-columns: 1fr;
  }
}
</style>
