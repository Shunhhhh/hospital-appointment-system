<template>
  <div class="profile-page">
    <section class="profile-head">
      <div>
        <el-button text class="back-button" @click="router.push('/hospital/home')">← 返回首页</el-button>
        <div class="head-title-row">
          <h1 class="page-title">个人中心</h1>
          <span class="head-note">维护实名资料、账户安全与就诊记录</span>
        </div>
      </div>
      <el-button type="primary" :loading="saving" @click="handleSave" :disabled="!profile">
        保存修改
      </el-button>
    </section>

    <section v-if="profile" class="profile-layout">
      <aside class="profile-aside">
        <div class="identity-card card-base">
          <div class="identity-bg"></div>
          <el-avatar :size="76" class="profile-avatar">{{ profile.patientName?.slice(0, 1) || '患' }}</el-avatar>
          <h2>{{ profile.patientName || '未填写姓名' }}</h2>
          <p>{{ maskedPhone }}</p>
          <div class="identity-tags">
            <el-tag type="primary" effect="light">{{ genderText }}</el-tag>
            <el-tag :type="profile.isBlacklist ? 'danger' : 'success'" effect="light">
              {{ profile.isBlacklist ? '限制挂号' : '正常就诊' }}
            </el-tag>
          </div>
        </div>

        <div class="summary-grid">
          <div class="summary-card card-base accent-orange">
            <span>信用积分</span>
            <strong>{{ profile.creditScore ?? 0 }}</strong>
          </div>
          <div class="summary-card card-base accent-blue">
            <span>就诊记录</span>
            <strong>{{ appointments.length }}</strong>
          </div>
          <div class="summary-card card-base accent-green">
            <span>爽约次数</span>
            <strong>{{ profile.noshowCount ?? 0 }}</strong>
          </div>
          <div class="summary-card card-base accent-slate">
            <span>资料完整度</span>
            <strong>{{ profileCompletion }}%</strong>
          </div>
        </div>

        <div class="notice-card card-base">
          <div class="notice-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div>
            <h3>实名信息已关联</h3>
            <p>身份证号用于挂号实名校验，暂不支持在网页端直接修改。</p>
          </div>
        </div>
      </aside>

      <main class="profile-main">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="个人信息" name="info">
            <div class="form-card card-base">
              <div class="section-head">
                <div>
                  <h2>基础资料</h2>
                  <p>请保持联系方式准确，便于接收预约和就诊通知。</p>
                </div>
                <el-tag type="success" effect="light">信用良好</el-tag>
              </div>

              <el-form :model="profile" label-position="top" class="profile-form">
                <div class="form-grid">
                  <el-form-item label="姓名">
                    <el-input v-model="profile.patientName">
                      <template #prefix><el-icon><User /></el-icon></template>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="手机号">
                    <el-input v-model="profile.patientPhone">
                      <template #prefix><el-icon><Phone /></el-icon></template>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="性别">
                    <el-segmented v-model="profile.patientGender" :options="genderOptions" />
                  </el-form-item>
                  <el-form-item label="出生日期">
                    <el-date-picker v-model="profile.patientBirthday" type="date" value-format="YYYY-MM-DD" />
                  </el-form-item>
                  <el-form-item label="邮箱">
                    <el-input v-model="profile.patientEmail">
                      <template #prefix><el-icon><Message /></el-icon></template>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="联系地址">
                    <el-input v-model="profile.patientAddress">
                      <template #prefix><el-icon><Location /></el-icon></template>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="身份证号" class="wide-item">
                    <el-input v-model="profile.idCard" disabled>
                      <template #prefix><el-icon><Postcard /></el-icon></template>
                    </el-input>
                  </el-form-item>
                </div>
              </el-form>
            </div>

            <div class="form-card card-base">
              <div class="section-head">
                <div>
                  <h2>就医补充信息</h2>
                  <p>补充医保、过敏史与紧急联系人，医生接诊时更容易判断情况。</p>
                </div>
              </div>

              <el-form :model="profile" label-position="top" class="profile-form">
                <div class="form-grid">
                  <el-form-item label="就诊卡号">
                    <el-input v-model="profile.medicalCardNo" placeholder="暂无就诊卡号">
                      <template #prefix><el-icon><Tickets /></el-icon></template>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="医保编号">
                    <el-input v-model="profile.insuranceNo" placeholder="请输入医保编号" />
                  </el-form-item>
                  <el-form-item label="紧急联系人">
                    <el-input v-model="profile.emergencyContact" placeholder="请输入联系人姓名" />
                  </el-form-item>
                  <el-form-item label="紧急联系电话">
                    <el-input v-model="profile.emergencyPhone" placeholder="请输入联系电话" />
                  </el-form-item>
                  <el-form-item label="过敏史" class="wide-item">
                    <el-input v-model="profile.allergyHistory" type="textarea" :rows="3" placeholder="如无过敏史可填写“无”" />
                  </el-form-item>
                  <el-form-item label="既往病史" class="wide-item">
                    <el-input v-model="profile.medicalHistory" type="textarea" :rows="3" placeholder="可填写慢病史、手术史等" />
                  </el-form-item>
                </div>
              </el-form>
            </div>
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <div class="password-layout">
              <div class="security-card card-base">
                <div class="security-icon">
                  <el-icon><Lock /></el-icon>
                </div>
                <h2>账户安全</h2>
                <p>建议使用 6 位以上密码，并定期更新，避免与其他平台重复。</p>
                <div class="security-line">
                  <span>当前登录手机号</span>
                  <strong>{{ maskedPhone }}</strong>
                </div>
              </div>

              <div class="form-card card-base">
                <div class="section-head">
                  <div>
                    <h2>修改登录密码</h2>
                    <p>修改后请使用新密码重新登录系统。</p>
                  </div>
                </div>
                <el-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" label-position="top" class="password-form">
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="pwdForm.newPassword" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                  </el-form-item>
                  <el-button type="primary" :loading="changingPwd" @click="handleChangePwd">确认修改</el-button>
                </el-form>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="就诊记录" name="history">
            <div class="history-panel card-base" v-loading="loadingHistory">
              <div class="section-head">
                <div>
                  <h2>近期就诊记录</h2>
                  <p>查看历史挂号、签到与完成状态。</p>
                </div>
                <el-button plain @click="loadAppointments">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>

              <div v-if="appointments.length" class="history-list">
                <article v-for="apt in appointments" :key="apt.appointmentID" class="history-card">
                  <div class="history-doctor">
                    <el-avatar :size="44" class="history-avatar">{{ apt.doctorName?.slice(0, 1) || '医' }}</el-avatar>
                    <div>
                      <h3>{{ apt.doctorName || '暂未分配医生' }}</h3>
                      <p>{{ apt.departmentName || '门诊科室' }}</p>
                    </div>
                  </div>
                  <div class="history-time">
                    <span>{{ apt.appointmentDate }}</span>
                    <strong>{{ apt.timeSlot === 1 ? '上午' : '下午' }}</strong>
                  </div>
                  <el-tag :type="getStatusType(apt.appointmentStatus)" effect="light">
                    {{ getStatusText(apt.appointmentStatus) }}
                  </el-tag>
                </article>
              </div>
              <el-empty v-else description="暂无就诊记录" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </main>
    </section>

    <el-empty v-else class="profile-empty" description="暂无个人信息" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  CircleCheck,
  Location,
  Lock,
  Message,
  Phone,
  Postcard,
  Refresh,
  Tickets,
  User
} from '@element-plus/icons-vue'
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

const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 2 }
]

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

const genderText = computed(() => {
  if (!profile.value) return '未填写'
  return profile.value.patientGender === 2 ? '女' : '男'
})

const maskedPhone = computed(() => {
  const phone = profile.value?.patientPhone || ''
  if (phone.length < 7) return phone || '未绑定手机号'
  return `${phone.slice(0, 3)}****${phone.slice(-4)}`
})

const profileCompletion = computed(() => {
  if (!profile.value) return 0
  const fields = [
    profile.value.patientName,
    profile.value.patientPhone,
    profile.value.patientGender,
    profile.value.patientBirthday,
    profile.value.patientEmail,
    profile.value.patientAddress,
    profile.value.idCard,
    profile.value.emergencyContact,
    profile.value.emergencyPhone,
    profile.value.allergyHistory,
    profile.value.medicalHistory
  ]
  const filled = fields.filter(item => item !== undefined && item !== null && item !== '').length
  return Math.round((filled / fields.length) * 100)
})

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
  min-height: 100vh;
  background: #f5f8fc;
  padding: 24px 32px 40px;
  max-width: 1504px;
  margin: 0 auto;
  color: #1f2937;
}

.card-base {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 28px rgba(31, 41, 55, 0.06);
}

.profile-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  padding: 20px 24px;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 28px rgba(31, 41, 55, 0.06);
}

.back-button {
  padding: 0;
  height: auto;
  color: #1677ff;
  font-size: 15px;
}

.head-title-row {
  display: flex;
  align-items: flex-end;
  gap: 14px;
  margin-top: 16px;
}

.page-title {
  margin: 0;
  font-size: 30px;
  line-height: 1.15;
  font-weight: 700;
  color: #111827;
}

.head-note {
  margin-bottom: 3px;
  color: #6b7280;
  font-size: 14px;
}

.profile-layout {
  display: grid;
  grid-template-columns: 328px minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.profile-aside {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 24px;
}

.identity-card {
  position: relative;
  overflow: hidden;
  padding: 26px 20px 22px;
  text-align: center;
}

.identity-bg {
  position: absolute;
  inset: 0 0 auto;
  height: 98px;
  background:
    linear-gradient(135deg, rgba(22, 119, 255, 0.18), rgba(32, 201, 151, 0.12)),
    linear-gradient(90deg, #eaf4ff, #f6fbff);
}

.profile-avatar {
  position: relative;
  z-index: 1;
  margin-top: 26px;
  background: linear-gradient(135deg, #1677ff, #51a8ff);
  color: #ffffff;
  font-size: 28px;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(22, 119, 255, 0.22);
}

.identity-card h2 {
  margin: 14px 0 4px;
  font-size: 22px;
  color: #111827;
}

.identity-card p {
  margin: 0;
  color: #6b7280;
}

.identity-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 14px;
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.summary-card {
  padding: 14px;
  min-height: 88px;
}

.summary-card span {
  display: block;
  margin-bottom: 10px;
  color: #6b7280;
  font-size: 13px;
}

.summary-card strong {
  display: block;
  font-size: 26px;
  line-height: 1;
  color: #111827;
}

.summary-card.accent-orange strong {
  color: #f59e0b;
}

.summary-card.accent-blue strong {
  color: #1677ff;
}

.summary-card.accent-green strong {
  color: #10b981;
}

.notice-card {
  display: grid;
  grid-template-columns: 42px 1fr;
  gap: 12px;
  padding: 16px;
}

.notice-icon {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #eaf4ff;
  color: #1677ff;
  font-size: 22px;
}

.notice-card h3 {
  margin: 0 0 5px;
  font-size: 15px;
}

.notice-card p {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.55;
}

.profile-main {
  min-width: 0;
}

.profile-tabs {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 0 22px 22px;
  box-shadow: 0 10px 28px rgba(31, 41, 55, 0.06);
}

.profile-tabs :deep(.el-tabs__header) {
  margin: 0 0 16px;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background: #e5e7eb;
}

.profile-tabs :deep(.el-tabs__item) {
  height: 56px;
  padding: 0 24px;
  color: #6b7280;
  font-size: 15px;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  color: #1677ff;
  font-weight: 600;
}

.form-card {
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: none;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid #eef2f7;
}

.section-head h2 {
  margin: 0;
  font-size: 20px;
  color: #111827;
}

.section-head p {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.profile-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.profile-form :deep(.el-form-item__label) {
  color: #4b5563;
  font-weight: 600;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 18px;
  row-gap: 2px;
}

.wide-item {
  grid-column: 1 / -1;
}

.profile-form :deep(.el-input__wrapper),
.password-form :deep(.el-input__wrapper),
.profile-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dce3ec inset;
}

.profile-form :deep(.el-input__wrapper),
.password-form :deep(.el-input__wrapper) {
  min-height: 42px;
}

.profile-form :deep(.el-date-editor.el-input) {
  width: 100%;
}

.profile-form :deep(.el-segmented) {
  --el-segmented-item-selected-color: #ffffff;
  --el-segmented-item-selected-bg-color: #1677ff;
  width: 100%;
  height: 42px;
  padding: 4px;
}

.profile-form :deep(.el-segmented__item) {
  flex: 1;
}

.password-layout {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 16px;
}

.security-card {
  padding: 22px;
  background: linear-gradient(180deg, #f4f9ff, #ffffff);
}

.security-icon {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  border-radius: 14px;
  color: #1677ff;
  background: #eaf4ff;
  font-size: 26px;
}

.security-card h2 {
  margin: 18px 0 8px;
  font-size: 22px;
}

.security-card p {
  margin: 0;
  color: #6b7280;
  line-height: 1.7;
}

.security-line {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 22px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  color: #6b7280;
}

.security-line strong {
  color: #111827;
}

.password-form {
  max-width: 460px;
}

.password-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.history-panel {
  padding: 20px;
  box-shadow: none;
}

.history-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 16px;
  display: grid;
  grid-template-columns: minmax(220px, 1fr) 160px 96px;
  gap: 16px;
  align-items: center;
  background: #ffffff;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-doctor {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.history-avatar {
  background: #eaf4ff;
  color: #1677ff;
  font-weight: 700;
}

.history-doctor h3 {
  margin: 0 0 4px;
  font-size: 16px;
  color: #111827;
}

.history-doctor p,
.history-time {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.history-time {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.history-time strong {
  color: #1f2937;
  font-size: 15px;
}

.profile-empty {
  margin-top: 32px;
  padding: 56px;
  background: #ffffff;
  border-radius: 12px;
}

@media (max-width: 1180px) {
  .profile-layout,
  .password-layout {
    grid-template-columns: 1fr;
  }

  .profile-aside {
    position: static;
  }

  .summary-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 760px) {
  .profile-page {
    padding: 16px;
  }

  .profile-head,
  .head-title-row,
  .section-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .form-grid,
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .history-card {
    grid-template-columns: 1fr;
    gap: 10px;
  }
}
</style>
