<template>
  <aside class="side-bar">
    <div class="side-panel">
      <div class="side-title">快捷导航</div>
      <div
        v-for="item in menus"
        :key="item.label"
        class="side-item"
        :class="{ active: isActive(item.path) }"
        @click="handleClick(item.path)"
      >
        <el-icon class="side-icon">
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.label }}</span>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import {
  Calendar,
  Document,
  FirstAidKit,
  House,
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
  { label: '检查报告', path: '/hospital/reports', icon: Document },
  { label: '个人中心', path: '/hospital/profile', icon: User }
]

const isActive = (path: string) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const handleClick = (path: string) => {
  if (route.path !== path) {
    router.push(path)
  }
}
</script>

<style scoped>
.side-panel {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
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
</style>
