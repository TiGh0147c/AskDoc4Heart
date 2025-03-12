<template>
    <div class="container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-item" @click="goTo('home')">返回主页</div>
        <div class="sidebar-item" @click="goTo('tutorial')">教程</div>
        <div class="sidebar-item" @click="goTo('appointment')">预约</div>
        <div class="sidebar-item" @click="goTo('settings')">设置</div>
        <div class="sidebar-item" @click="goTo('history')">历史会话</div>
        <div class="sidebar-item" @click="goTo('review')">评价</div>
        <div class="sidebar-item active" @click="goTo('currentChat')">当前对话</div>
      </div>
  
      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="top-right">
          <p class="welcome">欢迎回来，{{ username }}！</p>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
  
        <div class="card">
          <h1>当前对话</h1>
          <p>这是用户的当前对话页面。您可以在这里查看正在进行的对话。</p>
          <!-- 示例对话列表 -->
          <div class="chat-list">
            <div class="chat-item" v-for="(chat, index) in chats" :key="index">
              <p><strong>{{ chat.name }}</strong></p>
              <p>{{ chat.lastMessage }}</p>
              <p class="timestamp">{{ chat.timestamp }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { computed } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    name: 'UserCurrentChat',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
  
      // 示例对话数据
      const chats = [
        {
          name: '咨询师A',
          lastMessage: '你好，有什么问题吗？',
          timestamp: '2025-03-12 10:00'
        }
      ]
  
      const logout = () => {
        store.dispatch('logout')
        router.push('/login')
      }
  
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
          case 'review':
            router.push('/user/review')
            break
          case 'currentChat':
            router.push('/user/currentChat')
            break
          default:
            console.error('Invalid path')
        }
      }
  
      return {
        username,
        chats,
        logout,
        goTo
      }
    }
  }
  </script>
  
  <style scoped>
  /* 样式保持与 UserHome.vue 一致 */
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
  
  /* 对话列表样式 */
  .chat-list {
    margin-top: 20px;
  }
  
  .chat-item {
    border-bottom: 1px solid #ddd;
    padding: 10px 0;
  }
  
  .chat-item:last-child {
    border-bottom: none;
  }
  
  .timestamp {
    font-size: 0.8rem;
    color: #666;
  }
  </style>