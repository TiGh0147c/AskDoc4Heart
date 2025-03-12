<template>
    <div class="container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-item" @click="goTo('home')">返回主页</div>
        <div class="sidebar-item" @click="goTo('manage')">管理注册</div>
        <div class="sidebar-item active" @click="goTo('notifications')">通知</div>
      </div>
  
      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="top-right">
          <p class="welcome">欢迎回来，{{ username }}！</p>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
  
        <div class="card">
          <h1>通知</h1>
          <p>这是管理员的通知页面。您可以在这里查看和管理通知。</p>
          <!-- 示例通知列表 -->
          <div class="notification-list">
            <div class="notification-item" v-for="(notification, index) in notifications" :key="index">
              <p><strong>{{ notification.title }}</strong></p>
              <p>{{ notification.message }}</p>
              <p class="timestamp">{{ notification.timestamp }}</p>
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
    name: 'AdminNotifications',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
  
      // 示例通知数据
      const notifications = [
        {
          title: '系统更新通知',
          message: '系统将于今晚12点进行维护，届时将短暂中断服务。',
          timestamp: '2025-03-06 10:00'
        },
        {
          title: '新用户注册提醒',
          message: '有新用户注册，待审核。',
          timestamp: '2025-03-06 09:30'
        }
      ]
  
      const logout = () => {
        store.dispatch('logout')
        router.push('/login')
      }
  
      const goTo = (path) => {
        switch (path) {
          case 'home':
            router.push('/admin/home')
            break
          case 'manage':
            router.push('/admin/manage')
            break
          case 'notifications':
            router.push('/admin/notifications')
            break
          default:
            console.error('Invalid path')
        }
      }
  
      return {
        username,
        notifications,
        logout,
        goTo
      }
    }
  }
  </script>
  
  <style scoped>
  /* 样式保持与 AdminHome.vue 一致 */
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
  
  /* 通知列表样式 */
  .notification-list {
    margin-top: 20px;
  }
  
  .notification-item {
    border-bottom: 1px solid #ddd;
    padding: 10px 0;
  }
  
  .notification-item:last-child {
    border-bottom: none;
  }
  
  .timestamp {
    font-size: 0.8rem;
    color: #666;
  }
  </style>