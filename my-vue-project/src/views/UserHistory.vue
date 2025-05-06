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
              @click="openSession(history)"
            >
              <div class="history-info">
                <h3>{{ history.counselorName }} - {{ history.type }}</h3>
                <p class="history-date">咨询日期: {{ history.date }}</p>
                <p class="history-messages">消息数量: {{ history.messagesCount }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 会话详情弹窗 -->
    <div v-if="selectedSession" class="modal-overlay" @click="closeSessionDetail">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>会话详情</h2>
          <button class="close-btn" @click="closeSessionDetail">&times;</button>
        </div>
        
        <div class="session-detail-header">
          <div class="user-info">
            <div class="user-detail-info">
              <h3>{{ selectedSession.counselorName }}</h3>
              <p>{{ selectedSession.type }}</p>
              <p class="session-date-info">{{ selectedSession.date }}</p>
            </div>
          </div>
        </div>
        
        <div class="messages-container">
          <div v-if="isLoading" class="loading-indicator">
            加载中...
          </div>
          <div v-else-if="messages.length === 0" class="no-messages">
            暂无消息记录
          </div>
          <div 
            v-else
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message', message.sender === 'user' ? 'sent' : 'received']">
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.text)"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <button class="action-btn download-btn" @click="downloadChatHistory">
            导出记录
          </button>
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
    const selectedSession = ref(null)
    const messages = ref([])
    const isLoading = ref(false)
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    // 使用不同的变量名来避免重复声明
    const userDisplayName = ref(userData?.username || '用户')
    const userId = ref(userData?.user_id || 1)

    // 加载历史记录
    const loadHistory = async () => {
      try {
        // 使用从localStorage获取的userId
        const response = await fetch(`/api/user/completed?userId=${userId.value}`)
        const sessions = await response.json()
        
        // 转换数据格式
        chatHistory.value = sessions.map(session => {
          return {
            id: session.sessionId,
            counselorId: session.counselorId,
            // 咨询师姓名暂时使用ID代替
            counselorName: `咨询师${session.counselorId}`,
            type: '心理咨询',
            date: formatDate(session.sessionStartTime),
            messagesCount: 0 // 消息数量暂时设为0
          }
        })
        
        // 获取每个会话的消息数量
        for (const session of chatHistory.value) {
          try {
            const messagesResponse = await fetch(`/api/counselor/chats/${session.id}`)
            const messagesData = await messagesResponse.json()
            session.messagesCount = messagesData.messages?.length || 0
          } catch (error) {
            console.error(`获取会话 ${session.id} 的消息数量失败:`, error)
          }
        }
      } catch (error) {
        console.error('加载历史会话失败:', error)
        chatHistory.value = []
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }
    
    // 格式化消息时间
    const formatMessageTime = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }

    // 打开会话详情
    const openSession = async (session) => {
      selectedSession.value = { ...session }
      isLoading.value = true
      messages.value = []
      
      try {
        const response = await fetch(`/api/counselor/chats/${session.id}`)
        const data = await response.json()
        
        // 确保消息数组存在并且格式正确
        if (data.messages && Array.isArray(data.messages)) {
          messages.value = data.messages
          console.log('获取到的消息:', messages.value) // 添加日志以便调试
        } else {
          console.error('返回的消息数据格式不正确:', data)
          messages.value = []
        }
      } catch (error) {
        console.error('获取会话消息失败:', error)
      } finally {
        isLoading.value = false
      }
    }
    
    // 关闭会话详情
    const closeSessionDetail = () => {
      selectedSession.value = null
      messages.value = []
    }
    
    // 格式化消息内容，支持换行和链接
    const formatMessage = (text) => {
      if (!text) return ''
      const withLineBreaks = text.replace(/\n/g, '<br>')
      const withLinks = withLineBreaks.replace(
        /(https?:\/\/[^\s]+)/g, 
        '<a href="$1" target="_blank">$1</a>'
      )
      return withLinks
    }
    
    // 下载聊天记录
    const downloadChatHistory = () => {
      if (!selectedSession.value) return
      
      try {
        // 创建要下载的内容
        let content = `聊天记录 - ${selectedSession.value.counselorName} - ${selectedSession.value.type}\n`
        content += `日期: ${selectedSession.value.date}\n\n`
        
        messages.value.forEach(msg => {
          const sender = msg.sender === 'user' ? userDisplayName.value : selectedSession.value.counselorName
          content += `[${msg.time}] ${sender}: ${msg.text}\n`
        })
        
        // 创建 Blob 对象和下载链接
        const blob = new Blob([content], { type: 'text/plain' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `chat_${selectedSession.value.id}_${selectedSession.value.date}.txt`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        
        alert('聊天记录已下载')
      } catch (error) {
        console.error('下载聊天记录失败', error)
        alert('下载失败，请重试')
      }
    }

    // 查看历史记录详情 - 保留此方法以兼容路由跳转
    const viewHistoryDetail = (historyId) => {
      const session = chatHistory.value.find(h => h.id === historyId)
      if (session) {
        openSession(session)
      } else {
        router.push(`/user/history/${historyId}`)
      }
    }

    // 在组件挂载时加载历史记录
    onMounted(loadHistory)

    return {
      username,
      logout,
      goTo,
      chatHistory,
      viewHistoryDetail,
      selectedSession,
      messages,
      isLoading,
      openSession,
      closeSessionDetail,
      formatMessage,
      formatMessageTime,
      downloadChatHistory
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

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.3rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

.session-detail-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.user-detail-info {
  display: flex;
  flex-direction: column;
}

.user-detail-info h3 {
  margin: 0 0 5px 0;
  font-size: 1.1rem;
}

.user-detail-info p {
  margin: 0 0 5px 0;
  color: #666;
}

.session-date-info {
  font-size: 0.85rem;
  color: #888;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 50vh;
}

.loading-indicator,
.no-messages {
  text-align: center;
  padding: 20px;
  color: #666;
}

.message {
  display: flex;
  max-width: 75%;
}

.sent {
  align-self: flex-end;
}

.received {
  align-self: flex-start;
}

.message-content {
  padding: 10px 15px;
  border-radius: 18px;
  position: relative;
}

.sent .message-content {
  background-color: #007bff;
  color: white;
  border-bottom-right-radius: 5px;
}

.received .message-content {
  background-color: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 5px;
}

.message-text {
  line-height: 1.4;
  word-break: break-word;
}

.message-time {
  font-size: 0.7rem;
  margin-top: 5px;
  text-align: right;
  opacity: 0.8;
}

.modal-actions {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.download-btn {
  background-color: #007bff;
  color: white;
}

.download-btn:hover {
  background-color: #0056b3;
}
</style>