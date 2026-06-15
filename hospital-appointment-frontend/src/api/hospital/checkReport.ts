/**
 * 检查报告 API
 */

import request from '../request'

export interface CheckReport {
  reportID: number
  patientID: number
  doctorID: number
  departmentID: number
  appointmentID?: string
  reportName: string
  reportType: string
  reportContent?: string
  doctorAdvice?: string
  reportStatus: number
  checkDate?: string
  createTime: string
  doctorName?: string
  departmentName?: string
}

export const checkReportAPI = {
  /**
   * 获取患者的检查报告列表
   */
  getByPatient: (patientId: number, reportType?: string) => {
    return request.get<CheckReport[]>(`/hospital/report/patient/${patientId}`, {
      params: reportType ? { reportType } : {}
    })
  },

  /**
   * 获取报告详情
   */
  getById: (id: number) => {
    return request.get<CheckReport>(`/hospital/report/${id}`)
  }
}
