<template>
  <div class="appointment-workbench">
        <div class="page-head card-base">
          <div class="page-head-main">
            <div class="page-title-row">
              <h1 class="page-title">{{ currentStep === 'doctorSearch' ? '医生查询' : '预约挂号' }}</h1>
              <span class="page-subtitle">
                {{ currentStep === 'doctorSearch' ? '按医生、擅长、职称快速筛选专家' : '先选日期和科室，再选医生号源' }}
              </span>
            </div>
          </div>
          <div v-if="currentStep !== 'doctorSearch'" class="page-head-tags">
            <span class="page-tag active">{{ selectedDateLabel }}</span>
          </div>
        </div>

        <section v-if="currentStep !== 'doctorSearch'" class="filter-card card-base">
          <div class="filter-row">
            <el-input
              v-model="searchKeyword"
              class="service-search"
              placeholder="搜索挂号科室、医生"
              clearable
              @keyup.enter="performSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <div class="date-tabs">
            <button
              v-for="date in dateOptions"
              :key="date.key"
              type="button"
              class="date-tab"
              :class="{ active: selectedDateKey === date.key }"
              @click="handleDateChange(date.key)"
            >
              <span class="date-label">{{ date.label }}</span>
              <span class="date-value">{{ date.value }}</span>
            </button>
          </div>
        </section>

        <section v-if="currentStep === 'doctorSearch'" class="doctor-search-page">
          <div class="doctor-search-card card-base">
            <div class="doctor-search-row">
              <el-input
                v-model="doctorSearchKeyword"
                class="doctor-search-input"
                placeholder="搜索医生、擅长"
                clearable
                @keyup.enter="performDoctorSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" class="doctor-search-button" @click="performDoctorSearch">搜索</el-button>
            </div>

            <div class="doctor-filter-strip">
              <el-select v-model="doctorSearchSort" class="doctor-filter-select" placeholder="默认排序">
                <el-option label="默认排序" value="default" />
                <el-option label="按近预约量" value="appointments" />
                <el-option label="按余号量" value="available" />
              </el-select>
              <el-select v-model="doctorSearchTitle" class="doctor-filter-select" placeholder="医生职称">
                <el-option label="医生职称" value="全部" />
                <el-option label="住院医师" value="住院医师" />
                <el-option label="主治医师" value="主治医师" />
                <el-option label="主任中医师" value="主任中医师" />
                <el-option label="主任医师" value="主任医师" />
                <el-option label="主任药师" value="主任药师" />
              </el-select>
            </div>
          </div>

          <div class="doctor-search-results card-base">
            <article v-for="doctor in filteredDoctorSearchResults" :key="doctor.id" class="search-doctor-item">
              <el-avatar :size="72" :src="doctor.avatar" class="search-doctor-avatar">{{ doctor.name.slice(0, 1) }}</el-avatar>
              <div class="search-doctor-main">
                <div class="search-doctor-head">
                  <h2>{{ doctor.name }}</h2>
                  <span>{{ doctor.title }}</span>
                </div>
                <div class="search-doctor-hospital">
                  宁波大学附属第一医院
                  <el-tag type="success" effect="light" size="small">三甲</el-tag>
                </div>
                <div class="search-doctor-dept">{{ doctor.department }}</div>
                <p class="search-doctor-intro">
                  <span>简介</span>
                  {{ doctor.introduction }}
                </p>
                <div class="search-doctor-stats">
                  <span class="search-rate">★ {{ doctor.goodRate }}</span>
                  <span>年度预约量：<strong>{{ doctor.annualAppointments }}</strong></span>
                  <span>可约余号：<strong>{{ getDoctorAvailableSlots(doctor.id) }}</strong></span>
                </div>
              </div>
              <div class="search-doctor-actions">
                <el-button plain @click="openDoctorDetail(doctor)">查看简介</el-button>
                <el-button type="primary" @click="openDoctorDetail(doctor)">预约</el-button>
              </div>
            </article>
            <el-empty v-if="filteredDoctorSearchResults.length === 0" description="暂无匹配医生" />
          </div>
        </section>

        <section v-if="currentStep === 'department'" class="department-layout">
          <div class="primary-panel">
            <button
              v-for="dept in primaryDepartments"
              :key="dept.id"
              type="button"
              class="primary-item"
              :class="{ active: dept.id === selectedPrimaryDepartmentId }"
              @click="selectPrimaryDepartment(dept)"
            >
              {{ dept.name }}
            </button>
          </div>

          <div class="secondary-panel card-base">
            <button
              v-for="sub in currentSecondaryDepartments"
              :key="sub.id"
              type="button"
              class="secondary-item"
              @click="openDoctorList(sub)"
            >
              <span>{{ sub.name }}</span>
              <span class="arrow">&gt;</span>
            </button>
          </div>

          <button type="button" class="helper-button">
            <el-icon><ChatDotRound /></el-icon>
            就医小帮手
          </button>
        </section>

        <section v-if="currentStep === 'doctorList'" class="doctor-layout">
          <div class="doctor-list-main">
            <div class="doctor-list-head card-base">
              <el-button text class="back-button" @click="goDepartmentStep">← 返回科室选择</el-button>
              <div class="doctor-list-title-row">
                <h2 class="doctor-list-title">{{ currentSubDepartmentName }}</h2>
                <el-tag type="info" effect="plain">12周岁及以上</el-tag>
              </div>
            </div>

            <div class="doctor-cards">
              <article v-for="doctor in filteredDoctors" :key="doctor.id" class="doctor-card card-base">
                <div class="doctor-main-row">
                  <el-avatar :size="64" :src="doctor.avatar" class="doctor-avatar">{{ doctor.name.slice(0, 1) }}</el-avatar>
                  <div class="doctor-body">
                    <div class="doctor-row-top">
                      <div>
                        <div class="doctor-name">{{ doctor.name }}</div>
                        <div class="doctor-meta">{{ doctor.title }} · {{ doctor.department }}</div>
                      </div>
                      <el-button text class="intro-link" @click="openDoctorDetail(doctor)">简介</el-button>
                    </div>

                    <div class="doctor-skill">{{ doctor.specialty }}</div>

                    <div class="doctor-stats-row">
                      <div class="score">
                        <span class="star">★</span>
                        {{ doctor.goodRate }}
                      </div>
                      <div class="volume">年度预约量：{{ doctor.annualAppointments }}</div>
                    </div>
                  </div>
                  <div class="doctor-actions">
                    <el-button
                      v-if="doctor.bookingStatus === 'full'"
                      class="status-btn full"
                      plain
                      disabled
                    >已约满</el-button>
                    <el-button
                      v-else-if="doctor.bookingStatus === 'am'"
                      class="status-btn"
                      plain
                      @click="openDoctorDetail(doctor)"
                    >上午可预约</el-button>
                    <el-button
                      v-else-if="doctor.bookingStatus === 'pm'"
                      class="status-btn"
                      plain
                      @click="openDoctorDetail(doctor)"
                    >下午可预约</el-button>
                    <el-button v-else type="primary" @click="openDoctorDetail(doctor)">选择预约</el-button>
                  </div>
                </div>
              </article>

              <el-empty v-if="filteredDoctors.length === 0" description="当前条件下暂无医生" />
            </div>
          </div>

          <aside class="doctor-filter card-base">
            <h3>筛选条件</h3>
            <div class="filter-group">
              <label>职称筛选</label>
              <el-radio-group v-model="titleFilter">
                <el-radio-button label="全部" />
                <el-radio-button label="主任医师" />
                <el-radio-button label="副主任医师" />
                <el-radio-button label="主治医师" />
              </el-radio-group>
            </div>
            <div class="filter-group">
              <label>可预约状态</label>
              <el-radio-group v-model="availabilityFilter">
                <el-radio-button label="全部" />
                <el-radio-button label="仅看可约" />
              </el-radio-group>
            </div>
            <div class="filter-group">
              <label>排序</label>
              <el-select v-model="sortBy" class="sort-select">
                <el-option label="综合排序" value="default" />
                <el-option label="预约量优先" value="appointments" />
                <el-option label="好评率优先" value="rating" />
              </el-select>
            </div>
          </aside>
        </section>

        <section v-if="currentStep === 'doctorDetail'" class="detail-layout">
          <div class="detail-top card-base">
            <el-button text class="back-button" @click="goDoctorListStep">← 返回医生列表</el-button>
          </div>

          <div class="detail-main-grid">
            <div class="detail-left">
              <div class="doctor-highlight">
                <div class="doctor-highlight-inner card-base">
                  <el-avatar :size="72" :src="activeDoctor?.avatar" class="doctor-avatar">{{ activeDoctor?.name?.slice(0, 1) }}</el-avatar>
                  <div class="doctor-highlight-body">
                    <h2>{{ activeDoctor?.name }}</h2>
                    <div class="doctor-highlight-meta">{{ activeDoctor?.title }} · {{ activeDoctor?.department }}</div>
                    <p class="doctor-highlight-intro">{{ activeDoctor?.introduction }}</p>
                    <div class="metric-row">
                      <div class="metric-item orange">
                        <div class="metric-value">{{ activeDoctor?.goodRate }}</div>
                        <div class="metric-label">好评率</div>
                      </div>
                      <div class="metric-item blue">
                        <div class="metric-value">{{ activeDoctor?.annualAppointments }}</div>
                        <div class="metric-label">年度预约量</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="source-card card-base">
              <div class="source-head">
                <div>
                  <h3>号源预约</h3>
                  <p>选择时段完成挂号</p>
                </div>
              </div>

              <div v-if="visibleSchedules.length" class="source-list">
                <article v-for="schedule in visibleSchedules" :key="schedule.id" class="source-item">
                  <div class="source-primary">
                    <div class="source-date">{{ schedule.displayDate }} {{ schedule.period }}</div>
                    <div class="source-clinic">{{ schedule.clinicName }}</div>
                  </div>
                  <div class="source-type">{{ schedule.registrationType }}</div>
                  <div class="source-fee">{{ schedule.fee.toFixed(2) }}元</div>
                  <div class="source-actions">
                    <el-button
                      v-if="schedule.remaining === 0"
                      class="remain-btn full"
                      plain
                      disabled
                    >余 0</el-button>
                    <el-button v-else class="remain-btn" plain>
                      余 {{ schedule.remaining }}
                    </el-button>
                    <el-button
                      class="booking-action"
                      :type="schedule.remaining > 0 ? 'primary' : 'info'"
                      :disabled="schedule.remaining === 0"
                      @click="handleBooking(schedule)"
                    >
                      {{ schedule.remaining === 0 ? '已约满' : '预约' }}
                    </el-button>
                  </div>
                </article>
              </div>

              <el-empty v-else description="当前日期暂无可预约号源，请选择其他日期" />
            </div>
          </div>
        </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Calendar,
  ChatDotRound,
  FirstAidKit,
  House,
  OfficeBuilding,
  Search,
  Tickets,
  User
} from '@element-plus/icons-vue'
import { appointmentAPI } from '@/api/hospital/appointment'
import { departmentAPI } from '@/api/hospital/department'
import { doctorAPI } from '@/api/hospital/doctor'
import { scheduleAPI } from '@/api/hospital/schedule'

type SubDepartment = {
  id: string
  name: string
}

type Department = {
  id: string
  name: string
  children: SubDepartment[]
}

type DoctorItem = {
  id: number
  subDepartmentId: string
  name: string
  avatar: string
  title: string
  department: string
  specialty: string
  introduction: string
  goodRate: string
  annualAppointments: number
  bookingStatus: 'full' | 'am' | 'pm' | 'available'
}

type ScheduleItem = {
  id: number
  doctorId: number
  dateKey: string
  displayDate: string
  period: '上午' | '下午'
  clinicName: string
  total: number
  remaining: number
  registrationType: string
  fee: number
}

const router = useRouter()
const route = useRoute()

const topMenus = [
  { label: '首页', path: '/hospital/home' },
  { label: '预约挂号', path: '/hospital/appointment/departments' },
  { label: '找医生', path: '/hospital/doctor-search' },
  { label: '我的预约', path: '/hospital/my-appointments' },
  { label: '个人中心', path: '/hospital/profile' }
]

const weekdayLabels = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

const toDateKey = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const toMonthDay = (date: Date) => {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
}

const buildDateOptions = () => {
  const today = new Date()
  const options = [{ key: 'all', label: '全部日期', value: '' }]
  for (let index = 0; index < 6; index += 1) {
    const date = new Date(today)
    date.setDate(today.getDate() + index)
    options.push({
      key: toDateKey(date),
      label: `${weekdayLabels[date.getDay()]}${index === 0 ? '(今)' : ''}`,
      value: toMonthDay(date)
    })
  }
  return options
}

const dateOptions = computed(() => buildDateOptions())
const todayDateKey = toDateKey(new Date())

const selectedDateKey = ref(todayDateKey)
const searchKeyword = ref('')
const doctorSearchKeyword = ref('')
const doctorSearchSort = ref<'default' | 'appointments' | 'available'>('default')
const doctorSearchTitle = ref('全部')

const selectedDateLabel = computed(() => {
  const found = dateOptions.value.find(item => item.key === selectedDateKey.value)
  return found ? `${found.label} ${found.value}`.trim() : '全部日期'
})

const departmentData = reactive<Department[]>([
  {
    id: 'cardio-internal',
    name: '心血管内科',
    children: [
      { id: 'cardio-general', name: '心血管' },
      { id: 'cardio-syncope', name: '晕厥起搏门诊' },
      { id: 'cardio-chd', name: '冠心病门诊' },
      { id: 'cardio-arrhythmia', name: '心律失常门诊' },
      { id: 'cardio-hypertension', name: '高血压门诊' }
    ]
  },
  { id: 'hematology', name: '血液科', children: [{ id: 'hema-general', name: '血液普通门诊' }] },
  { id: 'psychosomatic', name: '心身医学科', children: [{ id: 'psy-general', name: '心身门诊' }] },
  { id: 'respiratory', name: '呼吸内科/呼吸与危重症', children: [{ id: 'resp-general', name: '呼吸专病门诊' }] },
  { id: 'digestive', name: '消化内科', children: [{ id: 'dig-general', name: '消化门诊' }] },
  { id: 'endocrine', name: '内分泌科', children: [{ id: 'endo-general', name: '内分泌门诊' }] },
  { id: 'neuro', name: '神经内科', children: [{ id: 'neuro-general', name: '神经内科门诊' }] },
  { id: 'rheumatology', name: '风湿免疫科', children: [{ id: 'rheu-general', name: '风湿免疫门诊' }] },
  { id: 'geriatric', name: '老年医学科', children: [{ id: 'geri-general', name: '老年医学门诊' }] },
  { id: 'general', name: '全科医学科', children: [{ id: 'gen-general', name: '全科门诊' }] },
  { id: 'pediatrics', name: '儿科', children: [{ id: 'ped-general', name: '儿科门诊' }] },
  { id: 'rehab', name: '理疗康复针灸科', children: [{ id: 'rehab-general', name: '康复门诊' }] }
])

const doctors = reactive<DoctorItem[]>([
  {
    id: 101,
    subDepartmentId: 'cardio-general',
    name: '王世奇',
    avatar: '',
    title: '主任医师',
    department: '心血管内科',
    specialty: '擅长复杂冠心病介入治疗、心律失常射频消融及急危重症综合救治。',
    introduction:
      '长期从事冠心病及缓慢型心律失常的介入诊疗工作，擅长各种复杂冠心病的介入治疗、永久起搏器植入术、心内科急危重症的救治以及心内科各类疾病的临床诊疗工作。',
    goodRate: '100%',
    annualAppointments: 2385,
    bookingStatus: 'available'
  },
  {
    id: 102,
    subDepartmentId: 'cardio-general',
    name: '林若舟',
    avatar: '',
    title: '副主任医师',
    department: '心血管内科',
    specialty: '擅长高血压规范化治疗、冠脉CTA评估及心衰长期管理。',
    introduction: '主攻高血压与冠脉疾病评估，注重个体化降压与慢病随访。',
    goodRate: '99%',
    annualAppointments: 1980,
    bookingStatus: 'am'
  },
  {
    id: 103,
    subDepartmentId: 'cardio-general',
    name: '沈越',
    avatar: '',
    title: '主治医师',
    department: '心血管内科',
    specialty: '擅长心律失常、胸痛鉴别及冠心病早筛管理。',
    introduction: '长期参与胸痛中心值守，擅长快速评估胸痛与心律异常病例。',
    goodRate: '98%',
    annualAppointments: 1420,
    bookingStatus: 'pm'
  },
  {
    id: 104,
    subDepartmentId: 'cardio-general',
    name: '周铭',
    avatar: '',
    title: '主任医师',
    department: '心血管内科',
    specialty: '擅长顽固性高血压、冠心病多学科联合诊治。',
    introduction: '深耕心血管疑难病例与多学科诊疗。',
    goodRate: '97%',
    annualAppointments: 2210,
    bookingStatus: 'full'
  },
  {
    id: 201,
    subDepartmentId: 'neuro-general',
    name: '艾园园',
    avatar: '',
    title: '主治医师',
    department: '神经内科',
    specialty: '擅长头痛、眩晕、睡眠障碍及脑血管病长期管理。',
    introduction: '神经内科主治医师，长期参与头痛眩晕专病门诊，注重慢病随访和个体化用药指导。',
    goodRate: '100%',
    annualAppointments: 86,
    bookingStatus: 'available'
  },
  {
    id: 202,
    subDepartmentId: 'eye-general',
    name: '包亮',
    avatar: '',
    title: '住院医师',
    department: '眼科',
    specialty: '擅长眼科常见病、视疲劳、干眼及屈光问题评估。',
    introduction: '眼科住院医师，参与门诊眼表疾病与屈光异常评估，提供规范化检查和初步诊疗建议。',
    goodRate: '100%',
    annualAppointments: 166,
    bookingStatus: 'am'
  },
  {
    id: 203,
    subDepartmentId: 'endo-general',
    name: '包俊炜',
    avatar: '',
    title: '主治医师',
    department: '内分泌科',
    specialty: '擅长糖尿病、甲状腺疾病、痛风及代谢综合征诊疗。',
    introduction: '主治医师，熟悉内分泌常见病和慢病管理路径，擅长结合检查指标制定长期治疗方案。',
    goodRate: '100%',
    annualAppointments: 111,
    bookingStatus: 'pm'
  },
  {
    id: 204,
    subDepartmentId: 'kidney-general',
    name: '包斯增',
    avatar: '',
    title: '主治医师',
    department: '肾内科',
    specialty: '擅长泌尿系感染、急慢性肾炎、肾病综合征及肾功能异常评估。',
    introduction: '中共党员，主治医师，能熟练诊治各类泌尿系统感染、肾炎及慢性肾病相关问题。',
    goodRate: '100%',
    annualAppointments: 602,
    bookingStatus: 'available'
  },
  {
    id: 205,
    subDepartmentId: 'ent-general',
    name: '包卫亮',
    avatar: '',
    title: '主任医师',
    department: '耳鼻咽喉头颈外科',
    specialty: '擅长鼻窦炎、咽喉疾病、儿童耳鼻喉科门诊及头颈部常见病。',
    introduction: '耳鼻咽喉头颈外科主任医师，长期从事耳鼻喉门诊与疑难病例评估。',
    goodRate: '99%',
    annualAppointments: 760,
    bookingStatus: 'full'
  },
  {
    id: 206,
    subDepartmentId: 'pharmacy-general',
    name: '陈知行',
    avatar: '',
    title: '主任药师',
    department: '药学门诊',
    specialty: '擅长慢病联合用药评估、药物相互作用咨询及用药风险管理。',
    introduction: '主任药师，提供慢病用药咨询、处方用药风险评估和特殊人群用药指导。',
    goodRate: '98%',
    annualAppointments: 320,
    bookingStatus: 'available'
  }
])

const schedules = reactive<ScheduleItem[]>([
  {
    id: 1,
    doctorId: 101,
    dateKey: '2026-06-02',
    displayDate: '06月02日 周二',
    period: '上午',
    clinicName: '心血管',
    total: 50,
    remaining: 36,
    registrationType: '专家门诊',
    fee: 30.3
  },
  {
    id: 2,
    doctorId: 101,
    dateKey: '2026-06-02',
    displayDate: '06月02日 周二',
    period: '下午',
    clinicName: '心血管',
    total: 50,
    remaining: 0,
    registrationType: '专家门诊',
    fee: 30.3
  },
  {
    id: 3,
    doctorId: 101,
    dateKey: '2026-06-03',
    displayDate: '06月03日 周三',
    period: '上午',
    clinicName: '心血管',
    total: 50,
    remaining: 19,
    registrationType: '专家门诊',
    fee: 30.3
  },
  {
    id: 4,
    doctorId: 101,
    dateKey: '2026-06-04',
    displayDate: '06月04日 周四',
    period: '上午',
    clinicName: '心血管',
    total: 30,
    remaining: 12,
    registrationType: '专家门诊',
    fee: 30.3
  },
  {
    id: 5,
    doctorId: 102,
    dateKey: '2026-06-02',
    displayDate: '06月02日 周二',
    period: '上午',
    clinicName: '心血管',
    total: 40,
    remaining: 9,
    registrationType: '专家门诊',
    fee: 24.5
  }
])

const applyDynamicMockScheduleDates = () => {
  const upcomingDates = buildDateOptions().filter(item => item.key !== 'all')
  const scheduleDateIndexes = [0, 0, 1, 2, 0]
  schedules.forEach((schedule, index) => {
    const dateOption = upcomingDates[scheduleDateIndexes[index] ?? 0]
    if (!dateOption) return
    schedule.dateKey = dateOption.key
    const weekday = dateOption.label.replace('(今)', '')
    schedule.displayDate = `${dateOption.value.replace('-', '月')}日 ${weekday}`
  })
}

applyDynamicMockScheduleDates()

const selectedPrimaryDepartmentId = ref(departmentData[0].id)
const selectedSubDepartmentId = ref(departmentData[0].children[0].id)
const currentSubDepartmentName = ref(departmentData[0].children[0].name)
const titleFilter = ref<'全部' | '主任医师' | '副主任医师' | '主治医师'>('全部')
const availabilityFilter = ref<'全部' | '仅看可约'>('全部')
const sortBy = ref<'default' | 'appointments' | 'rating'>('default')
const activeDoctorId = ref<number>(101)

const departmentIdFallbackMap: Record<string, string> = {
  '3': 'pediatrics',
  '11': 'cardio-internal',
  '12': 'hematology',
  '13': 'psychosomatic',
  '14': 'respiratory',
  '15': 'digestive',
  '16': 'endocrine',
  '17': 'neuro',
  '18': 'rheumatology',
  '19': 'geriatric',
  '20': 'general',
  '21': 'rehab'
}

const resolveDepartmentRouteId = (id: string) => {
  if (!id) return ''
  if (departmentData.some(item => item.id === id)) return id
  return departmentIdFallbackMap[id] || id
}

const getCurrentPatientId = () => {
  const raw = localStorage.getItem('hospital_user')
  if (!raw) return null
  try {
    const user = JSON.parse(raw)
    return user.patientID ? Number(user.patientID) : null
  } catch {
    return null
  }
}

const formatScheduleDate = (dateText: string) => {
  const date = new Date(`${dateText}T00:00:00`)
  if (Number.isNaN(date.getTime())) return dateText
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}月${day}日 ${weekdayLabels[date.getDay()]}`
}

const getDateKeyAfter = (offset: number) => {
  const date = new Date()
  date.setDate(date.getDate() + offset)
  return toDateKey(date)
}

const mapRegistrationType = (type: number) => {
  if (type === 2) return '专家门诊'
  if (type === 3) return '特需门诊'
  return '普通门诊'
}

const resolveDoctorBookingStatus = (doctorId: number): DoctorItem['bookingStatus'] => {
  const doctorSchedules = schedules.filter(item => item.doctorId === doctorId && item.remaining > 0)
  if (doctorSchedules.length === 0) return 'full'
  const hasAM = doctorSchedules.some(item => item.period === '上午')
  const hasPM = doctorSchedules.some(item => item.period === '下午')
  if (hasAM && hasPM) return 'available'
  if (hasAM) return 'am'
  if (hasPM) return 'pm'
  return 'available'
}

const loadBackendAppointmentData = async () => {
  let loadedAnyBackendData = false

  const [departmentResult, doctorResult, scheduleResult] = await Promise.allSettled([
    departmentAPI.getList(),
    doctorAPI.getList(),
    scheduleAPI.getAvailable({})
  ])

  const departmentRes = departmentResult.status === 'fulfilled' ? departmentResult.value : null
  const doctorRes = doctorResult.status === 'fulfilled' ? doctorResult.value : null
  const scheduleRes = scheduleResult.status === 'fulfilled' ? scheduleResult.value : null

  if (departmentRes?.code === 200 && departmentRes.data?.length) {
    const mappedDepartments = departmentRes.data
      .filter(item => item.departmentStatus === 1)
      .map(item => ({
        id: String(item.departmentID),
        name: item.departmentName,
        children: [{ id: String(item.departmentID), name: item.departmentName }]
      }))
    departmentData.splice(0, departmentData.length, ...mappedDepartments)
    selectedPrimaryDepartmentId.value = departmentData[0]?.id || ''
    selectedSubDepartmentId.value = departmentData[0]?.children[0]?.id || ''
    currentSubDepartmentName.value = departmentData[0]?.children[0]?.name || ''
    loadedAnyBackendData = true
  }

  if (scheduleRes?.code === 200 && scheduleRes.data?.length) {
    const endDateKey = getDateKeyAfter(13)
    const mappedSchedules = scheduleRes.data
      .filter(item => item.scheduleDate >= todayDateKey && item.scheduleDate <= endDateKey)
      .map(item => ({
        id: item.scheduleID,
        doctorId: item.doctorID,
        dateKey: item.scheduleDate,
        displayDate: formatScheduleDate(item.scheduleDate),
        period: item.timeSlot === 2 ? '下午' as const : '上午' as const,
        clinicName: item.departmentName || '门诊',
        total: item.totalSlots,
        remaining: item.remainingSlots,
        registrationType: mapRegistrationType(item.registrationType),
        fee: Number(item.price || 0)
      }))
    schedules.splice(0, schedules.length, ...mappedSchedules)
    loadedAnyBackendData = true
  }

  if (doctorRes?.code === 200 && doctorRes.data?.length) {
    const mappedDoctors = doctorRes.data
      .filter(item => item.doctorStatus === 1)
      .map(item => ({
        id: item.doctorID,
        subDepartmentId: String(item.departmentID),
        name: item.doctorName,
        avatar: item.doctorPhoto || '',
        title: item.title,
        department: item.departmentName || '门诊科室',
        specialty: item.specialty || '擅长常见病、多发病诊疗和慢病随访管理。',
        introduction: item.doctorIntro || item.specialty || '暂无简介',
        goodRate: '100%',
        annualAppointments: 600 + (item.doctorID % 1000) * 17,
        bookingStatus: resolveDoctorBookingStatus(item.doctorID)
      }))
    doctors.splice(0, doctors.length, ...mappedDoctors)
    if (!doctors.find(item => item.id === activeDoctorId.value)) {
      activeDoctorId.value = doctors[0]?.id || 0
    }
    loadedAnyBackendData = true
  }

  syncStateWithRoute()

  if (!loadedAnyBackendData) {
    ElMessage.warning('后端科室医生数据加载失败，已使用页面内置数据')
  }
}

const currentStep = computed<'department' | 'doctorList' | 'doctorDetail' | 'doctorSearch'>(() => {
  if (route.path.includes('/doctor-search')) {
    return 'doctorSearch'
  }
  if (route.path.includes('/appointment/doctor-detail') || route.path.includes('/hospital/schedule')) {
    return 'doctorDetail'
  }
  if (route.path.includes('/appointment/doctors')) {
    return 'doctorList'
  }
  return 'department'
})

const primaryDepartments = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) return departmentData
  return departmentData.filter(item =>
    item.name.toLowerCase().includes(keyword) ||
    item.children.some(sub => sub.name.toLowerCase().includes(keyword))
  )
})

const currentSecondaryDepartments = computed(() => {
  const found = departmentData.find(item => item.id === selectedPrimaryDepartmentId.value)
  if (!found) return []
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) return found.children
  return found.children.filter(item => item.name.toLowerCase().includes(keyword))
})

const activeDoctor = computed(() => doctors.find(item => item.id === activeDoctorId.value) || doctors[0])

const filteredDoctors = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  let list = doctors.filter(item => item.subDepartmentId === selectedSubDepartmentId.value)

  if (titleFilter.value !== '全部') {
    list = list.filter(item => item.title === titleFilter.value)
  }
  if (availabilityFilter.value === '仅看可约') {
    list = list.filter(item => item.bookingStatus !== 'full')
  }
  if (keyword) {
    list = list.filter(item => [item.name, item.department, item.specialty].some(text => text.toLowerCase().includes(keyword)))
  }

  if (sortBy.value === 'appointments') {
    list = [...list].sort((a, b) => b.annualAppointments - a.annualAppointments)
  } else if (sortBy.value === 'rating') {
    list = [...list].sort((a, b) => Number.parseInt(b.goodRate, 10) - Number.parseInt(a.goodRate, 10))
  }

  return list
})

const getDoctorAvailableSlots = (doctorId: number) => {
  return schedules
    .filter(item => item.doctorId === doctorId && item.remaining > 0)
    .reduce((sum, item) => sum + item.remaining, 0)
}

const filteredDoctorSearchResults = computed(() => {
  const keyword = doctorSearchKeyword.value.trim().toLowerCase()
  let list = [...doctors]

  if (doctorSearchTitle.value !== '全部') {
    list = list.filter(item => item.title === doctorSearchTitle.value)
  }

  if (keyword) {
    list = list.filter(item => {
      return [item.name, item.title, item.department, item.specialty, item.introduction].some(text => {
        return text.toLowerCase().includes(keyword)
      })
    })
  }

  if (doctorSearchSort.value === 'appointments') {
    list = list.sort((a, b) => b.annualAppointments - a.annualAppointments)
  } else if (doctorSearchSort.value === 'available') {
    list = list.sort((a, b) => getDoctorAvailableSlots(b.id) - getDoctorAvailableSlots(a.id))
  }

  return list
})

const visibleSchedules = computed(() => {
  const selectedDoctorSchedules = schedules.filter(item => item.doctorId === activeDoctorId.value)
  if (selectedDateKey.value === 'all') {
    return selectedDoctorSchedules
  }
  return selectedDoctorSchedules.filter(item => item.dateKey === selectedDateKey.value)
})

const userName = computed(() => {
  const raw = localStorage.getItem('hospital_user')
  if (!raw) return '游客'
  const user = JSON.parse(raw)
  return user.patientName || user.doctorName || user.adminName || '用户'
})

const syncStateWithRoute = () => {
  const q = route.query
  const routeDepartmentId = typeof route.params.departmentId === 'string' ? route.params.departmentId : ''
  const primaryFromRoute = resolveDepartmentRouteId(typeof q.primary === 'string' ? q.primary : routeDepartmentId)
  const subFromRoute = resolveDepartmentRouteId(typeof q.sub === 'string' ? q.sub : routeDepartmentId)

  if (typeof q.date === 'string') {
    const validDateKeys = dateOptions.value.map(item => item.key)
    selectedDateKey.value = validDateKeys.includes(q.date) ? q.date : todayDateKey
  }
  if (primaryFromRoute) {
    selectedPrimaryDepartmentId.value = primaryFromRoute
  }
  if (subFromRoute) {
    selectedSubDepartmentId.value = subFromRoute
    const found = departmentData.flatMap(item => item.children).find(item => item.id === subFromRoute)
    if (found) currentSubDepartmentName.value = found.name
  }
  const activePrimary = departmentData.find(item => item.id === selectedPrimaryDepartmentId.value)
  const activeSub = activePrimary?.children.find(item => item.id === selectedSubDepartmentId.value)
  if (activePrimary && !activeSub && activePrimary.children[0]) {
    selectedSubDepartmentId.value = activePrimary.children[0].id
    currentSubDepartmentName.value = activePrimary.children[0].name
  }
  if (typeof q.doctorId === 'string') {
    activeDoctorId.value = Number(q.doctorId)
  }

  if (!doctors.find(item => item.id === activeDoctorId.value) && filteredDoctors.value.length > 0) {
    activeDoctorId.value = filteredDoctors.value[0].id
  }
}

const selectPrimaryDepartment = (dept: Department) => {
  selectedPrimaryDepartmentId.value = dept.id
  const firstSub = dept.children[0]
  if (firstSub) {
    selectedSubDepartmentId.value = firstSub.id
    currentSubDepartmentName.value = firstSub.name
  }
}

watch(
  () => route.fullPath,
  () => {
    syncStateWithRoute()
  },
  { immediate: true }
)

const isMenuActive = (path: string) => {
  if (path === '/hospital/doctor-search') {
    return route.path === path
  }
  if (path.includes('/appointment')) {
    return route.path.includes('/appointment') || route.path.includes('/hospital/doctors') || route.path.includes('/hospital/schedule')
  }
  return route.path === path
}

const handleTopMenuClick = (path: string) => {
  router.push(path)
}

const handleBackHome = () => {
  router.push('/hospital/home')
}

const handleDateChange = (dateKey: string) => {
  selectedDateKey.value = dateKey
  if (currentStep.value === 'department') return
  router.replace({
    query: {
      ...route.query,
      date: dateKey
    }
  })
}

const openDoctorList = (sub: SubDepartment) => {
  selectedSubDepartmentId.value = sub.id
  currentSubDepartmentName.value = sub.name
  router.push({
    path: '/hospital/appointment/doctors',
    query: {
      date: selectedDateKey.value,
      primary: selectedPrimaryDepartmentId.value,
      sub: sub.id
    }
  })
}

const openDoctorDetail = (doctor: DoctorItem) => {
  activeDoctorId.value = doctor.id
  router.push({
    path: '/hospital/appointment/doctor-detail',
    query: {
      date: selectedDateKey.value,
      primary: selectedPrimaryDepartmentId.value,
      sub: selectedSubDepartmentId.value,
      doctorId: doctor.id
    }
  })
}

const goDepartmentStep = () => {
  router.push({
    path: '/hospital/appointment/departments',
    query: {
      date: selectedDateKey.value,
      primary: selectedPrimaryDepartmentId.value,
      sub: selectedSubDepartmentId.value
    }
  })
}

const goDoctorListStep = () => {
  router.push({
    path: '/hospital/appointment/doctors',
    query: {
      date: selectedDateKey.value,
      primary: selectedPrimaryDepartmentId.value,
      sub: selectedSubDepartmentId.value
    }
  })
}

const performSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
  }
}

const performDoctorSearch = () => {
  if (!doctorSearchKeyword.value.trim()) {
    ElMessage.warning('请输入医生姓名或擅长关键词')
  }
}

const handleBooking = async (schedule: ScheduleItem) => {
  const doctor = activeDoctor.value
  if (!doctor || schedule.remaining === 0) {
    return
  }

  const patientID = getCurrentPatientId()
  if (!patientID) {
    ElMessage.warning('请先登录患者账号后再预约')
    router.push('/hospital/login')
    return
  }

  try {
    await ElMessageBox.confirm(
      [
        `医生：${doctor.name}`,
        `科室：${doctor.department}`,
        `日期：${schedule.displayDate}`,
        `时间段：${schedule.period}`,
        `挂号费：${schedule.fee.toFixed(2)}元`
      ].join('\n'),
      '确认预约',
      {
        confirmButtonText: '确认预约',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    const res = await appointmentAPI.create({
      patientID,
      scheduleID: schedule.id,
      appointmentDate: schedule.dateKey,
      timeSlot: schedule.period === '上午' ? 1 : 2,
      chiefComplaint: ''
    })
    if (res.code === 200) {
      schedule.remaining = Math.max(0, schedule.remaining - 1)
      ElMessage.success('预约成功')
      router.push('/hospital/my-appointments')
    } else {
      ElMessage.error(res.message || '预约失败')
    }
  } catch {
    // 用户取消不做提示
  }
}

onMounted(() => {
  loadBackendAppointmentData()
})
</script>

<style scoped>
.appointment-workbench {
  min-height: 100vh;
  background: #f5f8fc;
  color: #1f2937;
}

.top-header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 24px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.04);
  position: sticky;
  top: 0;
  z-index: 20;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 320px;
}

.brand-logo {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: linear-gradient(135deg, #1677ff 0%, #63b3ff 100%);
  font-weight: 700;
  font-size: 14px;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
}

.brand-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.top-nav {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: auto;
}

.top-nav-item {
  height: 36px;
  padding: 0 14px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.top-nav-item:hover,
.top-nav-item.active {
  background: rgba(22, 119, 255, 0.08);
  color: #1677ff;
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 260px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 8px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #1677ff, #74b9ff);
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
}

.user-role,
.user-arrow {
  font-size: 12px;
  color: #6b7280;
}

.side-panel,
.card-base {
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.06);
}

.side-panel {
  padding: 12px;
  position: sticky;
  top: 80px;
}

.side-title {
  padding: 4px 8px 10px;
  color: #6b7280;
  font-size: 12px;
}

.side-item {
  height: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  border-radius: 10px;
  cursor: pointer;
  color: #374151;
  transition: all 0.2s ease;
}

.side-item:hover,
.side-item.active {
  background: rgba(22, 119, 255, 0.08);
  color: #1677ff;
}

.side-icon {
  width: 20px;
  height: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  font-size: 18px;
}

.page-head {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.back-button {
  color: #1677ff;
  padding-left: 0;
}

.page-head-main {
  min-width: 0;
}

.page-title-row {
  margin-top: 2px;
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}

.page-title {
  margin: 0;
  font-size: 24px;
  line-height: 1.25;
}

.page-subtitle {
  color: #6b7280;
  font-size: 13px;
}

.page-head-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 0 0 auto;
}

.page-tag {
  padding: 6px 10px;
  border-radius: 999px;
  background: #f3f6fb;
  color: #4b5563;
  font-size: 13px;
  border: 1px solid #e5e7eb;
}

.page-tag.active {
  color: #1677ff;
  background: rgba(22, 119, 255, 0.08);
  border-color: rgba(22, 119, 255, 0.18);
}

.filter-card {
  padding: 14px 16px 12px;
}

.filter-row {
  display: grid;
  grid-template-columns: 180px minmax(260px, 460px);
  gap: 16px;
}

.date-tabs {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.date-tab {
  border: 1px solid #e5e7eb;
  background: #ffffff;
  border-radius: 10px;
  padding: 7px 12px;
  display: inline-flex;
  flex-direction: column;
  min-width: 104px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
}

.date-tab.active {
  background: #1677ff;
  border-color: #1677ff;
  color: #ffffff;
}

.date-label {
  font-size: 12px;
}

.date-value {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 600;
}

.doctor-search-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.doctor-search-card {
  padding: 14px 16px;
}

.doctor-search-row {
  display: grid;
  grid-template-columns: minmax(360px, 720px) 112px;
  gap: 12px;
  align-items: center;
}

.doctor-search-input :deep(.el-input__wrapper) {
  min-height: 44px;
  border-radius: 999px;
  background: #f8fbff;
  box-shadow: 0 0 0 1px #e5e7eb inset;
}

.doctor-search-button {
  height: 44px;
  border-radius: 999px;
  font-weight: 600;
}

.doctor-filter-strip {
  margin-top: 12px;
  height: 44px;
  border-top: 1px solid #eef2f7;
  display: grid;
  grid-template-columns: 220px 220px;
  gap: 16px;
  align-items: end;
}

.doctor-filter-select {
  width: 220px;
}

.doctor-filter-select :deep(.el-select__wrapper) {
  border: 0;
  box-shadow: none;
  justify-content: center;
  background: transparent;
  color: #1f2937;
}

.doctor-search-results {
  padding: 0;
  overflow: hidden;
}

.search-doctor-item {
  padding: 18px 20px;
  display: grid;
  grid-template-columns: 86px minmax(0, 1fr) 176px;
  gap: 18px;
  align-items: center;
  border-bottom: 1px solid #eef2f7;
  transition: background 0.2s ease;
}

.search-doctor-item:last-child {
  border-bottom: 0;
}

.search-doctor-item:hover {
  background: #f8fbff;
}

.search-doctor-avatar {
  background: linear-gradient(135deg, #c7d2fe, #93c5fd);
  color: #ffffff;
  font-size: 24px;
  font-weight: 700;
}

.search-doctor-main {
  min-width: 0;
}

.search-doctor-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}

.search-doctor-head h2 {
  margin: 0;
  color: #111827;
  font-size: 24px;
  line-height: 1.2;
}

.search-doctor-head span {
  color: #4b5563;
  font-size: 16px;
  font-weight: 600;
}

.search-doctor-hospital {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #6b7280;
  font-size: 15px;
}

.search-doctor-dept {
  margin-top: 6px;
  color: #4b5563;
  font-size: 15px;
}

.search-doctor-intro {
  margin: 8px 0 0;
  color: #374151;
  line-height: 1.7;
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.search-doctor-intro span {
  margin-right: 8px;
  color: #1677ff;
  font-weight: 700;
}

.search-doctor-stats {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
  color: #6b7280;
}

.search-doctor-stats strong {
  color: #ef4444;
}

.search-rate {
  color: #ef4444;
  font-weight: 700;
}

.search-doctor-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  flex-wrap: wrap;
}

.search-doctor-actions .el-button {
  width: 82px;
}

.department-layout {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  gap: 16px;
  position: relative;
}

.primary-panel {
  border-radius: 12px;
  background: #f3f6fb;
  padding: 8px;
  border: 1px solid #e5e7eb;
}

.primary-item {
  width: 100%;
  min-height: 44px;
  border: 0;
  text-align: left;
  background: transparent;
  border-radius: 10px;
  padding: 10px 12px;
  color: #4b5563;
  cursor: pointer;
  position: relative;
}

.primary-item.active {
  background: #ffffff;
  color: #1677ff;
  font-weight: 600;
}

.primary-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  background: #1677ff;
  border-radius: 99px;
}

.secondary-panel {
  padding: 8px 0;
}

.secondary-item {
  width: 100%;
  height: 60px;
  border: 0;
  background: transparent;
  border-bottom: 1px solid #eef2f7;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
  color: #1f2937;
  cursor: pointer;
}

.secondary-item:hover {
  background: #f8fbff;
}

.arrow {
  color: #9ca3af;
}

.helper-button {
  position: fixed;
  right: 36px;
  bottom: 32px;
  border: 0;
  border-radius: 999px;
  padding: 12px 18px;
  background: #1677ff;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 12px 24px rgba(22, 119, 255, 0.24);
  cursor: pointer;
}

.doctor-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 16px;
}

.doctor-list-head {
  padding: 12px 16px;
  margin-bottom: 12px;
}

.doctor-list-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}

.doctor-list-title {
  margin: 0;
  font-size: 22px;
}

.doctor-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.doctor-card {
  padding: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.doctor-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(31, 41, 55, 0.08);
}

.doctor-main-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 16px;
  align-items: center;
}

.doctor-row-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.doctor-name {
  font-size: 20px;
  font-weight: 700;
}

.doctor-meta,
.doctor-skill {
  color: #6b7280;
  margin-top: 4px;
}

.doctor-skill {
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.doctor-stats-row {
  margin-top: 8px;
  display: flex;
  gap: 18px;
  align-items: center;
}

.star,
.score {
  color: #ef4444;
  font-weight: 700;
}

.volume {
  color: #1677ff;
  font-weight: 600;
}

.intro-link {
  color: #1677ff;
}

.status-btn {
  color: #1677ff;
  border-color: #1677ff;
}

.status-btn.full {
  color: #9ca3af;
  border-color: #d1d5db;
}

.doctor-filter {
  padding: 16px;
  height: fit-content;
}

.doctor-filter h3 {
  margin: 0 0 12px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-group label {
  color: #6b7280;
  font-size: 13px;
}

.sort-select {
  width: 100%;
}

.detail-layout {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-top {
  padding: 10px 16px;
}

.detail-main-grid {
  display: grid;
  grid-template-columns: minmax(0, 0.92fr) minmax(420px, 0.64fr);
  gap: 16px;
  align-items: start;
}

.detail-left {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.doctor-highlight {
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #dcebff 0%, #e9f4ff 40%, #f7fbff 100%);
}

.doctor-highlight-inner {
  padding: 18px;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 16px;
}

.doctor-highlight-body h2 {
  margin: 0;
  font-size: 26px;
}

.doctor-highlight-meta {
  margin-top: 6px;
  color: #6b7280;
}

.doctor-highlight-intro {
  margin: 10px 0 0;
  line-height: 1.75;
  color: #4b5563;
}

.metric-row {
  margin-top: 12px;
  display: flex;
  gap: 12px;
}

.metric-item {
  min-width: 150px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
}

.metric-item.orange .metric-value {
  color: #f59e0b;
}

.metric-item.blue .metric-value {
  color: #1677ff;
}

.metric-value {
  font-size: 26px;
  font-weight: 700;
}

.metric-label {
  margin-top: 4px;
  color: #6b7280;
}

.online-banner {
  padding: 14px 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #e8fff4, #d8f7f1);
}

.online-title {
  font-size: 18px;
  font-weight: 700;
}

.online-desc {
  margin-top: 4px;
  color: #047857;
}

.consult-action {
  width: 112px;
  height: 36px;
}

.source-card {
  padding: 16px;
}

.source-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eef2f7;
}

.source-head h3 {
  margin: 0;
  font-size: 22px;
}

.source-head p {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.source-list {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.source-item {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
  padding: 12px 14px;
  display: grid;
  grid-template-columns: minmax(132px, 1fr) 78px 72px 96px;
  gap: 8px;
  align-items: center;
  min-width: 0;
}

.source-primary {
  min-width: 0;
}

.source-date {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.source-clinic,
.source-type,
.source-fee {
  color: #4b5563;
}

.source-clinic {
  margin-top: 4px;
  font-size: 13px;
}

.source-type,
.source-fee {
  white-space: nowrap;
}

.source-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: stretch;
  width: 96px;
  min-width: 0;
}

.remain-btn {
  color: #1677ff;
  border-color: #1677ff;
  width: 96px;
  height: 30px;
  padding: 0;
  margin: 0;
  font-weight: 600;
}

.remain-btn.full {
  color: #9ca3af;
  border-color: #d1d5db;
}

.booking-action {
  width: 96px;
  height: 30px;
  padding: 0;
  margin: 0;
  font-weight: 600;
}

@media (max-width: 1280px) {
  .doctor-layout {
    grid-template-columns: 1fr;
  }

  .doctor-filter {
    position: static;
  }

  .detail-main-grid {
    grid-template-columns: 1fr;
  }

  .source-card {
    position: static;
  }

  .source-item {
    grid-template-columns: minmax(132px, 1fr) 78px 72px 96px;
  }

  .search-doctor-item {
    grid-template-columns: 78px minmax(0, 1fr);
  }

  .search-doctor-actions {
    grid-column: 2;
    justify-content: flex-start;
  }
}

@media (max-width: 760px) {
  .page-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .page-head-tags {
    flex-wrap: wrap;
  }

  .doctor-highlight-inner {
    grid-template-columns: 1fr;
  }

  .source-head {
    flex-direction: column;
  }

  .source-item {
    grid-template-columns: 1fr;
  }

  .remain-btn,
  .booking-action {
    width: 100%;
  }

  .source-actions {
    width: 100%;
  }

  .doctor-search-row {
    grid-template-columns: 1fr;
  }

  .doctor-filter-strip {
    height: auto;
    grid-template-columns: 1fr;
    align-items: stretch;
  }

  .doctor-filter-select {
    width: 100%;
  }

  .search-doctor-item {
    grid-template-columns: 1fr;
  }

  .search-doctor-actions {
    grid-column: auto;
  }

  .search-doctor-actions .el-button {
    flex: 1;
    width: auto;
  }
}
</style>
