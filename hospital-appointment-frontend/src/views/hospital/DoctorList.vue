<template>
  <div class="doctor-list">
    <BackHomeButton />

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-select v-model="selectedTitle" placeholder="选择职称" clearable @change="handleFilter">
        <el-option label="主任医师" value="主任医师" />
        <el-option label="副主任医师" value="副主任医师" />
        <el-option label="主治医师" value="主治医师" />
        <el-option label="住院医师" value="住院医师" />
      </el-select>
    </div>

    <!-- 医生列表 -->
    <div class="doctor-grid" v-loading="loading">
      <div
        v-for="doctor in filteredDoctors"
        :key="doctor.doctorID"
        class="doctor-card"
        @click="goToDetail(doctor.doctorID)"
      >
        <div class="doctor-main">
          <div class="doctor-avatar">
            <el-avatar :size="80" :src="doctor.doctorPhoto">
              {{ doctor.doctorName?.charAt(0) }}
            </el-avatar>
            <div class="gender-tag" :class="doctor.doctorGender === 1 ? 'male' : 'female'">
              {{ doctor.doctorGender === 1 ? '男' : '女' }}
            </div>
          </div>
          <div class="doctor-info">
            <div class="doctor-header">
              <span class="doctor-name">{{ doctor.doctorName }}</span>
              <el-tag :type="getTitleTagType(doctor.title)" size="small">
                {{ doctor.title }}
              </el-tag>
            </div>
            <div class="doctor-dept">
              <el-icon><Location /></el-icon>
              {{ doctor.departmentName }}
            </div>
            <div class="doctor-specialty" v-if="doctor.specialty">
              <span class="label">擅长：</span>
              <span class="value">{{ doctor.specialty }}</span>
            </div>
            <div class="doctor-intro" v-if="doctor.doctorIntro">
              {{ doctor.doctorIntro }}
            </div>
          </div>
        </div>
        <div class="doctor-footer">
          <div class="fee">
            <span class="label">挂号费</span>
            <span class="price">¥{{ doctor.registrationFee }}</span>
          </div>
          <el-button type="primary" @click.stop="goToSchedule(doctor.doctorID)">
            选择预约
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && filteredDoctors.length === 0" description="暂无医生" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BackHomeButton from '@/components/hospital/BackHomeButton.vue'
import { doctorAPI } from '@/api/hospital/doctor'
import type { Doctor } from '@/api/hospital/doctor'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const doctors = ref<Doctor[]>([])
const selectedTitle = ref('')

const filteredDoctors = computed(() => {
  if (!selectedTitle.value) return doctors.value
  return doctors.value.filter(d => d.title === selectedTitle.value)
})

const getTitleTagType = (title: string) => {
  if (title.includes('主任')) return 'danger'
  if (title.includes('副主任')) return 'warning'
  if (title.includes('主治')) return 'success'
  return 'info'
}

const loadDoctors = async () => {
  loading.value = true
  try {
    const departmentId = route.params.departmentId as string
    let res
    if (departmentId) {
      res = await doctorAPI.getByDepartment(Number(departmentId))
    } else {
      res = await doctorAPI.getList()
    }
    if (res.code === 200) {
      doctors.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载医生列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  // 筛选逻辑由计算属性处理
}

const goToDetail = (doctorId: number) => {
  router.push(`/hospital/doctor/${doctorId}`)
}

const goToSchedule = (doctorId: number) => {
  router.push(`/hospital/schedule/${doctorId}`)
}

watch(() => route.params.departmentId, () => {
  loadDoctors()
})

onMounted(() => {
  loadDoctors()
})
</script>

<style scoped>
.doctor-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}

.doctor-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.doctor-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.doctor-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.doctor-main {
  display: flex;
  gap: 20px;
}

.doctor-avatar {
  position: relative;
  flex-shrink: 0;
}

.gender-tag {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  color: white;
}

.gender-tag.male {
  background: #409eff;
}

.gender-tag.female {
  background: #f56c6c;
}

.doctor-info {
  flex: 1;
  min-width: 0;
}

.doctor-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.doctor-name {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.doctor-dept {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.doctor-specialty {
  margin-bottom: 8px;
}

.doctor-specialty .label {
  color: #999;
  font-size: 13px;
}

.doctor-specialty .value {
  color: #667eea;
  font-size: 13px;
}

.doctor-intro {
  color: #999;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.doctor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.fee .label {
  color: #999;
  font-size: 13px;
  margin-right: 5px;
}

.fee .price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
</style>
