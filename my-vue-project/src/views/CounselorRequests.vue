<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item active" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('evaluation')">用户评估</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>用户申请</h1>
        <p>这是用户申请页面。您可以在这里查看和处理用户的咨询申请。</p>
        
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        
        <!-- 预约申请列表 -->
        <div v-else-if="requests.length > 0" class="requests-list">
          <div v-for="request in requests" :key="request.id" class="request-item">
            <div class="request-info">
              <h3>{{ request.username }} - {{ request.type }}</h3>
              <p>预约时间: {{ request.dateTime }}</p>
              <p>状态: 
                <span :class="['status-text', 
                             request.status === 'pending' ? 'pending' : 
                             request.status === 'confirmed' ? 'confirmed' : 'rejected']">
                  {{ request.status === 'pending' ? '等待确认' : 
                     request.status === 'confirmed' ? '已确认' : '已拒绝' }}
                </span>
              </p>
            </div>
            <div class="request-actions">
              <button class="primary-btn" 
                      @click="confirmAppointment(request.id)" 
                      v-if="request.status === 'pending'">
                确认预约
              </button>
              <button class="cancel-btn" 
                      @click="rejectAppointment(request.id)" 
                      v-if="request.status === 'pending'">
                拒绝预约
              </button>
            </div>
          </div>
        </div>
        <div v-else class="no-data">
          没有新的预约申请
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'CounselorRequests',
  setup() {
    const store = useStore()
    const router = useRouter()

    const username = computed(() => store.getters.username)
    const requests = ref([])
    const loading = ref(false)
    const errorMessage = ref('')

    // 从localStorage获取咨询师ID
    const counselorId = localStorage.getItem('counselor_id') || 
                       JSON.parse(localStorage.getItem('user'))?.counselorId || 
                       1 // 默认值

    // 加载预约数据
    const loadAppointments = async () => {
      loading.value = true
      try {
        const response = await axios.get(
          `http://localhost:8080/api/appointments/counselor/${counselorId}`
        )
        
        if (response.data && Array.isArray(response.data)) {
          requests.value = response.data.map(app => ({
            id: app.appointmentId,
            userId: app.userId,
            username: app.userName || '未知用户',
            counselorId: app.counselorId,
            counselorName: app.counselorName || '未知咨询师',
            type: '心理咨询', // 固定类型
            dateTime: `${app.appointmentDate} ${app.appointmentTime === 'morning' ? '上午' : '下午'}`,
            status: app.appointmentStatus === 'scheduled' ? 'pending' : 'confirmed'
          }))
        }
      } catch (error) {
        console.error('加载预约数据失败:', error)
        errorMessage.value = '加载预约数据失败，请刷新重试'
      } finally {
        loading.value = false
      }
    }

    // 确认预约
    const confirmAppointment = async (appointmentId) => {
      if (confirm('确认接受此预约吗？')) {
        try {
          const response = await axios.put(
            `http://localhost:8080/api/appointments/update/${appointmentId}`,
            { newStatus: 'confirmed' }
          )
          
          if (response.status === 200) {
            await loadAppointments()
          }
        } catch (error) {
          console.error('确认预约失败:', error)
          errorMessage.value = '确认预约失败，请重试'
        }
      }
    }

    // 拒绝预约
    const rejectAppointment = async (appointmentId) => {
      if (confirm('确认拒绝此预约吗？')) {
        try {
          const response = await axios.put(
            `http://localhost:8080/api/appointments/update/${appointmentId}`,
            { newStatus: 'rejected' }
          )
          
          if (response.status === 200) {
            await loadAppointments()
          }
        } catch (error) {
          console.error('拒绝预约失败:', error)
          errorMessage.value = '拒绝预约失败，请重试'
        }
      }
    }

    // 登出处理
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    // 页面导航
    const goTo = (path) => {
      const paths = {
        home: '/counselor/home',
        settings: '/counselor/settings',
        requests: '/counselor/requests',
        chat: '/counselor/chat',
        schedule: '/counselor/schedule',
        history: '/counselor/history',
        evaluation: '/counselor/evaluation'
      }
      const targetPath = paths[path]
      if (targetPath) {
        router.push(targetPath)
      }
    }

    // 组件挂载时加载数据
    onMounted(() => {
      loadAppointments()
    })

    return {
      username,
      requests,
      loading,
      errorMessage,
      confirmAppointment,
      rejectAppointment,
      logout,
      goTo
    }
  }
}
</script>

<style scoped>
/* 样式保持与咨询师主页一致 */
.container {
  display: flex;
  height: 100vh;
  background: #f4f4f4;
}

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

.sidebar-item:hover,
.sidebar-item.active {
  color: #007bff;
  transform: scale(1.05);
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

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

.card {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-top: 60px;
}

.requests-list {
  margin-top: 20px;
}

.request-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.request-info h3 {
  margin-top: 0;
  margin-bottom: 5px;
}

.status-text {
  font-weight: bold;
}

.status-text.pending {
  color: #ffc107;
}

.status-text.upcoming {
  color: #0d6efd;
}

.request-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.primary-btn {
  background-color: #0d6efd;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.primary-btn:hover {
  background-color: #0a58ca;
}

.cancel-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #bb2d3b;
}

.no-data {
  text-align: center;
  padding: 30px;
  color: #666;
  font-style: italic;
  background-color: #f9f9f9;
  border-radius: 6px;
  margin: 10px 0;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.error-message {
  color: #dc3545;
  font-weight: bold;
  text-align: center;
  padding: 10px;
}

.status-text.confirmed {
  color: #28a745;
}

.status-text.rejected {
  color: #dc3545;
}
</style>