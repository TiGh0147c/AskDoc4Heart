<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item active" @click="goTo('history')">历史会话</div>
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
        <h1>历史会话</h1>
        <p>这里可以查看您过去的咨询记录。历史记录保存一个月，之后将自动删除。</p>
        
        <div class="search-filters">
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchTerm" 
              placeholder="搜索用户名或会话内容..."
              @input="currentPage = 1"
            >
          </div>
          <div class="date-filter">
            <label for="date-range">时间范围：</label>
            <select id="date-range" v-model="dateRange" @change="currentPage = 1">
              <option value="all">所有时间</option>
              <option value="week">最近一周</option>
              <option value="month">最近一个月</option>
            </select>
          </div>
        </div>
        
        <div v-if="isLoading" class="loading-indicator">
          加载中...
        </div>
        <!-- img src中图片后期添加数据库储存 -->
        <div v-else-if="filteredHistory.length === 0" class="empty-state">
          <div class="empty-icon">
            <img src="/basic_avatar/basic_male.jpg" alt="历史记录为空"> 
          </div>
          <p>暂无符合条件的历史会话</p>
        </div>
        
        <div v-else class="history-list">
          <div 
            v-for="session in paginatedHistory" 
            :key="session.id" 
            class="history-item"
            @click="openSession(session)"
          >
            <div class="history-header">
              <div class="user-info">
                <div class="user-avatar">
                  <img :src="session.userAvatar" :alt="session.userName">
                </div>
                <div class="user-details">
                  <h3>{{ session.userName }}</h3>
                  <p class="session-type">{{ session.consultationType }}</p>
                </div>
              </div>
              <div class="session-date">
                <p>{{ formatDate(session.date) }}</p>
                <span class="expiry-badge" v-if="isExpiringSoon(session)">即将过期</span>
              </div>
            </div>
            
            <div class="session-preview">
              <p>{{ getSessionPreview(session) }}</p>
            </div>
            
            <div class="session-footer">
              <span class="message-count">{{ session.messages.length }} 条消息</span>
              <span class="session-id">会话ID: {{ session.id }}</span>
            </div>
          </div>
        </div>
        
        <div class="pagination" v-if="filteredHistory.length > 0">
          <button 
            :disabled="currentPage === 1" 
            @click="currentPage--"
            class="page-btn"
          >
            上一页
          </button>
          
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          
          <button 
            :disabled="currentPage === totalPages" 
            @click="currentPage++"
            class="page-btn"
          >
            下一页
          </button>
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
            <div class="user-avatar large">
              <img :src="selectedSession.userAvatar" :alt="selectedSession.userName">
            </div>
            <div class="user-detail-info">
              <h3>{{ selectedSession.userName }}</h3>
              <p>{{ selectedSession.consultationType }}</p>
              <p class="session-date-info">{{ formatDate(selectedSession.date) }}</p>
            </div>
          </div>
        </div>
        
        <div class="messages-container">
          <div 
            v-for="(message, index) in selectedSession.messages" 
            :key="index"
            :class="['message', message.sender === 'counselor' ? 'sent' : 'received']"
          >
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
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'CounselorHistory',
  setup() {
    const store = useStore()
    const router = useRouter()

    // 从localStorage获取咨询师信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const counselorId = ref(userData?.user_id || 1)
    const username = computed(() => store.getters.username)
    const isLoading = ref(true)
    const chatHistory = ref([])
    const searchTerm = ref('')
    const dateRange = ref('all')
    const currentPage = ref(1)
    const itemsPerPage = 5
    const selectedSession = ref(null)

    // 从后端获取历史聊天记录
    const fetchChatHistory = async () => {
      isLoading.value = true
      
      try {
        // 调用/completed接口获取已完成的会话
        const response = await fetch(`/api/counselor/completed?counselorId=${counselorId.value}`)
        const sessions = await response.json()
        
        // 处理会话数据
        const processedSessions = []
        
        for (const session of sessions) {
          // 获取会话消息
          const messagesResponse = await fetch(`/api/counselor/chats/${session.sessionId}`)
          const messagesData = await messagesResponse.json()
          
          processedSessions.push({
            id: session.sessionId,
            userName: `用户${session.userId}`, // 暂时使用userId代替用户名
            userAvatar: "/basic_avatar/basic_male.jpg", // 使用默认头像
            consultationType: "心理咨询",
            date: new Date(session.sessionStartTime),
            expiryDate: new Date(new Date(session.sessionEndTime).setMonth(new Date(session.sessionEndTime).getMonth() + 1)),
            messages: messagesData.messages || []
          })
        }
        
        chatHistory.value = processedSessions
      } catch (error) {
        console.error('获取历史会话失败:', error)
        chatHistory.value = []
      } finally {
        isLoading.value = false
      }
    }
    
    // 根据筛选条件过滤历史记录
    const filteredHistory = computed(() => {
      let result = [...chatHistory.value]
      
      // 搜索词筛选
      if (searchTerm.value.trim()) {
        const term = searchTerm.value.toLowerCase()
        result = result.filter(session => {
          // 检查用户名
          if (session.userName.toLowerCase().includes(term)) return true
          
          // 检查消息内容
          return session.messages.some(msg => 
            msg.text.toLowerCase().includes(term)
          )
        })
      }
      
      // 日期范围筛选
      if (dateRange.value !== 'all') {
        const now = new Date()
        let cutoffDate = new Date()
        
        switch (dateRange.value) {
          case 'week':
            cutoffDate.setDate(now.getDate() - 7)
            break
          case 'month':
            cutoffDate.setMonth(now.getMonth() - 1)
            break
        }
        
        result = result.filter(session => new Date(session.date) >= cutoffDate)
      }
      
      return result
    })
    
    // 分页
    const paginatedHistory = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return filteredHistory.value.slice(start, end)
    })
    
    // 计算总页数
    const totalPages = computed(() => {
      return Math.ceil(filteredHistory.value.length / itemsPerPage) || 1
    })
    
    // 格式化日期
    const formatDate = (date) => {
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
    }
    
    // 获取会话预览
    const getSessionPreview = (session) => {
      if (!session.messages || session.messages.length === 0) return "无消息内容"
      
      // 返回最后一条消息的前30个字符
      const lastMessage = session.messages[session.messages.length - 1]
      const preview = lastMessage.text
      
      if (preview.length > 30) {
        return preview.substring(0, 30) + "..."
      }
      
      return preview
    }
    
    // 判断是否即将过期
    const isExpiringSoon = (session) => {
      if (!session.expiryDate) return false
      
      const now = new Date()
      const expiryDate = new Date(session.expiryDate)
      const daysUntilExpiry = Math.floor((expiryDate - now) / (1000 * 60 * 60 * 24))
      
      return daysUntilExpiry <= 7 && daysUntilExpiry >= 0
    }
    
    // 打开会话详情
    const openSession = (session) => {
      selectedSession.value = { ...session }
    }
    
    // 关闭会话详情
    const closeSessionDetail = () => {
      selectedSession.value = null
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
        let content = `聊天记录 - ${selectedSession.value.userName} - ${selectedSession.value.consultationType}\n`
        content += `日期: ${formatDate(selectedSession.value.date)}\n\n`
        
        selectedSession.value.messages.forEach(msg => {
          const sender = msg.sender === 'counselor' ? username.value : selectedSession.value.userName
          content += `[${msg.time}] ${sender}: ${msg.text}\n`
        })
        
        // 创建 Blob 对象和下载链接
        const blob = new Blob([content], { type: 'text/plain' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `chat_${selectedSession.value.id}_${formatDate(selectedSession.value.date)}.txt`
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

    onMounted(() => {
      fetchChatHistory()
      
      // 检查是否有新保存的聊天记录
      const newChatHistory = localStorage.getItem('newCounselorChatHistory')
      if (newChatHistory) {
        try {
          const historyData = JSON.parse(newChatHistory)
          // 添加到历史记录列表开头
          chatHistory.value.unshift({
            id: historyData.id || `SESS${Date.now().toString().slice(-5)}`,
            userName: historyData.userName,
            userAvatar: historyData.userAvatar || "data:image/svg+xml;base64,",
            consultationType: historyData.consultationType || "常规咨询",
            date: new Date(),
            expiryDate: new Date(new Date().setMonth(new Date().getMonth() + 1)),
            messages: historyData.messages || []
          })
          
          // 清除本地存储
          localStorage.removeItem('newCounselorChatHistory')
        } catch (error) {
          console.error('解析新聊天记录数据出错', error)
        }
      }
    })

    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

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
        case 'help':
          router.push('/counselor/help')
          break
        default:
          console.error('Invalid path')
      }
    }

    return {
      username,
      logout,
      goTo,
      isLoading,
      searchTerm,
      dateRange,
      currentPage,
      filteredHistory,
      paginatedHistory,
      totalPages,
      selectedSession,
      formatDate,
      getSessionPreview,
      isExpiringSoon,
      openSession,
      closeSessionDetail,
      formatMessage,
      downloadChatHistory
    }
  }
}
</script>

<style scoped>
/* 保留原有样式 */
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

/* 新增样式 */
.search-filters {
  display: flex;
  gap: 15px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.search-box input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.date-filter {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-filter label {
  color: #666;
}

.date-filter select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
}

.loading-indicator {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #666;
}

.empty-icon {
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
}

.empty-icon img {
  width: 100%;
  height: 100%;
  opacity: 0.6;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin: 20px 0;
}

.history-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.3s;
}

.history-item:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar.large {
  width: 60px;
  height: 60px;
}

.user-details h3 {
  margin: 0 0 5px 0;
  font-size: 1rem;
}

.session-type {
  color: #666;
  font-size: 0.85rem;
  margin: 0;
}

.session-date {
  text-align: right;
}

.session-date p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 0.85rem;
}

.expiry-badge {
  background-color: #ff9800;
  color: white;
  padding: 3px 8px;
  border-radius: 10px;
  font-size: 0.7rem;
}

.session-preview {
  color: #333;
  font-size: 0.9rem;
  margin: 10px 0;
  line-height: 1.4;
}

.session-footer {
  display: flex;
  justify-content: space-between;
  color: #888;
  font-size: 0.8rem;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.page-btn {
  background-color: #f0f0f0;
  border: none;
  padding: 5px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
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