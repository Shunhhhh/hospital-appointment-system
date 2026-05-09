<template>
  <a-card :title="room.studyRoomID">
    <p>位置：{{ room.studyRoomLocation }}</p>
    <p>容量：{{ room.studyRoomCapacity }}</p>
    <p>当前可用座位：{{ room.currentlyIdleSeat }}</p>

    <a-tag v-if="room.status === 0" color="green">关闭中</a-tag>
    <a-tag v-if="room.status === 1" color="blue">开放中</a-tag>
    <a-tag v-if="room.status === 2" color="red">维护中</a-tag>

    <div style="margin-top:10px">
      <a-button type="primary" block @click="$emit('enter')">
        查看座位情况
      </a-button>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { StudyRoom } from '@/api/data';

defineProps<{ room: StudyRoom }>();
</script>
