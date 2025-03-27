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
              <img :src="chat.counselorAvatar" :alt="chat.counselorName">
            </div>
            <div class="chat-brief">
              <h3>{{ chat.counselorName }}</h3>
              <p class="chat-type">{{ chat.type }}</p>
              <p class="last-message">{{ chat.lastMessage }}</p>
            </div>
            <div class="chat-time">
              <p>{{ chat.lastMessageTime }}</p>
              <span v-if="chat.unreadCount > 0" class="unread-badge">{{ chat.unreadCount }}</span>
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
                :src="message.sender === 'user' ? userAvatar : currentCounselor.avatar" 
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
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

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
    const userAvatar = 'data:image/svg+xml;base64,'
    
    // 当前选中的咨询师，初始为 null
    const currentCounselor = ref(null)
    
    // 所有进行中的聊天
    const activeChats = ref([
      {
        id: 102,
        counselorId: 1,
        counselorName: '李明',
        counselorAvatar: 'data:image/svg+xml;base64,',
        type: '焦虑症咨询',
        lastMessage: '您好，请问有什么可以帮助您的？',
        lastMessageTime: '11:20',
        unreadCount: 2
      }
    ])
    
    // 当前聊天的消息列表
    const messages = ref([])
    
    // 模拟消息数据
    const mockMessages = {
      1: [ // 咨询师ID = 1 的消息历史
        {
          sender: 'counselor',
          text: '您好，我是李明，很高兴今天能和您交流。您最近感到焦虑的主要原因是什么？',
          time: '11:02'
        },
        {
          sender: 'user',
          text: '最近工作压力很大，经常失眠，感觉很难放松下来。',
          time: '11:05'
        },
        {
          sender: 'counselor',
          text: '理解您的感受。失眠和压力确实会形成恶性循环。我们可以先尝试一些放松技巧。您平时有什么放松的方式吗？',
          time: '11:08'
        },
        {
          sender: 'user',
          text: '以前喜欢跑步，但最近太忙了，没时间去。',
          time: '11:10'
        },
        {
          sender: 'counselor',
          text: '运动确实是缓解焦虑的好方法。即使很忙，我们也可以找到一些小的时间窗口来活动。您觉得每天抽15分钟做些简单的运动可行吗？',
          time: '11:15'
        },
        {
          sender: 'counselor',
          text: '此外，我还建议您尝试深呼吸练习，每天早晚各5分钟，这在科学上已被证明能有效降低焦虑水平。您能在工作间隙试试吗？',
          time: '11:20'
        }
      ]
    }
    
    // 检查并加载预约过来的咨询师信息
    onMounted(() => {
      const savedCounselor = localStorage.getItem('currentCounselor')
      
      if (savedCounselor) {
        const counselorData = JSON.parse(savedCounselor)
        
        // 在实际应用中，这些数据应该从API获取
        // 如果是从预约页面过来，设置当前咨询师
        const counselorInfo = {
          id: counselorData.id,
          name: counselorData.name,
          type: counselorData.type,
          avatar: 'data:image/svg+xml;base64,'
        }
        
        // 设置当前咨询师
        currentCounselor.value = counselorInfo
        
        // 加载聊天记录
        loadMessages(counselorInfo.id)
        
        // 清除localStorage
        localStorage.removeItem('currentCounselor')
        
        /* 
        后端需要实现：
        1. GET /api/chats/{counselorId}/messages
        2. 返回与该咨询师的聊天记录
        */
      }
    })

    // 监听消息变化，自动滚动到底部
    watch(messages, () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    })
    
    // 从模拟数据加载消息
    const loadMessages = (counselorId) => {
      if (mockMessages[counselorId]) {
        messages.value = [...mockMessages[counselorId]]
      } else {
        messages.value = []
      }
    }
    
    // 选择一个会话
    const selectChat = (chat) => {
      currentCounselor.value = {
        id: chat.counselorId,
        name: chat.counselorName,
        type: chat.type,
        avatar: chat.counselorAvatar
      }
      
      // 加载聊天记录
      loadMessages(chat.counselorId)
      
      // 清除未读标记
      const chatIndex = activeChats.value.findIndex(c => c.id === chat.id)
      if (chatIndex !== -1) {
        activeChats.value[chatIndex].unreadCount = 0
      }
    }
    
    // 离开当前会话
    const leaveChat = () => {
      currentCounselor.value = null
    }
    
    // 结束咨询
    const showRatingModal = ref(false);
    const rating = ref(5); // 默认5星
    const reviewComment = ref('');
    const endedCounselor = ref(null);

    const endConsultation = () => {
      if (confirm('确定要结束本次咨询吗？')) {
        // 保存当前咨询师信息
        endedCounselor.value = { ...currentCounselor.value };
        
        // 显示评分弹窗
        showRatingModal.value = true;
        
        // 移除活跃会话
        const chatIndex = activeChats.value.findIndex(c => c.counselorId === currentCounselor.value.id);
        if (chatIndex !== -1) {
          activeChats.value.splice(chatIndex, 1);
        }
        
        // 将消息保存到历史记录中
        saveToHistory();
        
        // 返回会话列表
        currentCounselor.value = null;
        
        /* 
        后端需要实现：
        1. PUT /api/appointments/{id}/complete
        2. 更新预约状态为completed
        */
      }
    }

    // 添加保存到历史记录的函数
    const saveToHistory = () => {
      const now = new Date();
      const chatHistory = {
        id: Date.now(), // 使用时间戳作为临时ID
        counselorId: currentCounselor.value.id,
        counselorName: currentCounselor.value.name,
        type: currentCounselor.value.type,
        date: now.toISOString().split('T')[0],
        messages: [...messages.value],
        expiryDate: new Date(now.setMonth(now.getMonth() + 1)).toISOString() // 1个月后过期
      };
      
      // 这里应该调用API保存历史记录
      // 在实际应用中，这些历史记录应该存储在后端数据库中
      
      /* 
      后端需要实现：
      1. POST /api/chat-history
      2. 需要发送完整的聊天历史记录
      3. 设置过期时间为1个月
      */
      
      console.log('对话已保存到历史记录', chatHistory);
    }

    // 添加提交评价的函数
    const submitRating = () => {
      alert(`感谢您的评价！您给${endedCounselor.value.name}咨询师的评分是${rating.value}星`);
      
      /* 
      后端需要实现：
      1. POST /api/counselors/{id}/ratings
      2. 发送评分和评价内容
      */
      
      showRatingModal.value = false;
      reviewComment.value = '';
      rating.value = 5;
      endedCounselor.value = null;
    }

    // 关闭评价弹窗
    const closeRatingModal = () => {
      showRatingModal.value = false;
      reviewComment.value = '';
      rating.value = 5;
      endedCounselor.value = null;
    }
    
    // 发送消息
    const sendMessage = () => {
      if (!newMessage.value.trim()) return
      const now = new Date()
      const timeStr = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`
      messages.value.push({
        sender: 'user',
        text: newMessage.value,
        time: timeStr
      })
      const sentMessage = newMessage.value
      newMessage.value = ''
      isTyping.value = true
      setTimeout(() => {
        isTyping.value = false
        const responses = [
          "我理解您的感受，请继续分享更多细节。",
          "这个情况听起来确实很困扰，让我们一起分析一下原因。",
          "您的感受是很正常的，我们可以尝试一些方法来改善这种状况。",
          "谢谢您的分享。在这种情况下，我建议您可以尝试以下几种方法...",
          "您描述的情况很常见，许多人都有类似的经历。我们可以一起探索解决方案。"
        ]
        const randomResponse = responses[Math.floor(Math.random() * responses.length)]
        messages.value.push({
          sender: 'counselor',
          text: randomResponse,
          time: `${now.getHours()}:${(now.getMinutes() + 1).toString().padStart(2, '0')}`
        })
      }, 2000)
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
      store.dispatch('logout')
      router.push('/login')
    }

    // 页面导航
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

    return {
      username,
      activeChats,
      currentCounselor,
      messages,
      newMessage,
      isTyping,
      messagesContainer,
      fileInput,
      userAvatar,
      selectChat,
      leaveChat,
      endConsultation,
      showRatingModal,
      rating,
      reviewComment,
      endedCounselor,
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