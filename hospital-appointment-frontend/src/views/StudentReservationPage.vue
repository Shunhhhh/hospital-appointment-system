<template>
  <div class="page">
    <a-card class="shell" :bordered="false">
      <!-- 标题 -->
      <div class="header">
        <h2>我的预约</h2>
      </div>

      <!-- 添加预约按钮 -->
      <a-button type="primary" @click="openAddModal">+ 添加预约</a-button>

      <a-table :dataSource="tableData" :columns="columns" rowKey="id" style="margin-top: 16px">
        <template #status="{ text }">
          <a-tag v-if="text === '已通过'" color="green">已通过</a-tag>
          <a-tag v-if="text === '待审核'" color="blue">待审核</a-tag>
          <a-tag v-if="text === '已拒绝'" color="red">已拒绝</a-tag>
          <a-tag v-if="text === '已取消'" color="default">已取消</a-tag>
          <a-tag v-if="text === '待候补'" color="orange">待候补</a-tag>
          <a-tag v-if="text === '候补成功待确认'" color="purple">候补成功待确认</a-tag>
        </template>

        <template #action="{ record }">
          <template v-if="record.type === 'WAITLIST'">
            <a-button
              v-if="record.status === '候补成功待确认'"
              type="primary"
              @click="confirmWaitlist(record.id)"
              style="margin-right:8px"
            >
              确认预约
            </a-button>
            <a-button danger @click="cancelWaitlist(record.id)">
              取消候补
            </a-button>
          </template>

          <template v-else>
            <a-button
              type="link" 
              danger
              @click="cancelReservation(record.id)"
            >
              取消预约
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts"  setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import dayjs from "dayjs";
import type { Dayjs } from "dayjs";
import { useStudentStore } from '@/store/studentStore'
import router from "@/router";


const studentStore = useStudentStore();


function getLocalWaitlist() {
  const waitlistStr = localStorage.getItem('localWaitlist');
  return waitlistStr ? JSON.parse(waitlistStr) : [];
}

// function saveLocalWaitlist(waitlist: any[]) {
//   localStorage.setItem('localWaitlist', JSON.stringify(waitlist));
// }
// function simulateWaitlistSuccess() {
//   const waitlist = getLocalWaitlist();
//   const index = waitlist.findIndex((r: any) => r.status === '待候补');
  
//   // 随机让一个候补成功 
//   if (index !== -1 && Math.random() < 0.3) { 
//     waitlist[index].status = '候补成功待确认';
//     saveLocalWaitlist(waitlist);
//     message.info(`您的候补申请 ${waitlist[index].seatId} 在本地模拟成功，请点击确认预约！`);
//     // loadData(); // 不在这里重新加载，避免死循环，在外部调用 loadData
//   }
// }

interface ReservationItem {
  id: string;
  roomId: string;
  seatId: string;
  date: string;
  timeSlot: string;
  status: string;
}

// 表格数据
const tableData = ref<ReservationItem[]>([]);

// 本地候补列表（从 localStorage 读取）
const localWaitlist = ref<any[]>(getLocalWaitlist());


// 表格列
const columns = [
  { title: "自习室", dataIndex: "roomId" },
  { title: "座位号", dataIndex: "seatId" },
  { title: "日期", dataIndex: "date" },
  { title: "时间段", dataIndex: "timeSlot" },
  { title: "状态", dataIndex: "status", slots: { customRender: "status" } },
  { title: "操作", slots: { customRender: "action" } }
];



const loadData = async () => {
  const currentID = studentStore.studentInfo.studentID; 
  try {
    // 同时请求正式预约和候补记录
    const [resRes, waitRes] = await Promise.all([
      axios.get("/api/reservation/getReservation", { params: { studentId: currentID } }),
      axios.get("/api/wait/getWaitlist", { params: { studentId: currentID } })
    ]);

    // 处理正式预约：直接匹配你日志中出现的字段名 id, roomId, seatId, date, timeSlot
    const reservationList = (resRes.data.data || []).map((r: any) => ({
      ...r,
      id: r.id, // 日志显示字段名为小写 id
      type: 'RESERVATION',
      roomId: r.roomId || r.seminarRoomId || r.seminarRoomID || '研讨室',
      seatId: r.seatId || r.seminarRoomID ,
      date: r.date,
      timeSlot: r.timeSlot,
      // 如果后端没返回 status 字段，根据你之前的描述，默认显示已通过
      status: r.status || '已通过' 
    }));

    // 处理候补记录
    const waitlistList = (waitRes.data.data || []).map((w: any) => ({
      ...w,
      id: w.waitlistRecordId, // 候补后端返回的是 waitlistRecordId
      type: 'WAITLIST',
      roomId: w.studyRoomID || w.roomId || w.studyRoomID || w.seminarRoomID || w.seminarRoomId || w.studyRoomId || '未知房间',
      seatId: w.seatID ||w.seatId|| '候补中',
      // 格式化时间
      date: dayjs(w.waitListStartTime).format('YYYY-MM-DD'),
      timeSlot: `${dayjs(w.waitListStartTime).format('HH:mm')} - ${dayjs(w.waitListEndTime).format('HH:mm')}`,
      status: w.waitListStatus === 1 ? '候补成功待确认' : '待候补'
    }));

    tableData.value = [...reservationList, ...waitlistList];
  } catch (e) {
    console.error("加载失败:", e);
    message.error('加载记录失败');
  }
};

// 确认候补预约 
const confirmWaitlist = async (id: number | string) => {
  try {
    const res = await axios.post('/api/wait/confirmWaitlist', null, {
      params: { waitlistRecordId: id }
    });
    if (res.data.code === 200) {
      message.success("预约已确认，已转为正式预约");
      loadData(); // 刷新列表
    } else {
      message.error(res.data.message || "确认预约失败");
    }
  } catch (e) {
    message.error("网络错误，确认预约失败");
  }
};

// 禁选过去日期
const disabledDate = (current: Dayjs | undefined): boolean => {
  return current ? current < dayjs().startOf('day') : false;
};

// 打开新增预约弹窗，清空表单
const openAddModal = () => {
  router.push({ path: '/seat-list' });
};


// 取消预约方法
const cancelReservation = async (id: string) => {
  try {
    // 💡 必须使用 params 传参，后端才能通过 @RequestParam 获取到
    await axios.post('/api/reservation/cancelReservation', null, {
      params: { reservationId: id } 
    });
    message.success('预约已取消');
    loadData();
  } catch {
    message.error('取消预约失败');
  }
};

// 取消候补方法
const cancelWaitlist = async (id: string) => {
  try {
    await axios.post('/api/wait/cancelWaitlist', null, {
      params: { waitlistRecordId: id }
    });
    message.success('候补已取消');
    loadData();
  } catch {
    message.error('取消候补失败');
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
h2 {
  margin-bottom: 16px;
}
</style>
