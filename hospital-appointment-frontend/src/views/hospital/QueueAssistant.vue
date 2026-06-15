<template>
  <div class="queue-page">
        <div class="page-head card-base">
          <div class="page-head-main">
            <div class="page-title-row">
              <h1 class="page-title">排队助手</h1>
              <span class="page-subtitle">实时查看就诊排队进度，每 10 秒自动刷新</span>
            </div>
          </div>
        </div>

        <!-- 排队中 -->
        <div v-if="queueList.length > 0" class="queue-list">
          <div v-for="item in queueList" :key="item.appointmentID" class="queue-card">
            <div class="card-header">
              <div class="doctor-info">
                <el-avatar :size="48" class="doctor-avatar">
                  {{ item.doctorName?.charAt(0) || '医' }}
                </el-avatar>
                <div>
                  <div class="doctor-name">{{ item.doctorName || '待分配' }}</div>
                  <div class="department-name">{{ item.departmentName || '' }}</div>
                </div>
              </div>
              <el-tag :type="item.queueStatus === 'seeing' ? 'warning' : 'primary'" effect="light">
                {{ item.queueStatus === 'seeing' ? '就诊中' : '等待中' }}
              </el-tag>
            </div>

            <!-- 进度展示 -->
            <div class="queue-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: item.progressPercent + '%' }"></div>
              </div>
              <div class="progress-nodes">
                <div class="node" :class="{ active: item.currentCallNumber > 0 }">
                  <span class="node-dot"></span>
                  <span class="node-label">当前叫号<br/>{{ item.currentCallNumber || '-' }}号</span>
                </div>
                <div class="node my-node" :class="{ active: true }">
                  <span class="node-dot"></span>
                  <span class="node-label">我的序号<br/>{{ item.myNumber }}号</span>
                </div>
              </div>
            </div>

            <!-- 统计信息 -->
            <div class="stats-row">
              <div class="stat-item">
                <span class="stat-value">{{ item.aheadCount }}</span>
                <span class="stat-label">前面等待</span>
              </div>
              <div class="stat-item">
                <span class="stat-value highlight">{{ item.estimatedWaitMinutes }}</span>
                <span class="stat-label">预计等待(分钟)</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ getTimeSlotText(item.timeSlot) }}</span>
                <span class="stat-label">就诊时段</span>
              </div>
            </div>

            <!-- 叫到你时提示 -->
            <div v-if="item.aheadCount <= 2 && item.queueStatus === 'waiting'" class="call-alert">
              <el-icon><Bell /></el-icon>
              <span>即将到您，请前往诊室候诊</span>
            </div>
          </div>
        </div>

        <!-- 无排队中时提示 -->
        <div v-else-if="!loading" class="empty-state">
          <el-empty description="暂无排队中的挂号">
            <template #image>
              <div class="empty-icon">📋</div>
            </template>
            <div class="empty-tips">
              <p>您当前没有处于"已签到"或"就诊中"状态的挂号</p>
              <el-button type="primary" @click="$router.push('/hospital/my-appointments')">查看我的预约</el-button>
              <el-button plain @click="$router.push('/hospital/home')">去预约挂号</el-button>
            </div>
          </el-empty>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="loading-area">
          <el-skeleton :rows="3" animated />
        </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { appointmentAPI } from '@/api/hospital/appointment'

interface QueueItem {
  appointmentID: string
  doctorName?: string
  departmentName?: string
  myNumber: number
  currentCallNumber: number
  aheadCount: number
  estimatedWaitMinutes: number
  appointmentStatus: number
  timeSlot: number
  progressPercent: number
  queueStatus: 'waiting' | 'seeing'
}

const loading = ref(false)
const queueList = ref<QueueItem[]>([])
let timer: ReturnType<typeof setInterval> | null = null

const getTimeSlotText = (slot: number) => {
  if (slot === 1) return '上午'
  if (slot === 2) return '下午'
  if (slot === 3) return '夜诊'
  return '未知'
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

const loadQueueData = async () => {
  const patientId = getCurrentPatientId()
  if (!patientId) return

  try {
    // 获取该患者所有"已签到"和"就诊中"的挂号
    const [checkedInRes, seeingRes] = await Promise.all([
      appointmentAPI.getByPatient(patientId, 2), // 已签到
      appointmentAPI.getByPatient(patientId, 3)  // 就诊中
    ])

    const allActive: any[] = []
    if (checkedInRes.code === 200 && checkedInRes.data) {
      allActive.push(...checkedInRes.data)
    }
    if (seeingRes.code === 200 && seeingRes.data) {
      allActive.push(...seeingRes.data)
    }

    if (allActive.length === 0) {
      queueList.value = []
      return
    }

    // 为每个挂号获取排队位置
    const items: QueueItem[] = []
    for (const apt of allActive) {
      try {
        const posRes = await appointmentAPI.getQueuePosition(apt.appointmentID)
        if (posRes.code === 200 && posRes.data) {
          const data = posRes.data
          const totalAhead = (data.aheadCount || 0)
          const progressPercent = Math.max(0, Math.min(100,
            totalAhead === 0 ? 80 : Math.round((1 - totalAhead / (totalAhead + 3)) * 100)
          ))
          items.push({
            appointmentID: apt.appointmentID,
            doctorName: data.doctorName || apt.doctorName,
            departmentName: data.departmentName || apt.departmentName,
            myNumber: data.myNumber,
            currentCallNumber: data.currentCallNumber,
            aheadCount: data.aheadCount,
            estimatedWaitMinutes: data.estimatedWaitMinutes,
            appointmentStatus: data.appointmentStatus,
            timeSlot: data.timeSlot,
            progressPercent,
            queueStatus: data.appointmentStatus === 3 ? 'seeing' : 'waiting'
          })
        }
      } catch {
        // 单个查询失败忽略
      }
    }

    // 就诊中的排最前面，等待的按序号排
    items.sort((a, b) => {
      if (a.queueStatus !== b.queueStatus) {
        return a.queueStatus === 'seeing' ? -1 : 1
      }
      return a.myNumber - b.myNumber
    })

    queueList.value = items
  } catch {
    // 静默处理
  }
}

onMounted(() => {
  const patientId = getCurrentPatientId()
  if (!patientId) {
    ElMessage.warning('请先登录')
    return
  }
  loadQueueData()
  // 每10秒刷新
  timer = setInterval(loadQueueData, 10000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.queue-page {
  min-height: 100vh;
}

.queue-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.queue-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(31, 41, 55, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.doctor-avatar {
  background: linear-gradient(135deg, #1677ff, #63b3ff);
}

.doctor-name {
  font-size: 18px;
  
  color: #111827;
}

.department-name {
  font-size: 13px;
  color: #6b7280;
  margin-top: 2px;
}

.queue-progress {
  margin-bottom: 20px;
}

.progress-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1677ff, #63b3ff);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-nodes {
  display: flex;
  justify-content: space-between;
  padding: 0 10%;
}

.node {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  opacity: 0.4;
}

.node.active {
  opacity: 1;
}

.node-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #d1d5db;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #d1d5db;
}

.node.active .node-dot {
  background: #1677ff;
  box-shadow: 0 0 0 4px rgba(22, 119, 255, 0.2);
}

.my-node .node-dot {
  background: #f59e0b !important;
  box-shadow: 0 0 0 4px rgba(245, 158, 11, 0.2) !important;
}

.node-label {
  font-size: 12px;
  text-align: center;
  color: #6b7280;
  line-height: 1.5;
}

.node-label strong {
  color: #f59e0b;
  font-size: 15px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  padding: 16px;
  background: #f8fbff;
  border-radius: 10px;
  border: 1px solid #eef2f7;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 28px;
  
  color: #1f2937;
}

.stat-value.highlight {
  color: #1677ff;
}

.stat-label {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #9ca3af;
}

.call-alert {
  margin-top: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.08), rgba(99, 179, 255, 0.08));
  border: 1px solid rgba(22, 119, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1677ff;
  font-size: 14px;
  
}

.empty-state {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-tips {
  margin-top: 12px;
}

.empty-tips p {
  color: #6b7280;
  margin-bottom: 16px;
}

.loading-area {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

</style>
