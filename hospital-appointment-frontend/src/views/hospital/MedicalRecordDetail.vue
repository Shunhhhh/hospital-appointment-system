<template>
  <div class="detail-page">
    <div class="page-shell">
      <SidebarNav />
      <main class="main-area">
        <!-- 返回 -->
        <div class="back-row">
          <el-button text @click="$router.push('/hospital/medical-records')">
            <el-icon><ArrowLeft /></el-icon> 返回门诊记录
          </el-button>
        </div>

        <div v-loading="loading">
          <template v-if="record">
            <!-- 就诊信息 -->
            <div class="info-card">
              <div class="card-title">就诊信息</div>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">就诊医生</span>
                  <span class="info-value">{{ record.doctorName || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">就诊科室</span>
                  <span class="info-value">{{ record.departmentName || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">就诊时间</span>
                  <span class="info-value">{{ formatDateTime(record.createTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">挂号编号</span>
                  <span class="info-value mono">{{ record.appointmentID }}</span>
                </div>
              </div>
            </div>

            <!-- 病历内容 -->
            <div class="content-card">
              <div class="card-title">病历详情</div>

              <div class="field-block" v-if="record.chiefComplaint">
                <div class="field-label">主诉</div>
                <div class="field-value">{{ record.chiefComplaint }}</div>
              </div>

              <div class="field-block" v-if="record.presentIllness">
                <div class="field-label">现病史</div>
                <div class="field-value">{{ record.presentIllness }}</div>
              </div>

              <div class="field-block" v-if="record.pastHistory">
                <div class="field-label">既往史</div>
                <div class="field-value">{{ record.pastHistory }}</div>
              </div>

              <div class="field-block" v-if="record.allergyHistory">
                <div class="field-label">过敏史</div>
                <div class="field-value">{{ record.allergyHistory }}</div>
              </div>

              <div class="field-block" v-if="record.physicalExamination">
                <div class="field-label">体格检查</div>
                <div class="field-value">{{ record.physicalExamination }}</div>
              </div>

              <div class="field-block" v-if="record.auxiliaryExamination">
                <div class="field-label">辅助检查</div>
                <div class="field-value">{{ record.auxiliaryExamination }}</div>
              </div>

              <div class="field-block highlight" v-if="record.preliminaryDiagnosis || record.finalDiagnosis">
                <div class="field-label">诊断</div>
                <div class="field-value diagnosis-text">
                  {{ record.finalDiagnosis || record.preliminaryDiagnosis }}
                </div>
              </div>

              <div class="field-block" v-if="record.treatmentPlan">
                <div class="field-label">治疗方案</div>
                <div class="field-value">{{ record.treatmentPlan }}</div>
              </div>

              <div class="field-block" v-if="record.medicalAdvice">
                <div class="field-label">医嘱</div>
                <div class="field-value">{{ record.medicalAdvice }}</div>
              </div>

              <div class="field-block" v-if="record.remarks">
                <div class="field-label">备注</div>
                <div class="field-value">{{ record.remarks }}</div>
              </div>
            </div>

            <!-- 处方清单 -->
            <div class="content-card" v-if="prescriptions.length > 0">
              <div class="card-title">处方清单</div>
              <div class="prescription-table">
                <div class="pres-header">
                  <span class="col-name">药品名称</span>
                  <span class="col-spec">规格</span>
                  <span class="col-dosage">用法用量</span>
                  <span class="col-qty">数量</span>
                </div>
                <div
                  v-for="p in prescriptions"
                  :key="p.prescriptionID"
                  class="pres-row"
                >
                  <span class="col-name">{{ p.medicineName }}</span>
                  <span class="col-spec">{{ p.medicineSpec || '-' }}</span>
                  <span class="col-dosage">
                    {{ p.dosage || '' }} {{ p.usage || '' }} {{ p.frequency || '' }}
                    <span v-if="p.course" class="course">· {{ p.course }}</span>
                  </span>
                  <span class="col-qty">{{ p.quantity || '-' }}{{ p.unit || '' }}</span>
                </div>
              </div>
            </div>
          </template>

          <el-empty v-if="!loading && !record" description="病历不存在" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import SidebarNav from '@/components/hospital/SidebarNav.vue'
import { medicalRecordAPI, type MedicalRecord, type Prescription } from '@/api/hospital/medicalRecord'

const route = useRoute()
const loading = ref(false)
const record = ref<MedicalRecord | null>(null)
const prescriptions = ref<Prescription[]>([])

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${min}`
}

const loadDetail = async () => {
  const recordId = Number(route.params.recordId)
  if (!recordId) {
    ElMessage.error('参数错误')
    return
  }
  loading.value = true
  try {
    const res = await medicalRecordAPI.getDetail(recordId)
    if (res.code === 200 && res.data) {
      record.value = res.data.record
      prescriptions.value = res.data.prescriptions || []
    }
  } catch {
    ElMessage.error('加载病历详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f8fc;
}

.page-shell {
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px;
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 16px;
}

.main-area {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.back-row {
  margin-bottom: 4px;
}

.info-card,
.content-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(31, 41, 55, 0.04);
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #9ca3af;
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.info-value.mono {
  font-family: monospace;
  font-size: 12px;
  color: #6b7280;
}

.field-block {
  margin-bottom: 16px;
}

.field-block.highlight {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 20px;
}

.field-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
  font-weight: 600;
  text-transform: uppercase;
}

.field-value {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.7;
}

.diagnosis-text {
  font-size: 16px;
  font-weight: 700;
  color: #b45309;
}

/* 处方表格 */
.prescription-table {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

.pres-header {
  display: flex;
  padding: 10px 14px;
  background: #f9fafb;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  border-bottom: 1px solid #e5e7eb;
}

.pres-row {
  display: flex;
  padding: 12px 14px;
  font-size: 14px;
  color: #1f2937;
  border-bottom: 1px solid #f3f4f6;
}

.pres-row:last-child {
  border-bottom: none;
}

.col-name {
  flex: 0 0 180px;
  font-weight: 600;
}

.col-spec {
  flex: 0 0 100px;
  color: #6b7280;
}

.col-dosage {
  flex: 1;
  min-width: 0;
}

.course {
  color: #6b7280;
}

.col-qty {
  flex: 0 0 80px;
  text-align: right;
  color: #6b7280;
}

@media (max-width: 960px) {
  .page-shell {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .pres-header,
  .pres-row {
    flex-wrap: wrap;
    gap: 8px;
  }

  .col-name {
    flex: 1 1 100%;
  }

  .col-spec,
  .col-dosage,
  .col-qty {
    flex: 0 0 auto;
  }
}
</style>
