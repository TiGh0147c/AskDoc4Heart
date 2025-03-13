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
      <div class="sidebar-item" @click="goTo('currentChat')">当前对话</div> <!-- 添加当前对话 -->
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>用户主页</h1>
        <p>这是用户的主页界面。您可以在这里查看或预约咨询服务。</p>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'UserHome',
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
        case 'currentChat':  // 添加当前对话路由
          router.push('/user/currentChat')
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
/* 容器样式 */
.container {
  display: flex;
  height: 100vh;
  background: #f4f4f4; /* 背景色调整为浅灰色 */
}

/* 侧边栏样式 */
.sidebar {
  width: 200px;
  background: #ffffff; /* 侧边栏背景色调整为白色 */
  padding: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  color: #333; /* 文字颜色调整为深灰色 */
  font-family: Arial, sans-serif;
  border-radius: 0 10px 10px 0; /* 添加圆角效果 */
}

.sidebar-item {
  margin-bottom: 10px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.2s, color 0.2s;
}

.sidebar-item:hover {
  color: #007bff; /* 悬停时文字颜色 */
  transform: scale(1.05);
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background: #ffffff; /* 主内容区域背景色调整为白色 */
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative; /* 用于绝对定位右上角内容 */
}

/* 右上角欢迎信息和退出登录 */
.top-right {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem; /* 缩小字体大小 */
}

.welcome {
  margin: 0;
  color: #333;
}

.logout-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 10px; /* 缩小按钮尺寸 */
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem; /* 缩小按钮字体大小 */
}

.logout-btn:hover {
  background-color: #0056b3;
}

/* 主内容卡片样式 */
.card {
  background-color: #ffffff; /* 卡片背景色调整为白色 */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-top: 60px; /* 避免与右上角内容重叠 */
}
</style>