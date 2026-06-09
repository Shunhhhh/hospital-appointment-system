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

export const hospitalRoutes = [
  {
    path: '/hospital',
    name: 'HospitalRoot',
    redirect: '/hospital/login'
  },
  {
    path: '/hospital/home',
    name: 'HospitalHome',
    component: HospitalHome,
    meta: { title: '医院首页', hideLayout: true }
  },
  {
    path: '/hospital/admin',
    name: 'HospitalAdminDashboard',
    component: AdminDashboard,
    meta: { title: '管理员后台', hideLayout: true }
  },
  {
    path: '/hospital/login',
    name: 'HospitalLogin',
    component: PatientLogin,
    meta: { title: '用户登录', hideLayout: true }
  },
  {
    path: '/hospital/register',
    name: 'HospitalRegister',
    component: PatientRegister,
    meta: { title: '用户注册', hideLayout: true }
  },
  {
    path: '/hospital/appointment/departments/:departmentId?',
    name: 'AppointmentDepartments',
    component: DoctorList,
    meta: { title: '预约挂号', hideLayout: true }
  },
  {
    path: '/hospital/appointment/doctors',
    name: 'AppointmentDoctors',
    component: DoctorList,
    meta: { title: '医生列表', hideLayout: true }
  },
  {
    path: '/hospital/appointment/doctor-detail',
    name: 'AppointmentDoctorDetail',
    component: DoctorList,
    meta: { title: '医生详情与号源', hideLayout: true }
  },
  {
    path: '/hospital/doctor-search',
    name: 'DoctorSearch',
    component: DoctorList,
    meta: { title: '医生查询', hideLayout: true }
  },
  {
    path: '/hospital/doctors/:departmentId?',
    name: 'DoctorList',
    component: DoctorList,
    meta: { title: '医生列表', hideLayout: true }
  },
  {
    path: '/hospital/doctor/:doctorId',
    name: 'DoctorDetail',
    component: DoctorList,
    meta: { title: '医生详情', hideLayout: true }
  },
  {
    path: '/hospital/schedule/:doctorId',
    name: 'DoctorSchedule',
    component: DoctorSchedule,
    meta: { title: '选择预约时间', hideLayout: true }
  },
  {
    path: '/hospital/my-appointments',
    name: 'MyAppointments',
    component: MyAppointments,
    meta: { title: '我的挂号', hideLayout: true }
  },
  {
    path: '/hospital/doctor/workbench',
    name: 'DoctorWorkbench',
    component: DoctorWorkbench,
    meta: { title: '医生工作台', hideLayout: true }
  },
  {
    path: '/hospital/review/:appointmentId',
    name: 'ReviewForm',
    component: ReviewForm,
    meta: { title: '就诊评价', hideLayout: true }
  },
  {
    path: '/hospital/profile',
    name: 'PatientProfile',
    component: PatientProfile,
    meta: { title: '个人信息', hideLayout: true }
  },
  {
    path: '/hospital/feedback',
    name: 'PatientFeedback',
    component: PatientFeedback,
    meta: { title: '意见反馈', hideLayout: true }
  },
  {
    path: '/hospital/feedback/submit',
    name: 'PatientFeedbackSubmit',
    component: PatientFeedbackSubmit,
    meta: { title: '提交工单', hideLayout: true }
  },
  {
    path: '/hospital/health-education',
    name: 'PatientHealthEducation',
    component: PatientHealthEducation,
    meta: { title: '健康宣传', hideLayout: true }
  }
]
