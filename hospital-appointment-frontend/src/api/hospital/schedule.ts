/**
 * 排班管理 API
 */

import request from '../request'

export interface DoctorSchedule {
  scheduleID: number
  doctorID: number
  doctorName?: string
  doctorTitle?: string
  departmentID?: number
  departmentName?: string
  scheduleDate: string
  timeSlot: number
  startTime: string
  endTime: string
  totalSlots: number
  remainingSlots: number
  registeredSlots: number
  price: number
  registrationType: number
  scheduleStatus: number
}

export interface ScheduleQuery {
  doctorId?: number
  departmentId?: number
  startDate?: string
  endDate?: string
}

export const scheduleAPI = {
  /**
   * 获取所有排班（管理员）
   */
  getAll: () => {
    return request.get<DoctorSchedule[]>('/hospital/schedule/admin/all')
  },

  /**
   * 获取医生排班列表
   */
  getByDoctor: (doctorId: number) => {
    return request.get<DoctorSchedule[]>(`/hospital/schedule/doctor/${doctorId}`)
  },

  /**
   * 获取指定日期的排班
   */
  getByDate: (date: string) => {
    return request.get<DoctorSchedule[]>(`/hospital/schedule/date/${date}`)
  },

  /**
   * 获取科室下所有医生的排班
   */
  getByDepartment: (departmentId: number, startDate?: string, endDate?: string) => {
    return request.get<DoctorSchedule[]>(`/hospital/schedule/department/${departmentId}`, {
      params: { startDate, endDate }
    })
  },

  /**
   * 获取可用号源
   */
  getAvailable: (query: ScheduleQuery) => {
    return request.get<DoctorSchedule[]>('/hospital/schedule/available', { params: query })
  },

  /**
   * 获取排班详情
   */
  getById: (id: number) => {
    return request.get<DoctorSchedule>(`/hospital/schedule/${id}`)
  },

  /**
   * 创建排班
   */
  add: (data: Partial<DoctorSchedule>) => {
    return request.post<string>('/hospital/schedule', data)
  },

  /**
   * 批量创建排班
   */
  addBatch: (data: Partial<DoctorSchedule>[]) => {
    return request.post<string>('/hospital/schedule/batch', data)
  },

  /**
   * 更新排班
   */
  update: (id: number, data: Partial<DoctorSchedule>) => {
    return request.put<string>(`/hospital/schedule/${id}`, data)
  },

  /**
   * 删除排班
   */
  delete: (id: number) => {
    return request.delete<string>(`/hospital/schedule/${id}`)
  },

  /**
   * 停诊
   */
  stop: (id: number) => {
    return request.put<string>(`/hospital/schedule/${id}/stop`)
  }
}
