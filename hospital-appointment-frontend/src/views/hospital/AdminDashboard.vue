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
              <el-button text type="primary" @click="openDoctorDialog(scope.row)">编辑</el-button>
              <el-button text type="warning" @click="openScheduleDialog(scope.row)">排班</el-button>
              <el-button text type="danger" @click="removeDoctor(scope.row.doctorID)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

      </el-tab-pane>

      <el-tab-pane label="排班管理" name="schedule">
        <div class="pane-toolbar">
          <el-select v-model="scheduleDoctorFilter" placeholder="按医生筛选" clearable style="width: 220px" @change="loadSchedules">
            <el-option v-for="doctor in doctors" :key="doctor.doctorID" :label="`${doctor.doctorName}(${doctor.doctorID})`" :value="doctor.doctorID" />
          </el-select>
          <el-button @click="loadSchedules">刷新排班</el-button>
        </div>

        <el-table :data="pagedSchedules" border stripe v-loading="scheduleLoading" style="width: 100%">
          <el-table-column prop="scheduleID" label="排班ID" width="100" />
          <el-table-column prop="doctorName" label="医生" min-width="120" />
          <el-table-column prop="departmentName" label="科室" min-width="120" />
          <el-table-column prop="scheduleDate" label="日期" width="120" />
          <el-table-column prop="timeSlot" label="时段" width="90" />
          <el-table-column prop="remainingSlots" label="剩余号源" width="100" />
          <el-table-column prop="scheduleStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.scheduleStatus === 1 ? 'success' : 'info'">
                {{ scope.row.scheduleStatus === 1 ? '可预约' : '不可用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button text type="primary" @click="openScheduleDialog(scope.row)">编辑</el-button>
              <el-button text type="warning" @click="stopSchedule(scope.row.scheduleID)">停诊</el-button>
              <el-button text type="danger" @click="removeSchedule(scope.row.scheduleID)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="scheduleCurrentPage"
            v-model:page-size="schedulePageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="schedules.length"
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
    </el-tabs>

    <el-dialog v-model="departmentDialogVisible" :title="departmentForm.departmentID ? '编辑科室' : '新增科室'" width="520px">
      <el-form :model="departmentForm" label-width="100px">
        <el-form-item label="科室名称"><el-input v-model="departmentForm.departmentName" /></el-form-item>
        <el-form-item label="科室类型"><el-input-number v-model="departmentForm.departmentType" :min="1" :max="12" /></el-form-item>
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
          <el-col :span="12"><el-form-item label="科室ID"><el-input-number v-model="doctorForm.departmentID" :min="1" /></el-form-item></el-col>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { departmentAPI } from '@/api/hospital/department'
import { doctorAPI } from '@/api/hospital/doctor'
import { scheduleAPI } from '@/api/hospital/schedule'
import { appointmentAPI } from '@/api/hospital/appointment'
import type { Department } from '@/api/hospital/department'
import type { Doctor } from '@/api/hospital/doctor'
import type { DoctorSchedule } from '@/api/hospital/schedule'
import type { Appointment } from '@/api/hospital/appointment'

const router = useRouter()
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
const scheduleDoctorFilter = ref<number | undefined>()
const scheduleCurrentPage = ref(1)
const schedulePageSize = ref(10)

const departmentLoading = ref(false)
const doctorLoading = ref(false)
const scheduleLoading = ref(false)
const appointmentLoading = ref(false)

const departmentDialogVisible = ref(false)
const doctorDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)

const departmentForm = reactive<Partial<Department>>({})
const doctorForm = reactive<Partial<Doctor & { doctorPassword?: string }>>({ doctorStatus: 1 })
const scheduleForm = reactive<Partial<DoctorSchedule>>({ scheduleStatus: 1, timeSlot: 1, registrationType: 1 })

const pagedSchedules = computed(() => {
  const start = (scheduleCurrentPage.value - 1) * schedulePageSize.value
  return schedules.value.slice(start, start + schedulePageSize.value)
})

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
    const res = scheduleDoctorFilter.value ? await scheduleAPI.getByDoctor(scheduleDoctorFilter.value) : await scheduleAPI.getAll()
    schedules.value = res.code === 200 ? (res.data || []) : []
    scheduleCurrentPage.value = 1
  } catch {
    ElMessage.error('加载排班失败')
  } finally {
    scheduleLoading.value = false
  }
}

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
</style>
