/**
 * 预问诊 API
 */

import request from '../request'

export const preDiagnosisAPI = {
  /**
   * 单轮对话：输入症状，返回 AI 建议
   */
  chat: (symptom: string) => {
    return request.post<string>('/hospital/pre-diagnosis/chat', { symptom })
  },

  /**
   * 多轮对话：携带历史消息
   */
  chatWithHistory: (messages: { role: string; content: string }[]) => {
    return request.post<string>('/hospital/pre-diagnosis/chat/history', { messages })
  }
}
