/**
 * 就医评价 API
 */

import request from '../request'

export interface Review {
  reviewID: string
  patientID: number
  patientName?: string
  doctorID: number
  doctorName?: string
  appointmentID: string
  departmentID: number
  departmentName?: string
  visitID?: number
  overallRating: number
  attitudeRating?: number
  skillRating?: number
  environmentRating?: number
  reviewContent?: string
  reviewImages?: string
  replyContent?: string
  replyTime?: string
  isAnonymous: number
  reviewStatus: number
  createTime: string
}

export interface CreateReviewData {
  patientID: number
  doctorID: number
  appointmentID: string
  departmentID: number
  visitID?: number
  overallRating: number
  attitudeRating?: number
  skillRating?: number
  environmentRating?: number
  reviewContent?: string
  reviewImages?: string
  isAnonymous?: number
}

export const reviewAPI = {
  /**
   * 提交评价
   */
  submit: (data: CreateReviewData) => {
    return request.post<string>('/hospital/review', data)
  },

  /**
   * 获取医生的评价列表
   */
  getByDoctor: (doctorId: number, page: number = 1, size: number = 10) => {
    return request.get<Review[]>(`/hospital/review/doctor/${doctorId}`, {
      params: { page, size }
    })
  },

  /**
   * 获取患者的历史评价
   */
  getByPatient: (patientId: number) => {
    return request.get<Review[]>(`/hospital/review/patient/${patientId}`)
  },

  /**
   * 获取评价详情
   */
  getById: (id: string) => {
    return request.get<Review>(`/hospital/review/${id}`)
  },

  /**
   * 获取医生的平均评分
   */
  getAverageRating: (doctorId: number) => {
    return request.get<number>(`/hospital/review/doctor/${doctorId}/rating`)
  },

  /**
   * 医生回复评价
   */
  reply: (id: string, reply: string) => {
    return request.put<string>(`/hospital/review/${id}/reply`, null, {
      params: { reply }
    })
  },

  /**
   * 审核评价（管理员）
   */
  audit: (id: string, status: number) => {
    return request.put<string>(`/hospital/review/${id}/audit`, null, { params: { status } })
  },

  /**
   * 获取待审核评价列表（管理员）
   */
  getPending: () => {
    return request.get<Review[]>('/hospital/review/pending')
  }
}
