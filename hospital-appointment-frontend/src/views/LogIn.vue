<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStudentStore } from '@/store/studentStore'
import { useAdminStore } from '@/store/adminStore'

// 学生信息 store
const studentStore = useStudentStore()
// 管理员信息 store
const adminStore = useAdminStore()


// 登录表单
const loginForm = reactive({
  id: '',
  password: '',
  role: 'student' // 默认为学生角色
})

// 学生注册表单
const registerForm = reactive({
  name: '',
  id: '',
  phone: '',
  college: '',
  grade: '',
  password: '',
  confirmPassword: ''
})

// 重置密码表单
const resetPasswordForm = reactive({
  id: '',
  phone: '',
  newPassword: '',
  confirmNewPassword: ''
})

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const isRegisterMode = ref(false)
const isResetPasswordMode = ref(false)
const router = useRouter()

// 处理登录逻辑
const handleLogin = async () => {
  if (!loginForm.id || !loginForm.password) {
    errorMessage.value = '请输入学号(工号)和密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 根据角色选择不同的登录API
    let apiUrl = '/api/auth/loginStudentById'
    if (loginForm.role === 'admin') {
      apiUrl = '/api/auth/loginAdminById'
    }

    // 调用登录API
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: loginForm.id,
        password: loginForm.password
      })
    })

    const data = await response.json()
    const token = data.data.token
    const loginInfo = data.data.user

    if (response.ok) {
      // 登录成功，保存 token 和 用户信息到 localStorage
      localStorage.setItem('token', token)
      localStorage.setItem('userId', loginInfo.studentID || loginInfo.adminId)
      
      // 根据角色保存不同的用户信息
      if (loginForm.role === 'student') {
        localStorage.setItem('studentInfo', JSON.stringify(loginInfo))
      } else {
        localStorage.setItem('adminInfo', JSON.stringify(loginInfo))
      }
      
      // 保存到 pinia store
      studentStore.setToken(token)
      
      if (loginForm.role === 'student') {
        studentStore.setStudentInfo({
          studentID: loginInfo.studentID,
          studentUserName: loginInfo.studentName,
          studentPhoneNumber: loginInfo.studentPhone,
          studentCollege: loginInfo.studentCollege,
          studentGrade: loginInfo.studentGrade,
          studentPoints: loginInfo.studentPoints || 0,
          avatar: loginInfo.avatar || loginInfo.photo || ''
        })
        console.log("学生Id" + studentStore.studentInfo.studentID)
        // 学生跳转到座位列表
        router.push(`/seat-list`)
      } else {
        // 管理员信息保存到store
        adminStore.setAdminInfo({
          adminID: loginInfo.adminId || loginInfo.id,
          adminName: loginInfo.adminName || loginInfo.name,
          adminPosition: data.adminPosition || '',
          adminPermission: data.adminPermission || 0,
          adminPhoneNumber: loginInfo.adminPhoneNumber || loginInfo.phone || '',
          avatar: loginInfo.avatar || loginInfo.photo || ''
        })
        // 管理员跳转到管理后台
        router.push(`/admin-reservation`)
      }
    } else {
      errorMessage.value = data.message || '学号(工号)或密码错误'
    }
  } catch (error) {
    console.error('登录过程中发生错误:', error)
    errorMessage.value = '登录失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

// 学生处理注册逻辑
const handleRegister = async () => {
  if (!registerForm.name || !registerForm.id || !registerForm.phone || !registerForm.college || !registerForm.grade || !registerForm.password || !registerForm.confirmPassword) {
    errorMessage.value = '请填写所有字段'
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 调用注册API
    const response = await fetch(`/api/auth/registerStudent`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: registerForm.name,
        id: registerForm.id,
        phone: registerForm.phone,
        college: registerForm.college,
        grade: registerForm.grade,
        password: registerForm.password,
      })
    })

    const data = await response.json()

    if (response.ok) {
      // 注册成功后自动登录：保存 token 和 学生信息到 localStorage 与 store
      const regToken = data.data.token
      const regUser = data.data.user
      localStorage.setItem('token', regToken)
      localStorage.setItem('userId', regUser.studentID)
      localStorage.setItem('studentInfo', JSON.stringify(regUser))
      studentStore.setToken(regToken)
      studentStore.setStudentInfo({
        studentID: regUser.studentID,
        studentUserName: regUser.studentUserName || regUser.name || '',
        studentPhoneNumber: regUser.studentPhoneNumber || '',
        studentCollege: regUser.studentCollege || '',
        studentGrade: regUser.studentGrade || 0,
        studentPoints: regUser.studentPoints || 0,
        avatar: regUser.avatar || regUser.photo || ''
      })
      router.push('/seat-list')
    } else {
      errorMessage.value = data.message || '注册失败'
    }
  } catch (error) {
    console.error('注册过程中发生错误:', error)
    errorMessage.value = '注册失败，请稍后再试'
  } finally {
    loading.value = false
  }
}


const handleResetPassword = async () => {
  // 验证所有字段是否填写
  if (!resetPasswordForm.id || !resetPasswordForm.phone ||
      !resetPasswordForm.newPassword || !resetPasswordForm.confirmNewPassword) {
    errorMessage.value = '请填写所有字段'
    return
  }

  // 验证两次密码是否一致
  if (resetPasswordForm.newPassword !== resetPasswordForm.confirmNewPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 调用重置密码API，该API内部验证学号和电话号码是否匹配
    const response = await fetch(`/api/auth/resetStudentPassword`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: resetPasswordForm.id,
        phone: resetPasswordForm.phone,
        newPassword: resetPasswordForm.newPassword
      })
    })

    const data = await response.json()

    if (response.ok) {
      successMessage.value = '密码重置成功，请重新登录'
      // 重置表单并返回登录界面
      setTimeout(() => {
        isResetPasswordMode.value = false
        resetForms()
      }, 2000)
    } else {
      // 显示后端返回的具体错误信息
      if (data.message) {
        errorMessage.value = data.message
      } else if (data.error) {
        errorMessage.value = data.error
      } else {
        errorMessage.value = '学号或电话号码不匹配'
      }
    }
  } catch (error) {
    console.error('重置密码过程中发生错误:', error)
    errorMessage.value = '密码重置失败，请稍后再试'
  } finally {
    loading.value = false
  }
}



// 切换模式
const toggleMode = () => {
  isRegisterMode.value = !isRegisterMode.value
  isResetPasswordMode.value = false
  resetForms()
}

// 切换到重置密码模式
const toggleResetPasswordMode = () => {
  isResetPasswordMode.value = !isResetPasswordMode.value
  isRegisterMode.value = false
  resetForms()
}

// 重置所有表单
const resetForms = () => {
  // 清空表单数据
  Object.keys(loginForm).forEach(key => {
    loginForm[key as keyof typeof loginForm] = ''
  })
  loginForm.role = 'student' // 重置角色为默认值

  Object.keys(registerForm).forEach(key => {
    registerForm[key as keyof typeof registerForm] = ''
  })

  Object.keys(resetPasswordForm).forEach(key => {
    resetPasswordForm[key as keyof typeof resetPasswordForm] = ''
  })

  // 清空消息
  errorMessage.value = ''
  successMessage.value = ''
}
</script>

<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧：登录表单 -->
      <div class="login-form">
        <div class="welcome-text">欢迎进入智慧自习室预约系统</div>
        <h2 v-if="!isResetPasswordMode" class="form-title">{{ isRegisterMode ? '注册您的账户' : '登录您的账户' }}</h2>
        <h2 v-else>重置您的密码</h2>

        <form @submit.prevent="isRegisterMode ? handleRegister() :
                            isResetPasswordMode ? handleResetPassword() : handleLogin()">
          <!-- 登录表单 -->
          <div v-if="!isRegisterMode && !isResetPasswordMode">
            <div class="form-group">
              <label for="id">学号/工号:</label>
              <input
                type="text"
                id="id"
                v-model="loginForm.id"
                required
                placeholder="请输入学号或工号"
              />
            </div>

            <div class="form-group">
              <label for="password">密码:</label>
              <input
                type="password"
                id="password"
                v-model="loginForm.password"
                required
                placeholder="请输入密码"
              />
            </div>

            <!-- 角色选择 -->
            <div class="form-group">
              <label>登录角色:</label>
              <div class="role-selection">
                <label class="role-option">
                  <input 
                    type="radio" 
                    v-model="loginForm.role" 
                    value="student" 
                  />
                  学生
                </label>
                <label class="role-option">
                  <input 
                    type="radio" 
                    v-model="loginForm.role" 
                    value="admin" 
                  />
                  管理员
                </label>
              </div>
            </div>

            <div class="form-group">
              <button type="button" class="forgot-password-button" @click="toggleResetPasswordMode">
                忘记密码？
              </button>
            </div>
          </div>

          <!-- 注册表单 -->
          <div v-else-if="isRegisterMode">
            <div class="form-group">
              <label for="reg-name">姓名:</label>
              <input
                type="text"
                id="reg-name"
                v-model="registerForm.name"
                required
                placeholder="请输入姓名"
              />
            </div>

            <div class="form-group">
              <label for="reg-id">学号:</label>
              <input
                type="text"
                id="reg-id"
                v-model="registerForm.id"
                required
                placeholder="请输入学号"
              />
            </div>

            <div class="form-group">
              <label for="reg-phone">电话号码:</label>
              <input
                type="text"
                id="reg-phone"
                v-model="registerForm.phone"
                required
                placeholder="请输入电话号码"
              />
            </div>

            <div class="form-group">
              <label for="reg-college">学院:</label>
              <input
                type="text"
                id="reg-college"
                v-model="registerForm.college"
                required
                placeholder="请输入学院"
              />
            </div>

            <div class="form-group">
              <label for="reg-grade">年级:</label>
              <input
                type="text"
                id="reg-grade"
                v-model="registerForm.grade"
                required
                placeholder="请输入入学年份"
              />
            </div>

            <div class="form-group">
              <label for="reg-password">密码:</label>
              <input
                type="password"
                id="reg-password"
                v-model="registerForm.password"
                required
                placeholder="请输入密码"
              />
            </div>

            <div class="form-group">
              <label for="confirm-password">确认密码:</label>
              <input
                type="password"
                id="confirm-password"
                v-model="registerForm.confirmPassword"
                required
                placeholder="请再次输入密码"
              />
            </div>
          </div>

          <!-- 重置密码表单 -->
          <div v-else-if="isResetPasswordMode">
            <div class="form-group">
              <label for="reset-id">学号:</label>
              <input
                type="text"
                id="reset-id"
                v-model="resetPasswordForm.id"
                required
                placeholder="请输入学号"
              />
            </div>

            <div class="form-group">
              <label for="reset-phone">电话号码:</label>
              <input
                type="text"
                id="reset-phone"
                v-model="resetPasswordForm.phone"
                required
                placeholder="请输入电话号码"
              />
            </div>

            <div class="form-group">
              <label for="new-password">新密码:</label>
              <input
                type="password"
                id="new-password"
                v-model="resetPasswordForm.newPassword"
                required
                placeholder="请输入新密码"
              />
            </div>

            <div class="form-group">
              <label for="confirm-new-password">确认新密码:</label>
              <input
                type="password"
                id="confirm-new-password"
                v-model="resetPasswordForm.confirmNewPassword"
                required
                placeholder="请再次输入新密码"
              />
            </div>
          </div>

          <!-- 提交按钮 -->
          <div class="form-group">
            <button
              v-if="!isResetPasswordMode"
              type="submit"
              :disabled="loading"
            >
              {{ loading ?
                (isRegisterMode ? '注册中...' : '登录中...') :
                (isRegisterMode ? '注册' : '登录') }}
            </button>

            <button
              v-else
              type="submit"
              :disabled="loading"
            >
              {{ loading ? '重置中...' : '重置密码' }}
            </button>
          </div>

          <!-- 模式切换按钮 -->
          <div class="form-group">
            <button
              v-if="!isResetPasswordMode"
              type="button"
              class="toggle-button"
              @click="toggleMode"
            >
              {{ isRegisterMode ? '已有账户？去登录' : '没有账户？去注册' }}
            </button>

            <button
              v-else
              type="button"
              class="toggle-button"
              @click="toggleResetPasswordMode"
            >
              返回登录
            </button>
          </div>

          <!-- 消息显示 -->
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="success-message">
            {{ successMessage }}
          </div>
        </form>
      </div>


      <!-- 右侧：宣传图 + 标语 -->
      <div class="hero-section">
        <div class="hero-image">
          <img src="@/assets/images/office1.png" alt="办公场景" />
        </div>
        <div class="hero-text">
          <h3>预约自习室的通用解决方案</h3>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  gap: 0.5rem !important;
  padding: 0 0.5rem !important;
}

.login-form {
  width: 400px;
  padding: 2rem;
  background: transparent;
  border-radius: 8px;
  box-shadow: none;
  flex-shrink: 0;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  gap: 0.5rem;
}

.welcome-text {
  text-align: center;
  font-size: 2rem !important;
  color: #778873 !important;
  margin-bottom: 1.5rem;
  font-weight: bold;
}

.form-title {
  text-align: left;
  color: #909399;
  font-size: 1rem;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.9);
}

.form-group input:focus {
  outline: none;
  border-color: #409eff;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-group button {
  width: 100%;
  padding: 0.75rem;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.form-group button:hover:not(:disabled) {
  background-color: #337ecc;
}

.form-group button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.toggle-button {
  background-color: #67c23a !important;
}

.toggle-button:hover:not(:disabled) {
  background-color: #529b2e !important;
}

.form-group button.forgot-password-button {
  background: none;
  border: none;
  color: #909399;
  text-decoration: underline;
  cursor: pointer;
  padding: 0;
  font-size: 0.85rem;
  width: auto;
  float: right;
  margin-top: 0.5rem;
  font-weight: normal;
  font-family: inherit;
}


.form-group button.forgot-password-button:hover {
  color: white;
  text-decoration: underline; /* 悬停时显示下划线 */
}

/* 清除浮动 */
.form-group::after {
  content: "";
  display: table;
  clear: both;
}

.error-message {
  color: #f56c6c;
  text-align: center;
  margin-top: 1rem;
  font-size: 0.9rem;
}

.success-message {
  color: #67c23a;
  text-align: center;
  margin-top: 1rem;
  font-size: 0.9rem;
}

/* 角色选择样式 */
.role-selection {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.role-option {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.role-option input {
  width: auto;
  margin-right: 0.5rem;
}

.hero-section {
  flex: 1.5;
  margin-left: 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 2rem;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  max-width: 900px;
  height: fit-content;
  background: transparent;
  box-shadow: none;
  flex-shrink: 0;
}

.hero-image {
  position: relative;
  z-index: 1;
  margin-bottom: 1.5rem;
  width: 100%;
}

.hero-image img {
  width: 100%;
  max-width: none;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  object-fit: cover;
  background: transparent;
}

.hero-text {
  position: relative;
  z-index: 2;
  text-align: center;
}

.hero-text h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: #000;
}

.hero-text p {
  font-size: 1rem;
  color: #ccc;
  margin: 0.5rem 0;
}

</style>