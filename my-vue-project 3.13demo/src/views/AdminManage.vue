<template>
    <div class="container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-item" @click="goTo('home')">返回主页</div>
        <div class="sidebar-item" @click="goTo('manage')">管理注册</div>
        <div class="sidebar-item" @click="goTo('notifications')">通知</div>
      </div>
  
      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="top-right">
          <p class="welcome">欢迎回来，{{ username }}！</p>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
  
        <div class="card">
          <h1>管理注册</h1>
          <p>这是管理员的注册管理页面。您可以在这里管理用户的注册信息。</p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { computed } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    name: 'AdminManage',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
  
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
  </style>