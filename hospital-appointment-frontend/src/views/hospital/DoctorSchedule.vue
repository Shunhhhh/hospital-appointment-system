<template>
  <div class="appointment-page">
    <div class="back-bar">
      <el-button text @click="router.push('/hospital/home')">← 返回首页</el-button>
    </div>
    <h1 class="page-title">选择预约时间</h1>

    <!-- 医生信息卡片 -->
    <div class="doctor-card" v-if="doctor">
      <div class="doctor-avatar">
        <el-avatar :size="60" :src="doctor.doctorPhoto">
          {{ doctor.doctorName?.charAt(0) }}
        </el-avatar>
      </div>
      <div class="doctor-info">
        <div class="doctor-name">{{ doctor.doctorName }}</div>
        <div class="doctor-title">{{ doctor.title }}</div>
        <div class="doctor-dept">{{ doctor.departmentName }}</div>
      </div>
      <div class="doctor-fee">
        <div class="fee-label">挂号费</div>
        <div class="fee-price">¥{{ doctor.registrationFee }}</div>
      </div>
    </div>

    <!-- 日期选择 -->
    <div class="date-section">
      <h3 class="section-title">选择日期</h3>
      <div class="date-tabs">
        <div
          v-for="date in availableDates"
          :key="date.date"
          class="date-tab"
          :class="{ active: selectedDate === date.date, disabled: date.remainingSlots === 0 }"
          @click="selectDate(date)"
        >
          <div class="date-weekday">{{ date.weekday }}</div>
          <div class="date-day">{{ date.day }}</div>
          <div class="date-month">{{ date.month }}</div>
          <div class="date-slots" :class="{ full: date.remainingSlots === 0 }">
            {{ date.remainingSlots === 0 ? '已约满' : `剩余${date.remainingSlots}` }}
          </div>
        </div>
      </div>
    </div>

    <!-- 时段选择 -->
    <div class="time-section" v-if="selectedDate">
      <h3 class="section-title">选择时段</h3>
      <div class="time-slots">
        <div
          v-for="schedule in daySchedules"
          :key="schedule.scheduleID"
          class="time-slot"
          :class="{ disabled: schedule.remainingSlots === 0, selected: selectedSchedule?.scheduleID === schedule.scheduleID }"
          @click="selectSchedule(schedule)"
        >
          <div class="slot-time">
            {{ schedule.timeSlot === 1 ? '上午' : schedule.timeSlot === 2 ? '下午' : '夜诊' }}
          </div>
          <div class="slot-hours">{{ schedule.startTime }} - {{ schedule.endTime }}</div>
          <div class="slot-remaining" :class="{ full: schedule.remainingSlots === 0 }">
            {{ schedule.remainingSlots === 0 ? '已约满' : `剩余${schedule.remainingSlots}号` }}
          </div>
          <div class="slot-price">¥{{ schedule.price }}</div>
        </div>
      </div>
    </div>

    <!-- 病情描述 -->
    <div class="complaint-section" v-if="selectedSchedule">
      <h3 class="section-title">病情描述</h3>
      <el-input
        v-model="chiefComplaint"
        type="textarea"
        :rows="4"
        placeholder="请简单描述您的症状或病情（选填）"
        maxlength="200"
        show-word-limit
      />
    </div>

    <!-- 预约按钮 -->
    <div class="action-section">
      <el-button
        type="primary"
        size="large"
        :disabled="!selectedSchedule"
        @click="handleAppointment"
        :loading="submitting"
      >
        确认预约
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackHomeButton from '@/components/hospital/BackHomeButton.vue'
import { doctorAPI } from '@/api/hospital/doctor'
import { scheduleAPI } from '@/api/hospital/schedule'
import { appointmentAPI } from '@/api/hospital/appointment'
import type { Doctor } from '@/api/hospital/doctor'
import type { DoctorSchedule } from '@/api/hospital/schedule'

const route = useRoute()
const router = useRouter()
const doctor = ref<Doctor | null>(null)
const schedules = ref<DoctorSchedule[]>([])
const selectedDate = ref('')
const selectedSchedule = ref<DoctorSchedule | null>(null)
const chiefComplaint = ref('')
const submitting = ref(false)

const availableDates = computed(() => {
  const dates: any[] = []
  const today = new Date()
  for (let i = 0; i < 7; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const dateStr = date.toISOString().split('T')[0]
    const daySchedules = schedules.value.filter(s => s.scheduleDate === dateStr)
    const remainingSlots = daySchedules.reduce((sum, s) => sum + s.remainingSlots, 0)
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    dates.push({
      date: dateStr,
      weekday: weekdays[date.getDay()],
      day: date.getDate(),
      month: `${date.getMonth() + 1}月`,
      remainingSlots
    })
  }
  return dates
})

const daySchedules = computed(() => {
  if (!selectedDate.value) return []
  return schedules.value.filter(s => s.scheduleDate === selectedDate.value)
})

const loadDoctor = async () => {
  try {
    const doctorId = route.params.doctorId as string
    const res = await doctorAPI.getById(Number(doctorId))
    if (res.code === 200) {
      doctor.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载医生信息失败')
  }
}

const loadSchedules = async () => {
  try {
    const doctorId = route.params.doctorId as string
    const res = await scheduleAPI.getByDoctor(Number(doctorId))
    if (res.code === 200) {
      schedules.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载排班信息失败')
  }
}

const selectDate = (date: any) => {
  if (date.remainingSlots === 0) {
    ElMessage.warning('该日期已约满，请选择其他日期')
    return
  }
  selectedDate.value = date.date
  selectedSchedule.value = null
}

const selectSchedule = (schedule: DoctorSchedule) => {
  if (schedule.remainingSlots === 0) {
    ElMessage.warning('该时段已约满')
    return
  }
  selectedSchedule.value = schedule
}

const handleAppointment = async () => {
  if (!selectedSchedule.value) return

  try {
    await ElMessageBox.confirm(
      `确认预约 ${doctor.value?.doctorName} 医生？\n挂号费：¥${selectedSchedule.value.price}`,
      '预约确认',
      {
        confirmButtonText: '确认预约',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    submitting.value = true
    // TODO: 从用户信息获取patientID
    const patientID = 2001 // 临时测试ID
    const res = await appointmentAPI.create({
      patientID,
      scheduleID: selectedSchedule.value.scheduleID,
      appointmentDate: selectedSchedule.value.scheduleDate,
      timeSlot: selectedSchedule.value.timeSlot,
      chiefComplaint: chiefComplaint.value
    })

    if (res.code === 200) {
      ElMessage.success('预约成功！')
      router.push('/hospital/my-appointments')
    } else {
      ElMessage.error(res.message || '预约失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('预约失败')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadDoctor()
  loadSchedules()
})
</script>

<style scoped>
.appointment-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.doctor-card {
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.doctor-name {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.doctor-title {
  color: #667eea;
  font-size: 14px;
  margin: 5px 0;
}

.doctor-dept {
  color: #999;
  font-size: 13px;
}

.doctor-fee {
  margin-left: auto;
  text-align: right;
}

.fee-label {
  color: #999;
  font-size: 13px;
}

.fee-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

.section-title {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.date-tabs {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.date-tab {
  flex-shrink: 0;
  width: 80px;
  padding: 15px 10px;
  background: white;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.date-tab:hover:not(.disabled) {
  border-color: #667eea;
}

.date-tab.active {
  background: #667eea;
  color: white;
}

.date-tab.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.date-weekday {
  font-size: 12px;
}

.date-day {
  font-size: 24px;
  font-weight: bold;
  margin: 5px 0;
}

.date-month {
  font-size: 12px;
}

.date-slots {
  font-size: 11px;
  color: #999;
  margin-top: 5px;
}

.date-slots.full {
  color: #f56c6c;
}

.date-tab.active .date-slots {
  color: rgba(255, 255, 255, 0.8);
}

.time-slots {
  display: flex;
  gap: 15px;
}

.time-slot {
  flex: 1;
  padding: 20px;
  background: white;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.time-slot:hover:not(.disabled) {
  border-color: #667eea;
}

.time-slot.selected {
  background: #667eea;
  color: white;
}

.time-slot.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.slot-time {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.slot-hours {
  font-size: 13px;
  margin-bottom: 10px;
}

.slot-remaining {
  font-size: 12px;
  color: #667eea;
  margin-bottom: 5px;
}

.slot-remaining.full {
  color: #f56c6c;
}

.slot-price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.time-slot.selected .slot-remaining,
.time-slot.selected .slot-remaining.full {
  color: rgba(255, 255, 255, 0.9);
}

.time-slot.selected .slot-price {
  color: white;
}

.complaint-section {
  margin-top: 30px;
}

.action-section {
  margin-top: 30px;
  text-align: center;
}

.action-section .el-button {
  width: 200px;
  height: 50px;
  font-size: 16px;
}

.back-bar {
  margin-bottom: 16px;
}
</style>
