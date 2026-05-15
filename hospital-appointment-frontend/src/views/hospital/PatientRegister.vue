<template>
  <div class="register-page">
    <div class="register-box">
      <div class="register-header">
        <h1>用户注册</h1>
        <p>完善个人信息，开始便捷就医</p>
      </div>

      <el-form :model="registerForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="patientName">
          <el-input v-model="registerForm.patientName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="身份证" prop="idCard">
          <el-input v-model="registerForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>

        <el-form-item label="性别" prop="patientGender">
          <el-radio-group v-model="registerForm.patientGender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="手机号" prop="patientPhone">
          <el-input v-model="registerForm.patientPhone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="密码" prop="patientPassword">
          <el-input v-model="registerForm.patientPassword" type="password" placeholder="请设置密码" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" show-password />
        </el-form-item>

        <el-form-item label="出生日期" prop="patientBirthday">
          <el-date-picker
            v-model="registerForm.patientBirthday"
            type="date"
            placeholder="选择出生日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="地址" prop="patientAddress">
          <el-input v-model="registerForm.patientAddress" placeholder="请输入住址" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="register-btn">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-link">
        <span>已有账号？</span>
        <el-link type="primary" @click="goToLogin">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { patientAPI } from '@/api/hospital/patient'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const registerForm = reactive({
  patientName: '',
  idCard: '',
  patientGender: 1,
  patientPhone: '',
  patientPassword: '',
  confirmPassword: '',
  patientBirthday: '',
  patientAddress: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.patientPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  patientName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  patientGender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  patientPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  patientPassword: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const res = await patientAPI.register({
      patientName: registerForm.patientName,
      idCard: registerForm.idCard,
      patientGender: registerForm.patientGender,
      patientPhone: registerForm.patientPhone,
      patientPassword: registerForm.patientPassword,
      patientBirthday: registerForm.patientBirthday,
      patientAddress: registerForm.patientAddress
    })

    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/hospital/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    // 表单验证失败
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/hospital/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 450px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.register-header p {
  color: #999;
  font-size: 14px;
}

.register-btn {
  width: 100%;
}

.login-link {
  text-align: center;
  color: #999;
  font-size: 14px;
  margin-top: 20px;
}
</style>
