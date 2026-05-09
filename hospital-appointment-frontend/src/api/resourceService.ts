import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:3000/api', // 基础 URL
  timeout: 10000, // 超时时间
})

// 定义资源类型
export interface StudyRoom {
  studyRoomID: number
  studyRoomCapacity: number
  studyRoomLocation: string
  studyRoomType: number
  studyRoomOpentime: string
  studyRoomClosetime: string
  status: number
  currentlyIdleSeat: number //当前自习室的空闲座位数量
  studyRoomName: string
}


export interface SeminarRoom {
  seminarRoomID: number
  seminarRoomLocation: string
  seminarRoomMin: number //研讨室最低预约人数
  seminarRoomMax: number
  seminarRoomStatus: number
  currentNum: number // 当前占用研讨室的人数
  seminarRoomOpentime: string
  seminarRoomClosetime: string
  seminarRoomName: string
}


export interface Seat {
  seatID: number // 座位ID
  seatLocation?: string // 座位位置描述
  seatType: number // 座位类型：0-通用座位，1-专用座位
  seatBelonging: number // 座位所属自习室ID
  seatNumber: number // 座位号
  seatStatus: number // 0-可预约，1-已预约，2-未签到，3-已占用，4-暂离，5-维修中
}

// 违规记录类型
export interface ViolationRecord {
    violationRecordID: string
    studentID: number | null
    adminID?: number
    violationType: number // 违规类型：1-违规占座，2-签到超时，3-暂离超时，4-研讨室人数不足，5-未签退，6-早退
    details?: string // 违规的具体内容
    deductPoints: number // 扣除的信用积分
    violationTime?: string // 违规产生的时间
    status: number // 处理状态：1-已生成，2-待申诉，3-已确认，4-申诉中，5-已撤销
    reservationRecordID?: string
    attendanceRecordID?: number
    appealReason?: string // 申诉理由
    appealTime: string // 申诉时间
    processedTime?: string // 处理时间
    createTime?: string // 记录创建时间
}

// 后端统一响应类型
export interface BackendResponse<T> {
  code: number
  message: string
  data: T | null
}

// 查询违规记录返回的 data 结构
export interface ViolationQueryResult {
  total: number
  violations: ViolationRecord[]
}

export const RESOURCE_TYPE = {
    STUDY_ROOM: 0, // 自习室类型
    SEMINAR_ROOM: 1, // 研讨室类型
};

// 自习室具体类型
export const STUDY_ROOM_SUBTYPE = {
    GENERAL: 0, // 通用自习室
    DEDICATED: 1, // 专用自习室 (如考研教室)
};

// 资源类型文本转换函数
export const getResourceTypeText = (type: number): string => {
    if (type === RESOURCE_TYPE.STUDY_ROOM) {
        return '自习室';
    }
    if (type === RESOURCE_TYPE.SEMINAR_ROOM) {
        return '研讨室';
    }
    return '未知资源';
};

// 自习室子类型文本转换函数
export const getStudyRoomSubtypeText = (subtype: number): string => {
    if (subtype === STUDY_ROOM_SUBTYPE.GENERAL) {
        return '通用自习室';
    }
    if (subtype === STUDY_ROOM_SUBTYPE.DEDICATED) {
        return '专用自习室';
    }
    return '未知类型';
};

// API服务
export const resourceService = {
  // 获取自习室列表
  getStudyRooms: () => axios.get('/api/studyRoomManage/rooms'),

  // 获取研讨室列表
  getSeminarRooms: () => axios.get('/api/seminarRoomManage/seminarRooms'),

  // 获取座位列表（基于自习室ID）
  getSeats: (roomId: number) => axios.get(`/api/seatManage/seats/${roomId}`),

  // 添加自习室（不包含主键字段）
  addStudyRoom: (room: Omit<StudyRoom, 'studyRoomID'>) => axios.post('/api/studyRoomManage/addStudyRoom', room),

  // 添加研讨室（不包含主键字段）
  addSeminarRoom: (room: Omit<SeminarRoom, 'seminarRoomID'>) => axios.post('/api/seminarRoomManage/addSeminarRoom', room),

  // 更新自习室
  updateStudyRoom: (id: number, room: Omit<StudyRoom, 'studyRoomID'>) => axios.put(`/api/studyRoomManage/updateStudyRoom/${id}`, room),

  // 更新研讨室
  updateSeminarRoom: (id: number, room: Omit<SeminarRoom, 'seminarRoomID'>) => axios.put(`/api/seminarRoomManage/updateSeminarRoom/${id}`, room),

  // 删除自习室
  deleteStudyRoom: (id: number) => axios.delete(`/api/studyRoomManage/deleteStudyRoom/${id}`),

  // 删除研讨室
  deleteSeminarRoom: (id: number) => axios.delete(`/api/seminarRoomManage/deleteSeminarRoom/${id}`),

  // 更新座位状态
  updateSeat: (seatId: number, status: number) => axios.put(`/api/seatManage/updateSeat/${seatId}`, { status }),

  // 保存座位更改
  saveSeats: (roomId: number, seats: Seat[]) => axios.put<Seat[]>(`/admin-manage/studyRoom/${roomId}/seats`, seats),

  // 自习室列表
  getStudyRoomss: () => axios.get<StudyRoom[]>("/api/studyRoomManage/rooms"),

  // 研讨室列表
  getSeminarRoomss: () => axios.get<SeminarRoom[]>("/api/seminar-room/all"),

  // 座位列表
  getSeatsByRoomId: (roomId: number) =>
    axios.get(`/api/seatManage/seats/${roomId}`),
}

// 违规相关 API
export const violationService = {
  // 获取违规列表
  getViolationReports: () => apiClient.get<BackendResponse<ViolationRecord[]>>('/violation/all'),

  // 管理员添加一条违规记录
  addViolationRecord: (violationRecord: ViolationRecord) =>
    apiClient.post('/violation/add', violationRecord),

  // 查询违规记录
  satisfiesionRecords: (filters: {
    studentId?: number
    adminId?: number
    status?: number
    violationType?: number
  }) => apiClient.get<BackendResponse<ViolationQueryResult>>('/violation/query', { params: filters }),

  
  // 批量确认违规记录
  batchConfirmViolations: (violationRecordIds: string[]) =>
    apiClient.put('/api/violation/batch-confirm', violationRecordIds),

  // 删除违规信息
  deleteViolation: (violationId: string) => 
    apiClient.delete<BackendResponse<null>>('/violation/delete', { data: violationId }),

  // 可选：通知学生
  notifyStudent: (studentId: string, message: string) => apiClient.post(`/students/${studentId}/notify`, { message })
}


export const AppealService = {
  // 通过申诉
  approveAppeal: (violationRecord: ViolationRecord) => apiClient.put('/api/violation/approve', violationRecord),
  
  // 驳回申诉
  rejectAppeal: (violationRecord: ViolationRecord) => apiClient.post(`/violation/reject`, violationRecord),
}