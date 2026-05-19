<template>
  <div class="doctor-workbench">
    <BackHomeButton />

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

    <!-- 患者队列 -->
    <div class="queue-section">
      <div class="section-header">
        <h2>今日患者队列</h2>
        <el-button type="primary" @click="loadAppointments">刷新</el-button>
      </div>

      <el-table :data="appointments" v-loading="loading" stripe>
        <el-table-column prop="appointmentNumber" label="序号" width="80" />
        <el-table-column label="患者姓名" width="120">
          <template #default="{ row }">
            {{ row.patientName || '患者' + row.patientID }}
          </template>
        </el-table-column>
        <el-table-column label="就诊时间" width="120">
          <template #default="{ row }">
            {{ row.timeSlot === 1 ? '上午' : row.timeSlot === 2 ? '下午' : '夜诊' }}
          </template>
        </el-table-column>
        <el-table-column label="主诉" min-width="200">
          <template #default="{ row }">
            {{ row.chiefComplaint || '无' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.appointmentStatus)">
              {{ getStatusText(row.appointmentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.appointmentStatus === 2"
              type="primary"
              size="small"
              @click="startVisit(row)"
            >
              接诊
            </el-button>
            <el-button
              v-if="row.appointmentStatus === 3"
              type="success"
              size="small"
              @click="finishVisit(row)"
            >
              完成就诊
            </el-button>
            <el-button
              v-if="row.appointmentStatus >= 4"
              type="info"
              size="small"
              disabled
            >
              已完成
            </el-button>
            <el-button
              v-if="row.appointmentStatus === 8"
              type="danger"
              size="small"
              disabled
            >
              已失效
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import BackHomeButton from '@/components/hospital/BackHomeButton.vue'
import { appointmentAPI } from '@/api/hospital/appointment'
import type { Appointment } from '@/api/hospital/appointment'

const router = useRouter()
const loading = ref(false)
const appointments = ref<Appointment[]>([])
const doctor = ref<any>(null)

const stats = computed(() => {
  const todayTotal = appointments.value.length
  const checkedIn = appointments.value.filter(a => a.appointmentStatus >= 2 && a.appointmentStatus <= 4).length
  const finished = appointments.value.filter(a => a.appointmentStatus >= 4).length
  const remaining = appointments.value.filter(a => a.appointmentStatus === 2 || a.appointmentStatus === 3).length
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
      appointments.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载挂号列表失败')
  } finally {
    loading.value = false
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
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  color: #333;
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
