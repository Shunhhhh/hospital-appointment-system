<template>
  <div>
    <a-card>
      <p>请选择要预约的周：</p>

      <a-select v-model:value="model" style="width:100%">
        <a-select-option
          v-for="week in weeks"
          :key="week.value.start"
          :value="week.value.start"
          :disabled="week.disabled"
        >
          {{ week.label }}
        </a-select-option>
      </a-select>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
dayjs.extend(isoWeek);

type WeekValue = { start: string; end: string };
type WeekOption = { value: WeekValue; label: string; disabled?: boolean };


const props = defineProps<{
  modelValue: WeekValue | null;
  seatId: number;
  studyRoomId: number;
  disabledStarts?: string[]; 
}>();

const emit = defineEmits<{
  (e: "update:modelValue", v: WeekValue | null): void;
}>();

const model = computed<WeekValue | null>({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

const weeks = computed<WeekOption[]>(() => {
  const list: WeekOption[] = [];
  const disabledSet = new Set(props.disabledStarts ?? []);
  const now = dayjs();

// 本周周一（周一~周日）
const thisMonday = dayjs().startOf("isoWeek").startOf("day");

// 本周周日 22:00（窗口切换点）
const thisSunday22 = thisMonday
  .add(6, "day")
  .hour(22)
  .minute(0)
  .second(0)
  .millisecond(0);

// 周日22点前可约“下周”，周日22点后可约“下下周”
  const firstStart = now.isBefore(thisSunday22)
  ? thisMonday.add(1, "week")  // 下周周一
  : thisMonday.add(2, "week"); // 下下周周一


  for (let i = 0; i < 3; i++) {
    const start = firstStart.add(i, "week"); // 周一
    const end = start.add(6, "day");         // 周日

    const startStr = start.format("YYYY-MM-DD");
    const endStr = end.format("YYYY-MM-DD");

    list.push({
      value: { start: startStr, end: endStr },
      label: `${start.format("MM/DD")} - ${end.format("MM/DD")}`,
      disabled: disabledSet.has(startStr), 
    });
  }

  return list;
});
</script>

