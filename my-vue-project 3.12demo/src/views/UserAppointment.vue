<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('tutorial')">教程</div>
      <div class="sidebar-item active" @click="goTo('appointment')">预约</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('review')">评价</div>
      <div class="sidebar-item" @click="goTo('currentChat')">当前对话</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>预约咨询师</h1>
        <p>请从下方选择一位合适的咨询师进行预约</p>
        
        <!-- 筛选器部分 -->
        <div class="filter-section">
          <div class="filter-item">
            <label>状态筛选:</label>
            <select v-model="statusFilter" @change="applyFilters">
              <option value="all">全部</option>
              <option value="available">空闲</option>
              <option value="busy">繁忙</option>
            </select>
          </div>
          
          <div class="filter-item">
            <label>证书等级:</label>
            <select v-model="certificationFilter" @change="applyFilters">
              <option value="all">全部</option>
              <option value="无">无</option>
              <option value="三级">三级</option>
              <option value="二级">二级</option>
              <option value="一级">一级</option>
            </select>
          </div>
        </div>

        <!-- 咨询师列表 -->
        <div class="counselors-list">
          <div v-for="counselor in displayedCounselors" :key="counselor.id" class="counselor-card">
            <div class="counselor-avatar">
              <img :src="counselor.avatar" :alt="counselor.name + '的头像'" />
            </div>
            <div class="counselor-info">
              <h3>{{ counselor.name }}</h3>
              <p class="certification">证书等级: {{ counselor.certification }}</p>
              <p class="description">{{ counselor.description }}</p>
              <div class="status-row">
                <span :class="['status-badge', counselor.status === '空闲' ? 'available' : 'busy']">
                  {{ counselor.status }}
                </span>
                <button 
                  class="appointment-btn" 
                  :disabled="counselor.status === '繁忙'"
                  @click="bookAppointment(counselor)"
                >
                  预约
                </button>
              </div>
            </div>
          </div>

          <!-- 无数据提示 -->
          <div v-if="displayedCounselors.length === 0" class="no-data">
            没有找到符合条件的咨询师
          </div>
        </div>

        <!-- 分页控制 -->
        <div class="pagination">
          <button 
            :disabled="currentPage === 1" 
            @click="goToPage(1)" 
            class="page-btn"
          >
            首页
          </button>
          <button 
            :disabled="currentPage === 1" 
            @click="goToPage(currentPage - 1)" 
            class="page-btn"
          >
            上一页
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button 
            :disabled="currentPage === totalPages" 
            @click="goToPage(currentPage + 1)" 
            class="page-btn"
          >
            下一页
          </button>
          <button 
            :disabled="currentPage === totalPages" 
            @click="goToPage(totalPages)" 
            class="page-btn"
          >
            末页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'UserAppointment',
  setup() {
    const store = useStore()
    const router = useRouter()

    const username = computed(() => store.getters.username)
    
    // 分页相关状态
    const currentPage = ref(1)
    const itemsPerPage = 5
    
    // 筛选条件
    const statusFilter = ref('all')
    const certificationFilter = ref('all')
    
    // 模拟咨询师数据
    const counselors = ref([
      {
        id: 1,
        name: '李明',
        certification: '一级',
        status: '空闲',
        description: '专注于焦虑症治疗，拥有10年临床经验',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiM2NjYiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiM2NjYiLz48L3N2Zz4='
      },
      {
        id: 2,
        name: '张华',
        certification: '二级',
        status: '繁忙',
        description: '婚姻家庭咨询专家，擅长沟通技巧辅导',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiM4ODgiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiM4ODgiLz48L3N2Zz4='
      },
      {
        id: 3,
        name: '王芳',
        certification: '一级',
        status: '空闲',
        description: '抑郁症和情绪管理专家，心理学博士',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiM5OTkiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiM5OTkiLz48L3N2Zz4='
      },
      {
        id: 4,
        name: '赵刚',
        certification: '三级',
        status: '空闲',
        description: '青少年心理辅导专家，关注成长问题',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiM3NzciLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiM3NzciLz48L3N2Zz4='
      },
      {
        id: 5,
        name: '陈静',
        certification: '二级',
        status: '繁忙',
        description: '压力管理和职场心理健康顾问',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiNhYWEiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiNhYWEiLz48L3N2Zz4='
      },
      {
        id: 6,
        name: '林强',
        certification: '一级',
        status: '空闲',
        description: '创伤后应激障碍(PTSD)治疗专家',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiM4ODgiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiM4ODgiLz48L3N2Zz4='
      },
      {
        id: 7,
        name: '郑美',
        certification: '三级',
        status: '空闲',
        description: '人际关系和社交焦虑问题咨询师',
        avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PGNpcmNsZSBjeD0iNTAiIGN5PSIzNSIgcj0iMjUiIGZpbGw9IiM5OTkiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjExMCIgcj0iNTAiIGZpbGw9IiM5OTkiLz48L3N2Zz4='
      }
    ])

    // 应用筛选条件后的咨询师列表
    const filteredCounselors = computed(() => {
      return counselors.value.filter(c => {
        // 状态筛选
        if (statusFilter.value !== 'all') {
          if (statusFilter.value === 'available' && c.status !== '空闲') return false
          if (statusFilter.value === 'busy' && c.status !== '繁忙') return false
        }
        
        // 证书筛选
        if (certificationFilter.value !== 'all' && c.certification !== certificationFilter.value) {
          return false
        }
        
        return true
      })
    })
    
    // 计算总页数
    const totalPages = computed(() => {
      return Math.ceil(filteredCounselors.value.length / itemsPerPage)
    })
    
    // 当前页显示的咨询师
    const displayedCounselors = computed(() => {
      const startIdx = (currentPage.value - 1) * itemsPerPage
      const endIdx = startIdx + itemsPerPage
      return filteredCounselors.value.slice(startIdx, endIdx)
    })
    
    // 页面跳转
    const goToPage = (page) => {
      currentPage.value = page
    }
    
    // 应用筛选条件时重置到第一页
    const applyFilters = () => {
      currentPage.value = 1
    }
    
    // 预约处理
    const bookAppointment = (counselor) => {
      alert(`您正在预约${counselor.name}咨询师，稍后会为您跳转到预约详情页面`)
      // 实际项目中这里可能会跳转到详细预约页面或者打开弹窗
      // router.push(`/user/appointment/${counselor.id}`)
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
    
    // 当筛选条件改变时检查页码是否需要调整
    onMounted(() => {
      // 如果后端获取数据，这里可以调用API
      // fetchCounselors()
    })

    return {
      username,
      counselors,
      currentPage,
      totalPages,
      displayedCounselors,
      statusFilter,
      certificationFilter,
      goToPage,
      applyFilters,
      bookAppointment,
      logout,
      goTo
    }
  }
}
</script>

<style scoped>
/* 基础样式保持与原有一致 */
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

/* 新增样式 */
.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 5px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item select {
  padding: 5px 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
  background-color: white;
}

.counselors-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.counselor-card {
  display: flex;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.counselor-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.counselor-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.counselor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.counselor-info {
  flex: 1;
}

.counselor-info h3 {
  margin-top: 0;
  margin-bottom: 5px;
  color: #333;
}

.certification {
  margin: 5px 0;
  color: #666;
  font-size: 0.9rem;
}

.description {
  margin: 8px 0;
  color: #555;
  font-size: 0.9rem;
}

.status-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.status-badge {
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
}

.status-badge.available {
  background-color: #d4edda;
  color: #155724;
}

.status-badge.busy {
  background-color: #f8d7da;
  color: #721c24;
}

.appointment-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.appointment-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.appointment-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.page-btn {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.9rem;
  color: #666;
}

.no-data {
  text-align: center;
  padding: 30px;
  color: #666;
  font-style: italic;
  background-color: #f9f9f9;
  border-radius: 6px;
  margin: 10px 0;
}
</style>