<template>
  <div class="page">
    <div class="container">
      <!-- 顶部栏 -->
      <div class="header">
        <div class="title-wrap">
          <h2>{{ pageTitle }}</h2>
    <div class="sub">选择日期与时间段，查看可用资源</div>
        </div>
      </div>

      <!-- 查询条件卡片 -->
      <a-card class="filter-card" :bordered="false">
        <a-form layout="inline" class="filter-form">
          <a-form-item>
            <a-date-picker v-model:value="query.date" placeholder="选择日期" />
          </a-form-item>

          <a-form-item>
            <a-time-picker v-model:value="query.start" format="HH:mm" placeholder="开始时间" />
          </a-form-item>

          <a-form-item>
            <a-time-picker v-model:value="query.end" format="HH:mm" placeholder="结束时间" />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" :loading="loading" @click="search">查询</a-button>
          </a-form-item>
        </a-form>
      </a-card>

    <!-- 自习室列表 -->
    <div class="room-list">
      <a-spin :spinning="loading">
        <div v-if="rooms.length" class="room-grid">
          <a-card
            v-for="room in rooms"
            :key="room.id"
            class="room-card"
            :bordered="false"
            hoverable
            @click="openRoom(room)"
          >
            <template #title>
              <div class="card-title">
                <span class="room-name">{{ room.studyRoomName }}</span>
                <a-tag :color="statusColor(room.status)">
                  {{ getRoomStatusText(room.status) }}
                </a-tag>
              </div>
            </template>

            <div class="card-body">
              <div class="meta">
                <span class="label">容量</span>
                <span class="value">{{ room.studyRoomCapacity }} 人</span>
              </div>
              <div class="meta">
                <span class="label">类型</span>
                <span class="value">{{ getRoomTypeText(room.studyRoomType) }}</span>
              </div>
            </div>
          </a-card>
        </div>

        <a-empty v-else description="暂无自习室数据" />
      </a-spin>
    </div>
  </div>
 </div>
</template>

<script setup lang="ts">
import { ref, onMounted,computed, watch } from "vue";
import dayjs from "dayjs";
import axios from "axios";
import { StudyRoom, SeminarRoom, getRoomStatusText, getRoomTypeText
} from '@/api/data'
import {    RESOURCE_TYPE,getResourceTypeText}from '@/api/resourceService'
import {useRoute, useRouter } from "vue-router";
import { message } from "ant-design-vue";
import { useStudentStore } from '@/store/studentStore';

const route = useRoute();
const router = useRouter();
const studentStore = useStudentStore();


const statusColor = (status: number) => {
  switch (status) {
    case 0:
      return 'default'   // 关闭
    case 1:
      return 'green'     // 开放
    case 2:
      return 'orange'    // 维护
    default:
      return 'blue'
  }
}
const pageTitle = computed(() => {
  const path = route.path;
  console.log("当前路由路径:", path);
  if (route.path === '/study-seat-list') return '考研专座查询';
  if (route.path === '/seminar-room-list') return '研讨室预约查询';
  return '自习室普通座位查询';
});
const query = ref<{
  date: any;
  start: any;
  end: any;
}>({
  date: dayjs(),
  start: null,
  end: null,
});

const loading = ref(false);
const rooms = ref<(StudyRoom & { id: number })[]>([]);


const loadRooms = async() => {
  loading.value = true;
  try {
      const config = {
      headers: { Authorization: `Bearer ${studentStore.token}` }
    };
    const studyRoomRequest = axios.get("/api/studyRoomManage/rooms", config);
    const seminarRoomRequest = axios.get("/api/seminar-room/all", config);

    const [studyRoomsRes, seminarRoomsRes] = await Promise.all([
      studyRoomRequest,
      seminarRoomRequest
    ]);

    const studyRooms = (studyRoomsRes.data.data || []).map((room: any) => ({
      ...room,
      id: room.studyRoomID,
      studyRoomName: room.studyRoomName,
}));

    const seminarRooms = (seminarRoomsRes.data.data || []).map((room: any) => ({
        id: room.seminarRoomID,
        studyRoomCapacity: room.seminarRoomMax,
       // studyRoomLocation: room.seminarRoomLocation,
        studyRoomName: room.seminarRoomName,
        status: room.seminarRoomStatus,
        studyRoomType: 2,
        //studyRoomOpentime: room.seminarRoomOpentime,
        //studyRoomClosetime: room.seminarRoomClosetime,
        currentlyIdleSeat: (room.seminarRoomMax - (room.currentNum || 0))
    }));

    const allResources = [...studyRooms, ...seminarRooms];


    const path = route.path;
    if (path === '/study-seat-list') {
      // 考研自习室
      rooms.value = allResources.filter(r => r.studyRoomType === 1);
    } else if (path === '/seminar-room-list') {
      // 研讨室
      rooms.value = allResources.filter(r => r.studyRoomType === 2);
    } else {
      // 普通自习室 (默认 /seat-list)
      rooms.value = allResources.filter(r => r.studyRoomType === 0);
    }

    console.log("当前显示数据:", rooms.value);

  } catch (err) {
    console.error("加载资源失败", err);
    message.error("加载资源列表失败");
    rooms.value = [];
  } finally {
    loading.value = false;
  }
};
watch(() => route.path, (newPath) => {
  console.log("路由变化了，新路径是:", newPath);
  loadRooms();
});

onMounted(() => {
  loadRooms();
});

// 查询按钮
const search = async () => {
  await loadRooms();
  console.log("查询参数：", query.value);
};

const reset = async () => {
  query.value.date = dayjs();
  query.value.start = null;
  query.value.end = null;
  await search();
};

onMounted(() => {
  loadRooms();
});

// 点击自习室跳转座位页
const openRoom = (room: any) => {
    // 检查是否为研讨室
    if (room.studyRoomType === 2) {
        router.push({
            path: "/reservation-form",
            query: {
                //roomId: room.studyRoomID,
                roomId: room.id,
                seatType: 'STUDYROOM', // 告诉表单这是研讨室
                //roomName: room.studyRoomName,
                //maxCapacity: room.studyRoomCapacity
            }
        });
    } else {
        router.push({
            path: "/seat-map",
            query: {
                //roomId: room.studyRoomID,
                roomId: room.id,
                roomName: room.studyRoomName, 
                //seatCount: room.studyRoomCapacity
                seatType: room.studyRoomType === 1 ? 'POSTGRAD' : 'NORMAL'
            }
        });
    }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  width: 100%;
  flex: 1;             
  box-sizing: border-box;
  background: #f5f7fb;
  padding: 24px;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
}

.header {
  display: flex;
  flex-direction: column; 
  gap: 10px;
  margin-bottom: 14px;
}


.title-wrap {
  width: 100%;
  text-align: center;
}

.title-wrap h2 {
  margin: 0;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.sub {
  margin-top: 2px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

.filter-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

:deep(.ant-form-item) {
  margin-bottom: 0;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.room-card {
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.room-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.room-id {
  font-weight: 700;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 12px;
}

.label {
  color: rgba(0, 0, 0, 0.45);
}

.value {
  font-weight: 600;
}

@media (max-width: 768px) {
  .page {
    padding: 16px;
  }
  .header {
    flex-wrap: wrap;
  }
}
</style>
