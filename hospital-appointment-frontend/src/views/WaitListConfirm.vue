<template>
  <a-modal :open="true" title="候补成功" :footer="null">
    <p>有座位提前释放！请在 15 分钟内确认，否则将自动取消。</p>

    <h3 style="margin-top:10px">
      剩余时间：{{ countdown }} 秒
    </h3>

    <div style="margin-top:20px; display:flex; gap:10px">
      <a-button type="primary" block @click="confirmSeat">确认占用</a-button>
      <a-button danger block @click="rejectSeat">放弃</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";

// 💡 假设通过 props 传入当前记录的 ID
const props = defineProps<{
  waitlistId: string;
}>();

const emit = defineEmits(['close', 'refresh']);
const countdown = ref(900);
let timer: any;

onMounted(() => {
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      rejectSeat(); // 超时自动放弃
    }
  }, 1000);
});

onUnmounted(() => clearInterval(timer));


const confirmSeat = async () => {
  try {
    const res = await axios.post(`/api/wait/confirmWaitlist?waitlistRecordId=${props.waitlistId}`);
    if (res.data.code === 200) {
      message.success("确认成功，已为您转为正式预约！");
      emit('refresh');
      emit('close');
    }
  } catch (e) {
    message.error("确认失败");
  }
};


const rejectSeat = async () => {
  try {
    await axios.post(`/api/wait/cancelWaitlist?waitlistRecordId=${props.waitlistId}`);
    message.info("已放弃候补名额");
    emit('close');
  } catch (e) {
    console.error(e);
  }
};
</script>