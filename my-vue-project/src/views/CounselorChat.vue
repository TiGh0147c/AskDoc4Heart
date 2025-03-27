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
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <!-- 如果没有选择会话，显示会话列表 -->
      <div v-if="!currentChat" class="card no-chat">
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
              <span v-if="chat.unreadCount > 0" class="unread-badge">{{ chat.unreadCount }}</span>
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
          <div class="user-info">
            <h2>{{ currentChat.userName }}</h2>
            <p>{{ currentChat.type }}</p>
          </div>
          <div class="chat-actions">
            <button class="action-btn notes-btn" title="用户笔记" @click="showNotes = true">
              笔记
            </button>
            <button 
              class="action-btn"
              :class="{'pause-btn': !isPaused, 'resume-btn': isPaused}" 
              title="暂停/继续咨询" 
              @click="togglePause"
            >
              {{ isPaused ? '继续' : '暂停' }}
            </button>
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
          <div v-if="isPaused" class="pause-notice">
            <p>咨询已暂停，用户无法看到您的新消息</p>
            <button class="resume-btn" @click="togglePause">继续咨询</button>
          </div>
          <textarea 
            v-else
            v-model="newMessage" 
            @keydown.enter.prevent="sendMessage"
            placeholder="输入您的回复..." 
            rows="3"
          ></textarea>
          <div class="input-actions" v-if="!isPaused">
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
      </div>
    </div>

    <!-- 用户笔记弹窗 -->
    <div v-if="showNotes" class="modal-overlay" @click="showNotes = false">
      <div class="modal-content" @click.stop>
        <h2>用户笔记</h2>
        <p>{{ currentChat?.userName }} - {{ currentChat?.type }}</p>
        
        <div class="notes-content">
          <textarea 
            v-model="userNotes" 
            placeholder="在这里记录用户的咨询笔记..." 
            rows="8"
          ></textarea>
        </div>
        
        <div class="previous-notes" v-if="previousNotes.length > 0">
          <h3>历史笔记</h3>
          <div 
            v-for="(note, index) in previousNotes" 
            :key="index"
            class="note-item"
          >
            <div class="note-date">{{ note.date }}</div>
            <div class="note-text">{{ note.text }}</div>
          </div>
        </div>
        
        <div class="notes-actions">
          <button @click="saveNotes" class="save-notes-btn">保存笔记</button>
          <button @click="showNotes = false" class="close-btn">关闭</button>
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
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'CounselorChat',
  setup() {
    const store = useStore()
    const router = useRouter()
    const messagesContainer = ref(null)
    const fileInput = ref(null)

    const username = computed(() => store.getters.username)
    const counselorAvatar = 'data:image/svg+xml;base64,'
    const showNotes = ref(false)
    const userNotes = ref('')
    const previousNotes = ref([])
    const showEndConfirm = ref(false)
    const endReason = ref('')
    const isPaused = ref(false)
    const isUserTyping = ref(false)
    
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
    const activeChats = ref([
      {
        id: 101,
        userId: 1001,
        userName: '张小明',
        type: '焦虑症咨询',
        lastMessage: '我最近总是感到很紧张，特别是在公共场合...',
        lastMessageTime: '09:15',
        unreadCount: 3,
        status: 'online'
      },
      {
        id: 102,
        userId: 1002,
        userName: '李华',
        type: '抑郁症咨询',
        lastMessage: '谢谢您的建议，我会试着去做。',
        lastMessageTime: '昨天',
        unreadCount: 0,
        status: 'offline'
      },
      {
        id: 103,
        userId: 1003,
        userName: '王芳',
        type: '人际关系咨询',
        lastMessage: '我该如何和我的同事相处呢？',
        lastMessageTime: '10:30',
        unreadCount: 1,
        status: 'online'
      }
    ])
    
    // 当前聊天的消息列表
    const messages = ref([])
    
    // 模拟消息数据
    const mockMessages = {
      101: [ // 聊天ID = 101 的消息历史
        {
          sender: 'counselor',
          text: '您好，我是您的咨询师。很高兴能和您交流，请告诉我您最近遇到了什么困扰？',
          time: '09:02'
        },
        {
          sender: 'user',
          text: '我最近总是感到很紧张，特别是在公共场合，有时候甚至会出现心跳加速、呼吸困难的情况。',
          time: '09:05'
        },
        {
          sender: 'counselor',
          text: '您描述的症状确实和焦虑有关。这种情况持续多久了？平时有什么情况会让症状加重吗？',
          time: '09:08'
        },
        {
          sender: 'user',
          text: '大概有两个月了。当我需要在公司开会发言或者在人多的地方，症状就会特别明显。',
          time: '09:10'
        },
        {
          sender: 'counselor',
          text: '了解了。这种情况被称为社交焦虑，是很常见的。您在遇到这种情况时，头脑中通常会有什么想法？',
          time: '09:12'
        },
        {
          sender: 'user',
          text: '我总是担心自己会出错，别人会笑话我或者看不起我。',
          time: '09:15'
        }
      ],
      102: [ // 聊天ID = 102 的消息历史
        {
          sender: 'counselor',
          text: '您好，很高兴能继续我们的交流。上次我们讨论到您感到情绪低落的情况，最近有什么变化吗？',
          time: '昨天 14:30'
        },
        {
          sender: 'user',
          text: '我按照您说的尝试了每天记录三件感恩的事，感觉有一点帮助。',
          time: '昨天 14:35'
        },
        {
          sender: 'counselor',
          text: '这是个很好的开始！能否分享一下您记录的内容或者在实践过程中的感受？',
          time: '昨天 14:40'
        },
        {
          sender: 'user',
          text: '我记录了阳光很好、朋友发来问候、完成了一项工作任务。做这个练习时我确实感到了一些积极的情绪。',
          time: '昨天 14:45'
        },
        {
          sender: 'counselor',
          text: '太棒了！注意到生活中的积极面是改善情绪的重要一步。除了感恩练习，我还建议您可以尝试适量的户外活动，阳光和运动对情绪也有很大帮助。',
          time: '昨天 14:50'
        },
        {
          sender: 'user',
          text: '谢谢您的建议，我会试着去做。',
          time: '昨天 14:55'
        }
      ],
      103: [ // 聊天ID = 103 的消息历史
        {
          sender: 'counselor',
          text: '您好！欢迎来到咨询，我是您的咨询师。请问您今天想要讨论什么话题？',
          time: '10:00'
        },
        {
          sender: 'user',
          text: '您好，我想咨询一下关于人际关系的问题，特别是工作中的人际关系。',
          time: '10:05'
        },
        {
          sender: 'counselor',
          text: '工作中的人际关系确实很重要。能否具体描述一下您遇到的困难？',
          time: '10:10'
        },
        {
          sender: 'user',
          text: '我在公司是新来的，感觉很难融入团队。有些同事似乎对我有些排斥，我不知道该怎么处理。',
          time: '10:15'
        },
        {
          sender: 'counselor',
          text: '初来乍到确实需要一段适应期。您有没有尝试过主动和同事交流或参加团队活动？',
          time: '10:20'
        },
        {
          sender: 'user',
          text: '我尝试过，但我性格比较内向，不太善于主动社交。我该如何和我的同事相处呢？',
          time: '10:30'
        }
      ]
    }
    
    // 模拟用户笔记数据
    const mockNotes = {
      101: [
        { date: '2025-03-20', text: '用户表现出明显的社交焦虑症状，主要在公众场合出现。考虑认知行为疗法方向。' },
        { date: '2025-03-15', text: '首次会谈，用户描述有心悸、呼吸急促等症状，需进一步评估是否为广泛性焦虑障碍。' }
      ],
      102: [
        { date: '2025-03-26', text: '用户抑郁症状有所改善，感恩日记练习效果良好。下次可以引入更多行为激活策略。' },
        { date: '2025-03-19', text: '用户表现出中度抑郁症状，睡眠和食欲都受到影响，建议进行规律作息训练。' }
      ],
      103: []
    }
    
    // 监听消息变化，自动滚动到底部
    watch(messages, () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    })
    
    // 从模拟数据加载消息
    const loadMessages = (chatId) => {
      if (mockMessages[chatId]) {
        messages.value = [...mockMessages[chatId]]
      } else {
        messages.value = []
      }
    }
    
    // 从模拟数据加载笔记
    const loadNotes = (chatId) => {
      if (mockNotes[chatId]) {
        previousNotes.value = [...mockNotes[chatId]]
        userNotes.value = '' // 清空当前笔记输入框
      } else {
        previousNotes.value = []
        userNotes.value = ''
      }
    }
    
    // 选择一个会话
    const selectChat = (chat) => {
      currentChat.value = {
        id: chat.id,
        userId: chat.userId,
        userName: chat.userName,
        type: chat.type,
      }
      
      // 加载聊天记录
      loadMessages(chat.id)
      
      // 清除未读标记
      const chatIndex = activeChats.value.findIndex(c => c.id === chat.id)
      if (chatIndex !== -1) {
        activeChats.value[chatIndex].unreadCount = 0
      }
      
      isPaused.value = false
      
      /* 
      后端需要实现：
      1. GET /api/counselor/chats/{chatId}
      2. 返回与该用户的聊天详情和历史消息
      3. 将未读消息标记为已读
      */
    }
    
    // 离开当前会话
    const leaveChat = () => {
      currentChat.value = null
      messages.value = []
      showNotes.value = false
      userNotes.value = ''
      previousNotes.value = []
      isPaused.value = false
    }
    
    // 暂停/继续会话
    const togglePause = () => {
      isPaused.value = !isPaused.value
      
      /* 
      后端需要实现：
      1. PUT /api/counselor/chats/{chatId}/status
      2. 更新聊天状态（暂停/继续）
      3. 暂停状态下用户消息不会实时推送给咨询师，但会保存
      */
    }
    
    // 结束咨询
    const endConsultation = () => {
      showEndConfirm.value = true
    }
    
    // 确认结束咨询
    const confirmEndConsultation = () => {
      /* 
      后端需要实现：
      1. PUT /api/counselor/chats/{chatId}/end
      2. 结束聊天会话
      3. 保存结束原因
      4. 将聊天移至历史记录
      */
      
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
    }
    
    // 保存用户笔记
    const saveNotes = () => {
      if (userNotes.value.trim()) {
        const now = new Date()
        const dateStr = now.toISOString().split('T')[0]
        
        // 添加到历史笔记中
        previousNotes.value.unshift({
          date: dateStr,
          text: userNotes.value.trim()
        })
        
        /* 
        后端需要实现：
        1. POST /api/counselor/users/{userId}/notes
        2. 保存咨询笔记
        3. 关联到特定用户
        */
        
        alert('笔记已保存')
        userNotes.value = ''
      }
    }
    
    // 快速回复
    const insertQuickResponse = (response) => {
      newMessage.value = response.text
    }
    
    // 发送消息
    const sendMessage = () => {
      if (!newMessage.value.trim() || isPaused.value) return
      
      const now = new Date()
      const timeStr = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`
      
      messages.value.push({
        sender: 'counselor',
        text: newMessage.value,
        time: timeStr
      })
      
      // 更新最后一条消息
      const chatIndex = activeChats.value.findIndex(c => c.id === currentChat.value.id)
      if (chatIndex !== -1) {
        activeChats.value[chatIndex].lastMessage = newMessage.value
        activeChats.value[chatIndex].lastMessageTime = timeStr
      }
      
      /* 
      后端需要实现：
      1. POST /api/counselor/chats/{chatId}/messages
      2. 发送消息到用户
      3. 消息内容、时间戳等信息
      */
      
      newMessage.value = ''
      
      // 模拟用户输入和回复
      simulateUserTypingAndReply()
    }
    
    // 模拟用户输入和回复
    const simulateUserTypingAndReply = () => {
      // 显示用户正在输入
      setTimeout(() => {
        isUserTyping.value = true
        
        // 模拟用户回复
        setTimeout(() => {
          isUserTyping.value = false
          
          const now = new Date()
          const timeStr = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`
          
          const responses = [
            "是的，我明白您的意思。这个建议我会尝试一下。",
            "谢谢您的理解和支持，我感觉好多了。",
            "这个问题我之前没有考虑过，您说的有道理。",
            "我对这个方法有些疑虑，能否详细解释一下？",
            "最近情况确实有所改善，但有时候还是会感到无助。"
          ]
          
          const randomResponse = responses[Math.floor(Math.random() * responses.length)]
          
          messages.value.push({
            sender: 'user',
            text: randomResponse,
            time: timeStr
          })
          
          // 更新最后一条消息
          const chatIndex = activeChats.value.findIndex(c => c.id === currentChat.value.id)
          if (chatIndex !== -1) {
            activeChats.value[chatIndex].lastMessage = randomResponse
            activeChats.value[chatIndex].lastMessageTime = timeStr
          }
        }, 3000)
      }, 1000)
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
      
      /* 
      后端需要实现：
      1. POST /api/counselor/chats/{chatId}/messages/attachment
      2. 上传图片附件
      3. 文件处理和存储
      */
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
    
    // 加载笔记
    const showUserNotes = (chatId) => {
      loadNotes(chatId)
      showNotes.value = true
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
      showNotes,
      userNotes,
      previousNotes,
      showEndConfirm,
      endReason,
      isPaused,
      quickResponses,
      selectChat,
      leaveChat,
      togglePause,
      endConsultation,
      confirmEndConsultation,
      saveNotes,
      insertQuickResponse,
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

.notes-btn {
  background-color: #17a2b8;
  color: white;
}

.notes-btn:hover {
  background-color: #138496;
}

.pause-btn {
  background-color: #ffc107;
  color: #212529;
}

.pause-btn:hover {
  background-color: #e0a800;
}

.resume-btn {
  background-color: #28a745;
  color: white;
}

.resume-btn:hover {
  background-color: #218838;
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

.pause-notice {
  background-color: #f8d7da;
  color: #721c24;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
  text-align: center;
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

.notes-content, .end-reason {
  margin: 15px 0;
}

.notes-content textarea, .end-reason textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: Arial, sans-serif;
  resize: vertical;
}

.previous-notes {
  margin-top: 20px;
  max-height: 200px;
  overflow-y: auto;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.previous-notes h3 {
  font-size: 1.1rem;
  margin-top: 0;
  margin-bottom: 10px;
}

.note-item {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.note-date {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 5px;
}

.note-text {
  font-size: 0.9rem;
  white-space: pre-wrap;
}

.notes-actions, .end-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

.save-notes-btn, .confirm-end-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.save-notes-btn:hover, .confirm-end-btn:hover {
  background-color: #0056b3;
}

.close-btn, .cancel-btn {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.close-btn:hover, .cancel-btn:hover {
  background-color: #5a6268;
}
</style>