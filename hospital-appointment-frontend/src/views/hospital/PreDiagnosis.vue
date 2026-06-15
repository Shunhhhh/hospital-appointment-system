<template>
  <div class="pre-diagnosis-page">
    <!-- 顶部导航 -->
    <div class="chat-header">
      <div class="back-bar">
      </div>
      <div class="header-info">
        <el-icon size="24" style="color:#409eff"><ChatDotRound /></el-icon>
        <div>
          <div class="header-title">智能预问诊</div>
          <div class="header-subtitle">AI 助手 · 仅供参考，不能替代医生诊断</div>
        </div>
      </div>
    </div>

    <!-- 对话区域 -->
    <div class="chat-body" ref="chatBodyRef">
      <!-- 欢迎消息 -->
      <div class="message-row ai">
        <div class="avatar ai-avatar">
          <el-icon size="20"><ChatDotRound /></el-icon>
        </div>
        <div class="message-bubble ai-bubble">
          <p>您好！我是智能预问诊助手。请描述您的症状，我将为您提供初步建议。</p>
          <p class="example-text">例如：<span class="example-link" @click="quickFill('我最近三天一直头痛，还有点发烧，全身乏力')">头痛发烧全身乏力</span>、<span class="example-link" @click="quickFill('我吃完饭肚子疼，经常反酸')">饭后腹痛反酸</span></p>
        </div>
      </div>

      <!-- 消息列表 -->
      <div v-for="(msg, index) in messages" :key="index" class="message-row" :class="msg.role">
        <div v-if="msg.role === 'ai'" class="avatar ai-avatar">
          <el-icon size="20"><ChatDotRound /></el-icon>
        </div>
        <div class="message-bubble" :class="msg.role === 'ai' ? 'ai-bubble' : 'user-bubble'">
          <div v-if="msg.role === 'user'">{{ msg.content }}</div>
          <div v-else class="ai-content" v-html="formatContent(msg.content)"></div>
        </div>
        <div v-if="msg.role === 'user'" class="avatar user-avatar">
          <el-icon size="20"><User /></el-icon>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="message-row ai">
        <div class="avatar ai-avatar">
          <el-icon size="20"><ChatDotRound /></el-icon>
        </div>
        <div class="message-bubble ai-bubble">
          <span class="typing-dots">AI 思考中<span class="dot">.</span><span class="dot">.</span><span class="dot">.</span></span>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-footer">
      <div class="input-wrapper">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="请描述您的症状..."
          :disabled="loading"
          @keyup.enter.prevent="sendMessage"
        />
        <el-button type="primary" :loading="loading" :disabled="!inputText.trim()" @click="sendMessage" class="send-btn">
          发送
        </el-button>
      </div>
      <div class="disclaimer">声明：AI 回答仅供参考，不能替代专业医生的诊断和治疗建议。</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound, User } from '@element-plus/icons-vue'
import { preDiagnosisAPI } from '@/api/hospital/preDiagnosis'

const router = useRouter()
const inputText = ref('')
const loading = ref(false)
const messages = ref<{ role: string; content: string }[]>([])
const chatBodyRef = ref<HTMLElement | null>(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  }
}

const quickFill = (text: string) => {
  inputText.value = text
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const res = await preDiagnosisAPI.chat(text)
    if (res.code === 200) {
      messages.value.push({ role: 'ai', content: res.data || '' })
    } else {
      ElMessage.error(res.message || '请求失败')
    }
  } catch {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const formatContent = (content: string) => {
  if (!content) return ''
  return content
    .replace(/\n/g, '<br>')
    .replace(/1\. /g, '<br><strong>1. </strong>')
    .replace(/2\. /g, '<br><strong>2. </strong>')
    .replace(/3\. /g, '<br><strong>3. </strong>')
    .replace(/4\. /g, '<br><strong>4. </strong>')
}
</script>

<style scoped>
.pre-diagnosis-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.chat-header {
  background: white;
  padding: 16px 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.header-subtitle {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.back-bar {
  margin-bottom: 4px;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-row {
  display: flex;
  gap: 10px;
  max-width: 700px;
}

.message-row.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-avatar {
  background: #ecf5ff;
  color: #409eff;
}

.user-avatar {
  background: #409eff;
  color: white;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  max-width: 500px;
}

.ai-bubble {
  background: white;
  border: 1px solid #e4e7ed;
  color: #333;
}

.user-bubble {
  background: #409eff;
  color: white;
}

.ai-content {
  line-height: 1.8;
}

.example-text {
  color: #999;
  font-size: 13px;
  margin-top: 8px;
}

.example-link {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
  margin: 0 4px;
}

.example-link:hover {
  color: #66b1ff;
}

.typing-dots {
  color: #999;
}

.dot {
  animation: blink 1.4s infinite;
  font-weight: bold;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

.chat-footer {
  background: white;
  padding: 12px 24px 16px;
  box-shadow: 0 -1px 4px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.input-wrapper .el-textarea {
  flex: 1;
}

.send-btn {
  height: 56px;
  width: 80px;
}

.disclaimer {
  text-align: center;
  color: #ccc;
  font-size: 11px;
  margin-top: 8px;
}
</style>
