<template>
  <div class="admin-violation-page">
    <a-card title="违规与举报管理" :bordered="false" class="violation-card">
      <template #extra>
        <a-space>
          <a-button type="primary" @click="openAddModal">
            <template #icon><PlusOutlined /></template>
            新增违规
          </a-button>
          <a-button @click="loadReports">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
        </a-space>
      </template>

      <!-- 查询条件 -->
      <div class="filter-section">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-input 
              v-model:value="searchForm.studentId" 
              placeholder="学生学号" 
              allow-clear
            />
          </a-col>
          <a-col :span="6">
            <a-select 
              v-model:value="searchForm.violationType" 
              placeholder="违规类型" 
              allow-clear
              style="width: 100%"
            >
              <a-select-option :value="1">违规占座</a-select-option>
              <a-select-option :value="2">签到超时</a-select-option>
              <a-select-option :value="3">暂离超时</a-select-option>
              <a-select-option :value="4">研讨室人数不足</a-select-option>
              <a-select-option :value="5">未签退</a-select-option>
              <a-select-option :value="6">早退</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="6">
            <a-select 
              v-model:value="searchForm.status" 
              placeholder="处理状态" 
              allow-clear
              style="width: 100%"
            >
              <a-select-option :value="1">已生成</a-select-option>
              <a-select-option :value="2">待申诉</a-select-option>
              <a-select-option :value="3">已确认</a-select-option>
              <a-select-option :value="4">申诉中</a-select-option>
              <a-select-option :value="5">已撤销</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="6">
            <a-space>
              <a-button type="primary" @click="handleSearch">
                <template #icon><SearchOutlined /></template>
                查询
              </a-button>
              <a-button @click="resetSearch">
                <template #icon><UndoOutlined /></template>
                重置
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </div>

      <!-- 批量操作工具栏 -->
      <div class="batch-actions" style="margin-bottom: 16px;">
        <a-space>
          <a-button 
            type="primary" 
            danger
            :disabled="selectedRowKeys.length === 0"
            @click="batchConfirm"
          >
            批量确认
          </a-button>
          <a-button 
            type="primary" 
            danger
            :disabled="selectedRowKeys.length === 0"
            @click="batchDelete"
          >
            批量删除
          </a-button>
          <span v-if="selectedRowKeys.length > 0" style="margin-left: 16px; color: #1890ff;">
            已选择 {{ selectedRowKeys.length }} 项
          </span>
        </a-space>
      </div>

      <!-- 数据表格 -->
      <a-table 
        :dataSource="reports" 
        :columns="columns" 
        :pagination="pagination"
        :loading="loading"
        row-key="violationRecordID"
        class="violation-table"
        @change="handleTableChange"
        :row-selection="rowSelection"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'violationType'">
            <a-tag :color="getViolationTypeColor(record.violationType)">
              {{ getViolationTypeName(record.violationType) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusName(record.status) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.dataIndex === 'deductPoints'">
            <span class="points-text">
              -{{ record.deductPoints }} 分
            </span>
          </template>
          
          <template v-else-if="column.dataIndex === 'violationTime'">
            <span v-if="record.violationTime">
              {{ formatDate(record.violationTime) }}
            </span>
            <span v-else>-</span>
          </template>
          
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button 
                v-if="record.status === 4"
                type="primary" 
                size="small"
                @click="openProcessModal(record)"
              >
                <template #icon><EditOutlined /></template>
                处理
              </a-button>
              <a-button 
                v-else 
                type="link" 
                size="small"
                disabled
              >
                {{ getStatusName(record.status) }}
              </a-button>
              
              <a-popconfirm
                title="确定要删除这条记录吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="deleteRecord(record.violationRecordID)"
              >
                <a-button type="text" danger size="small">
                  <template #icon><DeleteOutlined /></template>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增违规模态框 -->
    <a-modal
      v-model:open="showAdd"
      title="新增违规记录"
      :confirm-loading="addLoading"
      @ok="submitAdd"
      @cancel="closeAddModal"
      width="500px"
    >
      <a-form :model="ViolationForm" layout="vertical">
        <a-form-item label="学生学号" required>
          <a-input v-model:value="ViolationForm.studentID" placeholder="请输入学生学号" />
        </a-form-item>
        
        <a-form-item label="违规类型" required>
          <a-select v-model:value="ViolationForm.violationType" placeholder="请选择违规类型">
            <a-select-option :value="1">违规占座</a-select-option>
            <a-select-option :value="2">签到超时</a-select-option>
            <a-select-option :value="3">暂离超时</a-select-option>
            <a-select-option :value="4">研讨室人数不足</a-select-option>
            <a-select-option :value="5">未签退</a-select-option>
            <a-select-option :value="6">早退</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="扣分值" required>
          <a-input-number 
            v-model:value="ViolationForm.deductPoints" 
            :min="1" 
            :max="20" 
            placeholder="请输入扣分值"
            style="width: 100%"
          />
        </a-form-item>
        
        <a-form-item label="违规说明">
          <a-textarea 
            v-model:value="ViolationForm.details" 
            placeholder="请输入违规说明" 
            :rows="4"
          />
        </a-form-item>
        
        <a-form-item label="关联预约记录ID">
          <a-input v-model:value="ViolationForm.reservationRecordID" placeholder="请输入预约记录ID" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 处理模态框 -->
    <a-modal
      v-model:open="showProcess"
      title="处理违规记录"
      :confirm-loading="processLoading"
      @ok="submitProcess"
      @cancel="closeProcessModal"
      width="600px"
    >
      <a-descriptions 
        :column="1" 
        bordered 
        size="small"
        style="margin-bottom: 16px"
      >
        <a-descriptions-item label="记录ID">
          {{ processTarget?.violationRecordID }}
        </a-descriptions-item>
        <a-descriptions-item label="学生学号">
          {{ processTarget?.studentID }}
        </a-descriptions-item>
        <a-descriptions-item label="违规类型">
          {{ getViolationTypeName(processTarget?.violationType) }}
        </a-descriptions-item>
        <a-descriptions-item label="扣分">
          <span class="points-text">-{{ processTarget?.deductPoints }} 分</span>
        </a-descriptions-item>
        <a-descriptions-item label="违规说明">
          {{ processTarget?.details || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="违规时间">
          {{ processTarget?.violationTime ? formatDate(processTarget.violationTime) : '-' }}
        </a-descriptions-item>
      </a-descriptions>
      
      <a-form layout="vertical">
        <a-form-item label="处理状态" required>
          <a-select v-model:value="processForm.status" placeholder="请选择处理状态">
            <a-select-option :value="3">已确认</a-select-option>
            <a-select-option :value="2">待申诉</a-select-option>
            <a-select-option :value="5">已撤销</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="处理说明">
          <a-textarea 
            v-model:value="processForm.remark" 
            placeholder="请输入处理说明" 
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  PlusOutlined, 
  ReloadOutlined, 
  SearchOutlined, 
  UndoOutlined, 
  EditOutlined, 
  DeleteOutlined 
} from '@ant-design/icons-vue'
import { violationService } from '@/api/resourceService'
import dayjs from 'dayjs'

// 违规记录接口定义
interface ViolationRecord {
    violationRecordID: string
    studentID: number | null
    adminID?: number
    violationType: number // 违规类型：1-违规占座，2-签到超时，3-暂离超时，4-研讨室人数不足，5-未签退，6-早退
    details?: string // 违规的具体内容
    deductPoints: number // 扣除的信用积分
    violationTime?: string // 违规产生的时间
    status: number // 处理状态：1-已生成，2-待申诉，3-已确认，4-申诉中，5-已撤销
    reservationRecordID?: string
    attendanceRecordID?: number
    appealReason?: string // 申诉理由
    appealTime: string // 申诉时间
    processedTime?: string // 处理时间
    createTime?: string // 记录创建时间
}

// 数据相关
const reports = ref<ViolationRecord[]>([])
const loading = ref(false)
const addLoading = ref(false)
const processLoading = ref(false)

// 模态框状态
const showAdd = ref(false)
const showProcess = ref(false)

// 表格选择状态
const selectedRowKeys = ref<string[]>([])

// 表格选择配置
const rowSelection = ref({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (selectedKeys: string[]) => {
    selectedRowKeys.value = selectedKeys
  },
  getCheckboxProps: (record: ViolationRecord) => ({
    disabled: ![1, 2].includes(record.status), // 只能选择状态为1或2的记录
    name: record.violationRecordID,
  }),
})

// 表单数据
const ViolationForm = ref({ 
  studentID: 0,
  violationType: 1,
  deductPoints: 1,
  details: '',
  reservationRecordID: ''
})

const processTarget = ref<ViolationRecord | null>(null)
const processForm = ref({
  status: 3,
  remark: ''
})

// 查询表单
const searchForm = ref({
  studentId: '',
  violationType: undefined as number | undefined,
  status: undefined as number | undefined
})

// 表格配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const columns = [
  {
    title: '记录ID',
    dataIndex: 'violationRecordID',
    width: 250,
    align: 'center'
  },
  {
    title: '学生学号',
    dataIndex: 'studentID',
    width: 180,
    align: 'center'
  },
  {
    title: '违规类型',
    dataIndex: 'violationType',
    width: 150,
    align: 'center'
  },
  {
    title: '扣分',
    dataIndex: 'deductPoints',
    width: 120,
    align: 'center'
  },
  {
    title: '违规说明',
    dataIndex: 'details',
    ellipsis: true
  },
  {
    title: '违规时间',
    dataIndex: 'violationTime',
    width: 200,
    align: 'center'
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
    align: 'center'
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 250,
    align: 'center'
  }
]

// 获取数据
async function loadReports() {
  loading.value = true
  try {
    // 使用查询接口，传入过滤参数
    const filters = {
      studentId: searchForm.value.studentId ? Number(searchForm.value.studentId) : undefined,
      violationType: searchForm.value.violationType,
      status: searchForm.value.status
    }
    
    const res = await violationService.satisfiesionRecords(filters)
    if (res.data && res.data.data && res.data.data.violations) {
      reports.value = res.data.data.violations || []
      pagination.value.total = res.data.data.total || 0
    } else {
      reports.value = []
      pagination.value.total = 0
    }
  } catch (err) {
    console.error(err)
    message.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 查询处理
function handleSearch() {
  loadReports()
}

function resetSearch() {
  searchForm.value = {
    studentId: '',
    violationType: undefined,
    status: undefined
  }
  loadReports()
}

// 表格分页变化
function handleTableChange(pager: { current: number; pageSize: number }) {
  pagination.value.current = pager.current
  pagination.value.pageSize = pager.pageSize
}

// 违规类型名称映射
function getViolationTypeName(type: number | undefined): string {
  const typeMap: Record<number, string> = {
    1: '违规占座',
    2: '签到超时',
    3: '暂离超时',
    4: '研讨室人数不足',
    5: '未签退',
    6: '早退'
  }
  return type ? typeMap[type] || '未知类型' : '未知类型'
}

// 违规类型颜色映射
function getViolationTypeColor(type: number | undefined): string {
  const colorMap: Record<number, string> = {
    1: 'red',
    2: 'orange',
    3: 'gold',
    4: 'purple',
    5: 'cyan',
    6: 'magenta'
  }
  return type ? colorMap[type] || 'blue' : 'blue'
}

// 状态名称映射
function getStatusName(status: number | undefined): string {
  const statusMap: Record<number, string> = {
    1: '已生成',
    2: '待申诉',
    3: '已确认',
    4: '申诉中',
    5: '已撤销'
  }
  return status ? statusMap[status] || '未知状态' : '未知状态'
}

// 状态颜色映射
function getStatusColor(status: number | undefined): string {
  const colorMap: Record<number, string> = {
    1: 'blue',
    2: 'orange',
    3: 'green',
    4: 'purple',
    5: 'gray'
  }
  return status ? colorMap[status] || 'default' : 'default'
}

// 格式化日期
function formatDate(dateString: string | undefined): string {
  if (!dateString) return '-'
  return dayjs(dateString).format('YYYY-MM-DD HH:mm')
}

// 批量确认功能
async function batchConfirm() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择要确认的违规记录')
    return
  }

  try {
    // 调用批量确认API
    const response = await violationService.batchConfirmViolations(selectedRowKeys.value)
    
    if (response.data.success) {
      message.success(`批量确认完成：成功${response.data.successCount}条，失败${response.data.failedCount}条`)
      // 重新加载数据
      loadReports()
      // 清空选择
      selectedRowKeys.value = []
    } else {
      message.error(response.data.message || '批量确认失败')
    }
  } catch (err) {
    console.error('批量确认失败:', err)
    message.error('批量确认失败，请重试')
  }
}

// 批量删除功能
async function batchDelete() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择要删除的违规记录')
    return
  }

    try {
      // 逐个删除选中的记录，按后端统一响应判断
      let successCount = 0
      let failedCount = 0

      for (const recordId of selectedRowKeys.value) {
        try {
          const res = await violationService.deleteViolation(recordId)
          if (res && res.data && res.data.code === 200) {
            successCount++
          } else {
            failedCount++
            console.error(`删除记录 ${recordId} 后端返回错误:`, res?.data?.message)
          }
        } catch (err) {
          console.error(`删除记录 ${recordId} 失败:`, err)
          failedCount++
        }
      }

      message.success(`批量删除完成：成功${successCount}条，失败${failedCount}条`)
      // 重新加载数据
      loadReports()
      // 清空选择
      selectedRowKeys.value = []
    } catch (err) {
      console.error('批量删除失败:', err)
      message.error('批量删除失败，请重试')
    }
}

// 模态框操作
function openAddModal() { 
  ViolationForm.value = { 
    studentID: 0,
    violationType: 1,
    deductPoints: 1,
    details: '',
    reservationRecordID: ''
  }
  showAdd.value = true 
}

function closeAddModal() { 
  showAdd.value = false 
}

// 新增违规
async function submitAdd() {
  if (!ViolationForm.value.studentID) { 
    message.warning('请输入学号')
    return 
  }
  if (!ViolationForm.value.deductPoints || ViolationForm.value.deductPoints <= 0) { 
    message.warning('扣分值需为正整数')
    return 
  }

  addLoading.value = true
  try {
    // 记录违规日志 — 发送完整的 ViolationRecord 对象
    const payload = {
      violationRecordID: '',
      studentID: ViolationForm.value.studentID,
      adminID: undefined,
      violationType: ViolationForm.value.violationType,
      details: ViolationForm.value.details,
      deductPoints: ViolationForm.value.deductPoints,
      violationTime: undefined,
      status: 1,
      reservationRecordID: ViolationForm.value.reservationRecordID || '',
      attendanceRecordID: undefined,
      appealReason: undefined,
      appealTime: '',
      processedTime: undefined,
      createTime: undefined
    }

    await violationService.addViolationRecord(payload)

    // 可选：通知学生
    try { 
      await violationService.notifyStudent(
        ViolationForm.value.studentID.toString(), 
        `您的账户因"${getViolationTypeName(ViolationForm.value.violationType)}"被扣除 ${ViolationForm.value.deductPoints} 分，原因：${ViolationForm.value.details || '管理员记录'}`
      ) 
    } catch { 
      console.log('通知发送失败，不影响主要流程') 
    }

    message.success('新增违规并扣分成功')
    closeAddModal()
    loadReports()
  } catch (err) {
    console.error(err)
  } finally {
    addLoading.value = false
  }
}

// 打开处理
function openProcessModal(record: ViolationRecord) { 
  processTarget.value = record
  processForm.value = {
    status: 3,
    remark: ''
  }
  showProcess.value = true 
}

// 关闭处理模态框
function closeProcessModal() { 
  showProcess.value = false
  processTarget.value = null
}

// 提交处理
async function submitProcess() {
  if (!processTarget.value) return
  
  processLoading.value = true
  try {
    // 调用API更新违规记录状态
    const updatedRecord = {
      ...processTarget.value,
      status: processForm.value.status,
      processedTime: new Date().toISOString()
    }
    await violationService.addViolationRecord(updatedRecord)

    message.success('处理成功')
    closeProcessModal()
    loadReports()
  } catch (err) {
    console.error(err)
    message.error('处理失败')
  } finally {
    processLoading.value = false
  }
}

// 删除记录
async function deleteRecord(id: string) {
  try {
    const res = await violationService.deleteViolation(id)
    if (res && res.data) {
      if (res.data.code === 200) {
        message.success(res.data.message || '删除成功')
        loadReports()
      } else {
        message.error(res.data.message || '删除失败')
      }
    } else {
      message.error('删除失败')
    }
  } catch (err) {
    console.error(err)
    message.error('删除失败')
  }
}

onMounted(() => { 
  loadReports() 
})
</script>

<style scoped>
.admin-violation-page {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.violation-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.batch-actions {
  margin-bottom: 16px;
}

.violation-table {
  margin-top: 16px;
}

.points-text {
  color: #ff4d4f;
  font-weight: 500;
}

:deep(.ant-table-thead > tr > th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr:hover) {
  background-color: #f0f8ff;
}
</style>