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
      <div class="sidebar-item" @click="goTo('help')">督导求助</div>
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
        <div v-else-if="errorMessage" class="no-data">{{ errorMessage }}</div>
        
        <!-- 预约申请列表 -->
        <div v-else-if="sortedAndPaginatedRequests.length > 0" class="requests-list">
          <div v-for="request in sortedAndPaginatedRequests" :key="request.id" class="request-item">
            <div class="request-info">
              <h3>{{ request.username }} - {{ request.type }}</h3>
              <p>预约时间: {{ request.dateTime }}</p>
              <!-- 修改状态显示部分 -->
              <p>状态: 
                <span :class="['status-text', 
                             request.status === 'pending' ? 'pending' : 
                             request.status === 'completed' ? 'completed' : 'rejected']">
                  {{ request.status === 'pending' ? '等待确认' : 
                     request.status === 'completed' ? '已完成' : '已拒绝' }}
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
          <!-- 分页控制 -->
          <div class="pagination-controls">
            <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
            <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
            <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
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
import { computed, onMounted, onUnmounted, ref } from 'vue'
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
    const currentPage = ref(1)
    const itemsPerPage = 5

    // 从localStorage获取咨询师ID
    const counselorId = localStorage.getItem('counselor_id') || 
                       JSON.parse(localStorage.getItem('user'))?.counselorId || 
                       1 // 默认值

    // 加载预约数据
    const loadAppointments = async () => {
      loading.value = true
      try {
        const response = await axios.get(
          `/api/appointments/counselor/${counselorId}`
        )
        
        if (response.data && Array.isArray(response.data)) {
          requests.value = response.data.map(app => ({
            id: app.appointmentId,
            userId: app.userId,
            username: app.userName || '用户', // 修改此处
            counselorId: app.counselorId,
            counselorName: app.counselorName || '未知咨询师',
            type: '心理咨询',
            dateTime: `${app.appointmentDate} ${app.appointmentTime === 'morning' ? '上午' : '下午'}`, 
            status: app.appointmentStatus === 'scheduled' ? 'pending' : 
                   app.appointmentStatus === 'completed' ? 'completed' : 'rejected'
          }))
        } else {
          requests.value = [] // 确保在没有数据时清空
        }
      } catch (error) {
        console.error('加载预约数据失败:', error)
        errorMessage.value = '您当前没有预约'
        requests.value = [] // 出错时也清空
      } finally {
        loading.value = false
      }
    }

    const sortedAndPaginatedRequests = computed(() => {
      // 排序逻辑
      const sorted = [...requests.value].sort((a, b) => {
        // 等待确认的排最前面
        if (a.status === 'pending' && b.status !== 'pending') return -1;
        if (a.status !== 'pending' && b.status === 'pending') return 1;
        
        // 按时间排序，越晚越靠后 (需要将dateTime转换为可比较的格式)
        const dateA = new Date(a.dateTime.replace('上午', '09:00').replace('下午', '14:00'));
        const dateB = new Date(b.dateTime.replace('上午', '09:00').replace('下午', '14:00'));
        return dateA - dateB; // 升序
      });

      // 分页逻辑
      const startIndex = (currentPage.value - 1) * itemsPerPage;
      const endIndex = startIndex + itemsPerPage;
      return sorted.slice(startIndex, endIndex);
    });

    const totalPages = computed(() => {
      return Math.ceil(requests.value.length / itemsPerPage);
    });

    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
      }
    };

    // 确认预约
    const confirmAppointment = async (appointmentId) => {
      if (confirm('确认接受此预约吗？')) {
        try {
          // 首先更新预约状态
          const response = await axios.post(
            `/api/appointments/update/${appointmentId}`,
            { newStatus: 'completed' }
          )
          
          if (response.status === 200) {
            // 获取当前预约的详细信息
            const appointment = requests.value.find(req => req.id === appointmentId);
            
            if (appointment) {
              // 创建咨询会话
              const sessionResponse = await axios.post(
                '/api/counselor/consultation/sessions/create',
                null,
                {
                  params: {
                    counselorId: counselorId,
                    userId: appointment.userId
                  }
                }
              );
              
              if (sessionResponse.data && sessionResponse.data.sessionId) {
                const sessionId = sessionResponse.data.sessionId;
                // 存储会话ID，以便在聊天页面使用
                localStorage.setItem('currentSessionId', sessionId);
                
                // 跳转到聊天页面
                router.push('/counselor/chat');
              }
            }
            
            await loadAppointments(); // 重新加载数据以更新列表和分页
            alert('预约已确认，并创建了咨询会话');
          }
        } catch (error) {
          console.error('确认预约失败:', error);
          errorMessage.value = '确认预约失败，请重试';
        }
      }
    }
    
    // 拒绝预约方法
    const rejectAppointment = async (appointmentId) => {
      if (confirm('确认拒绝此预约吗？')) {
        try {
          const response = await axios.post(
            `/api/appointments/cancel/${appointmentId}`
          )
          
          if (response.status === 200) {
            await loadAppointments() // 重新加载数据以更新列表和分页
            alert('预约已成功拒绝')
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
        evaluation: '/counselor/evaluation',
        help: '/counselor/help'
      }
      const targetPath = paths[path]
      if (targetPath) {
        router.push(targetPath)
      } else {
        console.error('Invalid path')
      }
    }

    // 在组件挂载时加载预约数据
    onMounted(async () => {
      await loadAppointments()
      
      // 设置定时器，每30秒刷新一次预约数据
      const intervalId = setInterval(loadAppointments, 30000)
      
      // 在组件卸载时清除定时器
      onUnmounted(() => {
        clearInterval(intervalId)
      })
    })

    return {
      username,
      requests, // 仍然返回原始数据，因为分页和排序是基于它的
      loading,
      errorMessage,
      confirmAppointment,
      rejectAppointment,
      logout,
      goTo,
      sortedAndPaginatedRequests, // 返回排序和分页后的数据
      currentPage,
      totalPages,
      nextPage,
      prevPage
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

.status-text.completed {
  color: #28a745;
}

.status-text.rejected {
  color: #dc3545;
}
</style>