import axios from "axios";

// 定义资源类型
export interface StudyRoom {
  id: number
  roomNumber: string
  seatCount: number
  openTime: string
  closeTime: string
  status: string
  capacity: number
  location: string
}

export interface SeminarRoom {
  id: number
  roomNumber: string
  openTime: string
  closeTime: string
  status: string
  capacity: number
  location: string
}

export type SeatStatus = 'available' | 'occupied' | 'maintenance' | 'closed'

export interface Seat {
  id: number
  number: number
  status: SeatStatus
  belonging: string
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface FeedbackCreateDTO {
  studentID: number;       
  feedbackType: number;    // 1-举报 2-建议 3-投诉 4-报修
  feedbackContent: string;
  priority?: number;      
  contactInfo?: string;
  relatedResourceID?: string;
  processStatus?: number; // 1-待处理，2-处理中，3-已回复，4-已关闭
}

export interface FeedbackRecord {
  feedbackID: string;
  studentID: number;
  processAdminID?: number | null;

  feedbackType: number;
  feedbackContent: string;

  processStatus: number; // 0/1 待处理 2处理中 3已回复 4已关闭
  feedbackTime: string;

  replyContent?: string | null;
  replyTime?: string | null;

  contactInfo?: string | null;
  relatedResourceID?: string | null;
}

export interface PageResult<T> {
  records: T[];
  total: number;
}

export interface StudentFeedbackResult {
  total: number;
  feedbacks: FeedbackRecord[] | null; 
}


const http = axios.create({
  baseURL: "", 
  timeout: 15000,
});

// Bearer Token
http.interceptors.request.use((config) => {
  const token = localStorage.getItem("token") || localStorage.getItem("authToken");
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});


export function getLocalStudentId(): number | undefined {
  try {
    const s1 = localStorage.getItem("studentInfo");
    if (s1) {
      const obj = JSON.parse(s1);
      const id = obj?.studentID ?? obj?.studentId;
      if (id != null) return Number(id);
    }

    const s2 = localStorage.getItem("student");
    if (s2) {
      const obj = JSON.parse(s2);
      const id =
        obj?.studentInfo?.studentID ??
        obj?.studentInfo?.studentId ??
        obj?.studentID ??
        obj?.studentId;
      if (id != null) return Number(id);
    }

    const s3 = localStorage.getItem("userId");
    if (s3 != null) return Number(s3);
  } catch {
    // ignore
  }
  return undefined;
}


export async function createFeedback(dto: Partial<FeedbackCreateDTO>) {

  const studentID = (dto as any).studentID ?? getLocalStudentId();
  if (!studentID) throw new Error("未获取到 studentID，请重新登录");

  const payload: FeedbackCreateDTO = {
    studentID: Number(studentID),
    feedbackType: Number(dto.feedbackType),
    feedbackContent: String(dto.feedbackContent ?? ""),
    priority: dto.priority,
    contactInfo: dto.contactInfo,
    relatedResourceID: dto.relatedResourceID,
    processStatus: 1, // 提交时固定为“待处理”
  };

  const res = await http.post<ApiResponse<FeedbackRecord>>("/api/feedback/submit", payload);
  if (res.data.code !== 200) throw new Error(res.data.message || "提交失败");
  return res.data.data;
}

/** GET /api/feedback/my?studentId=... */
export async function getMyFeedback(_params?: {
  status?: number;
  type?: number;
  keyword?: string;
  page?: number;
  size?: number;
}) {
  const studentId = getLocalStudentId();
  if (!studentId) throw new Error("未获取到 studentID，请重新登录");

  const params = {
  studentId,
  processStatus: _params?.status,
  feedbackType: _params?.type,
  keyword: _params?.keyword,
  page: _params?.page,
  size: _params?.size,
};


  Object.keys(params).forEach((k) => {
    const key = k as keyof typeof params;
    if (params[key] === undefined || params[key] === "") delete params[key];
  });

  const res = await http.get<ApiResponse<StudentFeedbackResult>>("/api/feedback/student", {
    params,
  });

  if (res.data.code !== 200) throw new Error(res.data.message || "加载失败");

  const data = res.data.data;
  const records = (data?.feedbacks ?? []) as FeedbackRecord[];
  const total = data?.total ?? records.length;

  const pageResult: PageResult<FeedbackRecord> = { records, total };
  return pageResult;
}



export async function getFeedbackDetail(feedbackID: string) {
  const { records } = await getMyFeedback({ page: 1, size: 1000 }); // 视后端上限调整
  const found = records.find((x) => String(x.feedbackID) === String(feedbackID));
  if (!found) throw new Error("加载详情失败：未找到该反馈记录");
  return found;
}

// API服务
export const resourceService = {

  // 自习室列表
  getStudyRoomss: () => axios.get<StudyRoom[]>("/api/studyRoomManage/rooms"),

  // 研讨室列表
  getSeminarRoomss: () => axios.get<SeminarRoom[]>("/api/seminar-room/all"),

  // 座位列表
  getSeatsByRoomId: (roomId: number) =>
    axios.get(`/api/seatManage/seats/${roomId}`),

}
