import axios from 'axios';
import { message } from 'ant-design-vue';

// 定义通知记录接口
export interface NoticeRecord {
  notificationID: string;
  adminID?: number;
  studentID: number;
  sendTime?: string; // 通知发送时间
  notificationStatus?: number; // 查看状态：1-未查看，2-已查看
  notificationType?: number; // 通知类型：1-违规通知，2-反馈通知，3-提醒通知，4-系统通知
  notificationContent?: string; // 通知的具体内容
  title: string; // 通知标题
  relatedReservationID?: string; // 关联的预约记录ID
  readTime?: string; // 查看时间
  expireTime?: string; // 过期时间
}

// 定义查询参数接口
export interface NotificationQueryParams {
  studentId?: number;
  notificationType?: number;
  notificationStatus?: number;
  adminId?: number;
}

// 定义API响应结构
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 定义通知服务
export const notificationService = {
  /**
   * 查询通知记录
   */
  async queryNotifications(params: NotificationQueryParams) {
    try {
      const response = await axios.get<ApiResponse<any>>('/api/notification/query', {
        params
      });
      return response.data;
    } catch (error: any) {
      console.error('查询通知记录失败:', error);
      if (error.response) {
        // 服务器响应了错误状态码
        message.error(error.response.data?.message || '查询通知记录失败');
      } else if (error.request) {
        // 请求已发出但没有收到响应
        message.error('网络错误，请检查网络连接');
      } else {
        // 其他错误
        message.error('查询通知记录失败');
      }
      throw error;
    }
  },

  /**
   * 标记通知为已读
   */
  async markAsRead(notificationId: string) {
    try {
      const response = await axios.put<ApiResponse<void>>(`/api/notification/${notificationId}/read`);
      return response.data;
    } catch (error: any) {
      console.error('标记通知为已读失败:', error);
      if (error.response?.status === 404) {
        message.error('通知不存在或已被删除');
      } else if (error.response) {
        message.error(error.response.data?.message || '标记通知为已读失败');
      } else if (error.request) {
        message.error('网络错误，请检查网络连接');
      } else {
        message.error('标记通知为已读失败');
      }
      throw error;
    }
  },

  /**
   * 一键标记通知为已读
   */
  async batchMarkAsRead(studentId: number) {
    try {
      const response = await axios.put<ApiResponse<void>>('/api/notification/student/{studentId}/read-all', {
        studentId
      });
      return response.data;
    } catch (error: any) {
      console.error('一键标记通知为已读失败:', error);
      if (error.response) {
        message.error(error.response.data?.message || '一键标记通知为已读失败');
      } else if (error.request) {
        message.error('网络错误，请检查网络连接');
      } else {
        message.error('一键标记通知为已读失败');
      }
      throw error;
    }
  },

  /**
   * 删除通知
   */
  async deleteNotification(notificationId: string) {
    try {
      const response = await axios.delete<ApiResponse<void>>(`/api/notification/${notificationId}`);
      return response.data;
    } catch (error: any) {
      console.error('删除通知失败:', error);
      if (error.response) {
        message.error(error.response.data?.message || '删除通知失败');
      } else if (error.request) {
        message.error('网络错误，请检查网络连接');
      } else {
        message.error('删除通知失败');
      }
      throw error;
    }
  },

  // 删除所有
  async batchDeleteAll(studentId: number) {
    try {
      const response = await axios.delete<ApiResponse<void>>(`/api/notification/student/${studentId}`);
      return response.data;
    } catch (error: any) {
      console.error('删除学生所有通知失败:', error);
      if (error.response) {
        message.error(error.response.data?.message || '删除学生所有通知失败');
      } else if (error.request) {
        message.error('网络错误，请检查网络连接');
      } else {
        message.error('删除学生所有通知失败');
      }
      throw error;
    }
  }
};