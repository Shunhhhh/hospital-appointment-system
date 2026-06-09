import request from '../request'
import type { FeedbackRecord } from '@/api/feedback'

export interface HospitalUser {
  type?: string
  patientID?: number
  patientName?: string
  patientPhone?: string
}

export interface FeedbackPageResult {
  total: number
  records: FeedbackRecord[]
}

export interface SubmitFeedbackPayload {
  businessType: number
  occurredAt: string
  title: string
  detail: string
}

const businessTypeLabels: Record<number, string> = {
  1: '积分',
  2: '挂号',
  3: '报告查询',
  4: '问诊'
}

function getHospitalUser(): HospitalUser | null {
  try {
    const raw = localStorage.getItem('hospital_user')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function getCurrentPatientId(): number | undefined {
  const user = getHospitalUser()
  return user?.patientID ? Number(user.patientID) : undefined
}

export function getCurrentPatientPhone(): string {
  return getHospitalUser()?.patientPhone || ''
}

export async function getPatientFeedbacks(params?: {
  status?: number
  type?: number
  keyword?: string
  page?: number
  size?: number
}) {
  const patientID = getCurrentPatientId()
  if (!patientID) throw new Error('未获取到患者信息，请重新登录')

  const query = {
    studentId: patientID,
    processStatus: params?.status,
    feedbackType: params?.type,
    keyword: params?.keyword,
    page: params?.page,
    size: params?.size
  }

  Object.keys(query).forEach((key) => {
    const typedKey = key as keyof typeof query
    if (query[typedKey] === undefined || query[typedKey] === '') {
      delete query[typedKey]
    }
  })

  const res: any = await request.get('/feedback/student', { params: query })
  const data = res?.data?.data ?? res?.data ?? {}
  const records = Array.isArray(data?.feedbacks) ? data.feedbacks : []

  return {
    total: Number(data?.total || records.length),
    records
  } satisfies FeedbackPageResult
}

export async function submitPatientFeedback(payload: SubmitFeedbackPayload) {
  const patientID = getCurrentPatientId()
  if (!patientID) throw new Error('未获取到患者信息，请重新登录')

  const patientPhone = getCurrentPatientPhone()
  const feedbackContent = [
    `业务类型：${businessTypeLabels[payload.businessType] || payload.businessType}`,
    `发生时间：${payload.occurredAt}`,
    `问题标题：${payload.title}`,
    '详细问题：',
    payload.detail.trim()
  ].join('\n')

  return request.post('/feedback/submit', {
    studentID: patientID,
    feedbackType: payload.businessType,
    feedbackContent,
    contactInfo: patientPhone,
    processStatus: 1
  })
}
