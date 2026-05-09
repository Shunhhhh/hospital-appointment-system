<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <div class="navbar">
      <!-- 左边：其他操作 -->

      <div class="nav-right">
        <a-button type="text" @click="handleLogout">
          <template #icon>
            <logout-outlined />
          </template>
          退出
        </a-button>
      </div>

      <!-- 中间：菜单 -->
      <div class="menu-center">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          class="main-menu"
        />
      </div>

      <!-- 右边： 头像和用户名-->
      <div class="user-info" @click="showUserDrawer">
        <a-avatar
          :size="40"
          :src="userAvatar"
          class="user-avatar"
        >
          {{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}
        </a-avatar>
        <span class="user-name">{{ userInfo.name || '未登录' }}</span>
        <down-outlined class="dropdown-icon" />
      </div>


    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 你的页面内容 -->
      <div class="content-area">
        <router-view />
      </div>
    </div>

    <!-- 用户信息抽屉 -->
    <a-drawer
      v-model:visible="userDrawerVisible"
      title="个人中心"
      placement="left"
      width="300"
    >
      <div class="user-drawer">
        <div class="drawer-header">
          <a-avatar :size="64" :src="userAvatar" class="drawer-avatar">
            {{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}
          </a-avatar>
          <h3 class="user-fullname">{{ userInfo.name || '未登录用户' }}</h3>
          <p class="user-id">{{ userInfo.studentId || '未设置学号' }}</p>
        </div>

        <a-divider />

        <a-menu mode="vertical" class="user-menu">
          <a-menu-item key="profile" @click="goToProfile">
            <user-outlined />
            <span>个人资料</span>
          </a-menu-item>
          <a-menu-item key="reservations" @click="goToReservations">
            <calendar-outlined />
            <span>我的预约</span>
          </a-menu-item>
          <a-menu-item key="settings" @click="goToSettings">
            <setting-outlined />
            <span>账户设置</span>
          </a-menu-item>
          <a-menu-item key="messages" @click="goToMessages">
            <message-outlined />
            <span>消息中心</span>
            <a-badge v-if="unreadCount > 0" :count="unreadCount" />
          </a-menu-item>
        </a-menu>

        <div class="drawer-footer">
          <a-button block type="primary" @click="goToProfile">
            进入个人中心
          </a-button>
          <a-button block @click="handleLogout" style="margin-top: 10px;">
            退出登录
          </a-button>
        </div>
      </div>
    </a-drawer>

    <!-- 下拉菜单 -->
    <a-dropdown
      v-model:visible="dropdownVisible"
      :trigger="['click']"
      placement="bottomLeft"
    >
      <div class="user-dropdown-trigger" @click="dropdownVisible = !dropdownVisible">
        <a-avatar :size="36" :src="userAvatar">
          {{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}
        </a-avatar>
      </div>
      <template #overlay>
        <a-menu>
          <a-menu-item key="profile" @click="goToProfile">
            <user-outlined />
            个人资料
          </a-menu-item>
          <a-menu-item key="reservations" @click="goToReservations">
            <calendar-outlined />
            我的预约
          </a-menu-item>
          <a-menu-item key="settings" @click="goToSettings">
            <setting-outlined />
            设置
          </a-menu-item>
          <a-menu-divider />
          <a-menu-item key="logout" @click="handleLogout">
            <logout-outlined />
            退出登录
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  AppstoreOutlined,
  SettingOutlined,
  UserOutlined,
  DownOutlined,
  LogoutOutlined,
  CalendarOutlined,
  MessageOutlined
} from '@ant-design/icons-vue';
import { h } from 'vue';
import type { MenuProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';

const router = useRouter();
const current = ref<string[]>(['mail']);
const userDrawerVisible = ref(false);
const dropdownVisible = ref(false);

// 用户信息（可以从 localStorage 或 Vuex/Pinia 获取）
const userInfo = ref({
  name: '张三',
  studentId: '20210001',
  avatar: '',
  college: '计算机科学与技术学院',
  grade: '大三'
});

const unreadCount = ref(3); // 未读消息数

// 计算头像URL
const userAvatar = computed(() => {
  return userInfo.value.avatar || '';
});

const items = ref<MenuProps['items']>([
  {
    key: 'sub1',
    icon: () => h(SettingOutlined),
    label: '预约',
    children: [
      {
        type: 'group',
        label: '预约座位',
        children: [
          {
            label: '普通座位',
            key: 'setting:1',
          },
          {
            label: '考研座位',
            key: 'setting:2',
          },
        ],
      },
      {
        type: 'group',
        label: '预约研讨室',
      },
    ],
  },
  {
    key: 'app',
    icon: () => h(AppstoreOutlined),
    label: '签到',
  },
]);

// 显示用户抽屉
const showUserDrawer = () => {
  userDrawerVisible.value = true;
  dropdownVisible.value = false;
};

// 导航到个人资料页
const goToProfile = () => {
  userDrawerVisible.value = false;
  router.push('/profile');
};

const goToReservations = () => {
  userDrawerVisible.value = false;
  router.push('/my-reservations');
};

const goToSettings = () => {
  userDrawerVisible.value = false;
  router.push('/settings');
};

const goToMessages = () => {
  userDrawerVisible.value = false;
  router.push('/messages');
};

// 退出登录
const handleLogout = () => {
  // 清除登录状态
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');

  message.success('已退出登录');
  router.push('/login');
};

// 初始化用户信息
onMounted(() => {
  // 从 localStorage 获取用户信息
  const savedUser = localStorage.getItem('userInfo');
  if (savedUser) {
    try {
      userInfo.value = JSON.parse(savedUser);
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }
});
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

/* 导航栏样式 */
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

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  min-width: 160px;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-avatar {
  margin-right: 12px;
  background-color: #1890ff;
  color: white;
  font-weight: bold;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
  margin-right: 8px;
}

.dropdown-icon {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

/* 中间菜单区域 */
.menu-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.main-menu {
  border: none;
  background: transparent;
  line-height: 62px;
}

/* 右侧操作区域 */
.nav-right {
  min-width: 160px;
  display: flex;
  justify-content: flex-end;
}

/* 主要内容区域 */
.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-area {
  background: white;
  border-radius: 8px;
  padding: 24px;
  min-height: calc(100vh - 148px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 抽屉样式 */
.user-drawer {
  padding: 4px;
}

.drawer-header {
  text-align: center;
  padding: 20px 0;
}

.drawer-avatar {
  background-color: #1890ff;
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

.user-fullname {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.85);
}

.user-id {
  margin: 4px 0 0;
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}

.user-menu {
  border-right: none;
}

.drawer-footer {
  padding: 20px 0;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  border-top: 1px solid #f0f0f0;
  background: white;
}

/* 下拉菜单触发器（备用） */
.user-dropdown-trigger {
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.3s;
}

.user-dropdown-trigger:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
  }

  .user-name {
    display: none;
  }

  .dropdown-icon {
    display: none;
  }

  .user-info {
    min-width: auto;
    padding: 8px;
  }

  .menu-center {
    flex: none;
  }

  .main-menu {
    font-size: 14px;
  }
}
</style>
