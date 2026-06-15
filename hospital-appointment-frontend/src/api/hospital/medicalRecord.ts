/**
 * 门诊病历 API
 */

import request from '../request'

export interface MedicalRecord {
  recordID: number
  visitID: number
  appointmentID: string
  patientID: number
  doctorID: number
  doctorName?: string
  patientName?: string
  departmentName?: string
  chiefComplaint?: string
  presentIllness?: string
  pastHistory?: string
  allergyHistory?: string
  physicalExamination?: string
  auxiliaryExamination?: string
  preliminaryDiagnosis?: string
  finalDiagnosis?: string
  treatmentPlan?: string
  medicalAdvice?: string
  remarks?: string
  createTime: string
  updateTime?: string
}

export interface Prescription {
  prescriptionID: number
  recordID: number
  visitID: number
  patientID: number
  doctorID: number
  medicineName: string
  medicineSpec?: string
  dosage?: string
  usage?: string
  frequency?: string
  course?: string
  quantity?: string
  unit?: string
  price?: number
  totalPrice?: number
  remarks?: string
  prescriptionType?: number
  status?: number
  createTime?: string
}

export interface RecordDetail {
  record: MedicalRecord
  prescriptions: Prescription[]
}

export const medicalRecordAPI = {
  /**
   * 获取患者病历列表
   */
  getByPatient: (patientId: number) => {
    return request.get<MedicalRecord[]>(`/hospital/medical-record/patient/${patientId}`)
  },

  /**
   * 获取病历详情（含处方）
   */
  getDetail: (recordId: number) => {
    return request.get<RecordDetail>(`/hospital/medical-record/${recordId}`)
  },

  /**
   * 保存病历
   */
  save: (data: Partial<MedicalRecord>) => {
    return request.post<MedicalRecord>('/hospital/medical-record', data)
  }
}
