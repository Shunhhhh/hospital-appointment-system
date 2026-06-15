<template>
  <div class="admin-dashboard">
    <header class="admin-header">
      <div>
        <p class="eyebrow">Hospital Admin</p>
        <h1>医院管理后台</h1>
        <p class="subtitle">科室、医生排班与挂号记录统一管理</p>
      </div>

      <el-dropdown trigger="click">
        <span class="admin-user">
          <el-avatar :size="36" class="avatar">{{ adminName.charAt(0) }}</el-avatar>
          <span>{{ adminName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </header>

    <section class="overview-grid">
      <el-card v-for="item in overviewCards" :key="item.title" shadow="never" class="overview-card">
        <div class="card-title">{{ item.title }}</div>
        <div class="card-value">{{ item.value }}</div>
        <div class="card-desc">{{ item.desc }}</div>
      </el-card>
    </section>

    <el-tabs v-model="activeTab" class="admin-tabs">
      <el-tab-pane label="科室管理" name="department">
        <div class="pane-toolbar">
          <el-button type="primary" @click="openDepartmentDialog()">新增科室</el-button>
          <el-button @click="loadDepartments">刷新</el-button>
        </div>

        <el-table :data="departments" border stripe v-loading="departmentLoading" style="width: 100%">
          <el-table-column prop="departmentID" label="ID" width="90" />
          <el-table-column prop="departmentName" label="科室名称" min-width="150" />
          <el-table-column prop="departmentType" label="类型" width="90" />
          <el-table-column prop="departmentLocation" label="位置" min-width="180" />
          <el-table-column prop="departmentStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.departmentStatus === 1 ? 'success' : 'info'">
                {{ scope.row.departmentStatus === 1 ? '正常' : '停诊' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button text type="primary" @click="openDepartmentDialog(scope.row)">编辑</el-button>
              <el-button text type="danger" @click="removeDepartment(scope.row.departmentID)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="医生管理" name="doctor">
        <div class="pane-toolbar">
          <el-button type="primary" @click="openDoctorDialog()">新增医生</el-button>
          <el-button @click="loadDoctors">刷新医生</el-button>
        </div>

        <el-table :data="doctors" border stripe v-loading="doctorLoading" style="width: 100%; margin-bottom: 16px;">
          <el-table-column prop="doctorID" label="医生ID" width="100" />
          <el-table-column prop="doctorName" label="姓名" min-width="120" />
          <el-table-column prop="doctorPhone" label="手机号" min-width="140" />
          <el-table-column prop="title" label="职称" min-width="120" />
          <el-table-column prop="departmentName" label="科室" min-width="140" />
          <el-table-column prop="doctorStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.doctorStatus === 1 ? 'success' : 'info'">
                {{ scope.row.doctorStatus === 1 ? '在职' : '停诊' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <div class="action-row">
                <el-button text type="primary" @click="openDoctorDialog(scope.row)">编辑</el-button>
                <el-button text type="warning" @click="openScheduleDialog(scope.row)">排班</el-button>
                <el-button text type="danger" @click="removeDoctor(scope.row.doctorID)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

      </el-tab-pane>

      <el-tab-pane label="排班管理" name="schedule">
        <div class="pane-toolbar">
          <el-date-picker v-model="scheduleDateFilter" type="date" placeholder="选择日期" clearable style="width: 160px" value-format="YYYY-MM-DD" />
          <el-input v-model="scheduleSearch" placeholder="搜索医生或科室" clearable style="width: 220px" />
          <el-button type="primary" @click="loadSchedules">刷新排班</el-button>
          <el-button @click="openScheduleDialog()">新增排班</el-button>
        </div>

        <el-table :data="pagedSchedules" border stripe v-loading="scheduleLoading" style="width: 100%">
          <el-table-column prop="scheduleDate" label="日期" width="120" sortable />
          <el-table-column prop="timeSlot" label="时段" width="90">
            <template #default="scope">
              {{ scope.row.timeSlot === 1 ? '上午' : scope.row.timeSlot === 2 ? '下午' : '夜诊' }}
            </template>
          </el-table-column>
          <el-table-column prop="doctorName" label="医生" min-width="100" />
          <el-table-column prop="departmentName" label="科室" min-width="100" />
          <el-table-column prop="remainingSlots" label="剩余/总数" width="110">
            <template #default="scope">
              <span :class="{ 'text-danger': scope.row.remainingSlots === 0 }">
                {{ scope.row.remainingSlots }}/{{ scope.row.totalSlots }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="挂号费" width="90" />
          <el-table-column prop="scheduleStatus" label="状态" width="90">
            <template #default="scope">
              <el-tag :type="scheduleStatusTagType(scope.row.scheduleStatus)">
                {{ scheduleStatusText(scope.row.scheduleStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <div class="action-row">
                <el-button text type="primary" @click="openScheduleDialog(scope.row)">编辑</el-button>
                <el-button text type="warning" @click="stopSchedule(scope.row.scheduleID)">停诊</el-button>
                <el-button text type="danger" @click="removeSchedule(scope.row.scheduleID)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="scheduleCurrentPage"
            v-model:page-size="schedulePageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="filteredSchedules.length"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="挂号管理" name="appointment">
        <div class="pane-toolbar">
          <el-button type="primary" @click="loadAppointments">刷新挂号</el-button>
        </div>

        <el-table :data="appointments" border stripe v-loading="appointmentLoading" style="width: 100%">
          <el-table-column prop="appointmentID" label="挂号ID" min-width="180" />
          <el-table-column prop="patientName" label="患者" min-width="120" />
          <el-table-column prop="doctorName" label="医生" min-width="120" />
          <el-table-column prop="departmentName" label="科室" min-width="120" />
          <el-table-column prop="appointmentDate" label="日期" width="120" />
          <el-table-column prop="appointmentNumber" label="序号" width="80" />
          <el-table-column prop="appointmentStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="appointmentTagType(scope.row.appointmentStatus)">
                {{ appointmentStatusText(scope.row.appointmentStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="paymentAmount" label="金额" width="100" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="反馈管理" name="feedback">
        <div class="pane-toolbar">
          <el-select v-model="feedbackStatusFilter" placeholder="按状态筛选" clearable style="width: 160px">
            <el-option label="全部" :value="-1" />
            <el-option label="待处理" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已回复" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
          <el-button type="primary" @click="loadFeedbacks">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>

        <el-table :data="filteredFeedbacks" border stripe v-loading="feedbackLoading" style="width: 100%">
          <el-table-column prop="feedbackID" label="反馈编号" width="160" />
          <el-table-column label="患者ID" width="100">
            <template #default="scope">
              {{ scope.row.studentID || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="反馈类型" width="100">
            <template #default="scope">
              <el-tag :type="feedbackTagType(scope.row.feedbackType)" size="small">
                {{ feedbackTypeText(scope.row.feedbackType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="feedbackContent" label="反馈内容" min-width="220" show-overflow-tooltip />
          <el-table-column label="优先级" width="90">
            <template #default="scope">
              <el-tag :type="priorityTagType(scope.row.priority)" size="small">
                {{ priorityText(scope.row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="feedbackStatusTagType(scope.row.processStatus)">
                {{ feedbackStatusText(scope.row.processStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" width="160">
            <template #default="scope">
              {{ formatFeedbackTime(scope.row.feedbackTime) }}
            </template>
          </el-table-column>
          <el-table-column label="回复内容" min-width="180" show-overflow-tooltip>
            <template #default="scope">
              <span v-if="scope.row.replyContent" class="reply-preview">{{ scope.row.replyContent }}</span>
              <span v-else class="text-muted">暂未回复</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <div class="action-row">
                <el-button text type="primary" size="small" @click="openFeedbackReplyDialog(scope.row)">
                  回复
                </el-button>
                <el-button
                  v-if="scope.row.processStatus !== 4"
                  text
                  type="warning"
                  size="small"
                  @click="closeFeedback(scope.row.feedbackID)"
                >
                  关闭
                </el-button>
                <el-button
                  v-if="scope.row.processStatus === 4"
                  text
                  type="success"
                  size="small"
                  @click="reopenFeedback(scope.row.feedbackID)"
                >
                  重开
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="feedbackReplyVisible" title="回复反馈" width="600px" :close-on-click-modal="false">
      <el-descriptions :column="1" border size="small" style="margin-bottom: 18px">
        <el-descriptions-item label="反馈编号">{{ feedbackReplyForm.feedbackID }}</el-descriptions-item>
        <el-descriptions-item label="患者ID">{{ feedbackReplyForm.studentID || '-' }}</el-descriptions-item>
        <el-descriptions-item label="反馈类型">{{ feedbackTypeText(feedbackReplyForm.feedbackType) }}</el-descriptions-item>
        <el-descriptions-item label="反馈内容">
          <div style="white-space: pre-wrap; max-height: 120px; overflow-y: auto;">{{ feedbackReplyForm.feedbackContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatFeedbackTime(feedbackReplyForm.feedbackTime) }}</el-descriptions-item>
        <el-descriptions-item label="已有回复">
          <span v-if="feedbackReplyForm.existingReply">{{ feedbackReplyForm.existingReply }}</span>
          <span v-else class="text-muted">暂无</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-form label-width="80px">
        <el-form-item label="回复内容" required>
          <el-input
            v-model="feedbackReplyForm.reply"
            type="textarea"
            :rows="5"
            placeholder="请输入回复内容..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackReplyVisible = false">取消</el-button>
        <el-button type="primary" :loading="feedbackReplyLoading" @click="submitFeedbackReply">
          提交回复
        </el-button>
      </template>
    </el-dialog>
  </div>

    <el-dialog v-model="departmentDialogVisible" :title="departmentForm.departmentID ? '编辑科室' : '新增科室'" width="520px">
      <el-form :model="departmentForm" label-width="100px">
        <el-form-item label="科室名称"><el-input v-model="departmentForm.departmentName" /></el-form-item>
        <el-form-item label="科室类型">
          <el-select v-model="departmentForm.departmentType" filterable placeholder="请选择科室">
            <el-option v-for="dept in departments" :key="dept.departmentID" :label="dept.departmentName" :value="dept.departmentName" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室位置"><el-input v-model="departmentForm.departmentLocation" /></el-form-item>
        <el-form-item label="科室简介"><el-input v-model="departmentForm.departmentDesc" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="departmentStatusSwitch" active-text="正常" inactive-text="停诊" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="departmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDepartment">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="doctorDialogVisible" :title="doctorForm.doctorID ? '编辑医生' : '新增医生'" width="640px">
      <el-form :model="doctorForm" label-width="100px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="doctorForm.doctorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="doctorForm.doctorPhone" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="密码"><el-input v-model="doctorForm.doctorPassword" show-password /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="doctorForm.doctorGender" placeholder="请选择"><el-option label="男" :value="1" /><el-option label="女" :value="2" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="科室">
              <el-select v-model="doctorForm.departmentID" filterable placeholder="请选择科室">
                <el-option v-for="dept in departments" :key="dept.departmentID" :label="dept.departmentName" :value="dept.departmentID" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="职称"><el-input v-model="doctorForm.title" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="挂号费"><el-input-number v-model="doctorForm.registrationFee" :min="0" :step="1" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-switch v-model="doctorStatusSwitch" active-text="在职" inactive-text="停诊" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="doctorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDoctor">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="scheduleDialogVisible" :title="scheduleForm.scheduleID ? '编辑排班' : '新增排班'" width="620px">
      <el-form :model="scheduleForm" label-width="100px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="日期"><el-date-picker v-model="scheduleForm.scheduleDate" type="date" value-format="YYYY-MM-DD" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="时段"><el-select v-model="scheduleForm.timeSlot"><el-option label="上午" :value="1" /><el-option label="下午" :value="2" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="scheduleForm.scheduleStatus"><el-option label="可预约" :value="1" /><el-option label="不可用" :value="0" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="总号源"><el-input-number v-model="scheduleForm.totalSlots" :min="1" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="剩余号源"><el-input-number v-model="scheduleForm.remainingSlots" :min="0" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="开始时间"><el-time-picker v-model="scheduleForm.startTime" value-format="HH:mm:ss" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束时间"><el-time-picker v-model="scheduleForm.endTime" value-format="HH:mm:ss" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="挂号费"><el-input-number v-model="scheduleForm.price" :min="0" :step="1" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSchedule">保存</el-button>
      </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Refresh } from '@element-plus/icons-vue'
import { departmentAPI } from '@/api/hospital/department'
import { doctorAPI } from '@/api/hospital/doctor'
import { scheduleAPI } from '@/api/hospital/schedule'
import { appointmentAPI } from '@/api/hospital/appointment'
import request from '@/api/request'
import type { Department } from '@/api/hospital/department'
import type { Doctor } from '@/api/hospital/doctor'
import type { DoctorSchedule } from '@/api/hospital/schedule'
import type { Appointment } from '@/api/hospital/appointment'

interface FeedbackRecord {
  feedbackID: string
  studentID: number | null
  processAdminID?: number | null
  feedbackType: number
  feedbackContent: string
  processStatus: number
  feedbackTime: string
  replyContent?: string | null
  replyTime?: string | null
  contactInfo?: string | null
  priority: number
}

const router = useRouter()

const getAdminId = (): number | null => {
  try {
    const raw = localStorage.getItem('hospital_user')
    if (!raw) return null
    const user = JSON.parse(raw)
    return user.adminID ?? user.user?.adminID ?? null
  } catch {
    return null
  }
}
const activeTab = ref('department')
const adminName = computed(() => {
  try {
    const raw = localStorage.getItem('hospital_user')
    if (!raw) return '管理员'
    const user = JSON.parse(raw)
    return user.user?.adminName || user.adminName || '管理员'
  } catch {
    return '管理员'
  }
})

const departments = ref<Department[]>([])
const doctors = ref<Doctor[]>([])
const schedules = ref<DoctorSchedule[]>([])
const appointments = ref<Appointment[]>([])
const feedbacks = ref<FeedbackRecord[]>([])
const feedbackStatusFilter = ref<number>(-1)
const feedbackReplyForm = reactive({
  feedbackID: '',
  studentID: null as number | null,
  feedbackType: 0,
  feedbackContent: '',
  feedbackTime: '',
  existingReply: '',
  reply: ''
})
const scheduleSearch = ref('')
const scheduleDateFilter = ref('')
const scheduleCurrentPage = ref(1)
const schedulePageSize = ref(10)

const departmentLoading = ref(false)
const doctorLoading = ref(false)
const scheduleLoading = ref(false)
const appointmentLoading = ref(false)
const feedbackLoading = ref(false)
const feedbackReplyLoading = ref(false)

const departmentDialogVisible = ref(false)
const doctorDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)
const feedbackReplyVisible = ref(false)

const departmentForm = reactive<Partial<Department>>({})
const doctorForm = reactive<Partial<Doctor & { doctorPassword?: string }>>({ doctorStatus: 1 })
const scheduleForm = reactive<Partial<DoctorSchedule>>({ scheduleStatus: 1, timeSlot: 1, registrationType: 1 })

const departmentStatusSwitch = computed({
  get: () => departmentForm.departmentStatus === 1,
  set: (value: boolean) => { departmentForm.departmentStatus = value ? 1 : 0 }
})

const doctorStatusSwitch = computed({
  get: () => doctorForm.doctorStatus === 1,
  set: (value: boolean) => { doctorForm.doctorStatus = value ? 1 : 0 }
})

const overviewCards = computed(() => [
  { title: '科室总数', value: departments.value.length, desc: '可进行增删改查' },
  { title: '医生总数', value: doctors.value.length, desc: '包含排班与状态管理' },
  { title: '挂号记录', value: appointments.value.length, desc: '查看全量挂号流水' }
])

const loadDepartments = async () => {
  departmentLoading.value = true
  try {
    const res = await departmentAPI.getList()
    departments.value = res.code === 200 ? (res.data || []) : []
  } catch {
    ElMessage.error('加载科室失败')
  } finally {
    departmentLoading.value = false
  }
}

const loadDoctors = async () => {
  doctorLoading.value = true
  try {
    const res = await doctorAPI.getList()
    doctors.value = res.code === 200 ? (res.data || []) : []
  } catch {
    ElMessage.error('加载医生失败')
  } finally {
    doctorLoading.value = false
  }
}

const loadSchedules = async () => {
  scheduleLoading.value = true
  try {
    const res = await scheduleAPI.getAll()
    schedules.value = res.code === 200 ? (res.data || []) : []
    scheduleCurrentPage.value = 1
  } catch {
    ElMessage.error('加载排班失败')
  } finally {
    scheduleLoading.value = false
  }
}

const filteredSchedules = computed(() => {
  const today = new Date().toISOString().split('T')[0]
  const keyword = scheduleSearch.value.trim().toLowerCase()
  const dateFilter = scheduleDateFilter.value
  return schedules.value
    .filter(s => s.scheduleDate && s.scheduleDate >= today)
    .filter(s => dateFilter ? s.scheduleDate === dateFilter : true)
    .filter(s => {
      if (!keyword) return true
      return (s.doctorName || '').toLowerCase().includes(keyword) ||
             (s.departmentName || '').toLowerCase().includes(keyword)
    })
    .sort((a, b) => (a.scheduleDate || '').localeCompare(b.scheduleDate || ''))
})

const pagedSchedules = computed(() => {
  const start = (scheduleCurrentPage.value - 1) * schedulePageSize.value
  return filteredSchedules.value.slice(start, start + schedulePageSize.value)
})

const resetReactiveForm = <T extends Record<string, unknown>>(form: T) => {
  ;(Object.keys(form) as Array<keyof T>).forEach((key) => delete form[key])
}

const loadAppointments = async () => {
  appointmentLoading.value = true
  try {
    const res = await appointmentAPI.getAll()
    appointments.value = res.code === 200 ? (res.data || []) : []
  } catch {
    ElMessage.error('加载挂号记录失败')
  } finally {
    appointmentLoading.value = false
  }
}

const filteredFeedbacks = computed(() => {
  if (feedbackStatusFilter.value === -1 || !feedbackStatusFilter.value) {
    return feedbacks.value
  }
  return feedbacks.value.filter(f => f.processStatus === feedbackStatusFilter.value)
})

const feedbackTypeText = (type?: number) => {
  const map: Record<number, string> = { 1: '积分', 2: '挂号', 3: '报告查询', 4: '问诊' }
  return map[type ?? 0] || '未分类'
}

const feedbackTagType = (type?: number) => {
  const map: Record<number, string> = { 1: 'info', 2: 'warning', 3: '', 4: 'primary' }
  return (map[type ?? 0] || 'info') as 'info' | 'warning' | 'primary' | '' | 'success' | 'danger'
}

const priorityText = (priority?: number) => {
  const map: Record<number, string> = { 1: '低', 2: '中', 3: '高' }
  return map[priority ?? 2] || '中'
}

const priorityTagType = (priority?: number) => {
  const map: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
  return (map[priority ?? 2] || 'info') as 'info' | 'warning' | 'primary' | '' | 'success' | 'danger'
}

const feedbackStatusText = (status?: number) => {
  const map: Record<number, string> = { 1: '待处理', 2: '处理中', 3: '已回复', 4: '已关闭' }
  return map[status ?? 1] || '未知'
}

const feedbackStatusTagType = (status?: number) => {
  const map: Record<number, string> = { 1: 'warning', 2: '', 3: 'success', 4: 'info' }
  return (map[status ?? 1] || 'info') as 'info' | 'warning' | 'primary' | '' | 'success' | 'danger'
}

const formatFeedbackTime = (time?: string) => {
  if (!time) return '-'
  try {
    const d = new Date(time)
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const h = String(d.getHours()).padStart(2, '0')
    const min = String(d.getMinutes()).padStart(2, '0')
    return `${y}-${m}-${day} ${h}:${min}`
  } catch {
    return time
  }
}

const loadFeedbacks = async () => {
  feedbackLoading.value = true
  try {
    const res = await request.get('/feedback/all')
    const data = res?.data
    if (data && Array.isArray(data.feedbacks)) {
      feedbacks.value = data.feedbacks as FeedbackRecord[]
    } else {
      feedbacks.value = []
    }
  } catch {
    ElMessage.error('加载反馈列表失败')
    feedbacks.value = []
  } finally {
    feedbackLoading.value = false
  }
}

const openFeedbackReplyDialog = (record: FeedbackRecord) => {
  feedbackReplyForm.feedbackID = record.feedbackID
  feedbackReplyForm.studentID = record.studentID
  feedbackReplyForm.feedbackType = record.feedbackType
  feedbackReplyForm.feedbackContent = record.feedbackContent
  feedbackReplyForm.feedbackTime = record.feedbackTime
  feedbackReplyForm.existingReply = record.replyContent || ''
  feedbackReplyForm.reply = ''
  feedbackReplyVisible.value = true
}

const submitFeedbackReply = async () => {
  if (!feedbackReplyForm.reply.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  feedbackReplyLoading.value = true
  try {
    const payload = {
      feedbackID: feedbackReplyForm.feedbackID,
      replyContent: feedbackReplyForm.reply.trim(),
      processStatus: 3,
      processAdminID: getAdminId()
    }
    const res = await request.put('/feedback/update', payload)
    console.log('回复响应:', JSON.stringify(res))
    if (res?.code === 200) {
      ElMessage.success('回复已提交')
      feedbackReplyVisible.value = false
      await loadFeedbacks()
    } else {
      ElMessage.error(res?.message || '回复失败，请稍后重试')
    }
  } catch {
    ElMessage.error('回复请求失败，请检查网络')
  } finally {
    feedbackReplyLoading.value = false
  }
}

const closeFeedback = async (feedbackId: string) => {
  try {
    const res = await request.put('/feedback/status', null, { params: { feedbackId, newStatus: 4 } })
    if (res?.code === 200) {
      ElMessage.success('已关闭该反馈')
      await loadFeedbacks()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败，请检查网络')
  }
}

const reopenFeedback = async (feedbackId: string) => {
  try {
    const res = await request.put('/feedback/status', null, { params: { feedbackId, newStatus: 1 } })
    if (res?.code === 200) {
      ElMessage.success('已重开该反馈')
      await loadFeedbacks()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败，请检查网络')
  }
}

const openDepartmentDialog = (row?: Department) => {
  Object.assign(departmentForm, row ? { ...row } : { departmentStatus: 1, departmentType: 1, displayOrder: 0 })
  departmentDialogVisible.value = true
}

const saveDepartment = async () => {
  try {
    if (departmentForm.departmentID) {
      await departmentAPI.update(departmentForm.departmentID, departmentForm)
      ElMessage.success('科室已更新')
    } else {
      await departmentAPI.add(departmentForm)
      ElMessage.success('科室已新增')
    }
    departmentDialogVisible.value = false
    await loadDepartments()
  } catch {
    ElMessage.error('保存科室失败')
  }
}

const removeDepartment = async (id: number) => {
  await ElMessageBox.confirm('确认删除该科室？', '提示')
  await departmentAPI.delete(id)
  ElMessage.success('已删除')
  await loadDepartments()
}

const openDoctorDialog = (row?: Doctor) => {
  resetReactiveForm(doctorForm as Record<string, unknown>)
  Object.assign(doctorForm, row ? { ...row, doctorPassword: '' } : { doctorStatus: 1, doctorGender: 1, registrationFee: 0 })
  doctorDialogVisible.value = true
}

const saveDoctor = async () => {
  try {
    const payload = { ...doctorForm } as Partial<Doctor>
    if (doctorForm.doctorPassword) {
      ;(payload as Partial<Doctor & { doctorPassword: string }>).doctorPassword = doctorForm.doctorPassword
    }
    if (doctorForm.doctorID) {
      await doctorAPI.update(doctorForm.doctorID, payload)
      ElMessage.success('医生已更新')
    } else {
      await doctorAPI.add(payload)
      ElMessage.success('医生已新增')
    }
    doctorDialogVisible.value = false
    await loadDoctors()
  } catch {
    ElMessage.error('保存医生失败')
  }
}

const removeDoctor = async (id: number) => {
  await ElMessageBox.confirm('确认删除该医生？', '提示')
  await doctorAPI.delete(id)
  ElMessage.success('已删除')
  await loadDoctors()
}

const openScheduleDialog = (row?: Partial<DoctorSchedule> & { doctorID?: number }) => {
  resetReactiveForm(scheduleForm as Record<string, unknown>)
  Object.assign(scheduleForm, row ? { ...row } : { scheduleStatus: 1, timeSlot: 1, registrationType: 1 })
  scheduleDialogVisible.value = true
}

const saveSchedule = async () => {
  try {
    const payload = {
      ...scheduleForm,
      registrationType: scheduleForm.registrationType ?? 1
    }
    if (scheduleForm.scheduleID) {
      await scheduleAPI.update(scheduleForm.scheduleID, payload)
      ElMessage.success('排班已更新')
    } else {
      await scheduleAPI.add(payload)
      ElMessage.success('排班已新增')
    }
    scheduleDialogVisible.value = false
    await loadSchedules()
  } catch {
    ElMessage.error('保存排班失败')
  }
}

const stopSchedule = async (id: number) => {
  await scheduleAPI.stop(id)
  ElMessage.success('已停诊')
  await loadSchedules()
}

const removeSchedule = async (id: number) => {
  await ElMessageBox.confirm('确认删除该排班？', '提示')
  await scheduleAPI.delete(id)
  ElMessage.success('已删除')
  await loadSchedules()
}

const appointmentStatusText = (status?: number) => {
  const map: Record<number, string> = { 1: '已预约', 2: '已签到', 3: '就诊中', 4: '已完成', 5: '已取消', 6: '已退号', 7: '已爽约', 8: '已失效' }
  return map[status ?? 0] || '未知'
}

const scheduleStatusText = (status: number) => {
  return ({ 0: '已停诊', 1: '可预约', 2: '已约满' } as Record<number, string>)[status] || '未知'
}

const scheduleStatusTagType = (status: number) => {
  return ({ 0: 'info', 1: 'success', 2: 'warning' } as Record<number, string>)[status] || 'info'
}

const appointmentTagType = (status?: number) => {
  if (status === 1 || status === 2 || status === 3) return 'success'
  if (status === 4) return 'info'
  if (status === 5 || status === 6 || status === 7 || status === 8) return 'warning'
  return 'info'
}

const handleLogout = async () => {
  localStorage.removeItem('hospital_user')
  ElMessage.success('已退出登录')
  router.push('/hospital/login')
}

watch(activeTab, (value) => {
  if (value === 'department' && departments.value.length === 0) loadDepartments()
  if (value === 'doctor' && doctors.value.length === 0) loadDoctors()
  if (value === 'schedule' && schedules.value.length === 0) loadSchedules()
  if (value === 'appointment' && appointments.value.length === 0) loadAppointments()
  if (value === 'feedback' && feedbacks.value.length === 0) loadFeedbacks()
})

onMounted(async () => {
  await Promise.all([loadDepartments(), loadDoctors(), loadAppointments(), loadSchedules()])
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  padding: 24px;
  background: #f5f8fc;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 24px;
  margin: 0 auto 20px;
  max-width: 1440px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

.eyebrow {
  margin: 0 0 6px;
  color: #1677ff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.admin-header h1 {
  margin: 0;
  font-size: 28px;
  color: #111827;
}

.subtitle {
  margin: 6px 0 0;
  color: #6b7280;
}

.admin-user {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #374151;
}

.avatar {
  background: linear-gradient(135deg, #1677ff, #63b3ff);
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin: 0 auto 20px;
  max-width: 1440px;
}

.overview-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

.card-title {
  color: #6b7280;
  font-size: 14px;
}

.card-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  color: #111827;
}

.card-desc {
  margin-top: 8px;
  color: #6b7280;
  font-size: 13px;
}

.admin-tabs {
  background: #ffffff;
  padding: 18px 20px 24px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
  max-width: 1440px;
  margin: 0 auto;
}

.pane-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.action-row {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: nowrap;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 1100px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .admin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
.text-danger {
  color: #f56c6c;
  font-weight: 600;
}

.text-muted {
  color: #c0c4cc;
}
.reply-preview {
  color: #1d8a58;
}
</style>
