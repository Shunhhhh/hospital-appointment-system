// 医院预约系统路由配置
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
  }
]
