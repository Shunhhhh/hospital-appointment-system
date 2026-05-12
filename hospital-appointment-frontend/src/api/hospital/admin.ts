/**
 * 管理员管理 API
 */

import request from '../request'

export interface Admin {
  adminID: number
  adminPassword: string
  adminPosition: string
  adminPermission: number
  adminPhoneNumber: string
  adminName: string
}

export const adminAPI = {
  /**
   * 管理员登录
   */
  login: (adminID: number, password: string) => {
    return request.post<Admin>('/hospital/admin/login', null, { params: { adminID, password } })
  }
}
