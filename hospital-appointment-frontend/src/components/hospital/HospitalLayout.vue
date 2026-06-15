<template>
  <div class="hospital-layout">
    <header class="top-bar">
      <span class="top-title">医院预约挂号系统</span>
      <div class="top-user" @click="handleLogout">
        <el-avatar :size="32" class="top-avatar">{{ userName.charAt(0) || 'U' }}</el-avatar>
        <span class="top-name">{{ userName || '用户' }}</span>
        <el-icon class="top-logout-icon"><SwitchButton /></el-icon>
      </div>
    </header>
    <div class="layout-body">
      <aside class="side-panel">
        <div class="side-title">快捷导航</div>
        <div
          v-for="item in menus"
          :key="item.label"
          class="side-item"
          :class="{ active: isActive(item.path) }"
          @click="handleMenuClick(item.path)"
        >
          <el-icon class="side-icon">
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.label }}</span>
        </div>

      </aside>

      <main class="main-content">
        <el-button v-if="showBackButton" text class="back-home-btn" @click="router.push('/hospital/home')">
          ← 返回首页
        </el-button>
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar,
  Clock,
  Document,
  FirstAidKit,
  House,
  Memo,
  SwitchButton,
  Tickets,
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const menus = [
  { label: '首页', path: '/hospital/home', icon: House },
  { label: '预约挂号', path: '/hospital/appointment/departments', icon: Calendar },
  { label: '医生查询', path: '/hospital/doctor-search', icon: FirstAidKit },
  { label: '我的预约', path: '/hospital/my-appointments', icon: Tickets },
  { label: '排队助手', path: '/hospital/queue-assistant', icon: Clock },
  { label: '门诊记录', path: '/hospital/medical-records', icon: Memo },
  { label: '检查报告', path: '/hospital/reports', icon: Document },
  { label: '个人中心', path: '/hospital/profile', icon: User }
]

const isActive= (path: string) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const showBackButton = computed(() => {
  return route.path !== '/hospital/home'
})

const userName = computed(() => {
  const raw = localStorage.getItem('hospital_user')
  if (!raw) return ''
  try {
    const user = JSON.parse(raw)
    return user.patientName || user.doctorName || user.adminName || ''
  } catch {
    return ''
  }
})

const handleMenuClick = (path: string) => {
  if (route.path !== path) {
    router.push(path)
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确认退出登录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  localStorage.removeItem('hospital_user')
  ElMessage.success('已退出')
  router.push('/hospital/login')
}

</script>

<style scoped>
.hospital-layout {
  min-height: 100vh;
  background: #f5f8fc;
  position: relative;
}

/* 顶部栏 */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 52px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.top-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  letter-spacing: 1px;
}

.top-user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px 4px 6px;
  border-radius: 20px;
  transition: background 0.15s;
}

.top-user:hover {
  background: #f3f4f6;
}

.top-avatar {
  background: #1677ff;
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}

.top-name {
  font-size: 14px;
  color: #374151;
}

.top-logout-icon {
  font-size: 16px;
  color: #9ca3af;
}

/* 主体 */
.layout-body {
  max-width: 1440px;
  width: 100%;
  margin: 0 auto;
  padding: 16px;
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 0;
  align-items: start;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(31, 41, 55, 0.04);
}

/* 侧边栏 */
.side-panel {
  padding: 12px;
  position: sticky;
  top: 16px;
  border-right: 1px solid #f0f0f0;
}

.side-title {
  padding: 4px 8px 10px;
  color: #9ca3af;
  font-size: 12px;
  font-weight: 600;
}

.side-item {
  height: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  border-radius: 8px;
  cursor: pointer;
  color: #374151;
  font-size: 14px;
  transition: all 0.15s ease;
}

.side-item:hover {
  background: rgba(22, 119, 255, 0.06);
  color: #1677ff;
}

.side-item.active {
  background: rgba(22, 119, 255, 0.1);
  color: #1677ff;
  font-weight: 600;
}

.side-icon {
  flex: 0 0 auto;
  font-size: 18px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 返回按钮 */
.back-home-btn {
  margin-bottom: 12px;
}

/* 内容区 */
.main-content {
  min-width: 0;
  padding: 16px 20px 20px 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "PingFang SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: #1f2937;
}

.main-content :deep(h1),
.main-content :deep(h2),
.main-content :deep(h3),
.main-content :deep(strong) {
  font-weight: normal;
}

/* 统一的卡片样式 */
.main-content :deep(.card-base) {
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

/* 统一的页面头部 */
.main-content :deep(.page-head) {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.main-content :deep(.page-title-row) {
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}

.main-content :deep(.page-title) {
  margin: 0;
  font-size: 24px;
  line-height: 1.25;
  color: #111827;
}

.main-content :deep(.page-subtitle) {
  color: #6b7280;
  font-size: 13px;
}

@media (max-width: 960px) {
  .layout-body {
    grid-template-columns: 1fr;
  }

  .side-panel {
    display: none;
  }
}
</style>
