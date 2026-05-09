<template>
  <div class="admin-feedback-page">
    <a-card title="反馈管理" :bordered="false" class="feedback-card">
      <template #extra>
        <a-space>
          <a-button @click="loadFeedbacks">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
        </a-space>
      </template>

      <div class="filter-section">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-select v-model:value="searchForm.status" placeholder="按状态查询" allow-clear style="width: 100%">
              <a-select-option :value="-1">全部</a-select-option>
              <a-select-option :value="1">待处理</a-select-option>
              <a-select-option :value="2">处理中</a-select-option>
              <a-select-option :value="3">已回复</a-select-option>
              <a-select-option :value="4">已关闭</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="16">
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

      <a-table :dataSource="feedbacks" :columns="columns" :pagination="pagination" :loading="loading" row-key="feedbackID" class="feedback-table" @change="handleTableChange" :row-selection="rowSelection">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'processStatus'">
            <a-tag :color="record.processStatus === 3 ? 'green' : 'orange'">{{ record.processStatus === 3 ? '已回复' : (record.processStatus === 4 ? '已处理' : '未处理') }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'feedbackTime'">
            <span v-if="record.feedbackTime">{{ formatDate(record.feedbackTime) }}</span>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="primary" size="small" :disabled="record.processStatus === 3 || record.processStatus === 4" @click="openReplyModal(record)">回复</a-button>
              <a-popconfirm title="确定要删除这条反馈吗？" ok-text="确定" cancel-text="取消" @confirm="deleteRecord(record.feedbackID)">
                <a-button type="text" danger size="small" :disabled="record.processStatus === 3 || record.processStatus === 4">删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="showReply" title="回复反馈" :confirm-loading="replyLoading" @ok="submitReply" @cancel="closeReplyModal" width="600px">
        <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
          <a-descriptions-item label="反馈ID">{{ replyTarget?.feedbackID }}</a-descriptions-item>
          <a-descriptions-item label="学生学号">{{ replyTarget?.studentID }}</a-descriptions-item>
          <a-descriptions-item label="内容">{{ replyTarget?.feedbackContent }}</a-descriptions-item>
          <a-descriptions-item label="提交时间">{{ replyTarget?.feedbackTime ? formatDate(replyTarget.feedbackTime) : '-' }}</a-descriptions-item>
        </a-descriptions>

      <a-form layout="vertical">
        <a-form-item label="回复内容" required>
          <a-textarea v-model:value="replyForm.reply" placeholder="请输入回复内容" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { ReloadOutlined, SearchOutlined, UndoOutlined } from '@ant-design/icons-vue'
import axios from 'axios'
import dayjs from 'dayjs'

interface FeedbackRecord {
  feedbackID: string
  studentID: number | null
  processSdminID?: number | null
  feedbackType?: number // 反馈类型：1-违规举报，2-建议，3-投诉，4-设备报修
  feedbackContent?: string // 反馈的具体内容
  processStatus?: number // 处理状态：1-待处理，2-处理中，3-已回复，4-已关闭
  feedbackTime?: string // 反馈提交时间
  replyContent?: string // 管理员回复内容
  replyTime?: string // 管理员回复时间
  contactInfo?: string // 联系方式
  relatedResourceID?: string // 关联资源ID（自习室、座位、研讨室等）
  priority?: number // 优先级：1-高，2-中，3-低
}

interface SearchParams {
  studentId?: number
  status?: number
}

const baseUrl = 'http://localhost:3000/api'
const feedbacks = ref<FeedbackRecord[]>([])
const loading = ref(false)
const replyLoading = ref(false)
const showReply = ref(false)
const replyTarget = ref<FeedbackRecord | null>(null)
const replyForm = ref({ reply: '' })

const searchForm = ref<SearchParams>({ studentId: undefined, status: undefined })

const pagination = ref({ current: 1, pageSize: 10, total: 0, showSizeChanger: true, showQuickJumper: true, showTotal: (total: number) => `共 ${total} 条记录` })

const columns = [
  { title: '反馈ID', dataIndex: 'feedbackID', width: 160, align: 'center' },
  { title: '学生学号', dataIndex: 'studentID', width: 120, align: 'center' },
  { title: '内容', dataIndex: 'feedbackContent' },
  { title: '提交时间', dataIndex: 'feedbackTime', width: 140, align: 'center' },
  { title: '状态', dataIndex: 'processStatus', width: 90, align: 'center' },
  { title: '操作', dataIndex: 'action', width: 180, align: 'center' }
]

const selectedRowKeys = ref<string[]>([])
const rowSelection = ref({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (selectedKeys: string[]) => { selectedRowKeys.value = selectedKeys },
})

function formatDate(dateString?: string){ if (!dateString) return '-'; return dayjs(dateString).format('YYYY-MM-DD HH:mm') }

// 专门用于按状态查询反馈的方法
async function loadFeedbacksByStatus(status: number) {
  loading.value = true
  try {
    const statusUrl = `${baseUrl}/feedback/status?processStatus=${status}`
    const res = await axios.get(statusUrl)
    const data = res?.data?.data
    if (data && Array.isArray(data.feedbacks)) {
      feedbacks.value = data.feedbacks
      pagination.value.total = data.total || data.feedbacks.length
    } else {
      feedbacks.value = []
      pagination.value.total = 0
    }
  } catch (e) {
    console.error(e)
    message.error('获取反馈列表失败')
    feedbacks.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}

async function loadFeedbacks(){
  loading.value = true
  try{
    const params: SearchParams = {}
    if (searchForm.value.studentId) params.studentId = Number(searchForm.value.studentId)
    
    if (searchForm.value.status !== undefined && searchForm.value.status !== -1) {
      await loadFeedbacksByStatus(searchForm.value.status)
    } else {
      const url = `${baseUrl}/feedback/all` 
      const res = await axios.get(url, { params: Object.keys(params).length ? params : undefined })
      const data = res?.data?.data
      if (data && Array.isArray(data.feedbacks)){
        feedbacks.value = data.feedbacks
        pagination.value.total = data.total || data.feedbacks.length
      } else if (Array.isArray(res?.data)){
        feedbacks.value = res.data
        pagination.value.total = feedbacks.value.length
      } else {
        feedbacks.value = []
        pagination.value.total = 0
      }
    }
  }catch(e){ console.error(e); message.error('获取反馈列表失败') }finally{ loading.value = false }
}


// 处理表格分页变化
interface TablePagination {
  current?: number
  pageSize?: number
}

function handleTableChange(pager: TablePagination){ 
  if(pager.current) pagination.value.current = pager.current
  if(pager.pageSize) pagination.value.pageSize = pager.pageSize 
}

// 处理搜索
function handleSearch() {
  pagination.value.current = 1; // 重置到第一页
  loadFeedbacks();
}

// 重置搜索
function resetSearch() {
  searchForm.value.status = undefined;
  pagination.value.current = 1; // 重置到第一页
  loadFeedbacks();
}

// 打开回复模态框
function openReplyModal(record: FeedbackRecord){ 
  replyTarget.value = record; 
  replyForm.value = { reply: record.replyContent || '' }; 
  showReply.value = true 
}
// 关闭回复模态框
function closeReplyModal(){ showReply.value = false; replyTarget.value = null }

// 提交回复
async function submitReply(){
  if (!replyTarget.value) return
  if (!replyForm.value.reply) { message.warning('请输入回复内容'); return }
  replyLoading.value = true
  try{
    const payload: Partial<FeedbackRecord> = {
      feedbackID: replyTarget.value.feedbackID,
      replyContent: replyForm.value.reply,
      replyTime: new Date().toISOString(),
      processStatus: 3,
    }
    await axios.put(`${baseUrl}/feedback/update`, payload)
    message.success('回复已发送')
    closeReplyModal()
    loadFeedbacks()
  }catch(e){ console.error(e); message.error('回复失败') }finally{ replyLoading.value = false }
}

async function deleteRecord(id: string) {
  try {
    console.log('准备更新反馈状态:', id);
    const response = await axios.put(`${baseUrl}/feedback/status`, null, { 
      params: { feedbackId: id, newStatus: 4 } 
    });
    console.log('API 响应:', response);
    message.success('已删除该反馈记录');
    loadFeedbacks();
  } catch (error: unknown) {
    console.error('删除操作失败:', error);
    let errorMessage = '操作失败';
    if (error instanceof Error) {
      errorMessage += '：' + error.message;
    } else if (typeof error === 'object' && error !== null) {
      // 检查错误对象是否包含预期的结构
      const errObj = error as { response?: { data?: { message?: string } } };
      errorMessage += '：' + (errObj.response?.data?.message || '网络错误');
    } else {
      errorMessage += '：' + (error as {message?: string}).message || '网络错误';
    }
    message.error(errorMessage);
  }
}



onMounted(()=>{ loadFeedbacks() })
</script>

<style scoped>
.admin-feedback-page{padding:20px;background:#f0f2f5;min-height:calc(100vh - 64px)}
.feedback-card{border-radius:8px}
.filter-section{margin-bottom:20px;padding:16px;background:#fafafa;border-radius:6px}
:deep(.ant-table-thead > tr > th){background:#fafafa;font-weight:600}
</style>
