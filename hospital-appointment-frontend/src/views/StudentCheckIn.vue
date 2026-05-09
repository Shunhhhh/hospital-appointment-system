<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { CheckCircleOutlined, LogoutOutlined, UserOutlined, ClockCircleOutlined, HourglassOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useStudentStore } from '@/store/studentStore'

// 座位信息类型
interface SeatInfo {
  seatID: number // 座位ID
  seatLocation?: string // 座位位置描述
  seatType: number // 座位类型：0-通用座位，1-专用座位
  seatBelonging: number // 座位所属自习室ID
  seatNumber: number // 座位号
  seatStatus: number // 0-可预约，1-已预约，2-未签到，3-已占用，4-暂离，5-维修中
}

// 签到记录类型
interface CheckInRecord {
  attendanceRecordID: number
  studentID: number
  studyRoomID?: number
  seatID?: number
  seminarRoomID?: number
  seminarRoomNum?: number
  checkInTime: string
  checkOutTime?: string
  attendanceStatus: number // 1-已签到，2-已完成，3-未按时签到，4-未按时签退
  reservationRecordID: string;
}

// 今日签到记录类型
interface TodayCheckInRecord {
  attandenceRecordID: number
  studentID: number
  studyRoomID?: number
  seatID?: number
  seminarRoomID?: number
  seminarRoomNum?: number
  checkInTime?: string
  checkOutTime?: string
  attendanceStatus: number // 1-已签到，2-已完成，3-未按时签到，4-未按时签退
  reservationRecordID: string;
}

// 预约记录类型
interface ReservationRecord {
  reservationRecordID: string
  studentID: number
  studyRoomID?: number
  seatID?: number
  seminarRoomID?: number
  seminarRoomNum?: number
  reservationStartTime: string
  reservationEndTime: string
  reservationRecordStatus: number //预约状态：0-待审批，1-已通过，2-已开始，3-已结束，4-已取消
}

// 使用学生信息store
const studentStore = useStudentStore()
const { studentInfo } = storeToRefs(studentStore)


// 签到记录
const CheckInRecord = ref<CheckInRecord | null>(null)

// 座位信息
const seatInfo = ref<SeatInfo | null>(null)

// 暂离状态
const temporaryLeaveStatus = ref({
  isOnTemporaryLeave: false,
  startTime: ''
});

// 控制暂离提示弹窗显示
const showTemporaryLeaveModal = ref(false);

// 控制签到完成提示弹窗显示
const showCompletionModal = ref(false)

// 添加预约信息响应式变量
const reservationInfo = ref<ReservationRecord | null>(null)

// 今日所有签到记录
const todayCheckInRecords = ref<TodayCheckInRecord[]>([])

// 后端服务器
const API_BASE_URL = 'http://localhost:3000/api'

// 格式化时间为本地可读字符串，若无法解析则返回原值或空字符串
const formatDate = (iso?: string) => {
  if (!iso) return ''
  const d = new Date(iso)
  if (isNaN(d.getTime())) return iso
  return d.toLocaleString()
}

// 加载本地存储的签到状态和获取学生信息
onMounted(async () => {
  try {
    // 从服务器获取距离当前时间最近的预约信息
    let reservationData: ReservationRecord | null = null;

    // 如果有学生信息，尝试从预约接口获取
    if (studentInfo.value?.studentID) {
      reservationData = await fetchEarliestReservation();
    }

    // 如果有预约信息，保存座位信息和预约详情
    if (reservationData) {
      seatInfo.value = {
        seatID: reservationData.seatID ?? 0,
        seatLocation: '',
        seatType: 0,
        seatBelonging: reservationData.studyRoomID ?? 0,
        seatNumber: reservationData.seatID ?? 0,
        seatStatus: 0
      };
      reservationInfo.value = reservationData;
    }

    // 获取座位当前是否被占用
    await fetchSeatStatus()

    // 获取学生当前的签到记录
    await fetchCurrentCheckInRecord()

    // 获取今日所有签到记录
    await fetchTodayCheckInRecords()

  } catch (error) {
    console.error('初始化失败:', error)
    message.error('系统初始化失败')
  }
})

// 从服务器获取学生当天最早的预约信息，返回整条预约记录
const fetchEarliestReservation = async () => {
  if (!studentInfo.value?.studentID) return null;

  try {
    const response = await fetch(
      `${API_BASE_URL}/reservation/earliest-today?studentId=${studentInfo.value.studentID}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    )

    if (response.ok) {
      const result = await response.json();
      if (result.code === 200 && result.data) {
        return result.data;
      } else if (result.code === 200 && result.message === "当天没有预约记录") {
        // 当天没有预约记录，使用默认值
        return null;
      }
    }
    return null;
  } catch (error) {
    console.error('获取预约信息错误:', error)
    return null;
  }
}

// 从服务器获取指定座位的当前占用状态
const fetchSeatStatus = async () => {
  if (!seatInfo.value) return

  try {
    const response = await fetch(
      `${API_BASE_URL}/seatManage/seat?seatID=${seatInfo.value.seatID}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    )

    if (response.ok) {
      const result = await response.json()
      const data = result.data
      if (seatInfo.value) {
        seatInfo.value.seatStatus = data.seatCheckInStatus
      }
    }
  } catch (error) {
    console.error('获取座位状态错误:', error)
  }
}

// 获取学生当前签到状态（实时显示签到状态）
const fetchCurrentCheckInRecord = async () => {
  if (!studentInfo.value.studentID) return

  try {
    const response = await fetch(
      `${API_BASE_URL}/attendance/current?studentId=${studentInfo.value.studentID}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    )
    if (response.ok) {
      const data: CheckInRecord = (await response.json()).data
      if (data) {
        // 更新学生签到状态，统一格式化时间显示
        CheckInRecord.value = {
          attendanceRecordID: data.attendanceRecordID,
          studentID: data.studentID ?? studentInfo.value.studentID,
          studyRoomID: data.studyRoomID ?? 0,
          seatID: data.seatID ?? 0,
          seminarRoomID: data.seminarRoomID ?? 0,
          seminarRoomNum: data.seminarRoomNum ?? 0,
          attendanceStatus: data.attendanceStatus,
          checkInTime: formatDate(data.checkInTime),
          checkOutTime: data.checkOutTime ? formatDate(data.checkOutTime) : undefined,
          reservationRecordID: data.reservationRecordID
        }
        console.log('当前签到记录:', CheckInRecord.value)
      }
    }
  } catch (error) {
    console.error('获取签到记录错误:', error)
  }
}

// 获取今日所有签到记录
const fetchTodayCheckInRecords = async () => {
  if (!studentInfo.value?.studentID) return

  try {
    const response = await fetch(
      `${API_BASE_URL}/attendance/today?studentId=${studentInfo.value.studentID}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    )

    if (response.ok) {
      const result = await response.json()
      if (result.code === 200 && Array.isArray(result.data)) {
        todayCheckInRecords.value = result.data.map((record: TodayCheckInRecord) => ({
          ...record,
          checkInTime: formatDate(record.checkInTime),
          checkOutTime: record.checkOutTime ? formatDate(record.checkOutTime) : undefined
        }))
      }
    } else {
      console.error('获取今日签到记录失败:', await response.text())
    }
  } catch (error) {
    console.error('获取今日签到记录错误:', error)
  }
}
// 签到功能
const handleCheckIn = async () => {
  // 验证座位信息是否存在
  if (!seatInfo.value) {
    message.warning('座位信息无效')
    return
  }
  try {
    // 发送自习室座位签到请求到服务器
    console.log('发起签到请求:', {
      studentID: studentInfo.value.studentID,
      studyRoomID: seatInfo.value.seatBelonging,
      seatID: seatInfo.value.seatID
    })
    const response = await fetch(`${API_BASE_URL}/attendance/check-in`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        studentID: studentInfo.value.studentID,
        studyRoomID: seatInfo.value.seatBelonging,
        seatID: seatInfo.value.seatID
      })
    })

    // 处理服务器响应结果
    if (response.ok) {
      const result = await response.json()
      const data = result.data
       // 更新签到状态信息
      CheckInRecord.value = {
        attendanceStatus: data.attendanceStatus,
        studentID: data.studentID,
        checkInTime: formatDate(data.checkInTime),
        attendanceRecordID: data.attendanceRecordID,
        reservationRecordID: data.reservationRecordID,
      }

      // 从服务器获取最新的座位状态
      await fetchSeatStatus()
      message.success(`签到成功！签到时间：${CheckInRecord.value.checkInTime}`)

    } else {
      // 处理签到失败的情况
      const errorData = await response.json()
      message.error(errorData.message || '签到失败')
    }
  } catch (error) {
    // 处理网络或其他异常错误
    console.error('签到错误:', error)

    if (error instanceof TypeError && error.message.includes('Failed to fetch')) {
      message.error('无法连接到服务器，请检查后端服务是否启动')
    } else {
      message.error('签到失败，请稍后重试')
    }
  }
}

// 签退功能
const handleCheckOut = async () => {
  // 验证当前是否已签到
  if (!CheckInRecord.value || !CheckInRecord.value.attendanceStatus || !CheckInRecord.value.checkInTime) {
    message.warning('您还未签到')
    return
  }

  console.log('发起签退请求 for record ID:', CheckInRecord.value.attendanceRecordID)
  try {
    const response = await fetch(
      `${API_BASE_URL}/attendance/check-out/${CheckInRecord.value.attendanceRecordID}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    )

    if (response.ok) {
      const result = await response.json()
      const data = result.data
      const checkoutTime = data?.checkOutTime ? formatDate(data.checkOutTime) : new Date().toLocaleString()
      CheckInRecord.value = {
          attendanceRecordID: data.attendanceRecordID ?? CheckInRecord.value?.attendanceRecordID,
          studentID: studentInfo.value.studentID,
          studyRoomID: data.studyRoomID ?? CheckInRecord.value?.studyRoomID ?? 0,
          seatID: data.seatID ?? CheckInRecord.value?.seatID ?? 0,
          seminarRoomID: data.seminarRoomID ?? CheckInRecord.value?.seminarRoomID ?? 0,
          seminarRoomNum: data.seminarRoomNum ?? CheckInRecord.value?.seminarRoomNum ?? 0,
          attendanceStatus: data.attendanceStatus,
          checkInTime: CheckInRecord.value?.checkInTime,
          checkOutTime: checkoutTime,
          reservationRecordID: data.reservationRecordID ?? CheckInRecord.value?.reservationRecordID ?? 0
        };

      // 从服务器获取最新的座位状态
      await fetchSeatStatus()

      message.success(`签退成功！签退时间：${CheckInRecord.value.checkOutTime}`)

      // 显示完成弹窗
      showCompletionModal.value = true

      // 签退后获取今日所有签到记录
      await fetchTodayCheckInRecords()

      // 签退后刷新座位状态并尝试获取下一次预约信息（若存在则显示）
      const nextReservation = await fetchEarliestReservation()
      await fetchSeatStatus()
      if (nextReservation) {
        seatInfo.value = {
          seatID: nextReservation.seatId,
          seatLocation: '',
          seatType: 0,
          seatBelonging: nextReservation.studyRoomId,
          seatNumber: nextReservation.seatId,
          seatStatus: 0
        };
        reservationInfo.value = nextReservation;
      } else {
        reservationInfo.value = null
      }
    } else {
      const errorData = await response.json()
      message.error(errorData.message || '签退失败')
    }
    } catch (error) {
      console.error('签退错误:', error)

      if (error instanceof TypeError && error.message.includes('Failed to fetch')) {
        message.error('无法连接到服务器，请检查后端服务是否启动')
      } else {
        message.error('签退失败，请稍后重试')
      }
    }
}
// 返回上一页
const goBack = () => {
  window.history.back()
}

// 暂离功能
const handleTemporaryLeave = async () => {
  if (!CheckInRecord.value || CheckInRecord.value.attendanceStatus!= 1 ) {
    message.warning('您还未签到')
    return;
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/attendance/temporary-leave/${CheckInRecord.value.attendanceRecordID}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    )

    if (response.ok) {
      const data = await response.json();
      // 显示暂离弹窗
      temporaryLeaveStatus.value = {
        isOnTemporaryLeave: true,
        startTime: data?.temporaryLeaveTime ? formatDate(data.temporaryLeaveTime) : new Date().toLocaleString()
      };
      showTemporaryLeaveModal.value = true;

      message.success('暂离登记成功')
    } else {
      const errorData = await response.json()
      message.error(errorData.message || '暂离登记失败')
    }
  } catch (error) {
    console.error('暂离错误:', error)
    message.error('暂离失败，请稍后重试')
  }
}

// 取消暂离功能
const cancelTemporaryLeave = async () => {
  if (!CheckInRecord.value || CheckInRecord.value.attendanceStatus != 1) {
    message.warning('无效的签到记录');
    return;
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/attendance/cancel-temporary-leave/${CheckInRecord.value.attendanceRecordID}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.ok) {
      temporaryLeaveStatus.value.isOnTemporaryLeave = false;
      showTemporaryLeaveModal.value = false;
      message.success('取消暂离成功');
    } else {
      const errorData = await response.json();
      message.error(errorData.message || '取消暂离失败');
    }
  } catch (error) {
    console.error('取消暂离错误:', error);
    message.error('取消暂离失败，请稍后重试');
  }
};

// 关闭暂离弹窗
const closeTemporaryLeaveModal = () => {
  showTemporaryLeaveModal.value = false;
};


</script>

<template>
  <div class="checkin-container">
    <a-card title="学生自习室签到系统" style="max-width: 800px; margin: 20px auto;">
      <!-- 学生信息展示 -->
      <a-descriptions title="学生信息" :column="{ xs: 1, sm: 2 }" bordered>
        <a-descriptions-item label="学号">{{ studentInfo.studentID }}</a-descriptions-item>
        <a-descriptions-item label="姓名">{{ studentInfo.studentUserName }}</a-descriptions-item>
        <!-- <a-descriptions-item label="班级" :span="2">{{ studentInfo.class }}</a-descriptions-item> -->
      </a-descriptions>

      <!-- 签到状态显示 -->
      <a-alert
        v-if="CheckInRecord?.attendanceStatus == 1"
        message="已签到"
        type="success"
        show-icon
        style="margin: 20px 0"
      >
        <template #description>
          <p>签到时间: {{ CheckInRecord.checkInTime }}</p>
          <p>当前状态: 正在自习中</p>
        </template>
      </a-alert>
    </a-card>


      <!-- 预约信息显示 -->
      <a-card
        title="预约信息"
        style="max-width: 800px; margin: 20px auto;"
        v-if="reservationInfo"
      >
        <a-descriptions :column="1">
          <a-descriptions-item label="自习室">{{ seatInfo?.seatBelonging }}</a-descriptions-item>
          <a-descriptions-item label="座位号">{{ seatInfo?.seatNumber }}</a-descriptions-item>
          <a-descriptions-item label="预约时间">{{ formatDate(reservationInfo.reservationStartTime) }} - {{ formatDate(reservationInfo.reservationEndTime) }}</a-descriptions-item>
          <a-descriptions-item label="座位状态">
            <a-tag :color="seatInfo?.seatStatus === 0 ? 'green' : 'red'">
              {{ seatInfo?.seatStatus === 0 ? '未签到' : '已占用' }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-row :gutter="16" justify="center">
          <a-col>
            <a-button
              type="primary"
              size="large"
              @click="handleCheckIn"
              :disabled="CheckInRecord?.attendanceStatus == 1 && !CheckInRecord.checkOutTime"
            >
              <template #icon>
                <CheckCircleOutlined />
              </template>
              签到
            </a-button>
          </a-col>

          <a-col>
            <a-button
              type="primary"
              danger
              size="large"
              @click="handleCheckOut"
              :disabled="CheckInRecord?.attendanceStatus !== 1 || !!CheckInRecord?.checkOutTime"
            >
              <template #icon>
                <LogoutOutlined />
              </template>
              签退
            </a-button>
          </a-col>

          <a-col>
            <a-button
              type="primary"
              size="large"
              @click="handleTemporaryLeave"
              :disabled="!(CheckInRecord?.attendanceStatus === 1) || !!CheckInRecord?.checkOutTime"
            >
              <template #icon>
                <UserOutlined />
              </template>
              暂离
            </a-button>
          </a-col>
        </a-row>
      </div>

      <!-- 签到记录 -->
      <a-card title="今日签到记录"
        style="max-width: 800px; margin: 20px auto;"
        v-if="todayCheckInRecords.length > 0"
        >
        <a-list item-layout="horizontal" :data-source="todayCheckInRecords">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta description="今日签到记录">
                <template #title>
                  <a-typography-text strong>
                    {{ studentInfo.studentUserName }}
                  </a-typography-text>
                </template>
              </a-list-item-meta>
              <div>
                <p><ClockCircleOutlined /> 签到: {{ item.checkInTime }}</p>
                <p v-if="item.checkOutTime"><ClockCircleOutlined /> 签退: {{ item.checkOutTime }}</p>
                <p v-else><HourglassOutlined /> 状态: 自习中</p>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </a-card>

      <!-- 当只有一条记录且正在自习中时也显示 -->
      <a-card title="今日签到记录"
        style="max-width: 800px; margin: 20px auto;"
        v-else-if="CheckInRecord?.checkInTime && seatInfo"
      >
        <a-list item-layout="horizontal" :data-source="[CheckInRecord]">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta description="今日签到记录">
                <template #title>
                  <a-typography-text strong>
                    {{ studentInfo.studentUserName }} - {{ seatInfo.seatBelonging }}({{ seatInfo.seatNumber }})
                  </a-typography-text>
                </template>
              </a-list-item-meta>
              <div>
                <p><ClockCircleOutlined /> 签到: {{ item.checkInTime }}</p>
                <p v-if="item.checkOutTime"><ClockCircleOutlined /> 签退: {{ item.checkOutTime }}</p>
                <p v-else><HourglassOutlined /> 状态: 自习中</p>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </a-card>

    <!-- 暂离状态弹窗 -->
    <a-modal
      v-model:open="showTemporaryLeaveModal"
      title="暂离状态"
      :footer="null"
      :closable="false"
      :maskClosable="false"
      @cancel="closeTemporaryLeaveModal"
      width="400px"
    >
      <div style="text-align: center; padding: 20px;">
        <a-alert
          message="暂离中"
          type="warning"
          show-icon
          style="margin-bottom: 20px;"
        >
          <template #description>
            <p>暂离开始时间: {{ temporaryLeaveStatus.startTime }}</p>
          </template>
        </a-alert>

        <a-button
          type="primary"
          @click="cancelTemporaryLeave"
          size="large"
        >
          取消暂离
        </a-button>
      </div>
    </a-modal>

    <!-- 完成自习弹窗 -->
    <a-modal
      v-model:open="showCompletionModal"
      title="完成自习"
      :footer="null"
      :closable="false"
      :maskClosable="false"
      width="400px"
    >
      <div style="text-align: center; padding: 20px;">
        <a-alert
          message="已完成本次自习"
          type="success"
          show-icon
          style="margin-bottom: 20px;"
        >
          <template #description>
            <p v-if="CheckInRecord?.checkInTime">签到时间: {{ CheckInRecord.checkInTime }}</p>
            <p v-if="CheckInRecord?.checkOutTime">签退时间: {{ CheckInRecord.checkOutTime }}</p>
          </template>
        </a-alert>

        <a-button
          type="primary"
          @click="goBack"
          size="large"
        >
          返回上一页
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.checkin-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.action-buttons {
  margin: 30px 0;
  text-align: center;
}

.ant-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
