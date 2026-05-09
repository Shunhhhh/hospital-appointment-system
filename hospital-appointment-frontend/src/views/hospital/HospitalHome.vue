<template>
  <div class="hospital-home">
    <!-- 固定顶部导航栏 -->
    <div class="navbar">
      <div class="navbar-left">
        <h1 class="logo">🏥 医院预约挂号系统</h1>
      </div>
      <div class="navbar-right">
        <template v-if="!isLoggedIn">
          <el-button type="primary" size="default" @click="router.push('/hospital/login')">登录</el-button>
          <el-button size="default" @click="router.push('/hospital/register')">注册</el-button>
        </template>
        <template v-else>
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-avatar :size="32" style="background-color: #409eff;">
                {{ userName?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/hospital/my-appointments')">
                  我的挂号
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>

    <div class="main-content">
      <!-- 顶部搜索区域 -->
      <div class="search-section">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索科室、医生"
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
        </div>
      </div>

      <!-- 科室分类导航 -->
      <div class="department-types">
        <div class="type-item" 
             v-for="type in departmentTypes" 
             :key="type.value"
             @click="filterByType(type.value)">
          <div class="type-icon">{{ type.icon }}</div>
          <div class="type-name">{{ type.label }}</div>
        </div>
      </div>

      <!-- 科室列表 -->
      <div class="department-section">
        <h2 class="section-title">全部科室</h2>
        <div class="department-grid">
          <div 
            v-for="dept in filteredDepartments" 
            :key="dept.departmentID"
            class="department-card"
            @click="goToDoctors(dept.departmentID)"
          >
            <div class="dept-icon">{{ getDeptIcon(dept.departmentType) }}</div>
            <div class="dept-name">{{ dept.departmentName }}</div>
            <div class="dept-location">{{ dept.departmentLocation }}</div>
          </div>
        </div>
      </div>

      <!-- 推荐医生 -->
      <div class="doctor-section">
        <h2 class="section-title">推荐专家</h2>
        <div class="doctor-grid">
          <div 
            v-for="doctor in recommendedDoctors" 
            :key="doctor.doctorID"
            class="doctor-card"
            @click="router.push(`/hospital/schedule/${doctor.doctorID}`)"
          >
            <div class="doctor-avatar">
              <el-avatar :size="60" style="background-color: #67c23a;">
                {{ doctor.doctorName?.charAt(0) }}
              </el-avatar>
            </div>
            <div class="doctor-info">
              <div class="doctor-name">{{ doctor.doctorName }}</div>
              <div class="doctor-title">{{ doctor.title }}</div>
              <div class="doctor-dept">{{ doctor.departmentName }}</div>
            </div>
            <div class="doctor-action">
              <el-button type="primary" size="small">预约挂号</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { departmentAPI } from '@/api/hospital/department'
import { doctorAPI } from '@/api/hospital/doctor'
import type { Department } from '@/api/hospital/department'
import type { Doctor } from '@/api/hospital/doctor'

const router = useRouter()
const searchKeyword = ref('')
const departments = ref<Department[]>([])
const recommendedDoctors = ref<Doctor[]>([])
const selectedType = ref<number | null>(null)

// 登录状态
const isLoggedIn = computed(() => !!localStorage.getItem('hospital_user'))
const currentUser = computed(() => {
  const data = localStorage.getItem('hospital_user')
  return data ? JSON.parse(data) : null
})
const userName = computed(() => currentUser.value?.patientName || '用户')

const handleLogout = async () => {
  await ElMessageBox.confirm('确认退出登录？', '提示')
  localStorage.removeItem('hospital_user')
  ElMessage.success('已退出')
  router.push('/hospital/login')
}

const departmentTypes = [
  { label: '内科', value: 1, icon: '🫀' },
  { label: '外科', value: 2, icon: '🏥' },
  { label: '儿科', value: 3, icon: '👶' },
  { label: '妇科', value: 4, icon: '👩' },
  { label: '骨科', value: 6, icon: '🦴' },
  { label: '眼科', value: 7, icon: '👁' },
  { label: '口腔科', value: 9, icon: '🦷' },
  { label: '皮肤科', value: 10, icon: '🧴' },
]

const filteredDepartments = computed(() => {
  if (selectedType.value) {
    return departments.value.filter(d => d.departmentType === selectedType.value)
  }
  return departments.value
})

const getDeptIcon = (type: number) => {
  const icons: Record<number, string> = {
    1: '🫀', 2: '🏥', 3: '👶', 4: '👩', 5: '🤰',
    6: '🦴', 7: '👁', 8: '👂', 9: '🦷', 10: '🧴', 11: '🀄', 12: '🚑'
  }
  return icons[type] || '🏥'
}

const loadDepartments = async () => {
  try {
    const res = await departmentAPI.getList()
    if (res.code === 200) {
      departments.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载科室列表失败')
  }
}

const loadRecommendedDoctors = async () => {
  try {
    const res = await doctorAPI.getList()
    if (res.code === 200) {
      recommendedDoctors.value = (res.data || []).slice(0, 4)
    }
  } catch (error) {
    ElMessage.error('加载推荐医生失败')
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  try {
    const res = await doctorAPI.search(searchKeyword.value)
    if (res.code === 200) {
      recommendedDoctors.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  }
}

const filterByType = (type: number) => {
  selectedType.value = selectedType.value === type ? null : type
}

const goToDoctors = (departmentId: number) => {
  router.push(`/hospital/doctors/${departmentId}`)
}

onMounted(() => {
  loadDepartments()
  loadRecommendedDoctors()
})
</script>

<style scoped>
.hospital-home {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 固定顶部导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  background: white;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  z-index: 1000;
}

.navbar-left .logo {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
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

.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 主内容区 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 20px 20px;
}

.search-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.search-box {
  display: flex;
  gap: 10px;
  max-width: 600px;
  margin: 0 auto;
}

.department-types {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.type-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.type-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.type-name {
  font-size: 14px;
  color: #333;
}

.section-title {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.department-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.department-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.department-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dept-icon {
  font-size: 36px;
  margin-bottom: 10px;
}

.dept-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.dept-location {
  font-size: 12px;
  color: #999;
}

.doctor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.doctor-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.doctor-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.doctor-avatar {
  margin-bottom: 15px;
}

.doctor-info {
  text-align: center;
  margin-bottom: 15px;
}

.doctor-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.doctor-title {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.doctor-dept {
  font-size: 12px;
  color: #999;
}
</style>
