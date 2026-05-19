<template>
  <div class="profile-page">
    <div class="back-bar">
      <el-button text @click="router.push('/hospital/home')">← 返回首页</el-button>
    </div>
    <h1 class="page-title">个人信息</h1>

    <el-tabs v-model="activeTab">
      <!-- 个人信息 -->
      <el-tab-pane label="个人信息" name="info">
        <el-form :model="profile" label-width="100px" v-if="profile">
          <el-form-item label="姓名">
            <el-input v-model="profile.patientName" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profile.patientPhone" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="profile.patientGender">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker v-model="profile.patientBirthday" type="date" value-format="YYYY-MM-DD" style="width:100%" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profile.patientEmail" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="profile.patientAddress" />
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="profile.idCard" disabled />
          </el-form-item>
          <el-form-item label="信用积分">
            <el-tag type="success">{{ profile.creditScore }}</el-tag>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" label-width="100px" style="max-width:400px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="changingPwd" @click="handleChangePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 就诊记录 -->
      <el-tab-pane label="就诊记录" name="history">
        <div v-loading="loadingHistory">
          <div v-for="apt in appointments" :key="apt.appointmentID" class="history-card">
            <div class="history-header">
              <span class="doctor-name">{{ apt.doctorName }}</span>
              <el-tag :type="getStatusType(apt.appointmentStatus)" size="small">
                {{ getStatusText(apt.appointmentStatus) }}
              </el-tag>
            </div>
            <div class="history-body">
              <span>{{ apt.appointmentDate }} {{ apt.timeSlot === 1 ? '上午' : '下午' }}</span>
              <span>{{ apt.departmentName }}</span>
            </div>
          </div>
          <el-empty v-if="!loadingHistory && appointments.length === 0" description="暂无就诊记录" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { patientAPI } from '@/api/hospital/patient'
import { appointmentAPI } from '@/api/hospital/appointment'
import type { Patient } from '@/api/hospital/patient'
import type { Appointment } from '@/api/hospital/appointment'

const router = useRouter()
const activeTab = ref('info')
const saving = ref(false)
const changingPwd = ref(false)
const loadingHistory = ref(false)

const profile = ref<Patient | null>(null)
const appointments = ref<Appointment[]>([])
const pwdRef = ref()

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule: any, value: string, callback: any) => {
  if (value !== pwdForm.newPassword) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

const getStatusType = (s: number) => ({ 1: 'primary', 2: 'warning', 4: 'success', 5: 'info', 7: 'danger' }[s] || 'info')
const getStatusText = (s: number) => ({ 1: '已预约', 2: '已签到', 4: '已完成', 5: '已取消', 7: '已爽约' }[s] || '未知')

const loadProfile = async () => {
  const userStr = localStorage.getItem('hospital_user')
  if (!userStr) return router.push('/hospital/login')
  const user = JSON.parse(userStr)
  if (user.type !== 'patient') return
  try {
    const res = await patientAPI.getById(user.patientID)
    if (res.code === 200) profile.value = res.data
  } catch { ElMessage.error('加载个人信息失败') }
}

const handleSave = async () => {
  if (!profile.value) return
  saving.value = true
  try {
    const res = await patientAPI.update(profile.value.patientID, profile.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      const userStr = localStorage.getItem('hospital_user')
      if (userStr) {
        const user = JSON.parse(userStr)
        user.patientName = profile.value.patientName
        localStorage.setItem('hospital_user', JSON.stringify(user))
      }
    } else ElMessage.error(res.message || '保存失败')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

const handleChangePwd = async () => {
  try {
    await pwdRef.value.validate()
  } catch { return }
  const user = JSON.parse(localStorage.getItem('hospital_user') || '{}')
  if (!user.patientID) return
  changingPwd.value = true
  try {
    const res = await patientAPI.changePassword(user.patientID, pwdForm.oldPassword, pwdForm.newPassword)
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      pwdForm.oldPassword = pwdForm.newPassword = pwdForm.confirmPassword = ''
    } else ElMessage.error(res.message || '修改失败')
  } catch { ElMessage.error('修改失败') }
  finally { changingPwd.value = false }
}

const loadAppointments = async () => {
  const user = JSON.parse(localStorage.getItem('hospital_user') || '{}')
  if (!user.patientID) return
  loadingHistory.value = true
  try {
    const res = await appointmentAPI.getByPatient(user.patientID)
    if (res.code === 200) appointments.value = res.data || []
  } catch { ElMessage.error('加载就诊记录失败') }
  finally { loadingHistory.value = false }
}

onMounted(() => {
  loadProfile()
  loadAppointments()
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.back-bar { margin-bottom: 16px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 24px; }
.history-card {
  background: white;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.history-header .doctor-name { font-size: 16px; font-weight: bold; color: #333; }
.history-body { color: #999; font-size: 13px; display: flex; gap: 20px; }
</style>
