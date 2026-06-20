/**
 * 通知消息 API
 */

import request from '../request'

export interface Notification {
  notificationID: string
  patientID?: number
  doctorID?: number
  adminID?: number
  notificationType: number
  title: string
  notificationContent: string
  relatedID?: string
  notificationStatus: number
  sendTime: string
  readTime?: string
  expireTime?: string
}

export const notificationAPI = {
  getAll: () =>
    request.get<Notification[]>('/hospital/notification/all'),

  getLatest: (limit = 5) =>
    request.get<Notification[]>('/hospital/notification/latest', { params: { limit } }),

  getByFilter: (patientId?: number, type?: number) =>
    request.get<Notification[]>('/hospital/notification/filter', { params: { patientId, type } }),

  getById: (id: string) =>
    request.get<Notification>(`/hospital/notification/${id}`),

  publish: (data: Partial<Notification>) =>
    request.post<string>('/hospital/notification/publish', data),

  markRead: (id: string) =>
    request.put<string>(`/hospital/notification/${id}/read`),

  delete: (id: string) =>
    request.delete<string>(`/hospital/notification/${id}`)
}
