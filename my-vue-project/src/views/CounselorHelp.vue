<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('evaluation')">用户评估</div>
      <div class="sidebar-item active" @click="goTo('help')">督导求助</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>督导求助</h1>
        <p>这是咨询师求助督导的页面。您可以在这里向督导寻求专业帮助。</p>
        
        <div v-if="loading" class="loading-message">
          正在加载督导信息...
        </div>
        
        <div v-else-if="error" class="error-message">
          {{ error }}
        </div>
        
        <div v-else-if="supervisor" class="supervisor-info">
          <h2>您的督导</h2>
          <div class="supervisor-card">
            <div class="supervisor-avatar">{{ supervisor.name.charAt(0) }}</div>
            <div class="supervisor-details">
              <h3>{{ supervisor.name }}</h3>
              <p><strong>专长:</strong> {{ supervisor.expertiseArea || '咨询' }}</p>
              <p><strong>邮箱:</strong> {{ supervisor.email || '未设置' }}</p>
              <p><strong>电话:</strong> {{ supervisor.phone || '未设置' }}</p>
            </div>
          </div>
          
          <div class="action-buttons">
            <button class="start-chat-btn" @click="startChat">开始会话</button>
          </div>
        </div>
        
        <div v-else class="no-supervisor">
          <p>您当前没有绑定督导。请联系管理员为您分配督导。</p>
        </div>
        
        <div v-if="chatActive" class="chat-window">
          <div class="chat-header">
            <h3>与 {{ supervisor?.name }} 的会话</h3>
            <button class="close-btn" @click="closeChat">关闭</button>
          </div>
          <div class="chat-messages" ref="chatMessages">
            <div v-for="(message, index) in chatMessages" :key="index" 
                 :class="['message', message.sender === 'counselor' ? 'sent' : 'received']">
              <div class="message-content">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.time) }}</div>
            </div>
          </div>
          <div class="chat-input">
            <textarea 
              v-model="newMessage" 
              placeholder="输入消息..." 
              @keyup.enter="sendMessage"
            ></textarea>
            <button @click="sendMessage">发送</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'CounselorHelp',
  setup() {
    const router = useRouter()
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '咨询师')
    const counselorId = ref(localStorage.getItem('counselor_id') || userData?.counselorId || userData?.userId || null)
    
    // 督导信息
    const supervisor = ref(null)
    const loading = ref(true)
    const error = ref(null)
    
    // 聊天相关
    const chatActive = ref(false)
// 删除重复声明的chatMessages
    const newMessage = ref('')
    const chatMessages = ref(null)
    
    // 获取督导信息
    const fetchSupervisor = async () => {
      if (!counselorId.value) {
        error.value = '无法获取咨询师ID，请重新登录'
        loading.value = false
        return
      }
      
      try {
        loading.value = true
        const response = await axios.get(`/api/binding/supervisor/${counselorId.value}`)
        
        if (response.data) {
          supervisor.value = response.data
          console.log('获取到督导信息:', supervisor.value)
        } else {
          supervisor.value = null
        }
        
        loading.value = false
      } catch (err) {
        console.error('获取督导信息失败:', err)
        error.value = '获取督导信息失败，请稍后再试'
        loading.value = false
      }
    }
    
    // 开始聊天
    const startChat = () => {
      chatActive.value = true
      // 添加一条欢迎消息
      chatMessages.value = [
        {
          content: `您好，我是您的督导${supervisor.value.name}。有什么可以帮助您的吗？`,
          sender: 'supervisor',
          time: new Date()
        }
      ]
      
      // 等待DOM更新后滚动到底部
      nextTick(() => {
        scrollToBottom()
      })
    }
    
    // 关闭聊天
    const closeChat = () => {
      chatActive.value = false
    }
    
    // 发送消息
    const sendMessage = () => {
      if (!newMessage.value.trim()) return
      
      // 添加消息到聊天记录
      chatMessages.value.push({
        content: newMessage.value,
        sender: 'counselor',
        time: new Date()
      })
      
      // 清空输入框
      newMessage.value = ''
      
      // 滚动到底部
      nextTick(() => {
        scrollToBottom()
      })
      
      // 模拟督导回复
      setTimeout(() => {
        chatMessages.value.push({
          content: '收到您的消息，我会尽快回复。',
          sender: 'supervisor',
          time: new Date()
        })
        
        // 滚动到底部
        nextTick(() => {
          scrollToBottom()
        })
      }, 1000)
    }
    
    // 滚动到底部
    const scrollToBottom = () => {
      if (chatMessages.value) {
        chatMessages.value.scrollTop = chatMessages.value.scrollHeight
      }
    }
    
    // 格式化时间
    const formatTime = (date) => {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    }
    
    const logout = () => {
      // 清除登录状态
      localStorage.removeItem('isAuthenticated')
      localStorage.removeItem('userRole')
      localStorage.removeItem('user')
      router.push('/login')
    }
    
    const goTo = (route) => {
      if (route === 'home') {
        router.push('/counselor/home')
      } else {
        router.push(`/counselor/${route}`)
      }
    }
    
    onMounted(() => {
      fetchSupervisor()
    })
    
    return {
      username,
      counselorId,
      supervisor,
      loading,
      error,
      chatActive,
      chatMessages,
      newMessage,
      startChat,
      closeChat,
      sendMessage,
      formatTime,
      logout,
      goTo
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

.sidebar-item:hover {
  color: #007bff;
  transform: scale(1.05);
}

.sidebar-item.active {
  color: #007bff;
  font-weight: bold;
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

/* 督导信息样式 */
.supervisor-info {
  margin-top: 20px;
}

.supervisor-card {
  display: flex;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-top: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.supervisor-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-right: 15px;
}

.supervisor-details {
  flex: 1;
}

.supervisor-details h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.supervisor-details p {
  margin: 5px 0;
  color: #666;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.start-chat-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.start-chat-btn:hover {
  background-color: #218838;
}

.loading-message, .error-message, .no-supervisor {
  text-align: center;
  margin: 20px 0;
  padding: 15px;
  border-radius: 4px;
}

.loading-message {
  background-color: #e9f5ff;
  color: #0066cc;
}

.error-message {
  background-color: #ffebee;
  color: #d32f2f;
}

.no-supervisor {
  background-color: #fff8e1;
  color: #ff8f00;
}

/* 聊天窗口样式 */
.chat-window {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 350px;
  height: 450px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.chat-header {
  padding: 10px 15px;
  background-color: #007bff;
  color: white;
  border-radius: 8px 8px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h3 {
  margin: 0;
  font-size: 1rem;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1.2rem;
}

.chat-messages {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 8px;
  position: relative;
}

.sent {
  align-self: flex-end;
  background-color: #dcf8c6;
}

.received {
  align-self: flex-start;
  background-color: #f1f0f0;
}

.message-content {
  word-break: break-word;
}

.message-time {
  font-size: 0.7rem;
  color: #888;
  text-align: right;
  margin-top: 4px;
}

.chat-input {
  padding: 10px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.chat-input textarea {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 8px;
  resize: none;
  height: 40px;
}

.chat-input button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0 15px;
  cursor: pointer;
}

.chat-input button:hover {
  background-color: #0056b3;
}
</style>