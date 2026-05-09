<template>
  <div class="page">
    <a-card class="shell" :bordered="false">
      <div class="header">
        <div class="title">学生反馈</div>
      </div>

      <a-tabs v-model:activeKey="activeTab">
        <!-- Tab A：提交反馈 -->
        <a-tab-pane key="submit" tab="提交反馈">
          <a-form
            layout="vertical"
            :model="form"
            :rules="rules"
            ref="formRef"
          >
            <a-row :gutter="16">
              <a-col :xs="24" :md="12">
                <a-form-item label="反馈类型" name="feedbackType">
                  <a-select v-model:value="form.feedbackType" placeholder="请选择反馈类型">
                    <a-select-option :value="1">违规举报</a-select-option>
                    <a-select-option :value="2">建议</a-select-option>
                    <a-select-option :value="3">投诉</a-select-option>
                    <a-select-option :value="4">设备报修</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>


            </a-row>

            <a-form-item label="反馈内容" name="feedbackContent">
              <a-textarea
                v-model:value="form.feedbackContent"
                :rows="5"
                :maxlength="500"
                show-count
                placeholder="请尽量描述清楚：发生时间、地点/资源编号、具体问题或建议…"
              />
            </a-form-item>

            <a-row :gutter="16">
              <a-col :xs="24" :md="24">
                <!-- ===== 反馈关联资源选择 ===== -->
                <a-form-item label="关联资源（若非座位相关可选填）">
                  <a-space :size="12" wrap>
                    <!-- 第一层：资源类型 -->
                    <a-select
                      v-model:value="resourceType"
                      style="width: 160px"
                      placeholder="选择资源类型"
                      allowClear
                      :options="resourceTypeOptions"
                      @change="handleTypeChange"
                    />

                    <!-- 第二层：自习室 or 研讨室 -->
                    <a-select
                      v-model:value="secondId"
                      style="width: 260px"
                      :disabled="!resourceType"
                      :loading="loadingSecond"
                      allowClear
                      :placeholder="resourceType === 'SEMINAR' ? '选择研讨室' : '选择自习室'"
                      :options="secondOptions"
                      @change="handleSecondChange"
                      @dropdownVisibleChange="(visible) => {
                        if (visible) {
                          console.log('secondOptions=', secondOptions, 'len=', secondOptions?.length)
                          console.log('resourceType=', resourceType)
                        }
                      }"
                    />

                    <!-- 第三层：座位（仅普通/考研） -->
                    <a-select
                      v-if="resourceType === 'NORMAL' || resourceType === 'POSTGRAD'"
                      v-model:value="seatId"
                      style="width: 200px"
                      :disabled="!secondId"
                      :loading="loadingSeats"
                      allowClear
                      placeholder="选择座位"
                      :options="seatOptions"
                    />
                  </a-space>

                  <!-- 可选：给用户一个预览提示 -->
                  <div v-if="relatedText" style="margin-top: 8px; color: rgba(0,0,0,.45); font-size: 12px;">
                    将在提交时追加：关联资源：{{ relatedText }}
                  </div>
                </a-form-item>
              </a-col>


            </a-row>

            <div class="actions">
              <a-button @click="resetForm">重置</a-button>
              <a-button type="primary" :loading="submitLoading" @click="onSubmit">
                提交反馈
              </a-button>
            </div>
          </a-form>
        </a-tab-pane>

        <!-- Tab B：我的反馈 -->
        <a-tab-pane key="mine" tab="我的反馈">
            <a-card class="filter" :bordered="false">
                <a-row :gutter="[12, 12]" align="middle" class="filter-row">
                    <a-col :xs="24" :sm="12" :md="6" class="filter-item">
                        <a-select
                            v-model:value="filters.status"
                            allow-clear
                            placeholder="状态"
                            class="w-100"
                        >
                            <a-select-option :value="1">待处理</a-select-option>
                            <a-select-option :value="2">处理中</a-select-option>
                            <a-select-option :value="3">已回复</a-select-option>
                            <a-select-option :value="4">已关闭</a-select-option>
                        </a-select>
                    </a-col>

                    <a-col :xs="24" :sm="12" :md="6" class="filter-item">
                        <a-select
                            v-model:value="filters.type"
                            allow-clear
                            placeholder="类型"
                            class="w-100"
                        >
                            <a-select-option :value="1">违规举报</a-select-option>
                            <a-select-option :value="2">建议</a-select-option>
                            <a-select-option :value="3">投诉</a-select-option>
                            <a-select-option :value="4">设备报修</a-select-option>
                        </a-select>
                    </a-col>
                    <a-col :xs="0" :sm="0" :md="12"></a-col>

                    <a-col :xs="24" :sm="24" :md="6" class="filter-actions-col">
                        <a-space>
                            <a-button @click="clearFilters">清空筛选</a-button>
                            <a-button type="primary" :loading="listLoading" @click="reloadList(true)">刷新</a-button>
                        </a-space>
                    </a-col>

                </a-row>
            </a-card>

            <a-spin :spinning="listLoading">
                <a-list
                    :data-source="list"
                    :locale="{ emptyText: '暂无反馈记录' }"
                    item-layout="vertical"
                >
                    <template #renderItem="{ item }">
                        <a-list-item>
                            <a-card class="item" :bordered="false" @click="openDetail(item.feedbackID)">
                                <div class="item-head">
                                    <div class="left">
                                        <a-tag>{{ typeLabel(item.feedbackType) }}</a-tag>
                                        <a-tag :color="statusColor(item.processStatus)">{{ statusLabel(item.processStatus) }}</a-tag>
                                        <a-tag v-if="item.priority === 1">高优先级</a-tag>
                                    </div>
                                    <div class="right">
                                        <span class="time">提交：{{ formatTime(item.feedbackTime) }}</span>
                                    </div>
                                </div>

                                <div class="content">
                                    {{ brief(item.feedbackContent) }}
                                </div>

                                <div class="meta" v-if="item.relatedResourceID">
                                    关联资源：{{ item.relatedResourceID }}
                                </div>
                            </a-card>
                        </a-list-item>
                    </template>
                </a-list>

                <div class="pager" v-if="total > 0">
                    <a-pagination
                        v-model:current="page"
                        v-model:pageSize="pageSize"
                        :total="total"
                        show-size-changer
                        :pageSizeOptions="['5','10','20','50']"
                        @change="reloadList(false)"
                        @showSizeChange="onSizeChange"
                    />
                </div>
            </a-spin>

            <!-- 详情抽屉 -->
            <a-drawer
                v-model:open="detailOpen"
                title="反馈详情"
                width="520"
                :destroyOnClose="true"
            >
                <a-spin :spinning="detailLoading">
                    <template v-if="detail">
                        <a-descriptions bordered size="small" :column="1">
                            <a-descriptions-item label="反馈ID">{{ detail.feedbackID }}</a-descriptions-item>
                            <a-descriptions-item label="类型">{{ typeLabel(detail.feedbackType) }}</a-descriptions-item>
                            <a-descriptions-item label="状态">
                                <a-tag :color="statusColor(detail.processStatus)">{{ statusLabel(detail.processStatus) }}</a-tag>
                            </a-descriptions-item>
                            <a-descriptions-item label="提交时间">{{ formatTime(detail.feedbackTime) }}</a-descriptions-item>
                        </a-descriptions>

                        <div class="block">
                            <div class="block-title">反馈内容</div>
                            <div class="block-body">{{ detail.feedbackContent }}</div>
                        </div>

                        <div class="block">
                            <div class="block-title">管理员回复</div>
                            <div class="block-body" v-if="detail.replyContent">
                                <div>{{ detail.replyContent }}</div>
                                <div class="reply-time" v-if="detail.replyTime">回复时间：{{ formatTime(detail.replyTime) }}</div>
                            </div>
                            <div class="block-body muted" v-else>暂无回复，请耐心等待。</div>
                        </div>
                    </template>
                </a-spin>
            </a-drawer>
        </a-tab-pane>


      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from "vue";
import { message } from "ant-design-vue";
import type { FormInstance, Rule } from "ant-design-vue/es/form";
import { createFeedback, getFeedbackDetail, getMyFeedback, type FeedbackRecord, getLocalStudentId } from "@/api/feedback";
import { resourceService, type StudyRoom, type SeminarRoom, type Seat } from "@/api/feedback";


function unwrapArray(payload: any) {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.data)) return payload.data;
  if (Array.isArray(payload?.data?.records)) return payload.data.records;
  if (Array.isArray(payload?.records)) return payload.records;
  return [];
}


const activeTab = ref<"submit" | "mine">("submit");

const formRef = ref<FormInstance>();
const submitLoading = ref(false);

const form = reactive({
  feedbackType: undefined as undefined | number,
  priority: 2,
  feedbackContent: "",
  relatedResourceID: "",
});

const rules: Record<string, Rule[]> = {
  feedbackType: [{ required: true, message: "请选择反馈类型" }],
  feedbackContent: [
    { required: true, message: "请输入反馈内容" },
    { min: 10, message: "内容不少于 10 个字" },
    { max: 500, message: "内容不超过 500 个字" },
  ],
};

function resetForm() {
  form.feedbackType = undefined;
  form.feedbackContent = "";
  formRef.value?.clearValidate();

  // 三级联动值清空
  resourceType.value = undefined;
  secondId.value = undefined;
  seatId.value = undefined;

  // 三级联动列表也清空（推荐）
  studyRooms.value = [];
  seminarRooms.value = [];
  seats.value = [];
}


async function onSubmit() {
  try {
    await formRef.value?.validate();
    submitLoading.value = true;

    const baseContent = form.feedbackContent.trim();
    let finalContent = baseContent;

    if (relatedText.value) {
      finalContent = baseContent
        ? `${baseContent}\n\n关联资源：${relatedText.value}`
        : `关联资源：${relatedText.value}`;
    }


    const studentID = getLocalStudentId();
    if (!studentID) {
      message.error("未获取到 studentID，请重新登录");
      return;
    }

    await createFeedback({
      studentID,
      feedbackType: form.feedbackType as number,
      feedbackContent: finalContent,
    });

    message.success("提交成功！你可以在“我的反馈”里查看处理进度。");
    resetForm();
    activeTab.value = "mine";
    await reloadList(true);
  } catch (e: any) {
    // validate 失败不提示
    if (e?.errorFields) return;
    message.error(e?.message || "提交失败，请稍后再试");
  } finally {
    submitLoading.value = false;
  }
}


// ===== 反馈关联资源选择 =====

function unwrapList<T>(axiosRes: any): T[] {
  const d = axiosRes?.data;

  // 后端直接返回数组：[]
  if (Array.isArray(d)) return d;

  // 后端返回：{ code, data: [] }
  if (Array.isArray(d?.data)) return d.data;

  // 后端分页：{ data: { records: [] } }
  if (Array.isArray(d?.data?.records)) return d.data.records;

  return [];
}


type ResourceType = "NORMAL" | "POSTGRAD" | "SEMINAR";

// 三层选择值
const resourceType = ref<ResourceType | undefined>(undefined);
const secondId = ref<number | undefined>(undefined); // 自习室ID or 研讨室ID
const seatId = ref<number | undefined>(undefined);   // 座位ID（仅座位分支）

// 列表数据
const studyRooms = ref<StudyRoom[]>([]);
const seminarRooms = ref<SeminarRoom[]>([]);
const seats = ref<Seat[]>([]);

// 加载状态
const loadingSecond = ref(false);
const loadingSeats = ref(false);

// 第一层 options
const resourceTypeOptions = [
  { value: "NORMAL", label: "普通座位" },
  { value: "POSTGRAD", label: "考研座位" },
  { value: "SEMINAR", label: "研讨室" },
] as const;

// 第二层 options（根据第一层切换数据源）
const secondOptions = computed(() => {
  if (!resourceType.value) return [];

  if (resourceType.value === "SEMINAR") {
    return seminarRooms.value.map((r: any) => {
      const id = Number(r.seminarRoomID ?? r.id); 
      return {
        value: id,
        label: String(id), 
      };
    });
  }

  return studyRooms.value.map((r: any) => {
    const id = Number(r.studyRoomID);
    return {
      value: id,
      label: `${r.studyRoomName ?? "自习室"}（ID:${id}）`,
    };
  }).filter(o => !Number.isNaN(o.value));
});





// 第三层 options（座位）
const seatOptions = computed(() =>
  (seats.value ?? []).map((s: any) => ({
    label: `座位 ${s.seatNumber} ${s.seatLocation ?? ''}`.trim(),
    value: s.seatID,
  }))
)


// 第一层变化：清空二/三层 + 懒加载对应列表

async function handleTypeChange() {
  secondId.value = undefined;
  seatId.value = undefined;
  seats.value = [];

  studyRooms.value = [];
  seminarRooms.value = [];

  if (!resourceType.value) return;

  loadingSecond.value = true;
  try {
    if (resourceType.value === "SEMINAR") {
      const res = await resourceService.getSeminarRoomss();
      seminarRooms.value = unwrapList<SeminarRoom>(res);
    } else {
      const res = await resourceService.getStudyRoomss();

      console.log("status=", res.status);
      console.log("data=", res.data);
      console.log("res.data=", res.data);
      console.log("secondOptions(before)=", secondOptions.value);

      const allRooms = unwrapList<StudyRoom>(res);

      const wantedType = resourceType.value === "POSTGRAD" ? 1 : 0;

      studyRooms.value = allRooms.filter((r: any) => {
        const t = Number(r.studyRoomType ?? r.roomType ?? r.type);
        return t === wantedType;
      });

      console.log("studyRooms(after filter)=", studyRooms.value);
    }
  } catch (e) {
    message.error("加载资源列表失败");
  } finally {
    loadingSecond.value = false;
  }
}



async function handleSecondChange() {
  seats.value = [];
  seatId.value = undefined;

  if (!resourceType.value || !secondId.value) return;
  if (resourceType.value === "SEMINAR") return;

  loadingSeats.value = true;
  try {
    const res = await resourceService.getSeatsByRoomId(secondId.value);
    seats.value = unwrapList<Seat>(res);
  } catch (e) {
    message.error("加载座位列表失败");
  } finally {
    loadingSeats.value = false;
  }
}



const relatedText = computed(() => {
  if (!resourceType.value || !secondId.value) return "";

  if (resourceType.value === "SEMINAR") {
    const room = seminarRooms.value.find(r => Number(r.id) === Number(secondId.value));
    return room ? `研讨室：${room.roomNumber}（ID:${room.id}）` : `研讨室ID：${secondId.value}`;
  }

  // 座位分支
  if (!seatId.value) return ""; // 没选座位就不拼
  const room = studyRooms.value.find(r => Number(r.id) === Number(secondId.value));
  const seat = seats.value.find(s => Number(s.id) === Number(seatId.value));

  const typeName = resourceType.value === "POSTGRAD" ? "考研座位" : "普通座位";
  const roomText = room ? `${room.roomNumber}（ID:${room.id}）` : `自习室ID：${secondId.value}`;
  const seatText = seat ? `${seat.number}（ID:${seat.id}）` : `座位ID：${seatId.value}`;

  return `${typeName}，自习室：${roomText}，座位：${seatText}`;
});





// ===== 我的反馈：列表 =====
const listLoading = ref(false);
const list = ref<FeedbackRecord[]>([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);

const filters = reactive<{
  status?: number;
  type?: number;
  keyword?: string;
}>({
  status: undefined,
  type: undefined,
  keyword: "",
});

function clearFilters() {
  filters.status = undefined;
  filters.type = undefined;
  filters.keyword = "";
  reloadList(true);
}

async function reloadList(resetPage: boolean) {
  if (resetPage) page.value = 1;

  try {
    listLoading.value = true;
    const data = await getMyFeedback({
      status: filters.status,
      type: filters.type,
      keyword: filters.keyword?.trim() || undefined,
      page: page.value,
      size: pageSize.value,
    });

    list.value = data.records ?? [];
    total.value = data.total ?? 0;
  } catch (e: any) {
    message.error(e?.message || "加载反馈列表失败");
  } finally {
    listLoading.value = false;
  }
}

function onSizeChange(_current: number, size: number) {
  pageSize.value = size;
  reloadList(true);
}


watch(
  () => activeTab.value,
  (v) => {
    if (v === "mine" && list.value.length === 0) reloadList(true);
  },
  { immediate: false }
);


watch(
  () => [filters.status, filters.type, filters.keyword],
  () => {
    if (activeTab.value !== "mine") return; 
    reloadList(true);                       
  }
);

// ===== 详情 Drawer =====
const detailOpen = ref(false);
const detailLoading = ref(false);
const detail = ref<FeedbackRecord | null>(null);

async function openDetail(id: string) {
  detailOpen.value = true;
  detail.value = null;

  try {
    detailLoading.value = true;
    detail.value = await getFeedbackDetail(id);
  } catch (e: any) {
    message.error(e?.message || "加载反馈详情失败");
  } finally {
    detailLoading.value = false;
  }
}

// ===== UI 工具 =====
function typeLabel(t: number) {
  return ({ 1: "违规举报", 2: "建议", 3: "投诉", 4: "设备报修" } as any)[t] || "未知";
}
function statusLabel(s: number) {
  return ({ 1: "待处理", 2: "处理中", 3: "已回复", 4: "已关闭" } as any)[s] || "未知";
}
function statusColor(s: number) {
  return ({ 1: "warning", 2: "processing", 3: "success", 4: "default" } as any)[s] || "default";
}
function brief(text: string) {
  const t = (text || "").trim();
  return t.length > 60 ? t.slice(0, 60) + "…" : t;
}
function formatTime(t?: string | null) {
  if (!t) return "-";
  return t.replace("T", " ").slice(0, 19);
}
</script>

<style scoped>
.page {
  padding: 16px;
}
.shell {
  border-radius: 16px;
}
.header {
  margin-bottom: 12px;
}
.title {
  font-size: 20px;
  font-weight: 700;
}
.sub {
  margin-top: 4px;
  color: rgba(0, 0, 0, 0.55);
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}
.filter {
  border-radius: 12px;
  margin-bottom: 12px;
}
.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}
.item {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.15s ease;
}
.item:hover {
  transform: translateY(-1px);
}
.item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}
.left {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.time {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}
.content {
  font-size: 14px;
  line-height: 1.7;
}
.meta {
  margin-top: 8px;
  color: rgba(0, 0, 0, 0.55);
  font-size: 12px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
.block {
  margin-top: 16px;
}
.block-title {
  font-weight: 700;
  margin-bottom: 8px;
}
.block-body {
  white-space: pre-wrap;
  line-height: 1.7;
}
.muted {
  color: rgba(0, 0, 0, 0.45);
}
.reply-time {
  margin-top: 8px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}
.filter-row {
    width: 100%;
}

.filter-item {
    display: flex;
    align-items: center;
}

.w-160 {
    width: 160px;
    max-width: 100%;
}

.w-100 {
    width: 100%;
}


.filter-item :deep(.ant-search) {
    width: 100%;
}

/* 按钮区 */
.filter-actions-col {
    display: flex;
    justify-content: flex-end;
}

@media (max-width: 768px) {
    .filter-actions-col {
        margin-top: 8px;
    }
}
</style>
