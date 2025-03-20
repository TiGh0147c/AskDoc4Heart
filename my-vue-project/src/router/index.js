import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
//用户
import UserHome from '../views/UserHome.vue'
import UserTutorial from '../views/UserTutorial.vue'  // 用户教程页面
import UserAppointment from '../views/UserAppointment.vue'  // 用户预约页面
import UserSettings from '../views/UserSettings.vue'  // 用户设置页面
import UserHistory from '../views/UserHistory.vue'  // 用户历史会话页面

import UserCurrentChat from '../views/UserCurrentChat.vue'
//咨询师
import CounselorHome from '../views/CounselorHome.vue'
import CounselorSettings from '../views/CounselorSettings.vue' 
import CounselorRequests from '../views/CounselorRequests.vue'  // 添加用户申请页面
import CounselorChat from '../views/CounselorChat.vue'  // 添加咨询窗口页面
import CounselorSchedule from '../views/CounselorSchedule.vue'  // 添加排班表页面
//管理员
import AdminHome from '../views/AdminHome.vue'
import AdminManage from '../views/AdminManage.vue'  // 管理注册页面
import AdminSchedule from '../views/AdminSchedule.vue'  // 管理排班表页面
import AdminAccounts from '../views/AdminAccounts.vue'
import AdminSupervision from '../views/AdminSupervision.vue'
import AdminNotifications from '../views/AdminNotifications.vue'  // 通知页面
//其他




const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  //用户部分
  {
    path: '/user/home',
    name: 'UserHome',
    component: UserHome,
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/user/tutorial',
    name: 'UserTutorial',
    component: UserTutorial,
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/user/appointment',
    name: 'UserAppointment',
    component: UserAppointment,
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/user/settings',
    name: 'UserSettings',
    component: UserSettings,
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/user/history',
    name: 'UserHistory',
    component: UserHistory,
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/user/currentChat',
    name: 'UserCurrentChat',
    component: UserCurrentChat,
    meta: { requiresAuth: true, role: 'user' }  // 添加当前对话路由
  },
  //咨询师部分
  {
    path: '/counselor/home',
    name: 'CounselorHome',
    component: CounselorHome,
    meta: { requiresAuth: true, role: 'counselor' }
  },
  {
    path: '/counselor/settings',
    name: 'CounselorSettings',
    component: CounselorSettings,
    meta: { requiresAuth: true, role: 'counselor' }  // 咨询师设置页面
  },
  {
    path: '/counselor/requests',
    name: 'CounselorRequests',
    component: CounselorRequests,
    meta: { requiresAuth: true, role: 'counselor' }  // 用户申请页面
  },
  {
    path: '/counselor/chat',
    name: 'CounselorChat',
    component: CounselorChat,
    meta: { requiresAuth: true, role: 'counselor' }  // 咨询窗口页面
  },
  {
    path: '/counselor/schedule',
    name: 'CounselorSchedule',
    component: CounselorSchedule,
    meta: { requiresAuth: true, role: 'counselor' }  // 排班表页面
  },
  //管理员部分
  {
    path: '/admin/home',
    name: 'AdminHome',
    component: AdminHome,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/manage',
    name: 'AdminManage',
    component: AdminManage,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/schedules',
    name: 'AdminSchedule',
    component: AdminSchedule,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/accounts',
    name: 'AdminAccounts',
    component: AdminAccounts,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/supervision',
    name: 'AdminSupervision',
    component: AdminSupervision,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/notifications',
    name: 'AdminNotifications',
    component: AdminNotifications,
    meta: { requiresAuth: true, role: 'admin' }
  }
  //其他

]



const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫，检查用户是否已登录和角色权限
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true'
  const userRole = localStorage.getItem('userRole')

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAuth && to.meta.role !== userRole) {
    // 根据角色重定向到相应的主页
    if (userRole === 'user') next('/user/home')
    else if (userRole === 'counselor') next('/counselor/home')
    else if (userRole === 'admin') next('/admin/home')
    else next('/login')
  } else {
    next()
  }
})

export default router