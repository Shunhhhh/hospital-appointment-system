import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface StudentInfo {
  studentID: number
  studentUserName: string
  studentPhoneNumber: string
  studentCollege: string
  studentGrade: number
  studentPoints: number
  avatar?: string
}

export const useStudentStore = defineStore('student', () => {
  const studentInfo = ref<StudentInfo>({
    studentID: 0,
    studentUserName: '',
    studentPhoneNumber: '',
    studentCollege: '',
    studentGrade: 0,
    studentPoints: 0,
    avatar: ''
  })

  // 存储 token，方便全局判断登录状态
  const token = ref<string>('')

  const setStudentInfo = (info: Partial<StudentInfo>) => {
    Object.assign(studentInfo.value, info)
  }

  const setToken = (newToken: string) => {
    token.value = newToken
  }

  const clearStudentInfo = () => {
    studentInfo.value = {
      studentID: 0,
      studentUserName: '',
      studentPhoneNumber: '',
      studentCollege: '',
      studentGrade: 0,
      studentPoints: 0,
      avatar: ''
    }
  }

  const fetchStudentInfo = async () => {
    try {
      // 从本地存储获取学生信息
      const localStudentInfo = localStorage.getItem('studentInfo')
      
      if (localStudentInfo) {
        const data = JSON.parse(localStudentInfo)
        setStudentInfo({
          studentID: data.studentID || data.id || 0,
          studentUserName: data.studentUserName || data.name || '',
          studentPhoneNumber: data.studentPhoneNumber || '',
          studentCollege: data.studentCollege || '',
          studentGrade: data.studentGrade || data.class || 0,
          studentPoints: data.studentPoints || data.points || 0,
          avatar: data.avatar || data.photo || ''
        })
        return studentInfo.value
      } else {
        throw new Error('本地无学生信息')
      }
    } catch (error) {
      console.error('获取本地学生信息错误:', error)
      throw error
    }
  }

  // 登出
  const logout = () => {
    // 清除学生信息
    clearStudentInfo();
    token.value = ''
    
    // 清除本地存储中的相关信息
    localStorage.removeItem('studentInfo');
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo'); 

  };

  return {
    studentInfo,
    token,
    setStudentInfo,
    setToken,
    clearStudentInfo,
    fetchStudentInfo,
    logout
  }
}, {
  persist: true 
})