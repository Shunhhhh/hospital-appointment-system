/**
 * 科室管理 API
 */

import request from '../request'

export interface Department {
  departmentID: number
  departmentName: string
  departmentType: number
  departmentLocation: string
  departmentDesc?: string
  departmentIcon?: string
  departmentStatus: number
  displayOrder: number
}

export const departmentAPI = {
  /**
   * 获取科室列表
   */
  getList: () => {
    return request.get<Department[]>('/hospital/department/list')
  },

  /**
   * 获取科室详情
   */
  getById: (id: number) => {
    return request.get<Department>(`/hospital/department/${id}`)
  },

  /**
   * 根据科室类型获取科室列表
   */
  getByType: (type: number) => {
    return request.get<Department[]>(`/hospital/department/type/${type}`)
  },

  /**
   * 添加科室
   */
  add: (data: Partial<Department>) => {
    return request.post<string>('/hospital/department', data)
  },

  /**
   * 更新科室
   */
  update: (id: number, data: Partial<Department>) => {
    return request.put<string>(`/hospital/department/${id}`, data)
  },

  /**
   * 删除科室
   */
  delete: (id: number) => {
    return request.delete<string>(`/hospital/department/${id}`)
  },

  /**
   * 更新科室状态
   */
  updateStatus: (id: number, status: number) => {
    return request.put<string>(`/hospital/department/${id}/status`, { status })
  }
}
