<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('tutorial')">教程</div>
      <div class="sidebar-item" @click="goTo('appointment')">预约</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item active" @click="goTo('currentChat')">当前对话</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <!-- 如果没有选择会话，显示会话列表 -->
      <div v-if="!currentCounselor" class="card no-chat">
        <h1>当前对话</h1>
        <p>您有 {{ activeChats.length }} 个进行中的咨询会话</p>
        
        <div v-if="activeChats.length === 0" class="no-chat-message">
          <div class="empty-icon">
            <img src="/basic_avatar/basic_male.jpg" alt="无会话">
          </div>
          <p>您目前没有进行中的咨询会话</p>
          <button class="primary-btn" @click="goTo('appointment')">前往预约</button>
        </div>
        
        <!-- 活跃会话列表 -->
        <div v-else class="active-chats">
          <div 
            v-for="chat in activeChats" 
            :key="chat.id" 
            class="chat-preview"
            @click="selectChat(chat)"
          >
            <div class="chat-avatar">
              <img :src="chat.counselorAvatar || '/basic_avatar/basic_male.jpg'" :alt="chat.counselorName">
            </div>
            <div class="chat-brief">
              <h3>{{ chat.counselorName }}</h3>
              <p class="chat-type">{{ chat.type }}</p>
              <p class="last-message">{{ chat.lastMessage }}</p>
            </div>
            <div class="chat-time">
              <p>{{ chat.lastMessageTime }}</p>
              <span class="status-indicator" :class="chat.status"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 如果选择了会话，显示聊天界面 -->
      <div v-else class="chat-container">
        <!-- 聊天头部信息 -->
        <div class="chat-header">
          <button class="back-btn" @click="leaveChat">
            &larr; 返回
          </button>
          <div class="counselor-info">
            <h2>{{ currentCounselor.name }}</h2>
            <p>{{ currentCounselor.type }}</p>
          </div>
          <div class="chat-actions">
            <button class="action-btn" title="结束咨询" @click="endConsultation">
              结束
            </button>
          </div>
        </div>
        
        <!-- 聊天消息区域 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-if="messages.length === 0" class="chat-start-info">
            <p>您的咨询已开始，可以开始提问</p>
          </div>
          
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message', message.sender === 'user' ? 'user-message' : 'counselor-message']"
          >
            <div class="message-avatar">
              <img 
                :src="message.sender === 'user' ? userAvatar : currentCounselor.avatar || '/basic_avatar/basic_male.jpg'" 
                :alt="message.sender === 'user' ? '我' : currentCounselor.name"
              >
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.text)"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
          
          <div v-if="isTyping" class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
        
        <!-- 聊天输入区域 -->
        <div class="chat-input-area">
          <textarea 
            v-model="newMessage" 
            @keydown.enter.prevent="sendMessage"
            placeholder="输入您的问题或疑虑..." 
            rows="2"
          ></textarea>
          <div class="input-actions">
            <button class="upload-btn" @click="triggerFileUpload" title="上传图片">
              <span>📎</span>
            </button>
            <input 
              type="file" 
              ref="fileInput" 
              style="display: none" 
              accept="image/*" 
              @change="handleFileUpload"
            >
            <button 
              class="send-btn" 
              :disabled="!newMessage.trim()" 
              @click="sendMessage"
            >
              发送
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 弹窗形式的评价窗口 -->
    <div v-if="showRatingModal" class="modal-overlay" @click="closeRatingModal">
      <div class="modal-content" @click.stop>
        <h2>评价本次咨询</h2>
        <p>请为{{ endedCounselor?.name }}咨询师的服务进行评价</p>
        
        <div class="star-rating">
          <span 
            v-for="star in 5" 
            :key="star"
            class="star" 
            :class="{ 'active': star <= rating }"
            @click="rating = star"
          >★</span>
        </div>
        
        <div class="rating-comment">
          <textarea 
            v-model="reviewComment" 
            placeholder="请输入您的评价（可选）" 
            rows="3"
          ></textarea>
        </div>
        
        <div class="rating-actions">
          <button @click="submitRating" class="submit-rating-btn">提交评价</button>
          <button @click="closeRatingModal" class="close-rating-btn">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'UserCurrentChat',
  setup() {
    const store = useStore()
    const router = useRouter()
    const messagesContainer = ref(null)
    const fileInput = ref(null)

    const username = computed(() => store.getters.username)
    const newMessage = ref('')
    const isTyping = ref(false)
    
    // 用户头像
    const userAvatar = '/basic_avatar/user_default.jpg'
    
    // 当前选中的咨询师，初始为 null
    const currentCounselor = ref(null)
    
    const userId = computed(() => localStorage.getItem('user_id') || 
                       JSON.parse(localStorage.getItem('user'))?.userId || 
                       1) // 默认值
    
    // 所有进行中的聊天
    const activeChats = ref([])
    
    // 当前聊天的消息列表
    const messages = ref([])
    
    // 当前会话ID
    const currentSessionId = ref(null)
    
    // 评价相关
    const showRatingModal = ref(false)
    const rating = ref(0)
    const reviewComment = ref('')
    const endedCounselor = ref(null)
    
    // 添加轮询相关变量
    const pollingInterval = ref(null)
    const lastMessageId = ref(0)
    const lastActivityTime = ref(Date.now())
    const isActive = ref(true)
    const basePollingRate = 3000 // 活跃状态下3秒轮询一次
    const inactivePollingRate = 10000 // 不活跃状态下10秒轮询一次
    
    // 监听消息变化，自动滚动到底部
    watch(messages, () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    })
    
    // 从后端加载活跃会话列表
    const loadActiveChats = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/user/chats?userId=${userId.value}`)
        
        if (response.data) {
          activeChats.value = response.data
        }
      } catch (error) {
        console.error('加载活跃会话失败:', error)
        // 模拟数据作为备用
        activeChats.value = [
          {
            id: 1,
            counselorId: 101,
            counselorName: '张医生',
            counselorAvatar: '/basic_avatar/basic_male.jpg',
            type: '心理咨询',
            lastMessage: '您好，有什么可以帮助您的吗？',
            lastMessageTime: '10:30',
            status: 'active'
          }
        ]
      }
    }
    
    // 在组件挂载时加载数据
    onMounted(() => {
      loadActiveChats()
      
      // 检查是否有从预约页面传递过来的咨询师信息
      const savedCounselor = localStorage.getItem('currentCounselor')
      
      if (savedCounselor) {
        const counselorData = JSON.parse(savedCounselor)
        
        // 设置当前咨询师
        currentCounselor.value = {
          id: counselorData.id,
          name: counselorData.name,
          type: counselorData.type,
          avatar: counselorData.avatar || '/basic_avatar/basic_male.jpg'
        }
        
        // 加载聊天记录并开始轮询
        loadChatSession(counselorData.id)
        
        // 清除localStorage
        localStorage.removeItem('currentCounselor')
      }
      
      // 添加用户活动监听器
      window.addEventListener('mousemove', updateActivityStatus)
      window.addEventListener('keydown', updateActivityStatus)
      window.addEventListener('click', updateActivityStatus)
      
      // 设置定时检查用户活跃状态
      setInterval(checkInactivity, 60000) // 每分钟检查一次
    })
    
    // 在组件卸载时清理
    onUnmounted(() => {
      // 清除轮询定时器
      if (pollingInterval.value) {
        clearInterval(pollingInterval.value)
      }
      
      // 移除事件监听器
      window.removeEventListener('mousemove', updateActivityStatus)
      window.removeEventListener('keydown', updateActivityStatus)
      window.removeEventListener('click', updateActivityStatus)
    })
    
    // 更新用户活动状态
    const updateActivityStatus = () => {
      lastActivityTime.value = Date.now()
      
      if (!isActive.value) {
        isActive.value = true
        // 如果当前有会话且正在轮询，调整轮询频率
        if (currentSessionId.value && pollingInterval.value) {
          clearInterval(pollingInterval.value)
          startMessagePolling()
        }
      }
    }
    
    // 检查用户是否不活跃
    const checkInactivity = () => {
      const now = Date.now()
      // 如果超过2分钟没有活动，标记为不活跃
      if (now - lastActivityTime.value > 120000) {
        isActive.value = false
        // 如果当前有会话且正在轮询，调整轮询频率
        if (currentSessionId.value && pollingInterval.value) {
          clearInterval(pollingInterval.value)
          startMessagePolling()
        }
      }
    }
    
    // 开始轮询获取新消息
    const startMessagePolling = () => {
      // 先清除可能存在的轮询
      if (pollingInterval.value) {
        clearInterval(pollingInterval.value)
      }
      
      // 根据用户活跃状态设置轮询频率
      const pollingRate = isActive.value ? basePollingRate : inactivePollingRate
      
      // 设置定时器，定期获取新消息
      pollingInterval.value = setInterval(() => {
        pollNewMessages()
        checkInactivity() // 每次轮询时检查用户活跃状态
      }, pollingRate)
      
      // 立即执行一次轮询
      pollNewMessages()
    }
    
    // 轮询获取新消息
    const pollNewMessages = async () => {
      if (!currentSessionId.value) return
      
      try {
        // 修改为使用 counselor 接口而不是 user 接口
        const response = await axios.get(
          `http://localhost:8080/api/counselor/chats/${currentSessionId.value}`
        )
        
        if (response.data && response.data.messages) {
          // 过滤出新消息
          const newMessages = response.data.messages.filter(msg => {
            // 检查消息是否已存在
            return !messages.value.some(existingMsg => 
              existingMsg.text === msg.text && 
              existingMsg.time === msg.time && 
              existingMsg.sender === msg.sender
            )
          })
          
          if (newMessages.length > 0) {
            // 添加新消息到列表
            messages.value = [...messages.value, ...newMessages]
            
            // 更新最后一条消息在活跃会话列表中的显示
            const chatIndex = activeChats.value.findIndex(c => c.id === currentSessionId.value)
            if (chatIndex !== -1 && newMessages.length > 0) {
              const lastMsg = newMessages[newMessages.length - 1]
              activeChats.value[chatIndex].lastMessage = lastMsg.text
              activeChats.value[chatIndex].lastMessageTime = lastMsg.time
            }
            
            // 如果有新消息，更新活跃状态
            updateActivityStatus()
          }
        }
      } catch (error) {
        console.error('获取新消息失败:', error)
      }
    }
    
    // 加载聊天会话
    const loadChatSession = async (counselorId) => {
      try {
        // 获取或创建会话
        const response = await axios.get(`http://localhost:8080/api/user/chats/counselor/${counselorId}`, {
          params: {
            userId: userId.value
          }
        })
        
        if (response.data) {
          currentSessionId.value = response.data.sessionId
          
          // 加载历史消息
          if (response.data.messages && response.data.messages.length > 0) {
            messages.value = response.data.messages.map(msg => ({
              id: msg.id || Date.now() + Math.random(),
              sender: msg.sender,
              text: msg.text,
              time: msg.time
            }))
            
            // 更新最后消息ID
            if (messages.value.length > 0) {
              lastMessageId.value = Math.max(...messages.value.map(msg => msg.id || 0))
            }
          }
          
          // 开始轮询新消息
          startMessagePolling()
        }
      } catch (error) {
        console.error('加载聊天会话失败:', error)
      }
    }
    
    // 选择聊天
    const selectChat = (chat) => {
      currentCounselor.value = {
        id: chat.counselorId,
        name: chat.counselorName,
        type: chat.type,
        avatar: chat.counselorAvatar || '/basic_avatar/basic_male.jpg'
      }
      
      // 加载聊天记录
      loadChatSession(chat.counselorId)
    }
    
    // 离开聊天
    const leaveChat = () => {
      // 清除轮询
      if (pollingInterval.value) {
        clearInterval(pollingInterval.value)
        pollingInterval.value = null
      }
      
      // 重置状态
      currentCounselor.value = null
      currentSessionId.value = null
      messages.value = []
      lastMessageId.value = 0
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!newMessage.value.trim() || !currentSessionId.value) return
      
      const messageText = newMessage.value
      newMessage.value = ''
      
      // 立即添加到本地消息列表
      const tempMessage = {
        id: Date.now(), // 临时ID
        sender: 'user',
        text: messageText,
        time: formatTime(new Date())
      }
      
      messages.value.push(tempMessage)
      
      try {
        // 发送到后端
        const response = await axios.post(`http://localhost:8080/api/user/chats/${currentSessionId.value}/messages`, {
          userId: userId.value,
          content: messageText
        })
        
        if (response.data) {
          // 更新临时消息的ID
          const index = messages.value.findIndex(msg => msg.id === tempMessage.id)
          if (index !== -1) {
            messages.value[index].id = response.data.messageId || tempMessage.id
            messages.value[index].time = response.data.time || tempMessage.time
          }
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        // 可以在这里添加消息发送失败的处理逻辑
      }
    }
    
    // 结束咨询
    const endConsultation = async () => {
      if (!currentCounselor.value || !currentSessionId.value) return
      
      try {
        // 调用后端结束会话接口
        await axios.put(`http://localhost:8080/api/user/chats/${currentSessionId.value}/end`, {
          userId: userId.value
        })
        
        // 保存当前咨询师信息用于评价
        endedCounselor.value = { ...currentCounselor.value }
        
        // 显示评价弹窗
        showRatingModal.value = true
      } catch (error) {
        console.error('结束会话失败:', error)
        // 即使API调用失败，也显示评价弹窗
        endedCounselor.value = { ...currentCounselor.value }
        showRatingModal.value = true
      }
    }
    
    // 提交评价
    const submitRating = async () => {
      if (!endedCounselor.value || !currentSessionId.value) return
      
      try {
        // 获取当前日期，格式为 YYYY-MM-DD
        const currentDate = new Date().toISOString().split('T')[0];
        
        // 使用正确的API路径和参数格式
        await axios.post(`http://localhost:8080/evaluation/user`, {
          evaluation_content: reviewComment.value || "无评价内容",
          rating: rating.value,
          session_id: currentSessionId.value,
          evaluation_time: currentDate
        })
        
        // 关闭评价弹窗
        closeRatingModal()
        
        // 返回会话列表
        leaveChat()
        
        // 重新加载活跃会话
        loadActiveChats()
      } catch (error) {
        console.error('提交评价失败:', error)
        // 即使评价失败，也关闭弹窗并返回列表
        closeRatingModal()
        leaveChat()
        loadActiveChats()
      }
    }
    
    // 关闭评价弹窗
    const closeRatingModal = () => {
      showRatingModal.value = false
      rating.value = 0
      reviewComment.value = ''
      endedCounselor.value = null
    }
    
    // 触发文件上传
    const triggerFileUpload = () => {
      fileInput.value.click()
    }
    
    // 处理文件上传
    const handleFileUpload = async (event) => {
      const file = event.target.files[0]
      if (!file || !currentSessionId.value) return
      
      // 检查文件类型
      if (!file.type.startsWith('image/')) {
        alert('只能上传图片文件')
        return
      }
      
      // 创建FormData对象
      const formData = new FormData()
      formData.append('file', file)
      formData.append('sessionId', currentSessionId.value)
      formData.append('senderId', userId.value)
      formData.append('receiverId', currentCounselor.value.id)
      
      try {
        // 上传图片
        const response = await axios.post('http://localhost:8080/api/messages/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data) {
          // 添加图片消息
          messages.value.push({
            id: response.data.id,
            sender: 'user',
            text: `<img src="${response.data.imageUrl}" alt="上传的图片" class="message-image">`,
            time: formatTime(new Date())
          })
          
          // 更新最后消息ID
          lastMessageId.value = Math.max(lastMessageId.value, response.data.id)
        }
      } catch (error) {
        console.error('上传图片失败:', error)
      }
      
      // 清除文件选择
      event.target.value = ''
    }
    
    // 格式化消息文本
    const formatMessage = (text) => {
      // 处理链接
      const linkRegex = /(https?:\/\/[^\s]+)/g
      const textWithLinks = text.replace(linkRegex, '<a href="$1" target="_blank">$1</a>')
      
      // 处理换行
      return textWithLinks.replace(/\n/g, '<br>')
    }
    
    // 格式化时间
    const formatTime = (date) => {
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    }
    
    // 导航
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
          router.push('/user/current-chat')
          break
        default:
          console.error('Invalid path')
      }
    }
    
    // 登出
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    return {
      username,
      userAvatar,
      activeChats,
      currentCounselor,
      messages,
      newMessage,
      isTyping,
      messagesContainer,
      fileInput,
      showRatingModal,
      rating,
      reviewComment,
      endedCounselor,
      selectChat,
      leaveChat,
      endConsultation,
      submitRating,
      closeRatingModal,
      sendMessage,
      triggerFileUpload,
      handleFileUpload,
      formatMessage,
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
  display: flex;
  flex-direction: column;
}

.top-right {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
  z-index: 10;
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
  flex: 1;
}

.no-chat {
  display: flex;
  flex-direction: column;
}

.no-chat-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 40px 0;
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
}

.primary-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 15px;
}

.primary-btn:hover {
  background-color: #0056b3;
}

.active-chats {
  margin-top: 20px;
}

.chat-preview {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-preview:hover {
  background-color: #f8f9fa;
}

.chat-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
}

.chat-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-brief {
  flex: 1;
}

.chat-brief h3 {
  margin: 0 0 5px 0;
  font-size: 1rem;
}

.chat-type {
  font-size: 0.8rem;
  color: #666;
  margin: 0 0 5px 0;
}

.last-message {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
}

.chat-time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 60px;
}

.chat-time p {
  margin: 0 0 5px 0;
  font-size: 0.8rem;
  color: #666;
}

.unread-badge {
  background-color: #007bff;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 40px);
  margin-top: 60px;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  background-color: #fff;
  border-radius: 8px 8px 0 0;
}

.back-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 1rem;
  padding: 5px 10px;
  margin-right: 10px;
}

.counselor-info {
  flex: 1;
}

.counselor-info h2 {
  margin: 0 0 5px 0;
  font-size: 1.2rem;
}

.counselor-info p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.chat-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.action-btn:hover {
  background-color: #bb2d3b;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chat-start-info {
  text-align: center;
  padding: 15px;
  background-color: #e9ecef;
  border-radius: 8px;
  margin: 20px 0;
}

.message {
  display: flex;
  margin-bottom: 10px;
  max-width: 75%;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.counselor-message {
  align-self: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 10px;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  background-color: #fff;
  padding: 10px 15px;
  border-radius: 18px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  position: relative;
}

.user-message .message-content {
  background-color: #dcedff;
  border-bottom-right-radius: 5px;
}

.counselor-message .message-content {
  background-color: #ffffff;
  border-bottom-left-radius: 5px;
}

.message-text {
  font-size: 0.95rem;
  line-height: 1.4;
  white-space: pre-wrap;
}

.message-time {
  font-size: 0.7rem;
  color: #999;
  text-align: right;
  margin-top: 3px;
}

.typing-indicator {
  align-self: flex-start;
  background-color: #f0f0f0;
  border-radius: 18px;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  margin-left: 60px;
}

.typing-indicator span {
  height: 8px;
  width: 8px;
  background-color: #999;
  border-radius: 50%;
  display: inline-block;
  margin: 0 2px;
  animation: typing 1.4s infinite both;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0% {
    transform: translateY(0px);
    background-color: #999;
  }
  28% {
    transform: translateY(-7px);
    background-color: #555;
  }
  44% {
    transform: translateY(0px);
    background-color: #999;
  }
}

.chat-input-area {
  padding: 15px;
  background-color: #fff;
  border-top: 1px solid #eee;
  border-radius: 0 0 8px 8px;
  display: flex;
  flex-direction: column;
}

.chat-input-area textarea {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 10px 15px;
  font-size: 0.95rem;
  resize: none;
  outline: none;
  margin-bottom: 10px;
  font-family: Arial, sans-serif;
}

.chat-input-area textarea:focus {
  border-color: #007bff;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-btn {
  background: none;
  border: none;
  color: #666;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
}

.upload-btn:hover {
  color: #007bff;
}

.send-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  min-width: 80px;
}

.send-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.send-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
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
  padding: 20px;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  position: relative;
}

.modal-content h2 {
  margin-top: 0;
  font-size: 1.5rem;
  text-align: center;
  color: #333;
}

.modal-content p {
  font-size: 1rem;
  color: #666;
  text-align: center;
  margin-bottom: 20px;
}

.star-rating {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.star {
  font-size: 2rem;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
  margin: 0 5px;
}

.star.active {
  color: #ffc107;
}

.rating-comment {
  margin-bottom: 20px;
}

.rating-comment textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: Arial, sans-serif;
}

.rating-actions {
  display: flex;
  justify-content: center;
}

.submit-rating-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.submit-rating-btn:hover {
  background-color: #0056b3;
}

.close-rating-btn {
  background-color: #ccc;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-left: 10px;
}

.close-rating-btn:hover {
  background-color: #aaa;
}
</style>