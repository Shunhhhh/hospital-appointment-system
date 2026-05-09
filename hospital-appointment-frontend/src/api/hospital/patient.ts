/**
 * 患者管理 API
 */

import request from '../request'

export interface Patient {
  patientID: number
  patientName: string
  idCard: string
  patientGender: number
  patientBirthday?: string
  patientPhone: string
  patientEmail?: string
  patientAddress?: string
  medicalCardNo?: string
  insuranceType?: number
  insuranceNo?: string
  allergyHistory?: string
  medicalHistory?: string
  emergencyContact?: string
  emergencyPhone?: string
  creditScore: number
  noshowCount: number
  isBlacklist: number
  patientStatus: number
}

export const patientAPI = {
  /**
   * 患者注册
   */
  register: (data: Partial<Patient>) => {
    return request.post<string>('/hospital/patient/register', data)
  },

  /**
   * 患者登录
   */
  login: (phone: string, password: string) => {
    return request.post<Patient>('/hospital/patient/login', null, { params: { phone, password } })
  },

  /**
   * 获取患者信息
   */
  getById: (id: number) => {
    return request.get<Patient>(`/hospital/patient/${id}`)
  },

  /**
   * 更新患者信息
   */
  update: (id: number, data: Partial<Patient>) => {
    return request.put<string>(`/hospital/patient/${id}`, data)
  },

  /**
   * 修改密码
   */
  changePassword: (id: number, oldPassword: string, newPassword: string) => {
    return request.put<string>(`/hospital/patient/${id}/password`, null, {
      params: { oldPassword, newPassword }
    })
  },

  /**
   * 检查是否在黑名单
   */
  isInBlacklist: (id: number) => {
    return request.get<boolean>(`/hospital/patient/${id}/blacklist`)
  },

  /**
   * 获取患者信用积分
   */
  getCreditScore: (id: number) => {
    return request.get<number>(`/hospital/patient/${id}/credit`)
  }
}
