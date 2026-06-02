<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <h1>医院预约挂号系统</h1>
        <p>便捷就医，从预约开始</p>
      </div>

      <div class="login-tabs">
        <el-radio-group v-model="loginType">
          <el-radio-button label="patient">患者登录</el-radio-button>
          <el-radio-button label="doctor">医生登录</el-radio-button>
          <el-radio-button label="admin">管理员登录</el-radio-button>
        </el-radio-group>
      </div>

      <el-form :model="loginForm" :rules="rules" ref="formRef">
        <el-form-item prop="phone">
          <el-input
            v-model="loginForm.phone"
            :placeholder="loginType === 'admin' ? '请输入管理员账号' : '请输入手机号'"
            size="large"
            prefix-icon="Phone"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <div class="form-footer">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
        </div>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="loginType === 'patient'" class="register-link">
        <span>还没有账号？</span>
        <el-link type="primary" @click="goToRegister">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { patientAPI } from '@/api/hospital/patient'
import { doctorAPI } from '@/api/hospital/doctor'
import { adminAPI } from '@/api/hospital/admin'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const loginType = ref('patient')
const rememberMe = ref(false)

const loginForm = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    let res: any

    if (loginType.value === 'patient') {
      res = await patientAPI.login(loginForm.phone, loginForm.password)
      if (res.code === 200) {
        ElMessage.success('登录成功')
        localStorage.setItem('hospital_user', JSON.stringify({
          type: 'patient',
          ...res.data
        }))
        router.push('/hospital/home')
        return
      }
    } else if (loginType.value === 'doctor') {
      // 医生登录
      res = await doctorAPI.login(loginForm.phone, loginForm.password)
      if (res.code === 200) {
        ElMessage.success('医生登录成功')
        localStorage.setItem('hospital_user', JSON.stringify({
          type: 'doctor',
          ...res.data
        }))
        router.push('/hospital/doctor/workbench')
        return
      }
    } else if (loginType.value === 'admin') {
      // 管理员登录（使用 adminID）
      res = await adminAPI.login(Number(loginForm.phone), loginForm.password)
      if (res.code === 200) {
        ElMessage.success('管理员登录成功')
        localStorage.setItem('hospital_user', JSON.stringify({
          type: 'admin',
          ...res.data
        }))
        router.push('/hospital/admin')
        return
      }
    }

    ElMessage.error(res?.message || '登录失败，请检查账号密码')
  } catch (error) {
    console.error('登录出错:', error)
    ElMessage.error('登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/hospital/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(22, 119, 255, 0.16), transparent 30%),
    radial-gradient(circle at bottom right, rgba(34, 197, 94, 0.12), transparent 26%),
    #f5f8fc;
}

.login-box {
  width: min(440px, 100%);
  padding: 40px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 30px rgba(31, 41, 55, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.login-header h1 {
  font-size: 28px;
  color: #111827;
  margin-bottom: 10px;
}

.login-header p {
  color: #6b7280;
  font-size: 14px;
}

.login-tabs {
  margin-bottom: 24px;
}

.login-tabs .el-radio-group {
  width: 100%;
  display: flex;
}

.login-tabs .el-radio-button {
  flex: 1;
}

.login-tabs .el-radio-button__inner {
  width: 100%;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 18px;
}

.login-btn {
  width: 100%;
  height: 46px;
}

.register-link {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
  margin-top: 15px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
}
</style>
