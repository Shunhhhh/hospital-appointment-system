<template>
    <div class="page">
        <a-card :bordered="false" class="card">
            <!-- 顶部：头像 + 姓名/学号 + 操作按钮 -->
            <div class="profile-header">
                <div class="profile-user">
                    <a-avatar
                        :size="72"
                        :src="userAvatar || undefined"
                        class="profile-avatar"
                    >
                        {{
                            (
                                form.studentUserName ||
                                form.studentName ||
                                "U"
                            ).charAt(0)
                        }}
                    </a-avatar>

                    <div class="profile-meta">
                        <div class="profile-name">
                            {{ form.studentUserName || form.studentName || "未命名用户" }}
                        </div>
                        <div class="profile-sub">维护你的账号信息与安全设置</div>

                    </div>
                </div>

                <div class="profile-actions">
  <a-space>
    <a-button @click="router.back()">返回</a-button>

    <a-button v-if="!editing" type="primary" @click="startEdit">编辑</a-button>
    <a-button v-else @click="cancelEdit">取消</a-button>
    <a-button v-if="editing" type="primary" :loading="saving" @click="save">保存</a-button>
  </a-space>
</div>


            </div>

            <a-divider />

            <a-spin :spinning="loading">
  <!-- 两列布局 -->
  <template v-if="!editing">
    <a-row :gutter="[16,16]">
  <!-- 左侧：可维护信息 + 安全 -->
  <a-col :xs="24" :lg="16">
    <a-card class="info-card" :bordered="false">
      <div class="section-title">个人信息</div>

      <div class="kv">
        <div class="kv-item">
          <div class="kv-label">用户名</div>
          <div class="kv-value">{{ form.studentUserName || "-" }}</div>
        </div>
        <div class="kv-item">
          <div class="kv-label">手机号</div>
          <div class="kv-value">{{ form.studentPhoneNumber || "-" }}</div>
        </div>
      </div>

      <a-divider />

      <div class="tag-block">
  <div class="tag-title">基础信息</div>
  <div class="tag-row">
    <a-tag class="tag-strong">学号：{{ data?.studentID }}</a-tag>
    <a-tag class="tag-strong">姓名：{{ data?.studentName }}</a-tag>
    <a-tag class="tag-strong">学院：{{ data?.studentCollege }}</a-tag>
  </div>
</div>


    </a-card>

    <a-card class="info-card" :bordered="false" style="margin-top:16px">
      <div class="section-title">安全</div>
      <div class="secure-row">
        <div class="secure-text">
          建议定期修改密码，手机号用于校验身份。
        </div>
        <a-button type="primary" @click="openPwdModal">重置密码</a-button>
      </div>
    </a-card>
  </a-col>

  <!-- 右侧：快捷入口 + 概览 -->
  <a-col :xs="24" :lg="8">
    <a-card class="side-card" :bordered="false">
      <div class="section-title">快捷入口</div>
      <div class="quick-grid">
        <a-button block @click="$router.push('/student-reservation')">预约记录</a-button>
        <a-button block @click="$router.push('/violation-record')">违规记录</a-button>
        <a-button block @click="$router.push('/feedback')">提交反馈</a-button>
      </div>

      <a-divider />

      <div class="section-title">账户概览</div>
      <div class="stat">
        <div class="stat-item">
          <div class="stat-label">积分</div>
          <div class="stat-value">{{ form.studentPoints ?? 0 }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-label">年级</div>
          <div class="stat-value">{{ form.studentGrade ?? "-" }}</div>
        </div>
      </div>

      <a-divider />

      <div class="tips-lite">
        <div class="tips-title">说明</div>
        <div class="tips-text">用户名与手机号可在“编辑”中修改。</div>
      </div>
    </a-card>
  </a-col>
</a-row>

  </template>

  <!-- 编辑态 -->
<div v-else class="edit-wrap">
  <a-row :gutter="[16, 16]">
    <!-- 左：可编辑 -->
    <a-col :xs="24" :lg="16">
      <a-card class="info-card edit-card" :bordered="false">
        <template #title>
          <span class="section-title">可编辑信息</span>
        </template>
        <template #extra>
          <a-tag color="blue">可修改</a-tag>
        </template>

        <a-form layout="vertical" :model="form" class="edit-form">
          <a-row :gutter="[16, 16]">
            <a-col :xs="24" :md="12">
              <a-form-item label="用户名">
                <a-input v-model:value="draft.studentUserName" placeholder="请输入用户名" />
              </a-form-item>
            </a-col>

            <a-col :xs="24" :md="12">
              <a-form-item label="手机号">
                <a-input v-model:value="draft.studentPhoneNumber" placeholder="请输入手机号" />
              </a-form-item>
            </a-col>
          </a-row>

          <div class="edit-hint">
            只可修改用户名/手机号，其他信息为系统基础信息不可修改。
          </div>
        </a-form>
      </a-card>

      <a-card class="info-card" :bordered="false" style="margin-top:16px">
        <div class="section-title">安全</div>
        <div class="secure-row">
          <div class="secure-text">
            建议定期修改密码，手机号用于校验身份。
          </div>
          <a-button type="primary" @click="openPwdModal">重置密码</a-button>
        </div>
      </a-card>
    </a-col>

    <!-- 右：只读信息 -->
    <a-col :xs="24" :lg="8">
      <a-card class="side-card" :bordered="false">
        <template #title>
          <span class="section-title">基础信息</span>
        </template>
        <template #extra>
          <span class="muted">不可修改</span>
        </template>

        <div class="ro-grid">
          <div class="ro-item">
            <div class="ro-label">学号</div>
            <div class="ro-value">{{ form.studentID || "-" }}</div>
          </div>
          <div class="ro-item">
            <div class="ro-label">姓名</div>
            <div class="ro-value">{{ form.studentName || "-" }}</div>
          </div>
          <div class="ro-item">
            <div class="ro-label">学院</div>
            <div class="ro-value">{{ form.studentCollege || "-" }}</div>
          </div>
          <div class="ro-item">
            <div class="ro-label">年级</div>
            <div class="ro-value">{{ form.studentGrade ?? "-" }}</div>
          </div>
          <div class="ro-item">
            <div class="ro-label">积分</div>
            <div class="ro-value">{{ form.studentPoints ?? "-" }}</div>
          </div>
        </div>
      </a-card>
    </a-col>
  </a-row>


</div>


  <a-empty v-if="!loading && !data" description="暂无个人信息" />
</a-spin>

        </a-card>

        <!-- 重置密码弹窗 -->
        <a-modal
            v-model:open="pwdOpen"
            title="重置密码"
            :confirm-loading="pwdLoading"
            ok-text="确定"
            cancel-text="取消"
            @ok="submitResetPassword"
        >
            <a-form layout="vertical">
                <a-form-item label="手机号（用于校验）">
                    <a-input v-model:value="pwdForm.phone" placeholder="请输入手机号" />
                </a-form-item>

                <a-form-item label="新密码">
                    <a-input-password v-model:value="pwdForm.password" placeholder="请输入新密码" />
                </a-form-item>

                <a-form-item label="确认新密码">
                    <a-input-password v-model:value="pwdForm.password2" placeholder="请再次输入新密码" />
                </a-form-item>
            </a-form>
        </a-modal>
    </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { message } from "ant-design-vue";
import { useRoute, useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useStudentStore } from "@/store/studentStore";
import { studentService, type StudentUser } from "@/api/student";

const router = useRouter();
const route = useRoute();

const loading = ref(false);
const saving = ref(false);
const editing = ref(false);

const data = ref<StudentUser | null>(null);

const studentStore = useStudentStore();
const { studentInfo } = storeToRefs(studentStore);

// 表单数据
const form = reactive<StudentUser>({
  studentID: 0,
  studentName: "",
  studentCollege: "",
  studentPoints: 0,
  studentGrade: 0,
  studentPhoneNumber: "",
  studentUserName: "",
});

// 头像
const userAvatar = computed(() => {
  return studentInfo.value?.avatar || "";
});

function getStudentID(): number {
  const id1 = Number(studentInfo.value?.studentID || 0);
  if (id1) return id1;

  try {
    const raw = localStorage.getItem("studentInfo") || localStorage.getItem("userInfo");
    if (!raw) return 0;
    const obj = JSON.parse(raw);
    const id2 = Number(obj.studentID || obj.id || obj.userId || 0);
    return id2 || 0;
  } catch {
    return 0;
  }
}

function fillForm(d: StudentUser) {
  form.studentID = d.studentID;
  form.studentName = d.studentName;
  form.studentCollege = d.studentCollege;
  form.studentPoints = d.studentPoints;
  form.studentGrade = d.studentGrade;
  form.studentPhoneNumber = d.studentPhoneNumber;
  form.studentUserName = d.studentUserName;
}

async function fetchProfile() {
  const studentID = getStudentID();
  if (!studentID) {
    message.error("未获取到 studentID，请先登录");
    data.value = null;
    return;
  }

  loading.value = true;
  try {
    const res = await studentService.checkSelfInformation(studentID);
    if (res.data.code !== 200) {
      message.error(res.data.message || "获取个人信息失败");
      data.value = null;
      return;
    }
    data.value = res.data.data;
    fillForm(res.data.data);
  } catch {
    message.error("获取个人信息失败（网络或接口错误）");
    data.value = null;
  } finally {
    loading.value = false;
  }
}

function startEdit() {
  if (!data.value) return;
  fillDraftFromForm(); 
  editing.value = true;
}


function cancelEdit() {
  if (!data.value) return;

  fillDraftFromForm(); 
  editing.value = false;

  router.replace({ path: "/profile", query: {} });
}


function syncUserToStoreAndStorage(patch: Record<string, any>) {
  // 更新 pinia
  studentStore.setStudentInfo({
    ...(studentInfo.value || {}),
    ...patch,
  });

  // 更新 localStorage
  const keys = ["studentInfo", "userInfo"];
  for (const k of keys) {
    const raw = localStorage.getItem(k);
    if (!raw) continue;
    try {
      const obj = JSON.parse(raw);
      localStorage.setItem(k, JSON.stringify({ ...obj, ...patch }));
    } catch {
      // ignore
    }
  }
}

// 编辑草稿
const draft = reactive<StudentUser>({
  studentID: 0,
  studentName: "",
  studentCollege: "",
  studentPoints: 0,
  studentGrade: 0,
  studentPhoneNumber: "",
  studentUserName: "",
});

// 把当前显示的 form 值复制到 draft
function fillDraftFromForm() {
  draft.studentID = form.studentID;
  draft.studentName = form.studentName;
  draft.studentCollege = form.studentCollege;
  draft.studentPoints = form.studentPoints;
  draft.studentGrade = form.studentGrade;
  draft.studentPhoneNumber = form.studentPhoneNumber;
  draft.studentUserName = form.studentUserName;
}


async function save() {
  if (!editing.value) return;

  saving.value = true;
  try {
    // 用 draft 提交
    const payload = {
      studentID: form.studentID,
      studentUserName: draft.studentUserName,
      studentPhoneNumber: draft.studentPhoneNumber,
    };

    const res = await studentService.modifySelfInformation(payload);
    if (res.data.code !== 200 || res.data.data !== true) {
      message.error(res.data.message || "保存失败");
      return;
    }

    message.success("保存成功");

    // 保存成功后才把 draft 写回 form
    form.studentUserName = draft.studentUserName;
    form.studentPhoneNumber = draft.studentPhoneNumber;

    // 立刻同步
    syncUserToStoreAndStorage({
      studentUserName: form.studentUserName,
      studentPhoneNumber: form.studentPhoneNumber,
    });

    // 再拉一次后端，确保 Profile 页与后端一致
    await fetchProfile();

    editing.value = false;

    router.replace({ path: "/profile", query: {} });
  } catch {
    message.error("保存失败（网络或接口错误）");
  } finally {
    saving.value = false;
  }
}


function applyEditFromRoute() {
  const shouldEdit = route.query.edit === "1" || route.query.edit === "true";
  if (shouldEdit && data.value) {
    fillDraftFromForm();     
    editing.value = true;
  }
}


/** ====== 重置密码 ====== */
const pwdOpen = ref(false);
const pwdLoading = ref(false);
const pwdForm = reactive({
  phone: "",
  password: "",
  password2: "",
});

function openPwdModal() {
  pwdForm.phone = "";
  pwdForm.password = "";
  pwdForm.password2 = "";
  pwdOpen.value = true;
}

async function submitResetPassword() {
  if (!form.studentID) return;

  if (!pwdForm.phone || !pwdForm.password || !pwdForm.password2) {
    message.warning("请填写完整信息");
    return;
  }
  if (pwdForm.password !== pwdForm.password2) {
    message.warning("两次输入的密码不一致");
    return;
  }

  pwdLoading.value = true;
  try {
    const res = await studentService.resetStudentPassword({
      id: form.studentID,
      phone: pwdForm.phone,
      password: pwdForm.password,
    });

    if (res.data.code !== 200) {
      message.error(res.data.message || "重置密码失败");
      return;
    }

    message.success("重置密码成功");
    pwdOpen.value = false;
  } catch {
    message.error("重置密码失败（网络或接口错误）");
  } finally {
    pwdLoading.value = false;
  }
}

onMounted(async () => {
  await fetchProfile();
  applyEditFromRoute();
});

watch(
  () => route.query.edit,
  () => applyEditFromRoute()
);
</script>

<style scoped>
/* =============== Layout =============== */
.page {
  padding: 0;
}

.card {
  width: min(1280px, calc(100% - 48px));
  margin: 16px auto;
  border-radius: 16px;
  box-shadow: 0 10px 34px rgba(0, 0, 0, 0.05);
}

/* =============== Header =============== */
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 6px 0 4px;
}

.profile-user {
  display: flex;
  align-items: center;
  gap: 14px;
}

.profile-avatar {
  background-color: #1677ff;
  color: #fff;
  font-weight: 700;
}

.profile-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.profile-name {
  font-size: 20px;
  font-weight: 650;
  color: rgba(0, 0, 0, 0.88);
  line-height: 1.2;
}

.profile-sub {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.55);
}

.profile-actions {
  flex-shrink: 0;
}

.dot {
  margin: 0 8px;
  color: rgba(0, 0, 0, 0.25);
}

/* =============== Common Cards =============== */
.info-card,
.side-card {
  border-radius: 16px;
  box-shadow: 0 6px 22px rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 14px;
  font-weight: 650;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 10px;
}

.muted {
  color: rgba(0, 0, 0, 0.45);
}

/* antd descriptions（如果你还有在用 desc） */
.desc :deep(.ant-descriptions-item-label) {
  color: rgba(0, 0, 0, 0.55);
  font-weight: 500;
}
.desc :deep(.ant-descriptions-item-content) {
  color: rgba(0, 0, 0, 0.88);
  font-weight: 520;
}

/* =============== Readonly Summary Blocks =============== */
.kv {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.kv-item {
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(0, 0, 0, 0.02);
}
.kv-label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}
.kv-value {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 650;
  color: rgba(0, 0, 0, 0.88);
}

/* =============== Side Panel =============== */
.quick-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.stat {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.stat-item {
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.02);
}

.stat-label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}

.stat-value {
  font-size: 18px;
  font-weight: 650;
  color: rgba(0, 0, 0, 0.88);
  margin-top: 2px;
}

/* =============== Tips / Tag Blocks =============== */
.tips {
  color: rgba(0, 0, 0, 0.65);
  line-height: 1.8;
}
.tips-title {
  font-weight: 650;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 6px;
}
.tips ul {
  margin: 0;
  padding-left: 18px;
}

.tips-lite {
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.02);
}
.tips-text {
  color: rgba(0, 0, 0, 0.65);
  line-height: 1.6;
}

/* 基础信息 Tag 区 */
.tag-block {
  margin-top: 12px;
  padding: 14px;
  border-radius: 14px;
  background: rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.06);
}
.tag-title {
  font-size: 13px;
  font-weight: 700;
  color: rgba(0, 0, 0, 0.88);
  margin-bottom: 10px;
}
.tag-row :deep(.ant-tag) {
  margin-right: 10px;
  margin-bottom: 8px;
}
.tag-strong {
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 600;
  background: rgba(22, 119, 255, 0.1);
  border: 1px solid rgba(22, 119, 255, 0.25);
  color: rgba(0, 0, 0, 0.85);
}

/* 如果你未来还用到这些“圆角标签”，保留 */
.minor {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.tag {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.65);
  background: rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.06);
  padding: 6px 10px;
  border-radius: 999px;
}

/* =============== Security Row =============== */
.secure-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.secure-text {
  color: rgba(0, 0, 0, 0.65);
}

/* =============== Edit Mode =============== */
.edit-wrap {
  padding-top: 6px;
}

.edit-form {
  padding-top: 6px;
}

.edit-card {
  background: rgba(22, 119, 255, 0.04);
  border: 1px solid rgba(22, 119, 255, 0.12);
}

.readonly-text {
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.02);
  color: rgba(0, 0, 0, 0.85);
}

/* 编辑态右侧只读信息块 */
.ro-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}
.ro-item {
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.02);
}
.ro-label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.55);
}
.ro-value {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.88);
}

.edit-hint {
  margin-top: 6px;
  color: rgba(0, 0, 0, 0.55);
  font-size: 12px;
}


.sticky-footer {
  position: sticky;
  bottom: 0;
  margin-top: 16px;
  padding: 12px 0;
  background: #fff;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

/* =============== Ant Input Polish =============== */
:deep(.ant-input),
:deep(.ant-input-password),
:deep(.ant-input-number) {
  border-radius: 10px;
  border-color: rgba(0, 0, 0, 0.12);
}

:deep(.ant-input:focus),
:deep(.ant-input-focused),
:deep(.ant-input-password:focus),
:deep(.ant-input-number:focus) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.12);
}

</style>

