<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item active" @click="goTo('chat')">咨询窗口</div>
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

      <!-- 并排显示会话列表和会话窗口 -->
      <div class="chat-layout">
        <!-- 会话列表 -->
        <div class="chat-list">
          <h1>咨询窗口</h1>
          <p>您有 {{ activeChats.length }} 个进行中的咨询会话</p>
          
          <div v-if="activeChats.length === 0" class="no-chat-message">
            <div class="empty-icon">
              <img src="/basic_avatar/basic_male.jpg" alt="无会话">
            </div>
            <p>您目前没有进行中的咨询会话</p>
            <p class="sub-message">请等待用户发起咨询或查看排班表</p>
            <button class="primary-btn" @click="goTo('schedule')">查看排班表</button>
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
                <img src="/basic_avatar/basic_male.jpg" alt="用户头像">
              </div>
              <div class="chat-brief">
                <h3>{{ chat.userName }}</h3>
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

        <!-- 会话窗口 -->
        <div class="chat-container" :class="{'empty': !currentChat}">
          <!-- 当没有选择会话时，显示一个固定大小的空白方框 -->
          <div v-if="!currentChat" class="placeholder-box">
            <p>请选择一个会话开始聊天</p>
          </div>

          <!-- 如果选择了会话，显示聊天界面 -->
          <template v-else>
            <!-- 聊天头部信息 -->
            <div class="chat-header">
              <button class="back-btn" @click="leaveChat">
                &larr; 返回
              </button>
              <div class="user-info">
                <h2>{{ currentChat.userName }}</h2>
                <p>{{ currentChat.type }}</p>
              </div>
              <div class="chat-actions">
                <button class="action-btn end-btn" title="结束咨询" @click="endConsultation">
                  结束
                </button>
              </div>
            </div>
          
            <!-- 聊天消息区域 -->
            <div class="messages-container" ref="messagesContainer">
              <div v-if="messages.length === 0" class="chat-start-info">
                <p>咨询已开始，等待用户发送消息</p>
              </div>
              
              <div 
                v-for="(message, index) in messages" 
                :key="index"
                :class="['message', message.sender === 'counselor' ? 'counselor-message' : 'user-message']"
              >
                <div class="message-avatar">
                  <img 
                    :src="message.sender === 'counselor' ? counselorAvatar : '/basic_avatar/user_default.jpg'" 
                    :alt="message.sender === 'counselor' ? '我' : currentChat.userName"
                  >
                </div>
                <div class="message-content">
                  <div class="message-text" v-html="formatMessage(message.text)"></div>
                  <div class="message-time">{{ message.time }}</div>
                </div>
              </div>
              
              <div v-if="isUserTyping" class="typing-indicator">
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
                placeholder="输入您的回复..." 
                rows="3"
              ></textarea>
              <div class="input-actions">
                <div class="quick-responses">
                  <button 
                    v-for="(response, index) in quickResponses" 
                    :key="index" 
                    class="quick-response-btn"
                    @click="insertQuickResponse(response)"
                  >
                    {{ response.label }}
                  </button>
                </div>
                <div class="send-actions">
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
          </template>
        </div>
      </div>
    </div>

    <!-- 结束咨询确认弹窗 -->
    <div v-if="showEndConfirm" class="modal-overlay" @click="showEndConfirm = false">
      <div class="modal-content" @click.stop>
        <h2>结束咨询</h2>
        <p>确定要结束与 {{ currentChat?.userName }} 的咨询吗？</p>
        
        <div class="end-reason">
          <textarea 
            v-model="endReason" 
            placeholder="请简要说明结束原因（可选）" 
            rows="3"
          ></textarea>
        </div>
        
        <div class="end-actions">
          <button @click="confirmEndConsultation" class="confirm-end-btn">确认结束</button>
          <button @click="showEndConfirm = false" class="cancel-btn">取消</button>
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
  name: 'CounselorChat',
  setup() {
    const store = useStore()
    const router = useRouter()
    const messagesContainer = ref(null)
    const fileInput = ref(null)

    const username = computed(() => store.getters.username)
    const counselorAvatar = 'data:image/svg+xml;base64,'
    const showEndConfirm = ref(false)
    const endReason = ref('')
    const isUserTyping = ref(false)
    
    const counselorId = computed(() => localStorage.getItem('counselor_id') || 
                       JSON.parse(localStorage.getItem('user'))?.counselorId || 
                       1) // 默认值
    
    // 当前选中的聊天，初始为 null
    const currentChat = ref(null)
    const newMessage = ref('')
    
    // 快速回复选项
    const quickResponses = ref([
      { label: '👋 问候', text: '您好，很高兴能为您提供帮助。请告诉我您目前遇到了什么问题？' },
      { label: '🤔 探索', text: '能否请您详细描述一下这种感受是什么时候开始的？' },
      { label: '💪 鼓励', text: '我理解这对您来说很困难，感谢您的勇气和信任。我们一起来面对这个问题。' },
      { label: '💡 建议', text: '您可以尝试以下放松技巧：深呼吸、渐进式肌肉放松或正念冥想，这些方法可能对缓解您的症状有所帮助。' },
      { label: '📝 总结', text: '让我来总结一下我们今天讨论的要点：' }
    ])
    
    // 所有进行中的聊天
    const activeChats = ref([])
    
    // 当前聊天的消息列表
    const messages = ref([])
    
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
        const response = await axios.get(`http://localhost:8080/api/counselor/chats?counselorId=${counselorId.value}`)
        
        if (response.data) {
          activeChats.value = response.data
        }
      } catch (error) {
        console.error('加载活跃会话失败:', error)
        // 保留模拟数据作为备用
      }
    }
    
    // 在组件挂载时加载数据
    onMounted(() => {
      loadActiveChats()
      
      // 添加用户活动监听器
      window.addEventListener('mousemove', updateActivityStatus)
      window.addEventListener('keydown', updateActivityStatus)
      window.addEventListener('click', updateActivityStatus)
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
        if (currentChat.value && pollingInterval.value) {
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
        if (currentChat.value && pollingInterval.value) {
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
      if (!currentChat.value) return
      
      try {
        // 使用GET请求获取所有消息，然后在前端过滤
        const response = await axios.get(
          `http://localhost:8080/api/counselor/chats/${currentChat.value.id}`
        )
        
        if (response.data && response.data.messages) {
          // 过滤出新消息（ID大于lastMessageId的消息）
          const newMessages = response.data.messages.filter(msg => {
            // 如果消息没有ID，可以使用其他方式判断是否为新消息
            // 这里假设后端返回的消息有id字段
            return !messages.value.some(existingMsg => 
              existingMsg.text === msg.text && 
              existingMsg.time === msg.time && 
              existingMsg.sender === msg.sender
            )
          })
          
          // 添加新消息到消息列表
          if (newMessages.length > 0) {
            messages.value = [...messages.value, ...newMessages]
            
            // 更新最后一条消息
            const chatIndex = activeChats.value.findIndex(c => c.id === currentChat.value.id)
            if (chatIndex !== -1 && newMessages.length > 0) {
              const lastMsg = newMessages[newMessages.length - 1]
              activeChats.value[chatIndex].lastMessage = lastMsg.text
              activeChats.value[chatIndex].lastMessageTime = lastMsg.time
            }
          }
        }
      } catch (error) {
        console.error('轮询新消息失败:', error)
      }
    }
    
    // 选择一个会话
    const selectChat = async (chat) => {
      try {
        const response = await axios.get(`http://localhost:8080/api/counselor/chats/${chat.id}`)
        
        if (response.data) {
          currentChat.value = {
            id: response.data.sessionId,
            userId: response.data.userId,
            userName: response.data.userName,
            type: response.data.type,
            status: response.data.status
          }
          
          // 加载消息历史
          messages.value = response.data.messages || []
          
          // 设置最后消息ID
          if (messages.value.length > 0) {
            const lastMsg = messages.value[messages.value.length - 1]
            lastMessageId.value = lastMsg.id || 0
          }
          
          // 开始轮询获取新消息
          startMessagePolling()
        }
      } catch (error) {
        console.error('加载会话详情失败:', error)
        alert('加载会话详情失败，请重试')
        return // 加载失败时直接返回，不选择聊天
      }
      
      // 清除未读标记
      const chatIndex = activeChats.value.findIndex(c => c.id === chat.id)
    }
    
    // 离开当前会话
    const leaveChat = () => {
      // 清除轮询定时器
      if (pollingInterval.value) {
        clearInterval(pollingInterval.value)
        pollingInterval.value = null
      }
      
      currentChat.value = null
      messages.value = []
      lastMessageId.value = 0
    }
    
    // 结束咨询
    const endConsultation = () => {
      showEndConfirm.value = true
    }
    
    // 确认结束咨询
    const confirmEndConsultation = async () => {
      try {
        const response = await axios.put(
          `http://localhost:8080/api/counselor/chats/${currentChat.value.id}/end`,
          null,
          {
            params: {
              reason: endReason.value
            }
          }
        )
        
        // 从活跃聊天中移除
        const chatIndex = activeChats.value.findIndex(c => c.id === currentChat.value.id)
        if (chatIndex !== -1) {
          activeChats.value.splice(chatIndex, 1)
        }
        
        // 通知用户会话已结束
        alert(`已结束与${currentChat.value.userName}的咨询会话`)
        
        // 清除状态
        showEndConfirm.value = false
        endReason.value = ''
        leaveChat()
      } catch (error) {
        console.error('结束会话失败:', error)
        alert('结束会话失败，请重试')
      }
    }
    
    // 快速回复
    const insertQuickResponse = (response) => {
      newMessage.value = response.text
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!newMessage.value.trim()) return
      
      // 创建消息对象
      const message = {
        sender: 'counselor',
        text: newMessage.value,
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      }
      
      // 添加到本地消息列表
      messages.value.push(message)
      
      try {
        // 通过HTTP API发送消息 - 修改为使用URL参数
        const response = await axios.post(
          `http://localhost:8080/api/counselor/chats/${currentChat.value.id}/messages`,
          null,  // 请求体为空
          {
            params: {  // 使用params传递URL参数
              counselorId: counselorId.value,
              content: newMessage.value
            }
          }
        )
        
        if (response.data && response.data.messageId) {
          // 更新最后消息ID
          lastMessageId.value = response.data.messageId
          
          // 更新最后一条消息
          const chatIndex = activeChats.value.findIndex(c => c.id === currentChat.value.id)
          if (chatIndex !== -1) {
            activeChats.value[chatIndex].lastMessage = newMessage.value
            activeChats.value[chatIndex].lastMessageTime = message.time
          }
        }
        
        // 清空输入框
        newMessage.value = ''
        
        // 更新活动时间
        updateActivityStatus()
      } catch (error) {
        console.error('发送消息失败:', error)
        alert('发送消息失败，请重试')
      }
    }
    
    // 格式化日期时间为HH:mm格式
    const formatTimeFromDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return ''
      const date = new Date(dateTimeStr)
      return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
    }
    
    // 触发文件上传
    const triggerFileUpload = () => {
      fileInput.value.click()
    }
    
    // 处理文件上传
    const handleFileUpload = (event) => {
      const file = event.target.files[0]
      if (!file) return
      alert('图片上传功能需要后端支持，这里仅做界面展示')
      event.target.value = ''
    }
    
    // 消息格式化，支持换行和链接
    const formatMessage = (text) => {
      if (!text) return ''
      const withLineBreaks = text.replace(/\n/g, '<br>')
      const withLinks = withLineBreaks.replace(
        /(https?:\/\/[^\s]+)/g, 
        '<a href="$1" target="_blank">$1</a>'
      )
      return withLinks
    }

    // 登出处理
    const logout = () => {
      // 如果当前有活跃聊天，先执行离开聊天的操作
      if (currentChat.value) {
        leaveChat()
      }
      store.dispatch('logout')
      router.push('/login')
    }

    // 页面导航
    const goTo = (path) => {
      // 如果当前有活跃聊天，先执行离开聊天的操作
      if (currentChat.value && path !== 'chat') {
        leaveChat()
      }
      
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
      counselorAvatar,
      activeChats,
      currentChat,
      messages,
      newMessage,
      isUserTyping,
      messagesContainer,
      fileInput,
      showEndConfirm,
      endReason,
      quickResponses,
      selectChat,
      leaveChat,
      endConsultation,
      confirmEndConsultation,
      insertQuickResponse,
      sendMessage,
      triggerFileUpload,
      handleFileUpload,
      formatMessage,
      logout,
      goTo,
      loadActiveChats,
      pollNewMessages,
      startMessagePolling
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

.sub-message {
  margin-top: 0;
  color: #999;
  font-size: 0.9rem;
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

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 5px;
}

.status-indicator.online {
  background-color: #28a745;
}

.status-indicator.offline {
  background-color: #dc3545;
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

.user-info {
  flex: 1;
}

.user-info h2 {
  margin: 0 0 5px 0;
  font-size: 1.2rem;
}

.user-info p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.chat-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.end-btn {
  background-color: #dc3545;
  color: white;
}

.end-btn:hover {
  background-color: #c82333;
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
  align-self: flex-start;
}

.counselor-message {
  align-self: flex-end;
  flex-direction: row-reverse;
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

.counselor-message .message-content {
  background-color: #dcedff;
  border-bottom-right-radius: 5px;
}

.user-message .message-content {
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
  flex-direction: column;
  gap: 10px;
}

.quick-responses {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 10px;
}

.quick-response-btn {
  background-color: #e9ecef;
  border: none;
  border-radius: 15px;
  padding: 5px 10px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.quick-response-btn:hover {
  background-color: #dee2e6;
}

.send-actions {
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
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 1.5rem;
  color: #333;
}

.modal-content p {
  margin-top: 0;
  color: #666;
}

.end-reason {
  margin: 15px 0;
}

.end-reason textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: Arial, sans-serif;
  resize: vertical;
}

.end-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

.confirm-end-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-end-btn:hover {
  background-color: #0056b3;
}

.cancel-btn {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #5a6268;
}

/* 添加并排布局样式 */
.chat-layout {
  display: flex;
  gap: 20px;
  margin-top: 60px;
  height: calc(100vh - 120px);
}

.chat-list {
  flex: 0 0 300px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto;
}

.chat-container {
  flex: 1;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-container.empty .placeholder-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: #f8f9fa;
  color: #666;
  font-size: 1.1rem;
}

/* 修改原有样式以适应新布局 */
.card {
  margin-top: 0;
}

.no-chat {
  margin-top: 60px;
}

.chat-container {
  margin-top: 0;
  height: 100%;
}
</style>