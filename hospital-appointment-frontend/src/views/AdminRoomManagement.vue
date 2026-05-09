<script setup lang="ts">
import { ref, reactive, onMounted} from 'vue'
import { TimePicker } from 'ant-design-vue'
import { resourceService, StudyRoom, SeminarRoom, Seat } from '../api/resourceService'

// 当前激活的标签页
const activeTab = ref<'studyRoom' | 'seminarRoom'>('studyRoom')

// 模态框相关状态
const showModal = ref(false)
const showDeleteConfirm = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const resourceToDelete = ref<number | null>(null)

// 加载中状态
const loading = ref(false)
const errorMessage = ref('')

// 当前编辑的资源
const currentResource = reactive({
  id: 0,
  // 通用字段
  roomNumber: '',
  location: '一号楼1楼',
  openTime: '08:00',
  closeTime: '22:00',
  status: 0,
  // 自习室字段
  seatCount: 0,
  // 研讨室字段
  seminarRoomMin: 0,
  seminarRoomMax: 0
})

// 自习室
const studyRooms = ref<StudyRoom[]>([])

// 研讨室
const seminarRooms = ref<SeminarRoom[]>([])

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    errorMessage.value = ''
    const [studyRoomsRes, seminarRoomsRes] = await Promise.all([
      resourceService.getStudyRooms(),
      resourceService.getSeminarRooms()
    ])
    console.log(studyRoomsRes.data)
    studyRooms.value = studyRoomsRes.data.data
    seminarRooms.value = seminarRoomsRes.data.data
  } catch (error) {
    console.error('加载数据失败:', error)
    errorMessage.value = '加载数据失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 在组件挂载时加载数据
onMounted(() => {
  loadData()
})

// 打开添加模态框
const openAddModal = (type: 'studyRoom' | 'seminarRoom') => {
  activeTab.value = type
  modalMode.value = 'add'

  // 重置表单
  currentResource.id = 0
  currentResource.roomNumber = ''
  currentResource.location = ''
  currentResource.seatCount = type === 'studyRoom' ? 50 : 0
  currentResource.seminarRoomMin = type === 'seminarRoom' ? 2 : 0
  currentResource.seminarRoomMax = type === 'seminarRoom' ? 10 : 0
  currentResource.openTime = '08:00'
  currentResource.closeTime = '22:00'
  currentResource.status = 0

  showModal.value = true
}

// openEditModal 函数签名
const openEditModal = (
  room: StudyRoom | SeminarRoom,
  type: 'studyRoom' | 'seminarRoom'
) => {
  activeTab.value = type
  modalMode.value = 'edit'

  if (type === 'studyRoom') {
    const r = room as StudyRoom
    // 标记当前正在操作的自习室
    selectedStudyRoom.value = r
    currentResource.id = r.studyRoomID
    currentResource.roomNumber = r.studyRoomName || String(r.studyRoomID)
    currentResource.location = r.studyRoomLocation
    currentResource.seatCount = r.studyRoomCapacity
    currentResource.openTime = r.studyRoomOpentime
    currentResource.closeTime = r.studyRoomClosetime
    currentResource.status = r.status
  } else {
    const r = room as SeminarRoom
    // 标记当前正在操作的研讨室
    selectedSeminarRoom.value = r
    currentResource.id = r.seminarRoomID
    currentResource.roomNumber = r.seminarRoomName || String(r.seminarRoomID)
    currentResource.location = r.seminarRoomLocation
    currentResource.seminarRoomMin = r.seminarRoomMin
    currentResource.seminarRoomMax = r.seminarRoomMax
    currentResource.openTime = r.seminarRoomOpentime
    currentResource.closeTime = r.seminarRoomClosetime
    currentResource.status = r.seminarRoomStatus
  }

  showModal.value = true
}

// 关闭模态框
const closeModal = () => {
  showModal.value = false
  // 关闭模态框时清理选中对象，避免残留
  selectedStudyRoom.value = null
  selectedSeminarRoom.value = null
}

// 保存资源
const saveResource = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    if (modalMode.value === 'add') {
      // 添加新资源
      if (activeTab.value === 'studyRoom') {
        const newRoom: Omit<StudyRoom, 'studyRoomID'> = {
          studyRoomCapacity: currentResource.seatCount,
          studyRoomLocation: currentResource.location,
          studyRoomType: 0,
          studyRoomOpentime: currentResource.openTime,
          studyRoomClosetime: currentResource.closeTime,
          status: Number(currentResource.status),
          currentlyIdleSeat: currentResource.seatCount,
          studyRoomName: currentResource.roomNumber
        }
        await resourceService.addStudyRoom(newRoom)
      } else {
        const newRoom: Omit<SeminarRoom, 'seminarRoomID'> = {
          seminarRoomLocation: currentResource.location,
          seminarRoomMin: currentResource.seminarRoomMin || 2,
          seminarRoomMax: currentResource.seminarRoomMax || 10,
          seminarRoomStatus: Number(currentResource.status) || 0,
          currentNum: 0,
          seminarRoomOpentime: currentResource.openTime,
          seminarRoomClosetime: currentResource.closeTime,
          seminarRoomName: currentResource.roomNumber
        }
        await resourceService.addSeminarRoom(newRoom)
      }
    } else {
      // 更新现有资源
      if (activeTab.value === 'studyRoom') {
        const updateData: Omit<StudyRoom, 'studyRoomID'> = {
          studyRoomCapacity: currentResource.seatCount,
          studyRoomLocation: currentResource.location,
          studyRoomType: 0,
          studyRoomOpentime: currentResource.openTime,
          studyRoomClosetime: currentResource.closeTime,
          status: Number(currentResource.status),
          currentlyIdleSeat: currentResource.seatCount,
          studyRoomName: currentResource.roomNumber
        }
        await resourceService.updateStudyRoom(Number(currentResource.id), updateData)
      } else {
        const updateData: Omit<SeminarRoom, 'seminarRoomID'> = {
          seminarRoomLocation: currentResource.location,
          seminarRoomMin: currentResource.seminarRoomMin,
          seminarRoomMax: currentResource.seminarRoomMax,
          seminarRoomStatus: Number(currentResource.status),
          currentNum: 0,
          seminarRoomOpentime: currentResource.openTime,
          seminarRoomClosetime: currentResource.closeTime,
          seminarRoomName: currentResource.roomNumber
        }
        await resourceService.updateSeminarRoom(Number(currentResource.id), updateData)
      }
    }

    // 重新加载数据
    await loadData()
    closeModal()
  } catch (error) {
    console.error('保存失败:', error)
    errorMessage.value = '保存失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 删除资源确认
const deleteResource = (id: number, type: 'studyRoom' | 'seminarRoom') => {
  resourceToDelete.value = id
  activeTab.value = type
  showDeleteConfirm.value = true
}

// 关闭删除确认对话框
const closeDeleteConfirm = () => {
  showDeleteConfirm.value = false
  resourceToDelete.value = null
}

// 确认删除
const confirmDelete = async () => {
  if (resourceToDelete.value !== null) {
    try {
      loading.value = true
      errorMessage.value = ''

      if (activeTab.value === 'studyRoom') {
        await resourceService.deleteStudyRoom(resourceToDelete.value)
      } else {
        await resourceService.deleteSeminarRoom(resourceToDelete.value)
      }

      // 重新加载数据
      await loadData()
      closeDeleteConfirm()
    } catch (error) {
      console.error('删除失败:', error)
      errorMessage.value = '删除失败，请稍后重试'
    } finally {
      loading.value = false
    }
  }
}

// 格式化时间范围
const formatTimeRange = (openTime: string, closeTime: string) => {
  return `${openTime} - ${closeTime}`
}

// 获取状态
const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '可预约'
    case 1:
      return '已预约'
    case 2:
      return '未签到'
    case 3:
      return '已占用'
    case 4:
      return '暂离'
    case 5:
      return '维修中'
    default:
      return '未知状态'
  }
}

// 添加座位相关状态
const showSeatManagement = ref(false)
const selectedStudyRoom = ref<StudyRoom | null>(null)
const selectedSeminarRoom = ref<SeminarRoom | null>(null)
const seats = ref<Seat[]>([])

// 座位状态映射
const SEAT_STATUS_MAP: Record<number, string> = {
  0: '可预约',
  1: '已预约',
  2: '未签到',
  3: '已占用',
  4: '暂离',
  5: '维修中'
}

// 打开座位管理界面
const openSeatManagement = async (room: StudyRoom) => {
  try {
    loading.value = true
    errorMessage.value = ''
    selectedStudyRoom.value = room
    // 从服务器获取座位数据（使用 studyRoomID）
    const response = await resourceService.getSeats(room.studyRoomID)
    seats.value = response.data.data
    showSeatManagement.value = true
  } catch (error) {
    console.error('加载座位数据失败:', error)
    // 如果获取失败，生成默认座位数据
    showSeatManagement.value = true
  } finally {
    loading.value = false
  }
}


// 修改座位状态
const changeSeatStatus = (seatId: number) => {
  const seat = seats.value.find(s => s.seatID === seatId)
  if (seat) {
    // 循环切换状态: 可预约 -> 已预约 -> 未签到 -> 已占用 -> 暂离 -> 维修中 -> 可预约
    if (seat.seatStatus === 0) {
      seat.seatStatus = 1
    } else if (seat.seatStatus === 1) {
      seat.seatStatus = 2
    } else if (seat.seatStatus === 2) {
      seat.seatStatus = 3
    } else if (seat.seatStatus === 3) {
      seat.seatStatus = 4
    } else if (seat.seatStatus === 4) {
      seat.seatStatus = 5
    } else {
      seat.seatStatus = 0
    }
  }
}

// 关闭座位管理界面
const closeSeatManagement = () => {
  showSeatManagement.value = false
  selectedStudyRoom.value = null
  seats.value = []
}

// 保存座位更改
const saveSeatChanges = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    if (selectedStudyRoom.value) {
      // 发送座位更改到服务器
      const response = await resourceService.saveSeats(selectedStudyRoom.value.studyRoomID, seats.value)
      alert('座位状态已保存')
    }
    closeSeatManagement()
  } catch (error) {
    console.error('保存座位失败:', error)
    errorMessage.value = '保存座位失败，请稍后重试'
  } finally {
    loading.value = false
  }
}


</script>


<template>
  <div class="admin-resource-management">
    <div class="page-header">
      <h1>资源管理</h1>
      <p>管理自习室和研讨室资源</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">加载中...</div>
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
      <button @click="errorMessage = ''" class="close-error">×</button>
    </div>

    <div class="tabs">
      <button
        :class="{ active: activeTab === 'studyRoom' }"
        @click="activeTab = 'studyRoom'"
      >
        自习室管理
      </button>
      <button
        :class="{ active: activeTab === 'seminarRoom' }"
        @click="activeTab = 'seminarRoom'"
      >
        研讨室管理
      </button>
    </div>

    <div class="content-area">
      <!-- 自习室管理 -->
      <div v-if="activeTab === 'studyRoom'" class="resource-list">
        <div class="toolbar">
          <button class="add-btn" @click="openAddModal('studyRoom')">
            添加自习室
          </button>
        </div>

        <div class="resource-table">
          <div class="table-header">
            <div class="header-cell">自习室编号</div>
            <div class="header-cell">自习室位置</div>
            <div class="header-cell">座位数量</div>
            <div class="header-cell">开放时间</div>
            <div class="header-cell">状态</div>
            <div class="header-cell">操作</div>
          </div>

          <div
            v-for="room in studyRooms"
            :key="room.studyRoomID"
            class="table-row"
          >
            <div class="cell">{{ room.studyRoomID }}</div>
            <div class="cell">{{ room.studyRoomLocation}}</div>
            <div class="cell">{{ room.studyRoomCapacity }}</div>
            <div class="cell">{{ formatTimeRange(room.studyRoomOpentime, room.studyRoomClosetime) }}</div>
            <div class="cell">
              <span :class="['status-badge', room.status]">
                {{ getStatusText(room.status) }}
              </span>
            </div>
            <div class="cell actions">
              <button class="edit-btn" @click="openEditModal(room, 'studyRoom')">
                编辑
              </button>
              <button class="delete-btn" @click="deleteResource(room.studyRoomID, 'studyRoom')">
                删除
              </button>
              <button class="manage-seat-btn" @click="openSeatManagement(room)">
                座位管理
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 研讨室管理 -->
      <div v-if="activeTab === 'seminarRoom'" class="resource-list">
        <div class="toolbar">
          <button class="add-btn" @click="openAddModal('seminarRoom')">
            添加研讨室
          </button>
        </div>

        <div class="resource-table">
          <div class="table-header">
            <div class="header-cell">研讨室编号</div>
            <div class="header-cell">研讨室位置</div>
            <div class="header-cell">容纳人数</div>
            <div class="header-cell">开放时间</div>
            <div class="header-cell">状态</div>
            <div class="header-cell">操作</div>
          </div>

          <div
            v-for="room in seminarRooms"
            :key="room.seminarRoomID"
            class="table-row"
          >
            <div class="cell">{{ room.seminarRoomID }}</div>
            <div class="cell">{{ room.seminarRoomLocation }}</div>
            <div class="cell">{{ room.seminarRoomMin }}-{{ room.seminarRoomMax }}人</div>
            <div class="cell">{{ formatTimeRange(room.seminarRoomOpentime, room.seminarRoomClosetime) }}</div>
            <div class="cell">
              <span :class="['status-badge', room.seminarRoomStatus]">
                {{ getStatusText(room.seminarRoomStatus) }}
              </span>
            </div>
            <div class="cell actions">
              <button class="edit-btn" @click="openEditModal(room, 'seminarRoom')">
                编辑
              </button>
              <button class="delete-btn" @click="deleteResource(room.seminarRoomID, 'seminarRoom')">
                删除
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 自习室 编辑/添加模态框（只处理自习室字段） -->
    <div v-if="showModal && activeTab === 'studyRoom'" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ modalMode === 'add' ? '添加' : '编辑' }}自习室</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="saveResource">
            <div class="form-group">
              <label for="roomNumber">房间编号：</label>
              <input id="roomNumber" v-model="currentResource.roomNumber" type="text" required />
            </div>

            <div class="form-group">
              <label for="location">位置：</label>
              <input id="location" v-model="currentResource.location" type="text" required />
            </div>

            <div class="form-group">
              <label for="seatCount">座位数量：</label>
              <input id="seatCount" v-model.number="currentResource.seatCount" type="number" min="1" required />
            </div>

            <div class="time-group">
              <div class="form-group">
                <label for="openTime">开放时间：</label>
                <TimePicker v-model:value="currentResource.openTime" format="HH:mm" value-format="HH:mm" placeholder="请选择开放时间" class="time-picker" />
              </div>
              <div class="form-group">
                <label for="closeTime">关闭时间：</label>
                <TimePicker v-model:value="currentResource.closeTime" format="HH:mm" value-format="HH:mm" placeholder="请选择关闭时间" class="time-picker" />
              </div>
            </div>

            <div class="form-group">
              <label for="status">状态：</label>
              <select id="status" v-model.number="currentResource.status" required>
                <option :value="0">开放</option>
                <option :value="5">维护中</option>
                <option :value="4">关闭</option>
              </select>
            </div>

            <div class="modal-footer">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 研讨室 编辑/添加模态框（只处理研讨室字段） -->
    <div v-if="showModal && activeTab === 'seminarRoom'" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ modalMode === 'add' ? '添加' : '编辑' }}研讨室</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="saveResource">
            <div class="form-group">
              <label for="roomNumber">研讨室名称：</label>
              <input id="roomNumber" v-model="currentResource.roomNumber" type="text" required />
            </div>

            <div class="form-group">
              <label for="location">位置：</label>
              <input id="location" v-model="currentResource.location" type="text" required />
            </div>

            <div class="form-group">
              <label for="seminarRoomMin">最少人数：</label>
              <input id="seminarRoomMin" v-model.number="currentResource.seminarRoomMin" type="number" min="1" required />
            </div>

            <div class="form-group">
              <label for="seminarRoomMax">最多人数：</label>
              <input id="seminarRoomMax" v-model.number="currentResource.seminarRoomMax" type="number" min="1" required />
            </div>

            <div class="time-group">
              <div class="form-group">
                <label for="openTime">开放时间：</label>
                <TimePicker v-model:value="currentResource.openTime" format="HH:mm" value-format="HH:mm" placeholder="请选择开放时间" class="time-picker" />
              </div>
              <div class="form-group">
                <label for="closeTime">关闭时间：</label>
                <TimePicker v-model:value="currentResource.closeTime" format="HH:mm" value-format="HH:mm" placeholder="请选择关闭时间" class="time-picker" />
              </div>
            </div>

            <div class="form-group">
              <label for="status">状态：</label>
              <select id="status" v-model.number="currentResource.status" required>
                <option :value="0">开放</option>
                <option :value="5">维护中</option>
                <option :value="4">关闭</option>
              </select>
            </div>

            <div class="modal-footer">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>


    <!-- 座位管理模态框 -->
    <div v-if="showSeatManagement" class="modal-overlay" @click="closeSeatManagement">
      <div class="seat-modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ selectedStudyRoom?.studyRoomName || selectedStudyRoom?.studyRoomID }} 座位管理</h2>
          <button class="close-btn" @click="closeSeatManagement">&times;</button>
        </div>

        <div class="seat-management-body">
          <div class="seat-grid">
            <div
              v-for="seat in seats"
              :key="seat.seatID"
              :class="['seat-item', seat.seatStatus]"
              @click="changeSeatStatus(seat.seatID)"
            >
              <div class="seat-number">{{ seat.seatNumber }}</div>
              <div class="seat-status">{{ SEAT_STATUS_MAP[seat.seatStatus] }}</div>
            </div>
          </div>

          <div class="seat-legend">
            <div class="legend-item">
              <div class="legend-color available"></div>
              <span>可用</span>
            </div>
            <div class="legend-item">
              <div class="legend-color occupied"></div>
              <span>已占用</span>
            </div>
            <div class="legend-item">
              <div class="legend-color maintenance"></div>
              <span>维护中</span>
            </div>
            <div class="legend-item">
              <div class="legend-color closed"></div>
              <span>关闭</span>
            </div>
          </div>

          <div class="seat-info">
            <p>点击座位可切换状态：<br>
            可用 → 已占用 → 维护中 → 关闭 → 可用</p>
          </div>
        </div>

        <div class="modal-footer">
          <button class="cancel-btn" @click="closeSeatManagement">取消</button>
          <button class="save-btn" @click="saveSeatChanges">保存更改</button>
        </div>
      </div>
    </div>


    <!-- 确认删除对话框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="closeDeleteConfirm">
      <div class="confirm-modal" @click.stop>
        <div class="confirm-header">
          <h3>确认删除</h3>
        </div>
        <div class="confirm-body">
          <p>确定要删除这个{{ activeTab === 'studyRoom' ? '自习室' : '研讨室' }}吗？此操作不可撤销。</p>
        </div>
        <div class="confirm-footer">
          <button class="cancel-btn" @click="closeDeleteConfirm">取消</button>
          <button class="confirm-delete-btn" @click="confirmDelete">确定删除</button>
        </div>
      </div>
    </div>
  </div>
</template>



<style scoped>
.admin-resource-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 1rem;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #ddd;
}

.tabs button {
  padding: 12px 24px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  transition: all 0.3s ease;
}

.tabs button.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.tabs button:hover:not(.active) {
  color: #333;
  background-color: #eee;
}

.content-area {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.add-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background-color: #337ecc;
}

.resource-table {
  width: 100%;
  border-collapse: collapse;
}

.table-header {
  display: flex;
  background-color: #f5f7fa;
  font-weight: bold;
  border-bottom: 2px solid #ebeef5;
}

.header-cell {
  flex: 1;
  padding: 12px 10px;
  text-align: left;
}

.table-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s;
}

.table-row:hover {
  background-color: #f5f7fa;
}

.cell {
  flex: 1;
  padding: 12px 10px;
  display: flex;
  align-items: center;
}

.actions {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.edit-btn {
  background-color: #67c23a;
  color: white;
}

.edit-btn:hover {
  background-color: #529b2e;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
}

.delete-btn:hover {
  background-color: #c45656;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
}

.status-badge.available {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-badge.maintenance {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.status-badge.closed {
  background-color: #fef0f0;
  color: #f56c6c;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #409eff;
}


.time-picker {
  width: 100%;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.cancel-btn,
.save-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn {
  background-color: #909399;
  color: white;
}

.cancel-btn:hover {
  background-color: #606266;
}

.save-btn {
  background-color: #409eff;
  color: white;
}

.save-btn:hover {
  background-color: #337ecc;
}


/* 座位管理按钮样式 */
.manage-seat-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  background-color: #909399;
  color: white;
}

.manage-seat-btn:hover {
  background-color: #606266;
}

/* 座位管理模态框样式 */
.seat-modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.seat-management-body {
  padding: 20px;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.seat-item {
  border: 2px solid #ddd;
  border-radius: 8px;
  padding: 10px 5px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.seat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.seat-item.available {
  border-color: #67c23a;
  background-color: #f0f9eb;
}

.seat-item.occupied {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.seat-item.maintenance {
  border-color: #e6a23c;
  background-color: #fdf6ec;
}

.seat-item.closed {
  border-color: #f56c6c;
  background-color: #fef0f0;
}

.seat-number {
  font-weight: bold;
  font-size: 1.1em;
  margin-bottom: 5px;
}

.seat-status {
  font-size: 0.8em;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.legend-color.available {
  background-color: #f0f9eb;
  border: 1px solid #67c23a;
}

.legend-color.occupied {
  background-color: #ecf5ff;
  border: 1px solid #409eff;
}

.legend-color.maintenance {
  background-color: #fdf6ec;
  border: 1px solid #e6a23c;
}

.legend-color.closed {
  background-color: #fef0f0;
  border: 1px solid #f56c6c;
}

.seat-info {
  text-align: center;
  color: #666;
  font-size: 0.9em;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .seat-grid {
    grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
    gap: 10px;
  }

  .seat-item {
    padding: 8px 3px;
  }

  .seat-number {
    font-size: 1em;
  }

  .seat-status {
    font-size: 0.7em;
  }

  .seat-legend {
    gap: 10px;
  }
}


/* 删除确认对话框 */
.confirm-modal {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

.confirm-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.confirm-header h3 {
  margin: 0;
  color: #333;
}

.confirm-body {
  padding: 20px;
}

.confirm-body p {
  margin: 0;
  color: #666;
}

.confirm-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.confirm-delete-btn {
  padding: 10px 20px;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.confirm-delete-btn:hover {
  background-color: #c45656;
}

/* 加载状态样式 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner {
  background-color: white;
  padding: 30px 50px;
  border-radius: 8px;
  font-size: 18px;
  color: #333;
}

/* 错误提示样式 */
.error-message {
  background-color: #fee;
  border-left: 4px solid #f56c6c;
  padding: 15px 20px;
  margin-bottom: 20px;
  border-radius: 4px;
  color: #d32f2f;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-error {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #d32f2f;
}

@media (max-width: 768px) {
  .admin-resource-management {
    padding: 10px;
  }

  .table-header,
  .table-row {
    flex-wrap: wrap;
  }

  .header-cell,
  .cell {
    flex: 0 0 50%;
  }

  .actions {
    flex: 0 0 100%;
    justify-content: flex-start;
    padding-left: 10px;
  }

  .time-group {
    flex-direction: column;
    gap: 0;
  }
}
</style>
