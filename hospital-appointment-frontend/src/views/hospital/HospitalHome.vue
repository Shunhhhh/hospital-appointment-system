<template>
  <div class="hospital-home-page">
    <div class="top-right-bar">
      <el-dropdown trigger="click">
        <span class="user-area">
          <el-avatar :size="40">{{ userName?.charAt(0) }}</el-avatar>
          <span class="user-name">{{ userName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="router.push('/hospital/profile')">个人中心</el-dropdown-item>
            <el-dropdown-item @click="router.push('/hospital/my-appointments')">我的预约</el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="page-shell">
      <SidebarNav />

      <main class="main-area">
        <section class="banner-card">
          <div class="banner-content">
            <div class="banner-kicker">智慧医院 · 便捷挂号 · 在线服务</div>
            <h1 class="banner-title">一站式预约挂号，少排队，更省心</h1>
            <p class="banner-desc">
              支持科室查询、医生筛选、预约挂号、检查报告与门诊记录查看，帮助患者快速找到合适的就诊入口。
            </p>
            <div class="banner-actions">
              <el-button type="primary" size="large" @click="router.push('/hospital/appointment/departments')">立即预约挂号</el-button>
              <el-button size="large" plain @click="router.push('/hospital/my-appointments')">查看预约记录</el-button>
            </div>
          </div>

          <div class="banner-illustration">
            <div class="illustration-card">
              <el-icon class="illustration-icon"><FirstAidKit /></el-icon>
              <div class="illustration-text">门诊大厅</div>
            </div>
            <div class="floating-note note-1">在线挂号</div>
            <div class="floating-note note-2">报告查询</div>
          </div>
        </section>

        <section class="quick-services">
          <div
            v-for="service in quickServices"
            :key="service.title"
            class="quick-card"
            @click="handleQuickAction(service.path)"
          >
            <div class="quick-card-left">
              <div class="quick-icon">
                <el-icon><component :is="service.icon" /></el-icon>
              </div>
              <div>
                <div class="quick-title">{{ service.title }}</div>
                <div class="quick-desc">{{ service.desc }}</div>
              </div>
            </div>
            <el-icon class="quick-arrow"><ArrowRight /></el-icon>
          </div>
        </section>

        <section class="content-grid">
          <div class="content-left">
            <div class="module-card">
              <div class="module-head">
                <h2>热门科室</h2>
                <span class="module-tip">共 {{ departments.length }} 个科室</span>
              </div>
              <div class="department-grid">
                <button
                  v-for="dept in visibleDepartments"
                  :key="dept.name"
                  class="department-card"
                  @click="goToDepartment(dept.id)"
                >
                  <div class="department-icon">
                    <el-icon><component :is="dept.icon" /></el-icon>
                  </div>
                  <div class="department-name">{{ dept.name }}</div>
                  <div class="department-desc">{{ dept.desc }}</div>
                </button>
              </div>
            </div>

            <div class="module-card">
              <div class="module-head">
                <h2>推荐医生</h2>
                <span class="module-tip">根据科室热度智能推荐</span>
              </div>
              <div class="doctor-list">
                <article v-for="doctor in visibleDoctors" :key="doctor.id" class="doctor-card">
                  <el-avatar :size="60" class="doctor-avatar">{{ doctor.name.slice(0, 1) }}</el-avatar>
                  <div class="doctor-main">
                    <div class="doctor-row">
                      <div>
                        <div class="doctor-name">{{ doctor.name }}</div>
                        <div class="doctor-meta">{{ doctor.title }} · {{ doctor.department }}</div>
                      </div>
                      <el-tag effect="light" type="success">{{ doctor.available }}</el-tag>
                    </div>
                    <div class="doctor-skill">擅长：{{ doctor.skill }}</div>
                    <div class="doctor-footer">
                      <span class="doctor-time">可预约：{{ doctor.time }}</span>
                      <el-button type="primary" size="small" @click="goToSchedule(doctor.id)">预约挂号</el-button>
                    </div>
                  </div>
                </article>
              </div>
            </div>
          </div>

          <aside class="content-right">
            <div class="module-card sticky-card">
              <div class="module-head">
                <h2>通知公告</h2>
                <span class="module-tip">最新动态</span>
              </div>
              <div class="notice-list">
                <div v-for="item in notices" :key="item.title" class="notice-item">
                  <div class="notice-dot" :class="item.level"></div>
                  <div class="notice-content">
                    <div class="notice-title">{{ item.title }}</div>
                    <div class="notice-desc">{{ item.desc }}</div>
                  </div>
                </div>
              </div>
            </div>
          </aside>
        </section>

        <section class="module-card">
          <div class="module-head">
            <h2>常用服务</h2>
            <span class="module-tip">医院常见入口，一键直达</span>
          </div>
          <div class="service-grid">
            <button v-for="service in commonServices" :key="service.title" class="service-card" @click="handleQuickAction(service.path)">
              <div class="service-icon">
                <el-icon><component :is="service.icon" /></el-icon>
              </div>
              <div class="service-title">{{ service.title }}</div>
              <div class="service-desc">{{ service.desc }}</div>
            </button>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SidebarNav from '@/components/hospital/SidebarNav.vue'
import {
  ArrowDown,
  ArrowRight,
  Bell,
  Calendar,
  ChatLineRound,
  Clock,
  DataAnalysis,
  Document,
  EditPen,
  Files,
  FirstAidKit,
  Guide,
  House,
  Location,
  Memo,
  OfficeBuilding,
  Postcard,
  Reading,
  Search,
  Service,
  SetUp,
  Suitcase,
  Tickets,
  User,
  Van,
  View
} from '@element-plus/icons-vue'
import type { Component } from 'vue'

type MenuItem = {
  label: string
  path: string
}

type DepartmentCard = {
  id: number
  name: string
  icon: Component
  desc: string
}

type DoctorCard = {
  id: number
  name: string
  title: string
  department: string
  skill: string
  time: string
  available: string
}

type AppointmentItem = {
  id: number
  department: string
  doctor: string
  time: string
  status: string
  statusType: 'success' | 'warning' | 'danger' | 'info'
}

type NoticeItem = {
  title: string
  desc: string
  level: 'success' | 'warning' | 'danger'
}

const router = useRouter()
const route = useRoute()
const searchKeyword = ref('')

const topMenus: MenuItem[] = [
  { label: '首页', path: '/hospital/home' },
  { label: '预约挂号', path: '/hospital/appointment/departments' },
  { label: '找医生', path: '/hospital/doctor-search' },
  { label: '检查检验', path: '/hospital/my-appointments' },
  { label: '住院服务', path: '/hospital/profile' },
  { label: '健康宣教', path: '/hospital/health-education' }
]

const departments: DepartmentCard[] = [
  { id: 11, name: '心血管内科', icon: FirstAidKit, desc: '冠心病、高血压、心律失常诊疗' },
  { id: 14, name: '呼吸内科/呼吸与危重症', icon: Service, desc: '咳嗽、哮喘、肺结节与呼吸管理' },
  { id: 15, name: '消化内科', icon: Document, desc: '胃肠、肝胆胰疾病与内镜咨询' },
  { id: 16, name: '内分泌科', icon: DataAnalysis, desc: '糖尿病、甲状腺与代谢管理' },
  { id: 17, name: '神经内科', icon: User, desc: '头痛眩晕、脑卒中与睡眠障碍' },
  { id: 3, name: '儿科', icon: User, desc: '儿童常见病与生长发育评估' },
  { id: 21, name: '理疗康复针灸科', icon: SetUp, desc: '颈肩腰腿痛、针灸与运动康复' },
  { id: 5, name: '骨科', icon: OfficeBuilding, desc: '骨关节、运动损伤与康复' }
]

const doctors: DoctorCard[] = [
  { id: 101, name: '李敏', title: '主任医师', department: '内科', skill: '高血压、糖尿病、慢病管理', time: '周一至周五 上午', available: '可预约' },
  { id: 102, name: '王强', title: '副主任医师', department: '外科', skill: '阑尾、疝气、腹部外科', time: '周二至周六 下午', available: '有号源' },
  { id: 103, name: '陈雪', title: '主任医师', department: '儿科', skill: '儿童呼吸道疾病、发热', time: '周一、周三、周五 上午', available: '推荐' },
  { id: 104, name: '周楠', title: '副主任医师', department: '眼科', skill: '近视防控、白内障、眼底病', time: '周一至周四 全天', available: '可预约' }
]

const appointments: AppointmentItem[] = [
  { id: 1, department: '呼吸内科', doctor: '李敏 主任医师', time: '2026-06-03 08:30', status: '待就诊', statusType: 'warning' },
  { id: 2, department: '眼科门诊', doctor: '周楠 副主任医师', time: '2026-06-05 13:10', status: '已确认', statusType: 'success' },
  { id: 3, department: '骨科复诊', doctor: '张立 主任医师', time: '2026-06-08 09:00', status: '待缴费', statusType: 'info' }
]

const notices: NoticeItem[] = [
  { title: '门诊号源已开放至下周五', desc: '请提前选择医生与时间段，避开高峰期排队。', level: 'warning' },
  { title: '儿科夜间门诊已恢复', desc: '周一至周日 18:00 - 21:00 提供夜间诊疗。', level: 'success' },
  { title: '体检中心预约通道升级', desc: '支持线上改期、取消和报告查询。', level: 'danger' }
]

const quickServices = [
  { title: '智能预问诊', desc: 'AI 分析症状建议', icon: ChatLineRound, path: '/hospital/pre-diagnosis' },
  { title: '预约挂号', desc: '快速选择科室与医生', icon: Calendar, path: '/hospital/appointment/departments' },
  { title: '找医生', desc: '按专长筛选专家', icon: FirstAidKit, path: '/hospital/doctor-search' },
  { title: '检查检验报告', desc: '查看化验和检查结果', icon: DataAnalysis, path: '/hospital/reports' },
]

const commonServices = [
  { title: '排队助手', desc: '实时查看排队进度', icon: Clock, path: '/hospital/queue-assistant' },
  { title: '门诊记录', desc: '快速查阅历史记录', icon: Memo, path: '/hospital/medical-records' },
  { title: '意见反馈', desc: '提交改进建议', icon: EditPen, path: '/hospital/feedback' },
  { title: '健康宣教', desc: '查看健康科普内容', icon: Reading, path: '/hospital/health-education' }
]

const currentUser = computed(() => {
  const raw = localStorage.getItem('hospital_user')
  return raw ? JSON.parse(raw) : null
})

const userName = computed(() => {
  const user = currentUser.value
  if (!user) return '游客'
  return user.patientName || user.doctorName || user.adminName || '用户'
})

const userTypeLabel = computed(() => {
  const user = currentUser.value
  if (!user) return '未登录'
  if (user.type === 'patient') return '患者'
  if (user.type === 'doctor') return '医生'
  return '管理员'
})

const visibleDepartments = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) {
    return departments
  }
  return departments.filter((item) => {
    return item.name.toLowerCase().includes(keyword) || item.desc.toLowerCase().includes(keyword)
  })
})

const visibleDoctors = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) {
    return doctors
  }
  return doctors.filter((item) => {
    return [item.name, item.title, item.department, item.skill].some((value) => value.toLowerCase().includes(keyword))
  })
})

const handleTopMenuClick = (path: string) => {
  if (path === '/hospital/home') {
    return
  }
  router.push(path)
}

const goToDepartment = (departmentId: number) => {
  router.push({
    path: '/hospital/appointment/departments',
    query: {
      primary: String(departmentId),
      sub: String(departmentId)
    }
  })
}

const goToSchedule = (doctorId: number) => {
  router.push(`/hospital/schedule/${doctorId}`)
}

const handleQuickAction = (path: string) => {
  router.push(path)
}

const handleLogout = () => {
  localStorage.removeItem('hospital_user')
  router.push('/hospital/login')
}

const performSearch = () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  ElMessage.success(`已筛选 ${keyword} 的相关内容`)
}
</script>

<style scoped>
.hospital-home-page {
  min-height: 100vh;
  background: #f5f8fc;
  color: #1f2937;
  position: relative;
}

.top-right-bar {
  position: absolute;
  top: 16px;
  right: 24px;
  z-index: 10;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  border-radius: 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.user-area:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.12);
}

.user-name {
  font-size: 15px;
  color: #374151;
  font-weight: 600;
}

.top-header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 24px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.04);
  position: sticky;
  top: 0;
  z-index: 20;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 320px;
}

.brand-logo {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: linear-gradient(135deg, #1677ff 0%, #63b3ff 100%);
  font-weight: 700;
  font-size: 14px;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
}

.brand-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.top-nav {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: auto;
}

.top-nav-item {
  height: 36px;
  padding: 0 14px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.top-nav-item:hover,
.top-nav-item.active {
  background: rgba(22, 119, 255, 0.08);
  color: #1677ff;
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 260px;
}

.notification-badge {
  display: inline-flex;
}

.icon-button {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  color: #1f2937;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 8px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #1677ff, #74b9ff);
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
}

.user-role {
  font-size: 12px;
  color: #6b7280;
}

.user-arrow {
  color: #6b7280;
}

.page-shell {
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px 20px 24px;
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 16px;
}

.side-panel,
.module-card,
.banner-card,
.quick-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

.side-panel {
  padding: 12px;
  position: sticky;
  top: 80px;
}

.side-title {
  padding: 4px 8px 10px;
  color: #6b7280;
  font-size: 12px;
}

.side-item {
  height: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  border-radius: 10px;
  cursor: pointer;
  color: #374151;
  transition: all 0.2s ease;
}

.side-item:hover,
.side-item.active {
  background: rgba(22, 119, 255, 0.08);
  color: #1677ff;
}

.side-icon {
  width: 20px;
  height: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  font-size: 18px;
}

.main-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.banner-card {
  min-height: 250px;
  padding: 24px;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(280px, 0.85fr);
  gap: 24px;
  background: linear-gradient(135deg, #eaf4ff 0%, #f8fbff 100%);
}

.banner-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
}

.banner-kicker {
  color: #1677ff;
  font-size: 13px;
  font-weight: 600;
}

.banner-title {
  margin: 0;
  font-size: 34px;
  line-height: 1.2;
}

.banner-desc {
  margin: 0;
  max-width: 720px;
  color: #6b7280;
  line-height: 1.7;
}

.banner-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.banner-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 8px;
}

.stat-item {
  padding: 14px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(229, 231, 235, 0.9);
  border-radius: 12px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #1677ff;
}

.stat-label {
  margin-top: 4px;
  color: #6b7280;
  font-size: 12px;
}

.banner-illustration {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.illustration-card {
  width: 100%;
  min-height: 220px;
  border-radius: 20px;
  border: 1px dashed rgba(22, 119, 255, 0.28);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.8), rgba(239, 246, 255, 0.95));
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 10px;
}

.illustration-icon {
  width: 92px;
  height: 92px;
  border-radius: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1677ff, #63b3ff);
  color: #ffffff;
  font-size: 52px;
  box-shadow: 0 14px 30px rgba(22, 119, 255, 0.2);
}

.illustration-text {
  color: #1677ff;
  font-size: 18px;
  font-weight: 700;
}

.floating-note {
  position: absolute;
  padding: 8px 12px;
  border-radius: 999px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.08);
  font-size: 13px;
  color: #374151;
}

.note-1 {
  top: 18px;
  left: 16px;
}

.note-2 {
  right: 12px;
  bottom: 18px;
}

.quick-services {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.quick-card {
  padding: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-card:hover,
.department-card:hover,
.doctor-card:hover,
.service-card:hover,
.appointment-item:hover,
.notice-item:hover {
  transform: translateY(-2px);
}

.quick-card-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1677ff, #63b3ff);
  color: #ffffff;
  font-size: 24px;
}

.quick-title,
.service-title,
.department-name,
.doctor-name,
.appointment-title,
.notice-title {
  font-weight: 700;
}

.quick-desc,
.service-desc,
.department-desc,
.doctor-meta,
.doctor-skill,
.doctor-time,
.appointment-desc,
.notice-desc,
.module-tip {
  color: #6b7280;
  font-size: 13px;
}

.quick-arrow {
  color: #1677ff;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 16px;
  align-items: start;
}

.content-left,
.content-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.module-card {
  padding: 18px;
}

.sticky-card {
  position: sticky;
  top: 80px;
}

.module-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.module-head h2 {
  margin: 0;
  font-size: 18px;
}

.department-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.department-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.department-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(22, 119, 255, 0.08);
  color: #1677ff;
  font-size: 26px;
}

.doctor-list {
  display: grid;
  gap: 12px;
}

.doctor-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px;
  display: flex;
  gap: 12px;
  background: #ffffff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.doctor-avatar {
  flex: 0 0 auto;
  background: linear-gradient(135deg, #1677ff, #7dc3ff);
}

.doctor-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.doctor-row,
.doctor-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.doctor-footer {
  flex-wrap: wrap;
}

.doctor-name {
  font-size: 16px;
}

.doctor-skill {
  line-height: 1.6;
}

.appointment-list,
.notice-list {
  display: grid;
  gap: 12px;
}

.appointment-item,
.notice-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  transition: transform 0.2s ease;
}

.notice-item {
  align-items: center;
}

.notice-item:hover,
.appointment-item:hover {
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

.notice-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  margin-top: 5px;
  flex: 0 0 auto;
}

.notice-dot.success {
  background: #22c55e;
}

.notice-dot.warning {
  background: #f59e0b;
}

.notice-dot.danger {
  background: #ef4444;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  margin-bottom: 4px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.service-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
  padding: 16px 14px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.service-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(22, 119, 255, 0.08);
  color: #1677ff;
  font-size: 22px;
}

@media (max-width: 1280px) {
  .banner-card {
    grid-template-columns: 1fr;
  }

  .banner-illustration {
    min-height: 180px;
  }

  .quick-services,
  .department-grid,
  .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .sticky-card {
    position: static;
  }
}

@media (max-width: 960px) {
  .top-header {
    height: auto;
    padding: 16px;
    flex-wrap: wrap;
  }

  .brand {
    min-width: 0;
  }

  .top-nav {
    order: 3;
    width: 100%;
    justify-content: flex-start;
  }

  .header-tools {
    margin-left: auto;
  }

  .page-shell {
    grid-template-columns: 1fr;
  }

  .side-panel {
    position: static;
  }

  .quick-services,
  .department-grid,
  .service-grid,
  .banner-stats {
    grid-template-columns: 1fr;
  }

  .search-input {
    width: 180px;
  }
}

@media (max-width: 640px) {
  .top-header {
    gap: 12px;
  }

  .header-tools {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
  }

  .banner-title {
    font-size: 26px;
  }

  .banner-actions {
    flex-direction: column;
  }

  .doctor-card,
  .appointment-item,
  .notice-item,
  .quick-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .doctor-row,
  .doctor-footer,
  .module-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
