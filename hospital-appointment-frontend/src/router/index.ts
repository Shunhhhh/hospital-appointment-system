// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { hospitalRoutes } from './hospital'
import StudentCheckIn from '@/views/StudentCheckIn.vue'
import AdminReservationManagement from '@/views/AdminReservationManagement.vue'
import SeatList from '@/views/SeatList.vue'
import SeatMap from '@/views/SeatMap.vue'
import ReservationForm from '@/views/ReservationForm.vue'
import WaitListConfirm from '@/views/WaitListConfirm.vue'
import StudentReservationPage from '@/views/StudentReservationPage.vue'
import AdminRoomManagement from '@/views/AdminRoomManagement.vue'
import StudentFeedback from "@/views/StudentFeedback.vue";
import AdminViolationManagement from "@/views/AdminViolationManagement.vue";
import AdminFeedbackManagement from "@/views/AdminFeedbackManagement.vue";
import Profile from "@/views/Profile.vue";
import StudentNotice from '@/views/StudentNotice.vue'
import { title } from 'process'
import StudentViolation from '@/views/StudentViolation.vue'


const routes: Array<RouteRecordRaw> = [
  // 原自习室路由...
  {
    path: '/',
    name: 'Home',
    redirect: '/hospital/login'  // 改为跳转到医院登录页
  },
  // 医院预约系统路由
  ...hospitalRoutes,
  // 原自习室路由继续保留...
  {
    path: '/logIn',
    redirect: '/hospital/login'
  },
  {
    path: '/checkIn',
    name: 'CheckIn',
    component: StudentCheckIn,
    meta: { title: '学生签到签退系统' }
  },
  {
    path: '/admin-roomManage',
    name: 'AdminRoomManagement',
    component: AdminRoomManagement,
    meta: { title: '管理员资源管理系统'}
  },
  {
    path: '/admin-reservation',
    name: 'AdminReservationManagement',
    component: AdminReservationManagement,
    meta: { title: '管理员预约管理系统' }
  },
  {
   path: '/admin-violation',
   name: 'AdminViolationManagement',
   component: AdminViolationManagement,
   meta: { title: '管理员违规管理系统' }
  },
  {
    path: '/admin-feedback',
    name: 'AdminFeedbackManagement',
    component: AdminFeedbackManagement,
    meta: { title: '管理员反馈管理系统' } 
  },
  {
    path: '/student-reservation',    
    name: 'StudentReservation',
    component: StudentReservationPage,
    meta: { title: '学生预约管理系统' }
  },
  {
    path: '/seat-list',
    name: 'SeatList',
    component: SeatList,
    meta: { title: '自习室查询' }
  },
  {
  path: '/study-seat-list',
  name: 'StudySeatList',
  component: SeatList, 
  meta: { title: '考研座位预约' }
},
{
  path: '/seminar-room-list',
  name: 'SeminarRoomList',
  component: SeatList, 
  meta: { title: '研讨室预约' }
},
  {
    path: '/seat-map',
    name: 'SeatMap',
    component: SeatMap,
    meta: { title: '座位状态' }
  },
  {
    path: '/reservation-form',
    name: 'ReservationForm',
    component: ReservationForm,
    meta: { title: '提交预约' }
  },
  {
    path: '/waitlist-confirm',
    name: 'WaitListConfirm',
    component: WaitListConfirm,
    meta: { title: '候补确认' }
  },
  {
    path: "/student-violation",
    name: "StudentViolation",
    component: StudentViolation,
    meta: { title: "学生违规记录" }
  },
  {
    path: "/feedback",
    name: "StudentFeedback",
    component: StudentFeedback,
    meta: {title: '学生反馈'}
  },
  {
    path: "/profile",
    name: "Profile",
    component: Profile,
    meta: { title: "个人资料", hideLayout: true },
  },
  {
    path: "/notice",
    name: "StudentNotice",
    component: StudentNotice,
    meta: { title: "通知", hideLayout: true },
  }


]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router;
