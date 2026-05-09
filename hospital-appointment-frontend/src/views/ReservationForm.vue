<template>
  <div class="page">
    <div class="container">
      <!-- 顶部栏 -->
      <div class="header">

      <div class="title-wrap">
        <h2>{{ seatTypeLabel }}</h2>
        <div class="sub">{{ subTitle }}</div>


      </div>
    </div>



      <!-- 座位信息卡片 -->
      <a-card class="info-card" :bordered="false">
        <div class="info-grid">
          <div class="info-item">
            <div class="label">座位编号</div>
            <div class="value">{{ seatInfo.number }}</div>
          </div>
          <div class="info-item">
            <div class="label">所属房间</div>
            <div class="value">{{ seatInfo.roomName }}</div>
          </div>
          <div class="info-item">
            <div class="label">类型</div>
            <div class="value">
              <a-tag
                :color="seatInfo.type === 'NORMAL' ? 'blue' : seatInfo.type === 'STUDYROOM' ? 'purple' : 'gold'"
              >
                {{ seatTypeLabel }}
              </a-tag>
            </div>
          </div>
        </div>
      </a-card>

      <!-- 普通/研讨室：日期 + 时段 -->
      <a-card v-if="seatInfo.type !== 'POSTGRAD'" class="reserve-card" :bordered="false">
        <div class="section">
          <div class="section-title">日期</div>
          <a-date-picker v-model:value="form.date" class="full" />
        </div>

        <div class="section-head">
          <div class="section-title">选择时段</div>
          <div class="legend">
            <span class="chip free"></span><span>可选</span>
            <span class="chip selected"></span><span>已选</span>
            <span class="chip preview"></span><span>预览</span>
            <span class="chip booked"></span><span>已被约</span>
            <span class="chip mybooked"></span><span>我已约</span>
            <span class="chip past"></span><span>已过期</span>
          </div>
        </div>

        <a-alert
          v-if="selectedStart"
          class="selected-alert"
          type="info"
          show-icon
        >
          <template #message>
            已选时间：
            <b>
              <span v-if="selectedStart && selectedEnd">{{ selectedStart }} - {{ selectedEnd }}</span>
              <span v-else>{{ selectedStart }} - ?</span>
            </b>
          </template>
        </a-alert>

        <div class="time-grid">
          <div
            v-for="slot in timeSlots"
            :key="slot.start"
            class="time-block"
            :class="[
              slot.status,
              isSelected(slot) || isStartOnly(slot) ? 'SELECTED' : '',
              isPreview(slot) ? 'PREVIEW' : ''
            ]"
            @click="selectSlot(slot)"
            @mouseenter="onHover(slot)"
            @mouseleave="onLeave"
          >
            {{ slot.start }}
          </div>
        </div>

        <div class="hint" v-if="seatInfo.type === 'NORMAL'">普通座位单次最多 6 小时</div>
        <div class="hint" v-if="seatInfo.type === 'STUDYROOM'">研讨室单次最多 3 小时（至少 2 人）</div>
      </a-card>

      <!-- 考研专座：整周预约 -->
      <div v-if="seatInfo.type === 'POSTGRAD'" class="block">
        <a-card class="reserve-card" :bordered="false">
          <div class="postgrad">
            <div class="postgrad-title">本座位为 <b>考研/考公专座</b>（按周预约）</div>
            <div class="postgrad-sub">本周预约截止时间：上周周日 22:00</div>
          </div>
        </a-card>

        <WeeklyPostgradPicker
  v-model="form.postgradWeek"
  :seat-id="seatInfo.seatId"
  :study-room-id="seatInfo.roomId"
  :disabled-starts="disabledWeekStarts"
/>

      </div>

      <!-- 研讨室预约 -->
      <div v-if="seatInfo.type === 'STUDYROOM'" class="block">
        <a-card class="reserve-card" :bordered="false">
          <a-alert
            type="info"
            message="研讨室至少 2 人，每次预约最多 3 小时"
            class="study-alert"
            show-icon
          />
          <a-form layout="vertical">
            <a-form-item label="预约人数 (含自己)">
              <a-input-number v-model:value="form.memberCount" :min="2" :max="6" class="full" />
            </a-form-item>

            <div v-for="i in (form.memberCount -1)" :key="i">
              <a-form-item :label="'同伴 ' + i + ' 学号'">
                <a-input v-model:value="form.members[i-1]" placeholder="请输入同伴学号" />
              </a-form-item>
            </div>
          </a-form>
        </a-card>
      </div>

      <!-- 操作区 -->
      <div class="action-bar">
        <a-button type="primary" size="large" block class="primary-btn" @click="onSubmit">
          提交预约
        </a-button>
        <a-button type="default" size="large" block class="ghost-btn" @click="$router.back()">
          返回
        </a-button>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">
    import { ref, computed, watch, onMounted } from "vue";
    import dayjs, { Dayjs } from "dayjs";
    import WeeklyPostgradPicker from "@/views/WeeklyPostgradPicker.vue";
    import { useRoute, useRouter } from "vue-router";
    import { useStudentStore } from "@/store/studentStore";
    import axios from "axios";
    import { message } from "ant-design-vue";

    const router = useRouter();
    const route = useRoute();
    const studentStore = useStudentStore();

    type SeatType = "NORMAL" | "POSTGRAD" | "STUDYROOM";

function normalizeSeatType(raw: any): SeatType {
  const s = String(raw ?? "").trim();

  if (s.includes("考研") || s.includes("考公") || s.includes("专座")) return "POSTGRAD";
  if (s.includes("研讨")) return "STUDYROOM";
  if (s.includes("普通")) return "NORMAL";

  const v = s.toUpperCase();
  if (v === "NORMAL" || v === "POSTGRAD" || v === "STUDYROOM") return v as SeatType;

  const n = Number(raw);
  if (!Number.isNaN(n)) {
    if (n === 1) return "POSTGRAD";
    if (n === 2) return "STUDYROOM"; 
    return "NORMAL";
  }

  return "NORMAL";
}


    function normalizeRoomName(q: any): string {
        return String(q.roomName ?? q.roomLabel ?? "自习室");
    }

    function normalizeSeatNumber(q: any): string {
        return String(q.seatNumber ?? q.seatId ?? q.seatID ?? "A01");
    }

    function normalizeSeatId(q: any): number {
        return Number(q.seatId ?? q.seatID ?? q.seatIDInternal ?? 0);
    }

    function normalizeRoomId(q: any): number {
        return Number(q.roomId ?? q.roomID ?? 1);
    }

    const seatInfo = ref({
  seatId: normalizeSeatId(route.query),
  number: normalizeSeatNumber(route.query),
  roomName: normalizeRoomName(route.query),
  type: normalizeSeatType(route.query.seatType ?? route.query.type), 
  roomId: normalizeRoomId(route.query),
});


    watch(
        () => route.query,
        () => {
            seatInfo.value = {
        seatId: normalizeSeatId(route.query),
      number: normalizeSeatNumber(route.query),
      roomName: normalizeRoomName(route.query),
      type: normalizeSeatType(route.query.seatType ?? route.query.type), 
      roomId: normalizeRoomId(route.query),
    };

            // 清空普通座位已选
            selectedStart.value = null;
            selectedEnd.value = null;
            hoverSlot.value = null;
            form.value.start = null;
            form.value.end = null;

            if (seatInfo.value.type !== "POSTGRAD") {
                loadTimeSlots();
            }
        },
        { deep: true }
    );

    // 显示中文类型
    const seatTypeLabel = computed(() => {
        return (
            {
                NORMAL: "普通座位",
                POSTGRAD: "考研专座",
                STUDYROOM: "研讨室",
            }[seatInfo.value.type] || "普通座位"
        );
    });

    const subTitle = computed(() => {
        if (seatInfo.value.type === "POSTGRAD") return "选择周次，提交预约申请";
        if (seatInfo.value.type === "STUDYROOM") return "填写同伴学号并选择时段";
        return "选择日期与时间段，提交预约申请";
    });

    // ---- 表单结构 ----
    const form = ref({
        // 普通 & 研讨室
        date: dayjs(),
        start: null as Dayjs | null,
        end: null as Dayjs | null,
        members: [] as string[],
        memberCount: 2,

        // 考研专座
        postgradWeek: null as any,
    });

    const timeSlots = ref<
        { start: string; end: string; status: "PAST" | "BOOKED" | "MY_BOOKED" | "FREE" | 'WAITING' }[]
    >([]);

    const selectedStart = ref<string | null>(null);
    const selectedEnd = ref<string | null>(null);
    const hoverSlot = ref<string | null>(null);

function selectSlot(slot: any) {

  if (slot.status === 'BOOKED') {
    Modal.confirm({
      title: '发起候补',
      content: `当前时段 ${slot.start}-${slot.end} 已被占用，是否加入候补队列？`,
      onOk: () => handleWaitlist(slot)
    });
    return;
  }
// 如果点击的是自己已预约、已过期或正在候补的格子，直接返回，不执行选择逻辑
  if (slot.status === 'MY_BOOKED' || slot.status === 'PAST' || slot.status === 'WAITING') {
    return;
  }

        if (selectedStart.value && selectedEnd.value) {
            selectedStart.value = slot.start;
            selectedEnd.value = null;
            hoverSlot.value = null;
            form.value.start = dayjs(slot.start, "HH:mm");
            form.value.end = null;
            return;
        }

        if (!selectedStart.value) {
            selectedStart.value = slot.start;
            form.value.start = dayjs(slot.start, "HH:mm");
            return;
        }

        const start = dayjs(selectedStart.value, "HH:mm");
        const end = dayjs(slot.end, "HH:mm");

        const maxHours =
            seatInfo.value.type === "STUDYROOM"
                ? 3
                : seatInfo.value.type === "NORMAL"
                ? 6
                : seatInfo.value.type === "POSTGRAD"
                ? 168
                : null;

        if (maxHours) {
            const diffHours = end.diff(start, "hour", true);
            if (diffHours > maxHours) {
                message.error(`该类型最多可预约 ${maxHours} 小时`);
                return;
            }
        }

        if (!end.isAfter(start)) {
            message.error("结束时间必须晚于开始时间，请重新选择");
            selectedStart.value = null;
            selectedEnd.value = null;
            hoverSlot.value = null;
            form.value.start = null;
            form.value.end = null;
            return;
        }

        selectedEnd.value = slot.end;
        form.value.end = end;
    }

    function isSelected(slot: any) {
        if (!selectedStart.value || !selectedEnd.value) return false;
        return slot.start >= selectedStart.value && slot.end <= selectedEnd.value;
    }

    function isStartOnly(slot: any) {
        return selectedStart.value && !selectedEnd.value && slot.start === selectedStart.value;
    }

    function onHover(slot: any) {
        if (!selectedStart.value || selectedEnd.value) return;
        hoverSlot.value = slot.end;
    }

    function onLeave() {
        hoverSlot.value = null;
    }

    function isPreview(slot: any) {
        if (!selectedStart.value || !hoverSlot.value || selectedEnd.value) return false;
        return slot.start >= selectedStart.value && slot.end <= hoverSlot.value;
    }

    function generateTimeSlots(start = "08:00", end = "22:00") {
        const slots: any[] = [];
        let current = dayjs(start, "HH:mm");
        const endTime = dayjs(end, "HH:mm");

        while (current.isBefore(endTime)) {
            const next = current.add(30, "minute");
            slots.push({
                start: current.format("HH:mm"),
                end: next.format("HH:mm"),
                status: "FREE",
            });
            current = next;
        }
        return slots;
    }

watch(() => form.value.date, (newDate, oldDate) => {
  // 仅在日期对象有效且日期不同时才加载
  if (newDate && (!oldDate || !newDate.isSame(oldDate, 'day'))) {
    loadTimeSlots();
  }
});


// 候补处理函数
const handleWaitlist = async (slot: any) => {
  try {
    const dateStr = dayjs(form.value.date).format("YYYY-MM-DD");
    const isStudyRoom = seatInfo.value.type === 'STUDYROOM';

    const url = isStudyRoom ? "/api/wait/waitSeminarRoom" : "/api/wait/waitSeat";

    const startTime = `${dateStr}T${slot.start}:00`;
    const endTime = `${dateStr}T${slot.end}:00`;

    let payload;
    if (isStudyRoom) {
      // 对应后端的 ReservationRecord 对象
      payload = {
        studentID: String(studentStore.studentInfo.studentID),
        seminarRoomID: Number(seatInfo.value.roomId),
        seatID: Number(seatInfo.value.number),
        reservationStartTime: startTime,
        reservationEndTime: endTime,
        reservationRecordStatus: 0,
        cancelPermission: 1
      };
    } else {
      // 对应后端的 ReserveSeatFormDto 对象
      payload = {
        studentId: Number(studentStore.studentInfo.studentID),
        studyRoomId: Number(seatInfo.value.roomId),
        seatId: Number(seatInfo.value.seatId),
        startTime: startTime,
        endTime: endTime
      };
    }

    const res = await axios.post(url, payload);

    if (res.data.code === 200) {
      message.success("已成功加入候补队列");
      await loadTimeSlots(); // 刷新状态
    } else {
      message.error(res.data.message || "候补失败");
    }
  } catch (e) {
    console.error(e);
    message.error("网络异常，请检查接口地址");
  }
};
async function loadTimeSlots() {
  if (!form.value.date) return;
  const dateStr = dayjs(form.value.date).format("YYYY-MM-DD");

  timeSlots.value = generateTimeSlots().map(slot => ({ ...slot, status: 'FREE' }));
  const now = dayjs();
  timeSlots.value.forEach(slot => {
    if (dayjs(`${dateStr} ${slot.end}`).isBefore(now)) slot.status = 'PAST';
  });

  try {
    const currentID = studentStore.studentInfo.studentID;
    const queryParams = {
      queryDate: dateStr,
      seminarRoomId: seatInfo.value.type === 'STUDYROOM' ? String(seatInfo.value.roomId) : null,
      studyRoomId: seatInfo.value.type === 'NORMAL' ? String(seatInfo.value.roomId) : null,
      seatId: seatInfo.value.type === 'NORMAL' ? String(route.query.seatIDInternal || seatInfo.value.seatId) : null,
    };

    // 获取数据
    const [occRes, waitRes] = await Promise.all([
      axios.post("/api/reservation/occupied-time-slots", queryParams),
      axios.get("/api/wait/getWaitlist", { params: { studentId: currentID } })
    ]);

    const reservations = (occRes.data.data || []).filter((r: any) => {
      if (seatInfo.value.type === 'NORMAL') return r.seatID !== null && Number(r.studentID) !== 0;
      if (seatInfo.value.type === 'STUDYROOM') return Number(r.seminarRoomID || r.seminarRoomId) === Number(seatInfo.value.roomId);
      return false;
    });
    const waitlist = waitRes.data.data || [];

    // 染预约记录
    reservations.forEach((r: any) => {
      const rStart = dayjs(r.reservationStartTime || r.startTime);
      const rEnd = dayjs(r.reservationEndTime || r.endTime);
      timeSlots.value.forEach(slot => {
        const slotStart = dayjs(`${dateStr} ${slot.start}`, "YYYY-MM-DD HH:mm");
        const slotEnd = dayjs(`${dateStr} ${slot.end}`, "YYYY-MM-DD HH:mm");
        if (slotStart.isBefore(rEnd) && slotEnd.isAfter(rStart)) {
          const isMyBooking = String(r.studentID || r.studentId).trim() === String(currentID).trim();
          slot.status = isMyBooking ? 'MY_BOOKED' : 'BOOKED';
        }
      });
    });

    // 渲染候补记录
    waitlist.forEach((w: any) => {
      const wStart = dayjs(w.startTime || w.reservationStartTime);
      const wEnd = dayjs(w.endTime || w.reservationEndTime);
      timeSlots.value.forEach(slot => {
        const slotStart = dayjs(`${dateStr} ${slot.start}`, "YYYY-MM-DD HH:mm");
        const slotEnd = dayjs(`${dateStr} ${slot.end}`, "YYYY-MM-DD HH:mm");
        if (slotStart.isBefore(wEnd) && slotEnd.isAfter(wStart)) {
          slot.status = 'WAITING'; // 只有我自己候补的才显示 WAITING
        }
      });
    });

  } catch (e) {
    console.error(e);
    message.error("加载预约时间失败");
  }
}
watch(
  () => dayjs(form.value.date).format("YYYY-MM-DD"),
  async () => {
    // 清空已选
    selectedStart.value = null;
    selectedEnd.value = null;
    hoverSlot.value = null;
    form.value.start = null;
    form.value.end = null;

            await loadTimeSlots();
        }
    );

    onMounted(() => {
        if (seatInfo.value.type !== "POSTGRAD") {
            loadTimeSlots();
        }
    });

    // ---- 校验 + 提交 ----
    const onSubmit = async () => {
  const type = seatInfo.value.type;

  // ========== 普通座位 ==========
  if (type === "NORMAL") {
    if (!form.value.date || !form.value.start || !form.value.end) {
      return message.error("请选择日期和时间段");
    }
    if (form.value.end.isBefore(form.value.start) || form.value.end.isSame(form.value.start)) {
      return message.error("结束时间必须晚于开始时间");
    }

    const dateStr = dayjs(form.value.date).format("YYYY-MM-DD");
    const startTime = `${dateStr}T${form.value.start.format("HH:mm:ss")}`;
    const endTime = `${dateStr}T${form.value.end.format("HH:mm:ss")}`;

    const requestData = [
      {
        studentId: Number(studentStore.studentInfo.studentID),
        studyRoomId: Number(seatInfo.value.roomId),
        seatId: Number(seatInfo.value.seatId),
        startTime,
        endTime,
        date: dateStr,
        timeSlot: `${form.value.start.format("HH:mm")}-${form.value.end.format("HH:mm")}`,
      },
    ];

    try {
      const res = await axios.post("/api/reservation/reserveSeat", requestData);
      if (res.data.code === 200) {
        message.success("座位预约成功");
        router.push("/student-reservation");
      } else {
        message.error(res.data.message || "预约失败");
      }
    } catch (e) {
      console.error(e);
      message.error("网络异常，请稍后再试");
    }
    return;
  }

  // ========== 研讨室 ==========
  if (type === "STUDYROOM") {
    if (!form.value.date || !form.value.start || !form.value.end) {
      return message.error("请选择日期和完整的时间段");
    }
    if (form.value.memberCount < 2) {
      return message.error("研讨室至少需要 2 人");
    }
    if (form.value.members.length < form.value.memberCount - 1) {
      return message.error("请填写所有同伴学号");
    }

    const dateStr = dayjs(form.value.date).format("YYYY-MM-DD");
    const startTime = `${dateStr}T${form.value.start.format("HH:mm:ss")}`;
    const endTime = `${dateStr}T${form.value.end.format("HH:mm:ss")}`;

    const myID = String(studentStore.studentInfo.studentID).trim();
    const records: any[] = [];

    // 队长
    records.push({
      studentID: myID,
      seminarRoomID: Number(seatInfo.value.roomId),
      seatID: Number(seatInfo.value.number), 
      reservationStartTime: startTime,
      reservationEndTime: endTime,
      reservationRecordStatus: 1,
      cancelPermission: 1,
    });

    // 同伴
    for (let i = 0; i < form.value.memberCount - 1; i++) {
      const mId = form.value.members[i] ? String(form.value.members[i]).trim() : "";
      if (!mId) return message.error(`请输入第 ${i + 2} 位成员的学号`);
      if (mId === myID) return message.error(`第 ${i + 2} 位同伴学号不能是你自己`);

      records.push({
        studentID: mId,
        seminarRoomID: Number(seatInfo.value.roomId),
        seatID: Number(seatInfo.value.number),
        reservationStartTime: startTime,
        reservationEndTime: endTime,
        reservationRecordStatus: 1,
        cancelPermission: 0,
      });
    }

    try {
      const res = await axios.post("/api/reservation/seminar-room", records);
      if (res.data.code === 200) {
        message.success("研讨室预约成功");
        router.push("/student-reservation");
      } else {
        message.error(res.data.message || "预约失败");
      }
    } catch (e) {
      console.error(e);
      message.error("网络异常，请稍后再试");
    }
    return;
  }

  // ========== 考研专座 ==========
  if (type === "POSTGRAD") {
    if (!form.value.postgradWeek) {
      return message.error("请选择预约周次");
    }

    const w: any = form.value.postgradWeek;
    let start = "";
    let end = "";

    if (typeof w === "string") {
      start = w;
      end = dayjs(w).add(6, "day").format("YYYY-MM-DD");
    } else if (typeof w === "object") {
      start = String(w.start || "");
      end = String(w.end || "");
      if (start && !end) end = dayjs(start).add(6, "day").format("YYYY-MM-DD");
    }

    if (!start || !end || start === "undefined" || end === "undefined") {
      console.error("postgradWeek invalid:", form.value.postgradWeek);
      return message.error("周次数据异常，请重新选择");
    }

    const requestData = [
      {
        studentId: Number(studentStore.studentInfo.studentID),
        studyRoomId: Number(seatInfo.value.roomId),
        seatId: Number(seatInfo.value.seatId),
        startTime: `${start}T08:00:00`,
        endTime: `${end}T22:00:00`,
        date: start,
        timeSlot: "08:00-22:00",
      },
    ];

    try {
      const res = await axios.post("/api/reservation/reserveSeat", requestData);
      if (res.data.code === 200) {
        message.success("考研专座申请已提交，请等待审核");
        router.push("/student-reservation");
      } else {
        message.error(res.data.message || "申请失败");
      }
    } catch (e) {
      console.error(e);
      message.error("网络异常，请稍后再试");
    }
    return;
  }
};

</script>


<style scoped>
.page {
  min-height: 100vh;
  padding: 22px 12px 28px;
  background: linear-gradient(180deg, #f6f8ff 0%, #ffffff 45%, #ffffff 100%);
}

.container {
  max-width: 760px;
  margin: 0 auto;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 14px;
}

.header-top {
  width: 100%;
  display: flex;
  justify-content: flex-start;
}

.back-btn {
  border-radius: 12px;
  height: 36px;
  padding: 0 14px;
}


.title-wrap h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.2px;
  color: #101828;
}

.sub {
  margin-top: 2px;
  font-size: 13px;
  color: #667085;
}

.info-card,
.reserve-card {
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(16, 24, 40, 0.06);
  margin-bottom: 14px;
}

.info-card :deep(.ant-card-body),
.reserve-card :deep(.ant-card-body) {
  padding: 16px 18px;
}

/* 座位信息 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  align-items: center;
}

@media (max-width: 560px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}

.info-item .label {
  font-size: 12px;
  color: #98a2b3;
}

.info-item .value {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 700;
  color: #101828;
}

/* section */
.section {
  margin-bottom: 10px;
}

.section-title {
  font-size: 14px;
  font-weight: 800;
  color: #101828;
  margin-bottom: 8px;
}

.full {
  width: 100%;
}

/* 时段标题 + 图例 */
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
  margin-top: 6px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.legend {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #667085;
}

.chip {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  display: inline-block;
  margin-right: 6px;
  vertical-align: middle;
}

.chip.free {
  background: #ffffff;
  border: 1px solid #d0d5dd;
}
.chip.selected {
  background: #1677ff;
}
.chip.preview {
  background: #e6f4ff;
  border: 1px solid #91caff;
}
.chip.booked {
  background: #ffe58f;
  border: 1px solid #ffd666;
}
.chip.mybooked {
  background: #95de64;
  border: 1px solid #b7eb8f;
}
.chip.past {
  background: #e6e8ee;
}

.selected-alert {
  border-radius: 14px;
  margin: 8px 0 12px;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 10px;
}

@media (max-width: 620px) {
  .time-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}
@media (max-width: 420px) {
  .time-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.time-block {
  height: 36px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 12px;
  font-weight: 650;
  user-select: none;

  border: 1px solid #e4e7ec;
  background: #ffffff;
  color: #101828;

  transition: transform 0.12s ease, box-shadow 0.12s ease, border-color 0.12s ease,
    background-color 0.12s ease;
}

.time-block.FREE {
  cursor: pointer;
}

.time-block.FREE:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(16, 24, 40, 0.08);
  border-color: #91caff;
}

.time-block.PAST,
.time-block.BOOKED,
.time-block.MY_BOOKED {
  cursor: not-allowed;
  color: #667085;
}

/* 状态色 */
.time-block.PAST {
  background-color: #f2f4f7;
}

.time-block.BOOKED {
  background-color: #fff7d6;
  border-color: #ffd666;
}

.time-block.MY_BOOKED {
  background-color: #eaffd6;
  border-color: #b7eb8f;
}


.time-block.WAITING {
  background-color: #e6f7ff ;
  border-color: #91d5ff;
  color: #1890ff;
}

/* 选择态 */
.time-block.SELECTED {
  border-color: #1677ff;
  box-shadow: inset 0 0 0 1px #1677ff;
}

.time-block.PREVIEW {
  background-color: #e6f4ff;
  border-color: #91caff;
}

/* 提示文案 */
.hint {
  margin-top: 10px;
  font-size: 12px;
  color: #667085;
}

/* 研讨室提示 */
.study-alert {
  border-radius: 14px;
  margin-bottom: 12px;
}

/* 考研专座信息 */
.postgrad-title {
  font-size: 14px;
  font-weight: 800;
  color: #101828;
  margin-bottom: 4px;
}

.postgrad-sub {
  font-size: 12px;
  color: #667085;
}

.block {
  margin-bottom: 14px;
}

/* 底部按钮 */
.action-bar {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.primary-btn,
.ghost-btn {
  border-radius: 14px;
  height: 44px;
}
</style>
