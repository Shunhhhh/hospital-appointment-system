<template>
  <div
    class="ai-float"
    :class="{ dragging: isDragging }"
    :style="{ left: posX + 'px', top: posY + 'px' }"
    ref="aiRef"
  >
    <img :src="aiImg" alt="AI助手" class="ai-img" draggable="false" />
    <span class="ai-tip">智能预问诊</span>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import aiImg from '@/assets/AI.png'

const router = useRouter()
const aiRef = ref<HTMLElement | null>(null)

const initRight = 20
const initBottom = 40
const posX = ref(0)
const posY = ref(0)
const isDragging = ref(false)

let startX = 0, startY = 0, startLeft = 0, startTop = 0, moved = false
let maxW = window.innerWidth - 70
let maxH = window.innerHeight - 70

const onDown = (e: PointerEvent) => {
  moved = false
  isDragging.value = true
  startX = e.clientX
  startY = e.clientY
  startLeft = posX.value
  startTop = posY.value
  ;(aiRef.value as HTMLElement).setPointerCapture(e.pointerId)
}

const onMove = (e: PointerEvent) => {
  const dx = e.clientX - startX
  const dy = e.clientY - startY
  posX.value = Math.max(0, Math.min(maxW, startLeft + dx))
  posY.value = Math.max(0, Math.min(maxH, startTop + dy))
  if (Math.abs(dx) > 3 || Math.abs(dy) > 3) moved = true
}

const onUp = () => {
  isDragging.value = false
  if (!moved) router.push('/hospital/pre-diagnosis')
}

const updateBounds = () => {
  maxW = window.innerWidth - 70
  maxH = window.innerHeight - 70
  posX.value = Math.max(0, Math.min(maxW, posX.value))
  posY.value = Math.max(0, Math.min(maxH, posY.value))
}

const initPosition = () => {
  posX.value = window.innerWidth - 70 - initRight
  posY.value = window.innerHeight - 70 - initBottom
}

onMounted(() => {
  initPosition()
  updateBounds()
  const el = aiRef.value
  if (!el) return
  el.addEventListener('pointerdown', onDown)
  el.addEventListener('pointermove', onMove)
  el.addEventListener('pointerup', onUp)
  window.addEventListener('resize', updateBounds)
})

onBeforeUnmount(() => {
  const el = aiRef.value
  if (!el) return
  el.removeEventListener('pointerdown', onDown)
  el.removeEventListener('pointermove', onMove)
  el.removeEventListener('pointerup', onUp)
  window.removeEventListener('resize', updateBounds)
})
</script>

<style scoped>
.ai-float {
  position: fixed;
  z-index: 999;
  cursor: grab;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  touch-action: none;
  user-select: none;
}

.ai-float.dragging {
  cursor: grabbing;
}

.ai-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 14px rgba(22, 119, 255, 0.3);
  border: 2px solid #fff;
  pointer-events: none;
  transition: transform 0.2s ease;
}

.ai-float:not(.dragging):hover .ai-img {
  transform: scale(1.1);
}

.ai-float:not(.dragging):hover .ai-tip {
  transform: scale(1.05);
}

.ai-tip {
  font-size: 11px;
  color: #1677ff;
  background: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  white-space: nowrap;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  pointer-events: none;
  transition: transform 0.2s ease;
}
</style>
