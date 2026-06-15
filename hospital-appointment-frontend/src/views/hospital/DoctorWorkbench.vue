<template>
  <div class="doctor-workbench">
    <div class="header">
      <h1>医生工作台</h1>
      <div class="doctor-info">
        <el-dropdown trigger="click">
          <span class="user-dropdown">
            <el-avatar :size="40">{{ doctor?.doctorName?.charAt(0) }}</el-avatar>
            <span>{{ doctor?.doctorName }} {{ doctor?.title }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 今日概览 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ stats.todayTotal }}</div>
        <div class="stat-label">今日挂号</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.checkedIn }}</div>
        <div class="stat-label">已签到</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.finished }}</div>
        <div class="stat-label">已完成</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.remaining }}</div>
        <div class="stat-label">待接诊</div>
      </div>
    </div>

    <!-- 叫号面板 -->
    <div class="call-panel" v-if="currentPatient">
      <div class="call-header">当前就诊</div>
      <div class="call-body">
        <div class="current-patient-info">
          <span class="call-number">{{ currentPatient.appointmentNumber }}号</span>
          <span class="patient-name">{{ currentPatient.patientName || '患者' + currentPatient.patientID }}</span>
          <span class="chief-complaint">{{ currentPatient.chiefComplaint || '无主诉' }}</span>
        </div>
        <el-button type="success" @click="finishVisit(currentPatient)">完成就诊</el-button>
      </div>
    </div>

    <!-- 患者队列 -->
    <div class="queue-section">
      <div class="section-header">
        <h2>待就诊队列 <span class="auto-refresh-tip">自动刷新中</span></h2>
        <el-button size="small" @click="loadAppointments">刷新</el-button>
      </div>

      <div class="queue-list" v-loading="loading">
        <div
          v-for="row in waitingAppointments"
          :key="row.appointmentID"
          class="queue-item"
          :class="{ 'is-called': row.appointmentStatus === 3 }"
        >
          <div class="queue-number">
            <span class="num-badge">{{ row.appointmentNumber }}</span>
          </div>
          <div class="queue-info">
            <div class="queue-name">{{ row.patientName || '患者' + row.patientID }}</div>
            <div class="queue-meta">
              {{ row.timeSlot === 1 ? '上午' : row.timeSlot === 2 ? '下午' : '夜诊' }}
              · {{ row.chiefComplaint || '无主诉' }}
            </div>
          </div>
          <div class="queue-status">
            <el-tag :type="getStatusType(row.appointmentStatus)" size="small" effect="light">
              {{ getStatusText(row.appointmentStatus) }}
            </el-tag>
          </div>
          <div class="queue-actions">
            <el-button
              v-if="row.appointmentStatus === 2"
              type="primary"
              size="small"
              @click="handleCallNext(row)"
            >
              叫号
            </el-button>
            <el-button
              v-if="row.appointmentStatus === 2"
              size="small"
              @click="startVisit(row)"
            >
              直接接诊
            </el-button>
            <el-button
              v-if="row.appointmentStatus >= 4"
              type="info"
              size="small"
              disabled
            >
              已完成
            </el-button>
          </div>
        </div>

        <div v-if="waitingAppointments.length === 0 && !loading" class="empty-queue">
          <el-empty description="暂无待就诊患者" :image-size="80" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

import { appointmentAPI } from '@/api/hospital/appointment'
import type { Appointment } from '@/api/hospital/appointment'

const router = useRouter()
const loading = ref(false)
const appointments = ref<Appointment[]>([])
const doctor = ref<any>(null)
let refreshTimer: ReturnType<typeof setInterval> | null = null

const currentPatient = computed(() => {
  return appointments.value.find(a => a.appointmentStatus === 3) || null
})

const waitingAppointments = computed(() => {
  return appointments.value.filter(a =>
    a.appointmentStatus === 2 || a.appointmentStatus === 3
  )
})

const stats = computed(() => {
  const allToday = appointments.value
  const todayTotal = allToday.length
  const checkedIn = allToday.filter(a =>
    a.appointmentStatus != null && a.appointmentStatus >= 2 && a.appointmentStatus <= 4
  ).length
  const finished = allToday.filter(a =>
    a.appointmentStatus != null && a.appointmentStatus >= 4
  ).length
  const remaining = allToday.filter(a =>
    a.appointmentStatus === 2 || a.appointmentStatus === 3
  ).length
  return { todayTotal, checkedIn, finished, remaining }
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'info', 6: 'info', 7: 'danger', 8: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    1: '已预约', 2: '已签到', 3: '就诊中', 4: '已完成', 5: '已取消', 6: '已退号', 7: '已爽约', 8: '已失效'
  }
  return texts[status] || '未知'
}

const loadDoctor = () => {
  const userStr = localStorage.getItem('hospital_user')
  if (userStr) {
    const user = JSON.parse(userStr)
    if (user.type === 'doctor') {
      doctor.value = user
    }
  }
}

const handleLogout = async () => {
  await ElMessageBox.confirm('确认退出登录？', '提示')
  localStorage.removeItem('hospital_user')
  ElMessage.success('已退出')
  router.push('/hospital/login')
}

const loadAppointments = async () => {
  if (!doctor.value) return
  loading.value = true
  try {
    const res = await appointmentAPI.getToday(doctor.value.doctorID)
    if (res.code === 200) {
      appointments.value = (res.data || []).sort((a: Appointment, b: Appointment) => {
        const slotA = a.timeSlot || 0
        const slotB = b.timeSlot || 0
        if (slotA !== slotB) return slotA - slotB
        return (a.appointmentNumber || 0) - (b.appointmentNumber || 0)
      })
    }
  } catch (error) {
    ElMessage.error('加载挂号列表失败')
  } finally {
    loading.value = false
  }
}

const handleCallNext = async (row: Appointment) => {
  try {
    const res = await appointmentAPI.callNext(row.appointmentID)
    if (res.code === 200) {
      ElMessage.success(`已叫号：${row.appointmentNumber}号 ${row.patientName || ''}`)
      loadAppointments()
    } else {
      ElMessage.error(res.message || '叫号失败')
    }
  } catch (error) {
    ElMessage.error('叫号失败')
  }
}

const startVisit = async (row: Appointment) => {
  try {
    const res = await appointmentAPI.confirm(row.appointmentID)
    if (res.code === 200) {
      ElMessage.success('开始接诊')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const finishVisit = async (row: Appointment) => {
  try {
    const res = await appointmentAPI.finish(row.appointmentID)
    if (res.code === 200) {
      ElMessage.success('就诊完成')
      loadAppointments()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadDoctor()
  if (doctor.value) {
    loadAppointments()
    // 每10秒自动刷新队列
    refreshTimer = setInterval(loadAppointments, 10000)
  }
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})
</script>

<style scoped>
.doctor-workbench {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 24px;
  color: #333;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #666;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 10px;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

/* 叫号面板 */
.call-panel {
  background: linear-gradient(135deg, #1677ff, #4dabf7);
  padding: 20px 24px;
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(22, 119, 255, 0.25);
}

.call-header {
  font-size: 13px;
  opacity: 0.85;
  margin-bottom: 8px;
}

.call-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-patient-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.call-number {
  font-size: 32px;
  font-weight: 700;
}

.patient-name {
  font-size: 20px;
  font-weight: 600;
}

.chief-complaint {
  font-size: 14px;
  opacity: 0.8;
}

/* 队列列表 */
.queue-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.auto-refresh-tip {
  font-size: 11px;
  color: #22c55e;
  font-weight: 400;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.queue-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  transition: background 0.2s;
}

.queue-item:hover {
  background: #f8fbff;
}

.queue-item.is-called {
  background: rgba(22, 119, 255, 0.04);
  border-left: 3px solid #1677ff;
}

.num-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #1677ff;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}

.is-called .num-badge {
  background: #f59e0b;
}

.queue-info {
  flex: 1;
  min-width: 0;
}

.queue-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

.queue-meta {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}

.queue-status {
  flex: 0 0 auto;
}

.queue-actions {
  flex: 0 0 auto;
  display: flex;
  gap: 8px;
}

.empty-queue {
  padding: 40px 0;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-dropdown:hover {
  background: #f0f2f5;
}
</style>
