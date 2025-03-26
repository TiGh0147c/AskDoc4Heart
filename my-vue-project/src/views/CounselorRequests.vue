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
        
        <!-- 预约申请列表 -->
        <div v-if="requests.length > 0" class="requests-list">
          <div v-for="request in requests" :key="request.id" class="request-item">
            <div class="request-info">
              <h3>{{ request.username }} - {{ request.type }}</h3>
              <p>预约时间: {{ request.dateTime }}</p>
              <p>状态: <span :class="['status-text', request.status === 'pending' ? 'pending' : 'upcoming']">{{ request.status === 'pending' ? '等待确认' : '即将开始' }}</span></p>
            </div>
            <div class="request-actions">
              <button class="primary-btn" @click="confirmAppointment(request.id)" v-if="request.status === 'pending'">确认预约</button>
              <button class="cancel-btn" @click="rejectAppointment(request.id)" v-if="request.status === 'pending'">拒绝预约</button>
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

export default {
  name: 'CounselorRequests',
  setup() {
    const store = useStore()
    const router = useRouter()

    const username = computed(() => store.getters.username)

    // 模拟从后端获取的预约申请数据
    const requests = ref([
      {
        id: 101,
        userId: 1,
        username: '用户1',
        counselorId: 3,
        counselorName: '王芳',
        type: '情绪管理咨询',
        dateTime: '2025-03-15 14:00',
        status: 'pending'
      },
      {
        id: 102,
        userId: 2,
        username: '用户2',
        counselorId: 1,
        counselorName: '李明',
        type: '焦虑症咨询',
        dateTime: '2025-03-13 11:00',
        status: 'pending'
      }
    ])

    // 确认预约
    const confirmAppointment = (requestId) => {
      if (confirm('确认接受此预约吗？')) {
        // 后端需要实现：
        // 1. PUT /api/requests/{id}/confirm
        // 2. 更新预约状态为confirmed
        console.log(`确认预约: ${requestId}`);
        // 示例代码：
        // axios.put(`/api/requests/${requestId}/confirm`)
        //   .then(response => {
        //     // 处理响应
        //     console.log(response.data);
        //     // 更新本地数据
        //     const request = requests.value.find(r => r.id === requestId);
        //     if (request) {
        //       request.status = 'confirmed';
        //     }
        //   })
        //   .catch(error => {
        //     // 处理错误
        //     console.error(error);
        //   });
      }
    }

    // 拒绝预约
    const rejectAppointment = (requestId) => {
      if (confirm('确认拒绝此预约吗？')) {
        // 后端需要实现：
        // 1. PUT /api/requests/{id}/reject
        // 2. 更新预约状态为rejected
        console.log(`拒绝预约: ${requestId}`);
        // 示例代码：
        // axios.put(`/api/requests/${requestId}/reject`)
        //   .then(response => {
        //     // 处理响应
        //     console.log(response.data);
        //     // 更新本地数据
        //     const request = requests.value.find(r => r.id === requestId);
        //     if (request) {
        //       request.status = 'rejected';
        //     }
        //   })
        //   .catch(error => {
        //     // 处理错误
        //     console.error(error);
        //   });
      }
    }

    // 登出处理
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    // 页面导航
    const goTo = (path) => {
      switch (path) {
        case 'home':
          router.push('/counselor/home')
          break
        case 'settings':
          router.push('/counselor/settings')
          break
        case 'requests':
          router.push('/counselor/requests')
          break
        case 'chat':
          router.push('/counselor/chat')
          break
        case 'schedule':
          router.push('/counselor/schedule')
          break
          case 'history':
          router.push('/counselor/history')
          break
          case 'evaluation':
          router.push('/counselor/evaluation')
          break
        default:
          console.error('Invalid path')
      }
    }

    // 加载数据
    onMounted(() => {
      console.log('加载预约申请数据');
      // 在实际项目中，这里应该调用API加载数据
      // fetchRequests()
    })

    return {
      username,
      requests,
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
</style>