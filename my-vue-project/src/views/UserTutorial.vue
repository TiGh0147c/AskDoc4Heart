<template>
    <div class="container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-item" @click="goTo('home')">返回主页</div>
        <div class="sidebar-item active" @click="goTo('tutorial')">教程</div>
        <div class="sidebar-item" @click="goTo('appointment')">预约</div>
        <div class="sidebar-item" @click="goTo('settings')">设置</div>
        <div class="sidebar-item" @click="goTo('history')">历史会话</div>
        <div class="sidebar-item" @click="goTo('currentChat')">当前对话</div>
      </div>
  
      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="top-right">
          <p class="welcome">欢迎回来，{{ username }}！</p>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
  
        <div class="card">
          <h1>教程</h1>
          <p>这是用户的教程页面。您可以在这里查看使用指南。</p>
          
          <!-- 教程内容区域 -->
          <div class="tutorial-content">
            <!-- 第一页 -->
            <div v-if="currentPage === 1" class="tutorial-page">
              <h2>基础教程</h2>
              <div class="tutorial-image">
                <img src="/tutorial/tutorial1.png" alt="基础教程" />
              </div>
            </div>
            
            <!-- 第二页 -->
            <div v-if="currentPage === 2" class="tutorial-page">
              <h2>进阶教程</h2>
              <div class="tutorial-image">
                <img src="/tutorial/tutorial2.png" alt="进阶教程" />
              </div>
            </div>
            
            <!-- 第三页 -->
            <div v-if="currentPage === 3" class="tutorial-page">
              <h2>功能详解 (1)</h2>
              <div class="tutorial-images-grid">
                <div class="tutorial-image">
                  <img src="/tutorial/tutorial3.1.png" alt="功能详解 3.1" />
                </div>
                <div class="tutorial-image">
                  <img src="/tutorial/tutorial3.2.png" alt="功能详解 3.2" />
                </div>
                <div class="tutorial-image">
                  <img src="/tutorial/tutorial3.3.png" alt="功能详解 3.3" />
                </div>
              </div>
            </div>
            
            <!-- 第四页 -->
            <div v-if="currentPage === 4" class="tutorial-page">
              <h2>功能详解 (2)</h2>
              <div class="tutorial-images-grid">
                <div class="tutorial-image">
                  <img src="/tutorial/tutorial4.1.png" alt="功能详解 4.1" />
                </div>
                <div class="tutorial-image">
                  <img src="/tutorial/tutorial4.2.png" alt="功能详解 4.2" />
                </div>
                <div class="tutorial-image">
                  <img src="/tutorial/tutorial4.3.png" alt="功能详解 4.3" />
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页控制 -->
          <div class="pagination-controls">
            <button 
              :disabled="currentPage === 1" 
              @click="currentPage--" 
              class="page-btn"
            >
              上一页
            </button>
            <span class="page-indicator">{{ currentPage }} / {{ totalPages }}</span>
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
    </div>
  </template>
  
  <script>
  import { computed, ref } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    name: 'UserTutorial',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
      
      // 分页相关状态
      const currentPage = ref(1)
      const totalPages = ref(4) // 总共4页
  
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
  
      return {
        username,
        logout,
        goTo,
        currentPage,
        totalPages
      }
    }
  }
  </script>
  
  <style scoped>
  /* 样式保持与咨询师主页一致 */
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
  
  /* 教程页面样式 */
  .tutorial-content {
    margin-top: 20px;
  }
  
  .tutorial-page {
    margin-bottom: 20px;
  }
  
  .tutorial-page h2 {
    margin-bottom: 15px;
    color: #333;
  }
  
  .tutorial-image {
    margin-bottom: 15px;
    text-align: center;
  }
  
  .tutorial-image img {
    max-width: 100%;
    border-radius: 4px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
  
  .tutorial-images-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 15px;
    margin-bottom: 15px;
  }
  
  /* 分页控制样式 */
  .pagination-controls {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 30px;
    gap: 15px;
  }
  
  .page-btn {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 8px 15px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9rem;
  }
  
  .page-btn:hover:not(:disabled) {
    background-color: #0056b3;
  }
  
  .page-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }
  
  .page-indicator {
    font-size: 0.9rem;
    color: #666;
  }
  </style>