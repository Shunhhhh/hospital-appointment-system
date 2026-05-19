<template>
  <div class="my-appointments">
    <div class="back-bar">
      <el-button text @click="router.push('/hospital/home')">← 返回首页</el-button>
    </div>
    <h1 class="page-title">我的挂号</h1>

    <!-- 状态筛选 -->
    <div class="status-tabs">
      <el-radio-group v-model="statusFilter" @change="handleFilter">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="1">待就诊</el-radio-button>
        <el-radio-button label="2">已签到</el-radio-button>
        <el-radio-button label="4">已完成</el-radio-button>
        <el-radio-button label="5">已取消</el-radio-button>
        <el-radio-button label="8">已失效</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 挂号列表 -->
    <div class="appointment-list" v-loading="loading">
      <div
        v-for="apt in appointments"
        :key="apt.appointmentID"
        class="appointment-card"
      >
        <div class="card-header">
          <div class="doctor-info">
            <el-avatar :size="50">
              {{ apt.doctorName?.charAt(0) }}
            </el-avatar>
            <div class="info">
              <div class="doctor-name">{{ apt.doctorName }}</div>
              <div class="department">{{ apt.departmentName }}</div>
            </div>
          </div>
          <el-tag :type="getStatusType(apt.appointmentStatus)">
            {{ getStatusText(apt.appointmentStatus) }}
          </el-tag>
        </div>

        <div class="card-body">
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span>{{ apt.appointmentDate }} {{ apt.timeSlot === 1 ? '上午' : apt.timeSlot === 2 ? '下午' : '夜诊' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Tickets /></el-icon>
            <span>就诊序号：{{ apt.appointmentNumber }}</span>
          </div>
        </div>

        <div class="card-footer" v-if="apt.appointmentStatus === 1 && !isCheckInExpired(apt)">
          <el-button size="small" @click="handleCancel(apt.appointmentID)">取消预约</el-button>
          <el-button type="primary" size="small" @click="handleCheckIn(apt.appointmentID)">签到</el-button>
        </div>

        <div class="card-footer" v-if="apt.appointmentStatus === 1 && isCheckInExpired(apt)">
          <el-button type="warning" size="small" disabled>签到已截止</el-button>
          <span class="waiting-hint">超过预约开始 5 分钟未签到会自动失效</span>
        </div>

        <div class="card-footer" v-if="apt.appointmentStatus === 2">
          <el-button type="success" size="small" disabled>已签到</el-button>
          <span class="waiting-hint">请等待叫号</span>
        </div>

        <div class="card-footer" v-if="apt.appointmentStatus === 8">
          <el-button type="info" size="small" disabled>已失效</el-button>
          <span class="waiting-hint">请重新预约其他时段</span>
        </div>

        <div class="card-footer" v-if="apt.appointmentStatus === 4 && apt.isReviewed === 0">
          <el-button size="small" @click="goToReview(apt)">去评价</el-button>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackHomeButton from '@/components/hospital/BackHomeButton.vue'
import { appointmentAPI } from '@/api/hospital/appointment'
import type { Appointment } from '@/api/hospital/appointment'

const router = useRouter()
const loading = ref(false)
const appointments = ref<Appointment[]>([])
const statusFilter = ref('')

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
    // TODO: 从用户信息获取patientID
    const patientID = 2001 // 临时测试ID
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

const handleFilter = () => {
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
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.status-tabs {
  margin-bottom: 20px;
}

.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.appointment-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.doctor-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.department {
  color: #999;
  font-size: 13px;
}

.card-body {
  padding: 15px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.card-footer {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}

.waiting-hint {
  color: #67c23a;
  font-size: 14px;
}

.back-bar {
  margin-bottom: 16px;
}
</style>
