<template>
  <div class="feedback-page">
    <section class="hero card-surface">
      <div class="hero-copy">
        <el-button text class="back-button" @click="router.push('/hospital/home')">← 返回首页</el-button>
        <h1>意见反馈</h1>
        <p>查看全部、待回复和已回复记录，点击下方按钮进入工单提交页。</p>
        <div class="hero-stats">
          <div class="stat-box">
            <strong>{{ stats.total }}</strong>
            <span>全部</span>
          </div>
          <div class="stat-box accent-warn">
            <strong>{{ stats.pending }}</strong>
            <span>待回复</span>
          </div>
          <div class="stat-box accent-success">
            <strong>{{ stats.replied }}</strong>
            <span>已回复</span>
          </div>
        </div>
      </div>

      <div class="hero-panel">
        <div class="panel-title">患者信息</div>
        <div class="panel-card">
          <div class="panel-line">
            <span>当前患者</span>
            <strong>{{ patientName }}</strong>
          </div>
          <div class="panel-line">
            <span>联系电话</span>
            <strong>{{ maskedPhone }}</strong>
          </div>
          <div class="panel-line">
            <span>登录状态</span>
            <strong>{{ loginStateText }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="card-surface list-shell">
      <div class="tab-row">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          type="button"
          class="tab-button"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          <span>{{ tab.label }}</span>
          <strong>{{ tab.count }}</strong>
        </button>
      </div>

      <div class="list-toolbar">
        <div class="toolbar-note">说明：待回复包含“待处理”和“处理中”的工单。</div>
        <el-button :loading="loading" @click="loadFeedbacks">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <div v-loading="loading" class="list-area">
        <template v-if="visibleRecords.length">
          <article v-for="item in visibleRecords" :key="item.feedbackID" class="feedback-card" @click="openDetail(item)">
            <div class="feedback-head">
              <div class="title-group">
                <h3>{{ renderTitle(item) }}</h3>
                <div class="tag-row">
                  <el-tag type="info" effect="light">{{ businessTypeLabel(item.feedbackType) }}</el-tag>
                  <el-tag :type="statusTagType(item.processStatus)" effect="light">
                    {{ statusLabel(item.processStatus) }}</el-tag>
                </div>
              </div>
              <div class="time-column">
                <span>提交时间</span>
                <strong>{{ formatTime(item.feedbackTime) }}</strong>
              </div>
            </div>

            <div class="feedback-body">
              <div class="detail-block">
                <span class="detail-label">发生时间</span>
                <p>{{ renderOccurredAt(item) }}</p>
              </div>
              <div class="detail-block">
                <span class="detail-label">问题描述</span>
                <p>{{ renderDetail(item) }}</p>
              </div>
              <div class="detail-block reply-block" v-if="item.replyContent">
                <span class="detail-label">回复内容</span>
                <p>{{ item.replyContent }}</p>
                <span class="reply-time" v-if="item.replyTime">回复时间：{{ formatTime(item.replyTime) }}</span>
              </div>
              <div class="detail-block muted" v-else>
                <span class="detail-label">回复内容</span>
                <p>当前工单暂未回复，请耐心等待。</p>
              </div>
            </div>
          </article>
        </template>

        <el-empty v-else :description="loading ? '正在加载反馈记录' : '暂无反馈记录'" />
      </div>
    </section>

    <el-button class="floating-action" type="primary" size="large" @click="goToSubmit">
      我要反馈
    </el-button>

    <el-drawer
      v-model="detailOpen"
      title="反馈详情"
      size="420px"
      direction="rtl"
      :destroy-on-close="true"
    >
      <template v-if="detailRecord">
        <div class="detail-panel">
          <div class="detail-line">
            <span>问题标题</span>
            <strong>{{ renderTitle(detailRecord) }}</strong>
          </div>
          <div class="detail-line">
            <span>业务类型</span>
            <strong>{{ businessTypeLabel(detailRecord.feedbackType) }}</strong>
          </div>
          <div class="detail-line">
            <span>发生时间</span>
            <strong>{{ renderOccurredAt(detailRecord) }}</strong>
          </div>
          <div class="detail-line">
            <span>提交时间</span>
            <strong>{{ formatTime(detailRecord.feedbackTime) }}</strong>
          </div>
          <div class="detail-line full">
            <span>详细问题</span>
            <p>{{ renderDetail(detailRecord) }}</p>
          </div>
          <div class="detail-line full" v-if="detailRecord.replyContent">
            <span>回复内容</span>
            <p>{{ detailRecord.replyContent }}</p>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { Refresh } from '@element-plus/icons-vue'
import { getPatientFeedbacks, type HospitalUser } from '@/api/hospital/feedback'
import type { FeedbackRecord } from '@/api/feedback'

type TabKey = 'all' | 'pending' | 'replied'

const router = useRouter()
const loading = ref(false)
const activeTab = ref<TabKey>('all')
const records = ref<FeedbackRecord[]>([])
const detailOpen = ref(false)
const detailRecord = ref<FeedbackRecord | null>(null)

const businessTypeOptions = [
  { label: '积分', value: 1 },
  { label: '挂号', value: 2 },
  { label: '报告查询', value: 3 },
  { label: '问诊', value: 4 }
]

const currentUser = computed<HospitalUser | null>(() => {
  try {
    const raw = localStorage.getItem('hospital_user')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
})

const patientName = computed(() => currentUser.value?.patientName || '患者')
const patientPhone = computed(() => currentUser.value?.patientPhone || '')
const maskedPhone = computed(() => {
  if (!patientPhone.value) return '未获取到联系电话'
  return patientPhone.value.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
})
const loginStateText = computed(() => (currentUser.value?.patientID ? '已登录' : '未登录'))

const tabs = computed(() => {
  const total = records.value.length
  const pending = records.value.filter((item) => isPending(item.processStatus)).length
  const replied = records.value.filter((item) => isReplied(item.processStatus)).length

  return [
    { key: 'all' as TabKey, label: '全部', count: total },
    { key: 'pending' as TabKey, label: '待回复', count: pending },
    { key: 'replied' as TabKey, label: '已回复', count: replied }
  ]
})

const stats = computed(() => ({
  total: records.value.length,
  pending: records.value.filter((item) => isPending(item.processStatus)).length,
  replied: records.value.filter((item) => isReplied(item.processStatus)).length
}))

const visibleRecords = computed(() => {
  if (activeTab.value === 'pending') {
    return records.value.filter((item) => isPending(item.processStatus))
  }

  if (activeTab.value === 'replied') {
    return records.value.filter((item) => isReplied(item.processStatus))
  }

  return records.value
})

function isReplied(status?: number | null) {
  const value = Number(status)
  return value === 3 || value === 4
}

function isPending(status?: number | null) {
  return !isReplied(status)
}

function statusLabel(status?: number | null) {
  return isReplied(status) ? '已回复' : '待回复'
}

function statusTagType(status?: number | null) {
  return isReplied(status) ? 'success' : 'warning'
}

function businessTypeLabel(type?: number | null) {
  const found = businessTypeOptions.find((item) => item.value === Number(type))
  return found ? found.label : '未分类'
}

function formatTime(value?: string | null) {
  if (!value) return '-'
  return dayjs(value).format('YYYY-MM-DD HH:mm')
}

function parseContent(content?: string | null) {
  const text = (content || '').trim()
  if (!text) {
    return { title: '', occurredAt: '', detail: '' }
  }

  const lines = text.split(/\r?\n/)
  const titleLine = lines.find((line) => line.startsWith('问题标题：')) || ''
  const occurredAtLine = lines.find((line) => line.startsWith('发生时间：')) || ''
  const detailIndex = lines.findIndex((line) => line.startsWith('详细问题：'))
  const detail = detailIndex >= 0 ? lines.slice(detailIndex + 1).join('\n').trim() : text

  return {
    title: titleLine.replace('问题标题：', '').trim(),
    occurredAt: occurredAtLine.replace('发生时间：', '').trim(),
    detail: detail || text
  }
}

function renderTitle(item: FeedbackRecord) {
  const parsed = parseContent(item.feedbackContent)
  return parsed.title || businessTypeLabel(item.feedbackType)
}

function renderOccurredAt(item: FeedbackRecord) {
  const parsed = parseContent(item.feedbackContent)
  return parsed.occurredAt || '暂无'
}

function renderDetail(item: FeedbackRecord) {
  const parsed = parseContent(item.feedbackContent)
  return parsed.detail || item.feedbackContent || ''
}

function goToSubmit() {
  router.push('/hospital/feedback/submit')
}

function openDetail(record: FeedbackRecord) {
  detailRecord.value = record
  detailOpen.value = true
}

async function loadFeedbacks() {
  if (!currentUser.value?.patientID) {
    records.value = []
    return
  }

  loading.value = true
  try {
    const res = await getPatientFeedbacks({ page: 1, size: 1000 })
    records.value = res.records || []
  } catch (error: any) {
    ElMessage.error(error?.message || '加载反馈记录失败')
    records.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!currentUser.value?.patientID) {
    ElMessage.warning('请先登录患者账号')
    router.push('/hospital/login')
    return
  }

  await loadFeedbacks()
})
</script>

<style scoped>
.feedback-page {
  min-height: 100vh;
  padding: 24px 24px 120px;
  background:
    radial-gradient(circle at top left, rgba(74, 144, 226, 0.16), transparent 32%),
    linear-gradient(180deg, #f6f9fe 0%, #edf4ff 100%);
}

.card-surface {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 18px 42px rgba(38, 79, 170, 0.12);
  backdrop-filter: blur(10px);
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 0.9fr);
  gap: 24px;
  padding: 28px;
  margin-bottom: 20px;
}

.kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  margin-bottom: 14px;
  border-radius: 999px;
  background: rgba(39, 111, 245, 0.12);
  color: #276ff5;
  font-size: 13px;
  font-weight: 600;
}

.hero h1 {
  margin: 0;
  color: #153a73;
  font-size: 34px;
  line-height: 1.15;
}

.hero p {
  margin: 14px 0 0;
  max-width: 760px;
  color: #5f6c86;
  font-size: 15px;
  line-height: 1.8;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 22px;
}

.stat-box {
  padding: 16px 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, #eef5ff 0%, #e2edff 100%);
}

.stat-box strong {
  display: block;
  color: #1f4ea8;
  font-size: 28px;
  line-height: 1;
}

.stat-box span {
  display: block;
  margin-top: 6px;
  color: #6a7590;
  font-size: 13px;
}

.stat-box.accent-warn {
  background: linear-gradient(180deg, #fff5df 0%, #ffe9b6 100%);
}

.stat-box.accent-warn strong {
  color: #b17500;
}

.stat-box.accent-success {
  background: linear-gradient(180deg, #e8fbf4 0%, #cff3e2 100%);
}

.stat-box.accent-success strong {
  color: #1d8a58;
}

.hero-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.panel-title {
  margin-bottom: 12px;
  color: #1b3f7a;
  font-size: 16px;
  font-weight: 700;
}

.panel-card {
  padding: 20px;
  border-radius: 20px;
  background: linear-gradient(180deg, #f8fbff 0%, #edf4ff 100%);
}

.panel-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  color: #50607d;
  font-size: 14px;
}

.panel-line strong {
  color: #173f7b;
}

.list-shell {
  padding: 18px;
}

.tab-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.tab-button {
  padding: 16px 18px;
  border: 0;
  border-radius: 18px;
  background: #f3f7fd;
  color: #6c768b;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-button strong {
  display: block;
  margin-top: 4px;
  font-size: 20px;
}

.tab-button.active {
  background: linear-gradient(135deg, #2f7cf6 0%, #4d92ff 100%);
  color: #fff;
  box-shadow: 0 10px 24px rgba(47, 124, 246, 0.24);
}

.list-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
}

.toolbar-note {
  color: #7a859c;
  font-size: 13px;
}

.list-area {
  margin-top: 18px;
}

.feedback-card {
  padding: 18px 18px 16px;
  margin-bottom: 16px;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 10px 24px rgba(38, 79, 170, 0.08);
  cursor: pointer;
}

.feedback-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.title-group h3 {
  margin: 0;
  color: #173f7b;
  font-size: 18px;
  line-height: 1.4;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.time-column {
  flex-shrink: 0;
  text-align: right;
  color: #7a8497;
  font-size: 12px;
}

.time-column strong {
  display: block;
  margin-top: 4px;
  color: #284a86;
  font-size: 14px;
}

.feedback-body {
  display: grid;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #edf1f7;
}

.detail-block {
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fbff;
}

.detail-block.muted {
  background: #fbfcfe;
  color: #76819a;
}

.detail-label {
  display: block;
  margin-bottom: 6px;
  color: #72809b;
  font-size: 12px;
  font-weight: 600;
}

.detail-block p {
  margin: 0;
  color: #314565;
  font-size: 14px;
  line-height: 1.75;
  white-space: pre-wrap;
}

.reply-block {
  background: #effaf5;
}

.reply-time {
  display: inline-block;
  margin-top: 8px;
  color: #4c8b62;
  font-size: 12px;
}

.floating-action {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 20;
  min-width: 128px;
  height: 52px;
  border-radius: 26px;
  box-shadow: 0 14px 30px rgba(47, 124, 246, 0.28);
}

.detail-panel {
  display: grid;
  gap: 14px;
}

.detail-line {
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fbff;
}

.detail-line span {
  display: block;
  margin-bottom: 6px;
  color: #73819a;
  font-size: 12px;
  font-weight: 600;
}

.detail-line strong,
.detail-line p {
  color: #173f7b;
  font-size: 14px;
  line-height: 1.75;
}

.detail-line p {
  margin: 0;
  white-space: pre-wrap;
}

.detail-line.full {
  background: #f1f7ff;
}

@media (max-width: 960px) {
  .hero {
    grid-template-columns: 1fr;
  }

  .hero h1 {
    font-size: 28px;
  }

  .hero-stats,
  .tab-row {
    grid-template-columns: 1fr;
  }

  .list-toolbar,
  .feedback-head {
    flex-direction: column;
    align-items: stretch;
  }

  .time-column {
    text-align: left;
  }

  .floating-action {
    right: 16px;
    bottom: 16px;
  }
}
</style>
