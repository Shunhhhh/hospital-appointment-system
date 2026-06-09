<template>
  <div class="submit-page">
    <section class="submit-shell card-surface">
      <header class="submit-head">
        <div>
          <el-button text class="back-button" @click="router.push('/hospital/feedback')">← 返回反馈列表</el-button>
          <div class="title-line">
            <h1 class="page-title">提交工单</h1>
            <span class="head-note">请填写业务类型、发生时间、问题标题和详细问题</span>
          </div>
        </div>
      </header>

      <div class="info-banner">
        <div>
          <span>当前患者</span>
          <strong>{{ patientName }}</strong>
        </div>
        <div>
          <span>联系电话</span>
          <strong>{{ maskedPhone }}</strong>
        </div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="feedback-form">
        <div class="form-grid">
          <el-form-item label="业务类型" prop="businessType">
            <el-select v-model="form.businessType" placeholder="请选择业务类型" class="form-control">
              <el-option
                v-for="item in businessTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="发生时间" prop="occurredAt">
            <el-date-picker
              v-model="form.occurredAt"
              class="form-control"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="请选择发生时间"
            />
          </el-form-item>

          <el-form-item label="问题标题" prop="title" class="wide-item">
            <el-input
              v-model="form.title"
              maxlength="20"
              show-word-limit
              placeholder="请输入20字以内标题"
            />
          </el-form-item>

          <el-form-item label="问题描述" prop="detail" class="wide-item">
            <el-input
              v-model="form.detail"
              type="textarea"
              :rows="8"
              maxlength="200"
              show-word-limit
              placeholder="请填写详细问题，限制 200 字"
            />
          </el-form-item>

          <el-form-item label="联系电话" class="wide-item">
            <el-input v-model="maskedPhone" disabled />
          </el-form-item>
        </div>

        <div class="action-row">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" :loading="submitting" @click="submitForm">提交工单</el-button>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getCurrentPatientId, getCurrentPatientPhone, submitPatientFeedback, type HospitalUser } from '@/api/hospital/feedback'

interface FeedbackForm {
  businessType?: number
  occurredAt: string
  title: string
  detail: string
}

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const businessTypeOptions = [
  { label: '积分', value: 1 },
  { label: '挂号', value: 2 },
  { label: '报告查询', value: 3 },
  { label: '问诊', value: 4 }
]

const form = reactive<FeedbackForm>({
  businessType: undefined,
  occurredAt: '',
  title: '',
  detail: ''
})

const rules: FormRules<FeedbackForm> = {
  businessType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  occurredAt: [{ required: true, message: '请选择发生时间', trigger: 'change' }],
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { max: 20, message: '问题标题不能超过20字', trigger: 'blur' }
  ],
  detail: [
    { required: true, message: '请输入详细问题', trigger: 'blur' },
    { max: 200, message: '详细问题不能超过200字', trigger: 'blur' }
  ]
}

const currentUser = computed<HospitalUser | null>(() => {
  try {
    const raw = localStorage.getItem('hospital_user')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
})

const patientName = computed(() => currentUser.value?.patientName || '患者')
const patientPhone = computed(() => getCurrentPatientPhone())
const maskedPhone = computed(() => {
  const phone = patientPhone.value
  if (!phone) return '未获取到联系电话'
  return phone.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
})

function resetForm() {
  form.businessType = undefined
  form.occurredAt = ''
  form.title = ''
  form.detail = ''
  formRef.value?.clearValidate()
}

async function submitForm() {
  try {
    await formRef.value?.validate()

    if (!getCurrentPatientId()) {
      ElMessage.warning('请先登录患者账号')
      router.push('/hospital/login')
      return
    }

    submitting.value = true
    await submitPatientFeedback({
      businessType: form.businessType as number,
      occurredAt: form.occurredAt,
      title: form.title.trim(),
      detail: form.detail.trim()
    })

    ElMessage.success('工单提交成功')
    resetForm()
    router.push('/hospital/feedback')
  } catch (error: any) {
    if (error?.errorFields) return
    ElMessage.error(error?.message || '提交工单失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!getCurrentPatientId()) {
    ElMessage.warning('请先登录患者账号')
    router.push('/hospital/login')
  }
})
</script>

<style scoped>
.submit-page {
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(74, 144, 226, 0.16), transparent 32%),
    linear-gradient(180deg, #f6f9fe 0%, #edf4ff 100%);
}

.card-surface {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 42px rgba(38, 79, 170, 0.12);
}

.submit-shell {
  max-width: 980px;
  margin: 0 auto;
  padding: 28px;
}

.submit-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.title-line {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  margin: 0;
  color: #153a73;
  font-size: 30px;
}

.head-note {
  color: #6a7590;
  font-size: 14px;
}

.info-banner {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 16px 18px;
  margin-bottom: 20px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f6faff 0%, #edf4ff 100%);
}

.info-banner span {
  display: block;
  color: #6f7c94;
  font-size: 12px;
}

.info-banner strong {
  display: block;
  margin-top: 6px;
  color: #173f7b;
  font-size: 16px;
}

.feedback-form {
  padding-top: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 16px;
}

.wide-item {
  grid-column: 1 / -1;
}

.form-control {
  width: 100%;
}

.action-row {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

@media (max-width: 960px) {
  .submit-shell {
    padding: 20px;
  }

  .submit-head {
    flex-direction: column;
  }

  .info-banner,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .action-row {
    flex-direction: column;
  }
}
</style>
