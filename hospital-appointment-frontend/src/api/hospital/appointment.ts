/**
 * 挂号预约 API
 */

import request from '../request'

export interface Appointment {
  appointmentID: string
  patientID: number
  patientName?: string
  scheduleID: number
  doctorID: number
  doctorName?: string
  departmentID: number
  departmentName?: string
  appointmentDate: string
  timeSlot: number
  appointmentNumber: number
  chiefComplaint?: string
  appointmentStatus: number
  paymentStatus: number
  paymentAmount: number
  paymentMethod?: string
  paymentTime?: string
  cancelReason?: string
  cancelTime?: string
  isReviewed: number
  createTime: string
  scheduleStartTime?: string
  scheduleEndTime?: string
}

export interface CreateAppointmentData {
  patientID: number
  scheduleID: number
  appointmentDate: string
  timeSlot: number
  chiefComplaint?: string
}

export const appointmentAPI = {
  /**
   * 获取所有挂号记录（管理员）
   */
  getAll: () => {
    return request.get<Appointment[]>('/hospital/appointment/admin/all')
  },

  /**
   * 创建挂号
   */
  create: (data: CreateAppointmentData) => {
    return request.post<string>('/hospital/appointment', data)
  },

  /**
   * 获取患者的所有挂号记录
   */
  getByPatient: (patientId: number, status?: number) => {
    return request.get<Appointment[]>(`/hospital/appointment/patient/${patientId}`, {
      params: status !== undefined ? { status } : {}
    })
  },

  /**
   * 获取医生的所有挂号记录
   */
  getByDoctor: (doctorId: number, status?: number) => {
    return request.get<Appointment[]>(`/hospital/appointment/doctor/${doctorId}`, {
      params: status !== undefined ? { status } : {}
    })
  },

  /**
   * 获取挂号详情
   */
  getById: (id: string) => {
    return request.get<Appointment>(`/hospital/appointment/${id}`)
  },

  /**
   * 取消挂号
   */
  cancel: (id: string, reason?: string) => {
    return request.put<string>(`/hospital/appointment/${id}/cancel`, null, {
      params: { reason }
    })
  },

  /**
   * 签到
   */
  checkIn: (id: string) => {
    return request.put<string>(`/hospital/appointment/${id}/checkin`)
  },

  /**
   * 支付成功回调
   */
  paySuccess: (id: string, paymentMethod: string) => {
    return request.put<string>(`/hospital/appointment/${id}/pay`, null, {
      params: { paymentMethod }
    })
  },

  /**
   * 确认就诊
   */
  confirm: (id: string) => {
    return request.put<string>(`/hospital/appointment/${id}/confirm`)
  },

  /**
   * 完成就诊
   */
  finish: (id: string) => {
    return request.put<string>(`/hospital/appointment/${id}/finish`)
  },

  /**
   * 获取当日挂号列表
   */
  getToday: (doctorId: number) => {
    return request.get<Appointment[]>('/hospital/appointment/today', { params: { doctorId } })
  },

  /**
   * 获取明日挂号数
   */
  getTomorrowCount: (doctorId: number) => {
    return request.get<number>('/hospital/appointment/tomorrow/count', { params: { doctorId } })
  }
}
