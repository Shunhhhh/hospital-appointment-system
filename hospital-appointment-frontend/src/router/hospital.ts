import HospitalLayout from '@/components/hospital/HospitalLayout.vue'
import HospitalHome from '@/views/hospital/HospitalHome.vue'
import DoctorList from '@/views/hospital/DoctorList.vue'
import DoctorSchedule from '@/views/hospital/DoctorSchedule.vue'
import MyAppointments from '@/views/hospital/MyAppointments.vue'
import PatientLogin from '@/views/hospital/PatientLogin.vue'
import PatientRegister from '@/views/hospital/PatientRegister.vue'
import DoctorWorkbench from '@/views/hospital/DoctorWorkbench.vue'
import AdminDashboard from '@/views/hospital/AdminDashboard.vue'
import ReviewForm from '@/views/hospital/ReviewForm.vue'
import PatientProfile from '@/views/hospital/PatientProfile.vue'
import PatientFeedback from '@/views/hospital/PatientFeedback.vue'
import PatientFeedbackSubmit from '@/views/hospital/PatientFeedbackSubmit.vue'
import PatientHealthEducation from '@/views/hospital/PatientHealthEducation.vue'
import PreDiagnosis from '@/views/hospital/PreDiagnosis.vue'
import CheckReports from '@/views/hospital/CheckReports.vue'
import QueueAssistant from '@/views/hospital/QueueAssistant.vue'
import MedicalRecords from '@/views/hospital/MedicalRecords.vue'
import MedicalRecordDetail from '@/views/hospital/MedicalRecordDetail.vue'

export const hospitalRoutes = [
  {
    path: '/hospital',
    redirect: '/hospital/home'
  },
  // 独立页面（不需要布局）
  {
    path: '/hospital/login',
    name: 'HospitalLogin',
    component: PatientLogin,
    meta: { title: '用户登录' }
  },
  {
    path: '/hospital/register',
    name: 'HospitalRegister',
    component: PatientRegister,
    meta: { title: '用户注册' }
  },
  {
    path: '/hospital/doctor/workbench',
    name: 'DoctorWorkbench',
    component: DoctorWorkbench,
    meta: { title: '医生工作台' }
  },
  {
    path: '/hospital/admin',
    name: 'HospitalAdminDashboard',
    component: AdminDashboard,
    meta: { title: '管理员后台' }
  },
  // 统一布局包裹的页面
  {
    path: '/hospital',
    component: HospitalLayout,
    children: [
      {
        path: 'home',
        name: 'HospitalHome',
        component: HospitalHome,
        meta: { title: '医院首页' }
      },
      {
        path: 'appointment/departments/:departmentId?',
        name: 'AppointmentDepartments',
        component: DoctorList,
        meta: { title: '预约挂号' }
      },
      {
        path: 'appointment/doctors',
        name: 'AppointmentDoctors',
        component: DoctorList,
        meta: { title: '医生列表' }
      },
      {
        path: 'appointment/doctor-detail',
        name: 'AppointmentDoctorDetail',
        component: DoctorList,
        meta: { title: '医生详情与号源' }
      },
      {
        path: 'doctor-search',
        name: 'DoctorSearch',
        component: DoctorList,
        meta: { title: '医生查询' }
      },
      {
        path: 'doctors/:departmentId?',
        name: 'DoctorList',
        component: DoctorList,
        meta: { title: '医生列表' }
      },
      {
        path: 'doctor/:doctorId',
        name: 'DoctorDetail',
        component: DoctorList,
        meta: { title: '医生详情' }
      },
      {
        path: 'schedule/:doctorId',
        name: 'DoctorSchedule',
        component: DoctorSchedule,
        meta: { title: '选择预约时间' }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: MyAppointments,
        meta: { title: '我的挂号' }
      },
      {
        path: 'review/:appointmentId',
        name: 'ReviewForm',
        component: ReviewForm,
        meta: { title: '就诊评价' }
      },
      {
        path: 'profile',
        name: 'PatientProfile',
        component: PatientProfile,
        meta: { title: '个人信息' }
      },
      {
        path: 'feedback',
        name: 'PatientFeedback',
        component: PatientFeedback,
        meta: { title: '意见反馈' }
      },
      {
        path: 'feedback/submit',
        name: 'PatientFeedbackSubmit',
        component: PatientFeedbackSubmit,
        meta: { title: '提交工单' }
      },
      {
        path: 'health-education',
        name: 'PatientHealthEducation',
        component: PatientHealthEducation,
        meta: { title: '健康宣传' }
      },
      {
        path: 'reports',
        name: 'CheckReports',
        component: CheckReports,
        meta: { title: '检查报告' }
      },
      {
        path: 'pre-diagnosis',
        name: 'PreDiagnosis',
        component: PreDiagnosis,
        meta: { title: '智能预问诊' }
      },
      {
        path: 'queue-assistant',
        name: 'QueueAssistant',
        component: QueueAssistant,
        meta: { title: '排队助手' }
      },
      {
        path: 'medical-records',
        name: 'MedicalRecords',
        component: MedicalRecords,
        meta: { title: '门诊记录' }
      },
      {
        path: 'medical-record/:recordId',
        name: 'MedicalRecordDetail',
        component: MedicalRecordDetail,
        meta: { title: '病历详情' }
      }
    ]
  }
]
