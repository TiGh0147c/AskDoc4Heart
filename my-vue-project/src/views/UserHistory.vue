<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('tutorial')">教程</div>
      <div class="sidebar-item" @click="goTo('appointment')">预约</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item active" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('currentChat')">当前对话</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>历史会话</h1>
        <p>这是用户的历史会话页面。您可以在这里查看过去的咨询记录。</p>

        <div class="history-list">
          <div v-if="chatHistory.length === 0" class="no-history">
            <p>暂无历史会话记录</p>
          </div>

          <div v-else class="history-items">
            <div
              v-for="history in chatHistory"
              :key="history.id"
              class="history-item"
              @click="viewHistoryDetail(history.id)"
            >
              <div class="history-info">
                <h3>{{ history.counselorName }} - {{ history.type }}</h3>
                <p class="history-date">咨询日期: {{ history.date }}</p>
                <p class="history-messages">消息数量: {{ history.messagesCount }}</p>
                <div class="history-rating">
                  <span>评分: </span>
                  <span class="stars">
                    <span
                      v-for="i in 5"
                      :key="i"
                      class="star"
                      :class="{ 'active': i <= history.rating }"
                    >
                      ★
                    </span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'UserHistory',
  setup() {
    const store = useStore()
    const router = useRouter()

    const username = computed(() => store.getters.username)

    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    const goTo = (path) => {
      const paths = {
        home: '/user/home',
        tutorial: '/user/tutorial',
        appointment: '/user/appointment',
        settings: '/user/settings',
        history: '/user/history',
        currentChat: '/user/currentChat'
      }
      const targetPath = paths[path]
      if (targetPath) {
        router.push(targetPath)
      } else {
        console.error('Invalid path')
      }
    }

    const chatHistory = ref([])

    // 加载历史记录
    const loadHistory = () => {
      // 模拟从后端获取数据
      chatHistory.value = [
        {
          id: 1001,
          counselorId: 1,
          counselorName: '李明',
          type: '焦虑症咨询',
          date: '2025-03-01',
          messagesCount: 12,
          rating: 5
        },
        {
          id: 1002,
          counselorId: 3,
          counselorName: '王芳',
          type: '情绪管理咨询',
          date: '2025-02-15',
          messagesCount: 8,
          rating: 4
        }
      ]
    }

    // 查看历史记录详情
    const viewHistoryDetail = (historyId) => {
      router.push(`/user/history/${historyId}`)
    }

    // 在组件挂载时加载历史记录
    onMounted(loadHistory)

    return {
      username,
      logout,
      goTo,
      chatHistory,
      viewHistoryDetail
    }
  }
}
</script>

<style scoped>
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

.history-list {
  margin-top: 20px;
}

.no-history {
  text-align: center;
  padding: 30px;
  background-color: #f9f9f9;
  border-radius: 8px;
  color: #666;
}

.history-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.history-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.history-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.history-date,
.history-messages {
  margin: 5px 0;
  color: #666;
  font-size: 0.9rem;
}

.history-rating {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.stars {
  display: inline-flex;
  margin-left: 5px;
}

.star {
  color: #ddd;
  font-size: 1rem;
}

.star.active {
  color: #ffc107;
}
</style>