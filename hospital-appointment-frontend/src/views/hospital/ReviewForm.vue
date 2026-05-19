<template>
  <div class="review-page">
    <div class="back-bar">
      <el-button text @click="router.push('/hospital/my-appointments')">← 返回我的挂号</el-button>
    </div>
    <h1 class="page-title">就诊评价</h1>

    <div class="doctor-card" v-if="appointment">
      <el-avatar :size="50">{{ appointment.doctorName?.charAt(0) }}</el-avatar>
      <div class="info">
        <div class="doctor-name">{{ appointment.doctorName }}</div>
        <div class="department">{{ appointment.departmentName }}</div>
        <div class="date">{{ appointment.appointmentDate }}</div>
      </div>
    </div>

    <div class="rating-section">
      <div class="rating-item" v-for="r in ratingFields" :key="r.key">
        <div class="rating-label">{{ r.label }}</div>
        <el-rate v-model="form[r.key]" :texts="r.texts" show-text :colors="['#f56c6c', '#e6a23c', '#67c23a']" />
      </div>
    </div>

    <div class="content-section">
      <h3>评价内容</h3>
      <el-input
        v-model="form.reviewContent"
        type="textarea"
        :rows="5"
        placeholder="请分享您的就诊体验..."
        maxlength="500"
        show-word-limit
      />
    </div>

    <div class="options-section">
      <el-checkbox v-model="form.isAnonymous">匿名评价</el-checkbox>
    </div>

    <div class="action-section">
      <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">提交评价</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { appointmentAPI } from '@/api/hospital/appointment'
import { reviewAPI } from '@/api/hospital/review'
import type { Appointment } from '@/api/hospital/appointment'

const route = useRoute()
const router = useRouter()
const submitting = ref(false)
const appointment = ref<Appointment | null>(null)

const ratingFields = [
  { key: 'overallRating', label: '总体评分', texts: ['很差', '较差', '一般', '满意', '非常满意'] },
  { key: 'attitudeRating', label: '服务态度', texts: ['很差', '较差', '一般', '满意', '非常满意'] },
  { key: 'skillRating', label: '医疗技术', texts: ['很差', '较差', '一般', '满意', '非常满意'] },
  { key: 'environmentRating', label: '就医环境', texts: ['很差', '较差', '一般', '满意', '非常满意'] }
]

const form = reactive({
  overallRating: 5,
  attitudeRating: 5,
  skillRating: 5,
  environmentRating: 5,
  reviewContent: '',
  isAnonymous: 0
})

const loadAppointment = async () => {
  const id = route.params.appointmentId as string
  if (!id) return
  try {
    const res = await appointmentAPI.getById(id)
    if (res.code === 200) {
      appointment.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载挂号信息失败')
  }
}

const handleSubmit = async () => {
  if (!form.overallRating) {
    ElMessage.warning('请给总体评分')
    return
  }
  if (!appointment.value) return

  submitting.value = true
  try {
    const userStr = localStorage.getItem('hospital_user')
    const user = userStr ? JSON.parse(userStr) : null
    const res = await reviewAPI.submit({
      patientID: user?.patientID || 2001,
      doctorID: appointment.value.doctorID,
      appointmentID: appointment.value.appointmentID,
      departmentID: appointment.value.departmentID,
      overallRating: form.overallRating,
      attitudeRating: form.attitudeRating,
      skillRating: form.skillRating,
      environmentRating: form.environmentRating,
      reviewContent: form.reviewContent,
      isAnonymous: form.isAnonymous,
      visitID: undefined
    })
    if (res.code === 200) {
      ElMessage.success('评价提交成功，感谢您的反馈！')
      router.push('/hospital/my-appointments')
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadAppointment()
})
</script>

<style scoped>
.review-page {
  padding: 20px;
  max-width: 700px;
  margin: 0 auto;
}

.back-bar {
  margin-bottom: 16px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 24px;
}

.doctor-card {
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.doctor-card .info {
  flex: 1;
}

.doctor-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.department, .date {
  color: #999;
  font-size: 13px;
  margin-top: 4px;
}

.rating-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 16px;
}

.rating-item:last-child {
  margin-bottom: 0;
}

.rating-label {
  width: 80px;
  font-size: 14px;
  color: #333;
  flex-shrink: 0;
}

.content-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.content-section h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.options-section {
  background: white;
  padding: 15px 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.action-section {
  text-align: center;
}

.action-section .el-button {
  width: 200px;
  height: 50px;
  font-size: 16px;
}
</style>
