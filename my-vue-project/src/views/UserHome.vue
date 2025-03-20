<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')" :class="{ active: currentPath === 'home' }">
        <i class="fa fa-home"></i> 返回主页
      </div>
      <div class="sidebar-item" @click="goTo('tutorial')" :class="{ active: currentPath === 'tutorial' }">
        <i class="fa fa-book"></i> 教程
      </div>
      <div class="sidebar-item" @click="goTo('appointment')" :class="{ active: currentPath === 'appointment' }">
        <i class="fa fa-calendar"></i> 预约
      </div>
      <div class="sidebar-item" @click="goTo('settings')" :class="{ active: currentPath === 'settings' }">
        <i class="fa fa-cog"></i> 设置
      </div>
      <div class="sidebar-item" @click="goTo('history')" :class="{ active: currentPath === 'history' }">
        <i class="fa fa-history"></i> 历史会话
      </div>
      <div class="sidebar-item" @click="goTo('currentChat')" :class="{ active: currentPath === 'currentChat' }">
        <i class="fa fa-comment"></i> 当前对话
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>用户主页</h1>
        <p>这是用户的主页界面。您可以在这里查看或预约咨询服务。</p>
        
        <!-- 添加预约信息概览 -->
        <div class="appointments-overview">
          <h2>我的预约</h2>
          
          <div v-if="pendingAppointments.length === 0 && upcomingAppointments.length === 0" class="no-appointments">
            <p>您当前没有预约</p>
            <button class="primary-btn" @click="goToAppointments">立即预约</button>
          </div>
          
          <div v-else>
            <!-- 待确认的预约 -->
            <div v-if="pendingAppointments.length > 0" class="appointment-section">
              <h3>待确认预约</h3>
              <div 
                v-for="appointment in pendingAppointments" 
                :key="appointment.id"
                class="appointment-item"
              >
                <div class="appointment-info">
                  <h4>{{ appointment.counselorName }} - {{ appointment.type }}</h4>
                  <p>预约时间: {{ appointment.dateTime }}</p>
                  <span class="status-tag pending">等待确认</span>
                </div>
              </div>
            </div>
            
            <!-- 即将开始的预约 -->
            <div v-if="upcomingAppointments.length > 0" class="appointment-section">
              <h3>即将开始的预约</h3>
              <div 
                v-for="appointment in upcomingAppointments" 
                :key="appointment.id"
                class="appointment-item"
              >
                <div class="appointment-info">
                  <h4>{{ appointment.counselorName }} - {{ appointment.type }}</h4>
                  <p>预约时间: {{ appointment.dateTime }}</p>
                  <p v-if="appointment.timeRemaining">距离开始: {{ appointment.timeRemaining }}</p>
                  <span class="status-tag upcoming">已确认</span>
                </div>
                <div v-if="appointment.canStart" class="appointment-action">
                  <button class="start-btn" @click="startChat(appointment)">开始咨询</button>
                </div>
              </div>
            </div>
            
            <div class="view-all">
              <button class="secondary-btn" @click="goToAppointments">查看全部预约</button>
            </div>
          </div>
        </div>
      </div>
      <div>
        <img src="/cat.jpg" alt="Cat Image">
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'UserHome',
  setup() {
    const store = useStore()
    const router = useRouter()
    const currentPath = computed(() => router.currentRoute.value.name) // 获取当前路径名称
    const username = computed(() => store.getters.username)

    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    const goTo = (path) => {
      switch (path) {
        case 'home':
          router.push('/user/home')
          break
        case 'tutorial':
          router.push('/user/tutorial')
          break
        case 'appointment':
          router.push('/user/appointment')
          break
        case 'settings':
          router.push('/user/settings')
          break
        case 'history':
          router.push('/user/history')
          break
        case 'currentChat':
          router.push('/user/currentChat')
          break
        default:
          console.error('Invalid path')
      }
    }

    const pendingAppointments = ref([])
    const upcomingAppointments = ref([])

    // 加载预约信息
    const loadAppointments = () => {
      // 模拟预约数据
      pendingAppointments.value = [
        {
          id: 101,
          counselorName: '王芳',
          type: '情绪管理咨询',
          dateTime: '2025-03-15 14:00',
          status: 'pending'
        }
      ]
      
      upcomingAppointments.value = [
        {
          id: 103,
          counselorName: '林强',
          type: '创伤治疗',
          dateTime: '2025-03-14 10:00',
          status: 'confirmed',
          timeRemaining: '23小时'
        },
        {
          id: 104,
          counselorName: '赵刚',
          type: '青少年心理咨询',
          dateTime: '2025-03-13 15:00',
          status: 'confirmed',
          timeRemaining: '3小时',
          canStart: true
        }
      ]
    }

    onMounted(() => {
      loadAppointments()
    })

    // 跳转到预约页面
    const goToAppointments = () => {
      router.push('/user/appointment')
    }

    // 开始聊天
    const startChat = (appointment) => {
      localStorage.setItem('currentCounselor', JSON.stringify({
        id: appointment.counselorId,
        name: appointment.counselorName,
        appointmentId: appointment.id,
        type: appointment.type
      }))
      router.push('/user/currentChat')
    }

    return {
      username,
      logout,
      goTo,
      currentPath, // 返回当前路径名称
      pendingAppointments,
      upcomingAppointments,
      goToAppointments,
      startChat
    }
  }
}
</script>

<style scoped>
/* 容器样式 */
.container {
  display: flex;
  height: 100vh;
  background: #f4f4f4;
}

/* 侧边栏样式 */
.sidebar {
  width: 200px;
  background: #ffffff;
  padding: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  color: #333;
  font-family: Arial, sans-serif;
  border-radius: 0 10px 10px 0;
}

.sidebar-item {
  margin-bottom: 10px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.2s, color 0.2s;
}

.sidebar-item:hover {
  color: #007bff;
  transform: scale(1.05);
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* 右上角欢迎信息和退出登录 */
.top-right {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
}

.welcome {
  margin: 0;
  color: #333;
}

.logout-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.logout-btn:hover {
  background-color: #0056b3;
}

/* 主内容卡片样式 */
.card {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-top: 60px;
}

.appointments-overview {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.appointments-overview h2 {
  margin-bottom: 15px;
  color: #333;
}

.no-appointments {
  text-align: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 15px;
}

.primary-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.primary-btn:hover {
  background-color: #0056b3;
}

.appointment-section {
  margin-bottom: 25px;
}

.appointment-section h3 {
  margin-bottom: 10px;
  font-size: 1.1rem;
  color: #444;
}

.appointment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  transition: transform 0.2s;
}

.appointment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
}

.appointment-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.appointment-info p {
  margin: 3px 0;
  color: #666;
  font-size: 0.9rem;
}

.status-tag {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
  margin-top: 5px;
}

.status-tag.pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-tag.upcoming {
  background-color: #cfe2ff;
  color: #084298;
}

.start-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.start-btn:hover {
  background-color: #218838;
}

.secondary-btn {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.secondary-btn:hover {
  background-color: #5a6268;
}

.view-all {
  text-align: center;
  margin-top: 10px;
}
</style>