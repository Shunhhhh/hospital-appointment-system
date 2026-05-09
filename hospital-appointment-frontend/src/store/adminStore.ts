import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface AdminInfo {
  adminID: number
  adminName: string
  adminPosition: string
  adminPermission: number
  adminPhoneNumber: string
  avatar?: string
}

export const useAdminStore = defineStore('admin', () => {
  const adminInfo = ref<AdminInfo>({
    adminID: 0,
    adminName: '',
    adminPosition: '',
    adminPermission: 0,
    adminPhoneNumber: '',
    avatar: ''
  })

  // 存储 token，方便全局判断登录状态
  const token = ref<string>('')

  const setAdminInfo = (info: Partial<AdminInfo>) => {
    Object.assign(adminInfo.value, info)
  }

  const setToken = (newToken: string) => {
    token.value = newToken
  }

  const clearAdminInfo = () => {
    adminInfo.value = {
        adminID: 0,
        adminName: '',
        adminPosition: '',
        adminPermission: 0,
        adminPhoneNumber: '',
        avatar: ''
    }
  }

  const fetchAdminInfo = async () => {
    try {
      // 从本地存储获取管理员信息
      const localAdminInfo = localStorage.getItem('adminInfo')
      
      if (localAdminInfo) {
        const data = JSON.parse(localAdminInfo)
        setAdminInfo({
          adminID: data.adminId || data.id || 0,
          adminName: data.adminName || data.name || '',
          adminPosition: data.adminPosition || '',
          adminPermission: data.adminPermission || 0,
          adminPhoneNumber: data.adminPhoneNumber || data.phone || '',
          avatar: data.avatar || data.photo || ''
        })
        return adminInfo.value
      } else {
        throw new Error('本地无管理员信息')
      }
    } catch (error) {
      console.error('获取本地管理员信息错误:', error)
      throw error
    }
  }

  // 登出
  const logout = () => {
    // 清除管理员信息
    clearAdminInfo();
    token.value = ''
    
    // 清除本地存储中的相关信息
    localStorage.removeItem('adminInfo');
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo'); 

  };

  return {
    adminInfo,
    token,
    setAdminInfo,
    setToken,
    clearAdminInfo,
    fetchAdminInfo,
    logout
  }
}, {
  persist: true 
})