<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import type { Dayjs } from 'dayjs'
import { title } from 'process'

const API_BASE_URL = 'http://localhost:3000/api'

interface ReservationRecord {
  reservationRecordID: number
  studentID: number | null
  studyRoomID?: number
  seatID?: number
  seminarRoomID?: number
  seminarRoomNum?:number
  reservationStartTime: string
  reservationEndTime: string
  reservationRecordStatus: number //预约状态：0-待审批，1-已通过，2-已开始，3-已结束，4-已取消，5-已拒绝
}

const ReservationtableData = ref<ReservationRecord[]>([])

const QueryParams = ref<{ studentID: number | null; reservationDate: Dayjs | null; studyRoomID: number | null; seminarRoomID: number | null }>({
  studentID: null,
  reservationDate: null,
  studyRoomID: null,
  seminarRoomID: null
})

const columns = [
  { title: '学号', dataIndex: 'studentID' },
  { title: '姓名', dataIndex: 'studentUserName' },
  { title: '房间类型', dataIndex: 'roomType', slots: { customRender: 'roomType' } },
  { title: '研讨室ID', dataIndex: 'seminarRoomID' , slots: { customRender: 'seminarRoomID' } } ,
  { title: '自习室ID', dataIndex: 'studyRoomID' , slots: { customRender: 'studyRoomID' }},
  { title: '座位ID', dataIndex: 'seatID', slots: { customRender: 'seatID' }  },
  { title: '预约开始时间', dataIndex: 'reservationStartTime' },
  { title: '预约结束时间', dataIndex: 'reservationEndTime' },
  { title: '状态', dataIndex: 'reservationRecordStatus', slots: { customRender: 'reservationRecordStatus' } },
  { title: '操作', slots: { customRender: 'action' } }
]

const loadData = async (params: Partial<typeof QueryParams.value> = {}) => {
  const p: Record<string, string | number> = {}
  if (params.studentID) p.studentId = params.studentID
  if (params.reservationDate) p.reservationDate = dayjs(params.reservationDate).format('YYYY-MM-DD')
  if (params.studyRoomID) p.studyRoomId = params.studyRoomID
  if (params.seminarRoomID) p.seminarRoomId = params.seminarRoomID

  try {
    const queryString = new URLSearchParams(p as Record<string, string>).toString()
    const response = await fetch(
      `${API_BASE_URL}/reservation/query?${queryString}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (response.ok) {
      const res = await response.json()
      ReservationtableData.value = Array.isArray(res.data) ? res.data : []
    } else {
      message.error('获取预约列表失败')
    }
  } catch (error) {
    console.error('获取预约列表错误:', error)
    message.error('获取预约列表失败')
  }
}

const search = () => {
  loadData({ studentID: QueryParams.value.studentID, reservationDate: QueryParams.value.reservationDate, studyRoomID: QueryParams.value.studyRoomID, seminarRoomID: QueryParams.value.seminarRoomID })
}

const reset = () => {
  QueryParams.value.studentID = null
  QueryParams.value.reservationDate = null
  QueryParams.value.studyRoomID = null
  QueryParams.value.seminarRoomID = null
  loadData()
}

const approveReservation = async (reservationRecordID: number, status: number) => {
  try {
    // 构造查询参数
    const params = new URLSearchParams({
      status: status.toString()
    });

    const response = await fetch(`${API_BASE_URL}/reservation/status/${reservationRecordID}?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (response.ok) {
      const res = await response.json();
      message.success(res.message || '已批准');
      // 如果后端返回了更新后的记录，则可以用它来更新本地数据
      if (res.data) {
        const index = ReservationtableData.value.findIndex(item => item.reservationRecordID === reservationRecordID);
        if (index !== -1) {
          ReservationtableData.value[index] = { ...ReservationtableData.value[index], ...res.data };
        }
      } else {
        // 否则重新加载数据
        loadData({ 
          studentID: QueryParams.value.studentID, 
          reservationDate: QueryParams.value.reservationDate,
          studyRoomID: QueryParams.value.studyRoomID,
          seminarRoomID: QueryParams.value.seminarRoomID
        });
      }
    } else {
      message.error('批准失败');
    }
  } catch (error) {
    console.error('批准请求错误:', error);
    message.error('批准失败');
  }
};

const rejectReservation = async (reservationRecordID: number, status: number) => {
  try {
    // 构造查询参数
    const params = new URLSearchParams({
      status: status.toString()
    });
    
    const response = await fetch(`${API_BASE_URL}/status/${reservationRecordID}?${params.toString()}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (response.ok) {
      const res = await response.json();
      message.success(res.message || '已拒绝');
      
      // 如果后端返回了更新后的记录，则用它来更新本地数据
      if (res.data) {
        const index = ReservationtableData.value.findIndex(item => item.reservationRecordID === reservationRecordID);
        if (index !== -1) {
          ReservationtableData.value[index] = { ...ReservationtableData.value[index], ...res.data };
        }
      } else {
        // 否则重新加载数据
        loadData({ 
          studentID: QueryParams.value.studentID, 
          reservationDate: QueryParams.value.reservationDate,
          studyRoomID: QueryParams.value.studyRoomID,
          seminarRoomID: QueryParams.value.seminarRoomID
        });
      }
    } else {
      message.error('拒绝失败');
    }
  } catch (error) {
    console.error('拒绝请求错误:', error);
    message.error('拒绝失败');
  }
};

onMounted(() => {
  loadData()
})
</script>


<template>
  <div class="admin-container">
    <a-card title="管理员预约管理">
      <div style="margin-bottom:12px; display:flex; gap:8px; align-items:center; flex-wrap:wrap">
        <a-input v-model:value="QueryParams.studentID" placeholder="学生学号" style="width:180px" />
        <a-input v-model:value="QueryParams.studyRoomID" placeholder="自习室ID" style="width:180px" />
        <a-input v-model:value="QueryParams.seminarRoomID" placeholder="研讨室ID" style="width:180px" />
        <a-date-picker v-model:value="QueryParams.reservationDate" placeholder="选择日期" style="width:180px" />
        <a-button type="primary" @click="search">查询</a-button>
        <a-button @click="reset">重置</a-button>
      </div>

      <a-table :dataSource="ReservationtableData" :columns="columns" rowKey="reservationRecordID">

        <!-- 房间类型列 -->
        <template #roomType="{ record }">
          <span>{{ record.studyRoomID ? '自习室' : record.seminarRoomID ? '研讨室' : '' }}</span>
        </template>

        <!-- 自习室ID列 -->
        <template #studyRoomID="{ record }">
          <span>{{ record.studyRoomID || '-' }}</span>
        </template>

        <!-- 座位ID列 -->
        <template #seatID="{ record }">
          <span>{{ record.seatID || '-' }}</span>
        </template>

        <!-- 研讨室ID列-->>
        <template #seminarRoomID="{ record }">
          <span>{{ record.seminarRoomID || '-' }}</span>
        </template>

        
        <!-- 日期列 -->
        <template #date="{ record }">
          <span>{{ record.reservationStartTime ? dayjs(record.reservationStartTime).format('YYYY-MM-DD') : '' }}</span>
        </template>
        
        <!-- 时间段列 -->
        <template #timeSlot="{ record }">
          <span>
            {{ record.reservationStartTime ? dayjs(record.reservationStartTime).format('HH:mm') : '' }} - 
            {{ record.reservationEndTime ? dayjs(record.reservationEndTime).format('HH:mm') : '' }}
          </span>
        </template>

        <!-- 状态列 -->
        <template #reservationRecordStatus="{ text }">
          <a-tag v-if="text === 0" color="blue">待审核</a-tag>
          <a-tag v-if="text === 1" color="green">已通过</a-tag>
          <a-tag v-if="text === 2" color="orange">已开始</a-tag>
          <a-tag v-if="text === 3" color="default">已结束</a-tag>
          <a-tag v-if="text === 4" color="default">已取消</a-tag>
          <a-tag v-if="text === 5" color="red">已拒绝</a-tag>
        </template>
        
        <!-- 操作列 -->
        <template #action="{ record }">
          <a-space>
            <a-button
              v-if="record.reservationRecordStatus === 0"
              type="primary"
              @click="approveReservation(record.reservationRecordID, 1)"
              size="small"
            >批准</a-button>
            <a-button
              v-if="record.reservationRecordStatus === 0"
              danger
              @click="rejectReservation(record.reservationRecordID, 5)"
              size="small"
            >拒绝</a-button>
          </a-space>
        </template>
      </a-table>
    </a-card>
  </div>
</template>


<style scoped>
.admin-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}
</style>