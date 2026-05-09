<template>
  <div class="home-container-nav">
    <div class="navbar">
      <!-- 左侧占位 -->
      <div class="nav-left"></div>

      <!-- 中间：菜单 - 仅在登录后显示 -->
      <div class="menu-center" v-if="isLoggedIn">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          class="main-menu"
          @click="handleMenuClick"
        />
      </div>

      <!-- 右边：根据登录状态决定显示内容 -->
      <div class="nav-right">
        <template v-if="isLoginStateReady && isLoggedIn">
          <a-button type="text" @click="handleLogout">
            <template #icon>
              <logout-outlined />
            </template>
            退出
          </a-button>

          <!-- 头像和用户名 -->
          <div class="user-info" @click="showUserDrawer">
            <a-avatar
              :size="40"
              :src="userAvatar || undefined"
              class="user-avatar"
            >
              {{ displayName.charAt(0) || 'U' }}
            </a-avatar>
            <span class="user-name">{{ displayName }}</span>
            <down-outlined class="dropdown-icon" />
          </div>
        </template>

        <template v-else-if="isLoginStateReady">
          <a-button type="link" @click="$router.push('/login')">登录</a-button>
        </template>

        <!-- 加载状态 -->
        <template v-else>
          <a-spin size="small" />
        </template>
      </div>
    </div>

    <!-- 用户抽屉 - 仅在登录后可用 -->
    <a-drawer
      v-if="isLoggedIn"
      v-model:visible="userDrawerVisible"
      title="个人中心"
      placement="left"
      width="300"
    >
      <div class="user-drawer">
        <div class="drawer-header">
          <a-avatar :size="64" :src="selectedUser.avatar || undefined" class="drawer-avatar">
            {{ (selectedUser.name && selectedUser.name.charAt(0)) || 'U' }}
          </a-avatar>
          <h3 class="user-fullname">{{ selectedUser.name || '未登录用户' }}</h3>
          <p class="user-id">{{ selectedUser.id || (isAdminLoggedIn ? '未设置工号' : '未设置学号') }}</p>
        </div>

        <a-divider />

        <a-menu mode="vertical" class="user-menu">
          <a-menu-item key="settings" @click="goToSettings">
            <setting-outlined />
            <span>账户设置</span>
          </a-menu-item>
          <!-- <a-menu-item key="messages" @click="goToMessages">
            <message-outlined />
            <span>消息中心</span>
            <a-badge v-if="unreadCount > 0" :count="unreadCount" />
          </a-menu-item> -->
        </a-menu>

        <div class="drawer-footer">
          <a-button block @click="handleLogout" style="margin-top: 10px;">
            退出登录
          </a-button>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { h } from 'vue';
import type { MenuProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { storeToRefs } from 'pinia'
import { useStudentStore } from '@/store/studentStore'
import { useAdminStore } from '@/store/adminStore'

import {
  AppstoreOutlined,
  SettingOutlined,
  UserOutlined,
  DownOutlined,
  LogoutOutlined,
  CalendarOutlined,
  MessageOutlined
} from '@ant-design/icons-vue';

const router = useRouter();
const current = ref<string[]>(['mail']);
const userDrawerVisible = ref(false);
const isLoginStateReady = ref(false); // 标识登录状态是否准备就绪

// 使用学生信息store
const studentStore = useStudentStore()
const { studentInfo, token } = storeToRefs(studentStore)

// 使用管理员信息 store
const adminStore = useAdminStore()
const { adminInfo, token: adminToken } = storeToRefs(adminStore)

// 显示用户名（优先管理员）
const displayName = computed(() => {
  if (adminInfo.value?.adminName) return adminInfo.value.adminName
  if (studentInfo.value?.studentUserName) return studentInfo.value.studentUserName

  try {
    const storedAdmin = localStorage.getItem('adminInfo')
    if (storedAdmin) {
      const data = JSON.parse(storedAdmin)
      return data.adminName || data.name || '管理员'
    }
  } catch {}

  try {
    const storedUserInfo = localStorage.getItem('studentInfo') || localStorage.getItem('userInfo')
    if (storedUserInfo) {
      const data = JSON.parse(storedUserInfo)
      return data.studentUserName || data.name || data.username || '用户'
    }
  } catch (e) {
    console.error('Parse error:', e)
  }

  return '用户'
})

// 判断管理员是否已登录
const isAdminLoggedIn = computed(() => {
  const hasTokenInStore = !!adminToken.value
  const hasAdminInfoInStore = !!adminInfo.value?.adminID
  const hasTokenInStorage = !!localStorage.getItem('token') && !!localStorage.getItem('adminInfo')
  return hasTokenInStore || hasAdminInfoInStore || hasTokenInStorage
})

// 判断是否已登录（学生或管理员）
const isLoggedIn = computed(() => {
  const hasStudentTokenInStore = !!token.value
  const hasStudentInfoInStore = !!studentInfo.value?.studentID
  const hasStudentTokenInStorage = !!localStorage.getItem('token') && !!localStorage.getItem('studentInfo')
  return isAdminLoggedIn.value || hasStudentTokenInStore || hasStudentTokenInStorage || hasStudentInfoInStore
})

// 挂载时尝试从本地同步 studentInfo / adminInfo 与 token
onMounted(async () => {
  try {
    // 优先从 studentInfo 获取学生信息
    if (!studentInfo.value?.studentID) {
      await studentStore.fetchStudentInfo().catch(() => {
        const storedUserInfo = localStorage.getItem('studentInfo') || localStorage.getItem('userInfo');
        if (storedUserInfo) {
          try {
            const data = JSON.parse(storedUserInfo);
            studentStore.setStudentInfo({
              studentID: data.studentID || data.id || data.userId || '',
              studentUserName: data.studentUserName || data.name || data.username || '',
              studentPhoneNumber: data.studentPhoneNumber || data.phone || '',
              studentCollege: data.studentCollege || data.college || '',
              studentGrade: data.studentGrade || data.grade || data.class || '',
              studentPoints: data.studentPoints || data.points || 0,
              avatar: data.avatar || data.photo || ''
            });
          } catch (e) {
            console.error('Parse stored user info error:', e);
          }
        }
      });
    }
  } catch (e) {
    console.error('Fetch student info error:', e);
  }

  // 尝试加载管理员信息（如果存在本地数据）
  try {
    if (!adminInfo.value?.adminID) {
      await adminStore.fetchAdminInfo().catch(() => {
        const storedAdmin = localStorage.getItem('adminInfo')
        if (storedAdmin) {
          try {
            const data = JSON.parse(storedAdmin)
            adminStore.setAdminInfo({
              adminID: data.adminID || data.id || 0,
              adminName: data.adminName || data.name || '',
              adminPosition: data.adminPosition || '',
              adminPermission: data.adminPermission || 0,
              adminPhoneNumber: data.adminPhoneNumber || data.phone || '',
              avatar: data.avatar || data.photo || ''
            })
          } catch (err) {
            console.error('Parse stored admin info error:', err)
          }
        }
      })
    }
  } catch (err) {
    console.error('Fetch admin info error:', err)
  }

  try {
    // 如果 store 中没有 token，尝试从 localStorage 获取（优先给 student）
    if (!token.value) {
      const t = localStorage.getItem('token');
      if (t) {
        studentStore.setToken(t);
      }
    }

    // 如果没有管理员 token，但本地有 adminInfo，则把 token 同步给 adminStore
    if (!adminToken.value && localStorage.getItem('adminInfo')) {
      const t = localStorage.getItem('token')
      if (t) adminStore.setToken(t)
    }
  } catch (e) {
    console.error('Token initialization error:', e);
  }

  // 设置登录状态准备完成标志
  isLoginStateReady.value = true;
});

// 监听 localStorage 的变更，实时同步到 pinia
const storageHandler = (e: StorageEvent) => {
  if (!e.key) return;
  if (e.key === 'token') {
    // 如果本地存在 adminInfo，则认为 token 属于管理员，否则属于学生
    const hasAdmin = !!localStorage.getItem('adminInfo');
    if (e.newValue) {
      if (hasAdmin) adminStore.setToken(e.newValue);
      else studentStore.setToken(e.newValue);
    } else {
      if (hasAdmin) adminStore.setToken('');
      else studentStore.setToken('');
    }
  }

  if (e.key === 'studentInfo' || e.key === 'userInfo') {
    try {
      const raw = e.newValue;
      if (!raw) return;
      const data = JSON.parse(raw);
      studentStore.setStudentInfo({
        studentID: data.studentID || data.id || data.userId || '',
        studentUserName: data.studentUserName || data.name || data.username || '',
        studentPhoneNumber: data.studentPhoneNumber || data.phone || '',
        studentCollege: data.studentCollege || data.college || '',
        studentGrade: data.studentGrade || data.grade || data.class || '',
        avatar: data.avatar || data.photo || ''
      });
    } catch {
      // ignore parse error
    }
  }
  // 管理员信息同步
  if (e.key === 'adminInfo') {
    try {
      const raw = e.newValue;
      if (!raw) return;
      const data = JSON.parse(raw);
      adminStore.setAdminInfo({
        adminID: data.adminID || data.id || 0,
        adminName: data.adminName || data.name || '',
        adminPosition: data.adminPosition || '',
        adminPermission: data.adminPermission || 0,
        adminPhoneNumber: data.adminPhoneNumber || data.phone || '',
        avatar: data.avatar || data.photo || ''
      });
    } catch {
      // ignore parse error
    }
  }
};

onMounted(() => window.addEventListener('storage', storageHandler));
onUnmounted(() => window.removeEventListener('storage', storageHandler));

// 监控登录状态变化，确保在 token 更新后能刷新界面
watch([token, studentInfo, adminToken, adminInfo], () => {
  if (!isLoginStateReady.value) {
    isLoginStateReady.value = true;
  }
});

const unreadCount = ref(3); // 未读消息数

// 头像URL
const userAvatar = computed(() => {
  if (isAdminLoggedIn.value) return adminInfo.value.avatar || ''
  return studentInfo.value.avatar || ''
})

// 根据角色分别定义菜单项
const studentItems: MenuProps['items'] = [
  {
    key: 'seat-reservation',
    label: '预约',
    icon: () => h(SettingOutlined),
    children: [
      { key: '/seat-list', label: '普通座位预约' },
      { key: '/study-seat-list', label: '考研座位预约' },
      { key: '/seminar-room-list', label: '研讨室预约' }
    ]
  },
  { key: '/checkin', icon: () => h(AppstoreOutlined), label: '签到' },
  { key: 'records', label: '个人记录', icon: () => h(SettingOutlined), children: [
      { key: '/student-reservation', label: '预约记录' },
      { key: '/student-violation', label: '违规记录' }
    ]
  },
  { key: '/feedback', icon: () => h(AppstoreOutlined), label: '反馈' },
  { key: '/notice', icon: () => h(MessageOutlined), label: '通知' }
]

const adminItems: MenuProps['items'] = [
  { key: '/admin-reservation', icon: () => h(AppstoreOutlined), label: '预约管理' },
  { key: '/admin-roomManage', icon: () => h(SettingOutlined), label: '资源管理' },
  { key: '/admin-violation', icon: () => h(UserOutlined), label: '违规管理' },
  { key: '/admin-feedback', icon: () => h(MessageOutlined), label: '反馈处理' }
]

const items = computed(() => (isAdminLoggedIn.value ? adminItems : studentItems))

// 当前展示的用户信息（管理员优先）
const selectedUser = computed(() => {
  if (isAdminLoggedIn.value) {
    return {
      name: adminInfo.value.adminName || '管理员',
      id: adminInfo.value.adminID || '',
      avatar: adminInfo.value.avatar || ''
    }
  }

  return {
    name: studentInfo.value.studentUserName || '用户',
    id: studentInfo.value.studentID || '',
    avatar: studentInfo.value.avatar || ''
  }
})

// 菜单点击处理函数
const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key);
};
/*
const handleMenuClick = (key: string) => {
  if (key === 'normal') {
    router.push('/seat-list');
  } else if (key === 'postgrad') {
    router.push('/study-seat-list');
  } else if (key === 'seminar') {
    router.push('/seminar-room-list');
  }
};
*/
// 显示用户抽屉
const showUserDrawer = () => {
  userDrawerVisible.value = true;
};

const goToSettings = () => {
  userDrawerVisible.value = false;
  router.push({ path: "/profile", query: { edit: "1" } });
};
// const goToMessages = () => { userDrawerVisible.value = false; router.push('/messages') }

// 退出登录（根据当前身份登出对应 store）
const handleLogout = () => {
  if (isAdminLoggedIn.value) {
    adminStore.logout()
  } else {
    studentStore.logout()
  }
  message.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  padding: 0 24px;
  height: 64px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  min-width: 160px
}

.user-info:hover { background-color: #f5f5f5 }

.user-avatar {
  margin-right: 12px;
  background-color: #1890ff;
  color: white;
  font-weight: bold
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: rgba(0,0,0,0.85);
  margin-right: 8px
}

.dropdown-icon {
  font-size: 12px;
  color: rgba(0,0,0,0.45)
}

.menu-center {
  flex: 1;
  display: flex;
  justify-content: center
}

.main-menu {
  border: none;
  background: transparent;
  line-height: 62px
}

.nav-right {
  min-width: 160px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.user-drawer { padding: 4px }

.drawer-header {
  text-align: center;
  padding: 20px 0
}

.drawer-avatar {
  background-color: #1890ff;
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px
}

.drawer-footer {
  padding: 20px 0;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  border-top: 1px solid #f0f0f0;
  background: white
}

.user-dropdown-trigger {
  cursor: pointer;
  padding: 4px;
  border-radius: 50%
}

.user-dropdown-trigger:hover {
  background-color: rgba(0,0,0,0.04)
}

@media (max-width:768px) {
  .user-name, .dropdown-icon { display: none }
  .menu-center { flex: none }
  .main-menu { font-size: 14px }
}
</style>
