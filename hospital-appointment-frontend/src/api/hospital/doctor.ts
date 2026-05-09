/**
 * 医生管理 API
 */

import request from '../request'

export interface Doctor {
  doctorID: number
  doctorName: string
  doctorGender: number
  doctorPhone: string
  doctorEmail?: string
  departmentID: number
  departmentName?: string
  title: string
  specialty?: string
  doctorIntro?: string
  doctorPhoto?: string
  registrationFee: number
  doctorStatus: number
}

export const doctorAPI = {
  /**
   * 获取医生列表
   */
  getList: () => {
    return request.get<Doctor[]>('/hospital/doctor/list')
  },

  /**
   * 获取医生详情
   */
  getById: (id: number) => {
    return request.get<Doctor>(`/hospital/doctor/${id}`)
  },

  /**
   * 根据科室获取医生列表
   */
  getByDepartment: (departmentId: number) => {
    return request.get<Doctor[]>(`/hospital/doctor/department/${departmentId}`)
  },

  /**
   * 根据职称获取医生列表
   */
  getByTitle: (title: string) => {
    return request.get<Doctor[]>(`/hospital/doctor/title/${title}`)
  },

  /**
   * 搜索医生
   */
  search: (keyword: string) => {
    return request.get<Doctor[]>('/hospital/doctor/search', { keyword })
  },

  /**
   * 医生登录
   */
  login: (phone: string, password: string) => {
    return request.post<Doctor>('/hospital/doctor/login', null, { params: { phone, password } })
  },

  /**
   * 添加医生
   */
  add: (data: Partial<Doctor>) => {
    return request.post<string>('/hospital/doctor', data)
  },

  /**
   * 更新医生信息
   */
  update: (id: number, data: Partial<Doctor>) => {
    return request.put<string>(`/hospital/doctor/${id}`, data)
  },

  /**
   * 删除医生
   */
  delete: (id: number) => {
    return request.delete<string>(`/hospital/doctor/${id}`)
  },

  /**
   * 更新医生状态
   */
  updateStatus: (id: number, status: number) => {
    return request.put<string>(`/hospital/doctor/${id}/status`, { status })
  }
}
