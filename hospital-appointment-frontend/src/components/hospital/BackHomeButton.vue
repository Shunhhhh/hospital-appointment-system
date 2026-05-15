<template>
  <div class="back-home-button">
    <el-button type="primary" plain :icon="ArrowLeft" @click="goHome">
      {{ buttonText }}
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

const canGoBack = computed(() => window.history.length > 1)
const buttonText = computed(() => (canGoBack.value ? '返回上一页' : '返回首页'))

const goHome = () => {
  if (canGoBack.value) {
    router.back()
    return
  }
  router.push('/hospital/home')
}
</script>

<style scoped>
.back-home-button {
  position: fixed;
  bottom: 20px;
  left: 20px;
  z-index: 1000;
}

@media (max-width: 768px) {
  .back-home-button {
    bottom: calc(12px + env(safe-area-inset-bottom));
    left: 12px;
  }
}
</style>
