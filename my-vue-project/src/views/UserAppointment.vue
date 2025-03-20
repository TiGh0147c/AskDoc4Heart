<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('tutorial')">教程</div>
      <div class="sidebar-item active" @click="goTo('appointment')">预约</div>
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

      <!-- 预约状态概览 -->
      <div class="appointment-status">
        <div class="status-card">
          <div class="status-icon pending" v-if="pendingAppointments.length > 0">
            <i class="status-dot"></i>
          </div>
          <div class="status-info">
            <h3>待确认预约</h3>
            <p>{{ pendingAppointments.length }}个</p>
            <a @click="showPendingAppointments" v-if="pendingAppointments.length > 0">查看详情</a>
          </div>
        </div>
        
        <div class="status-card">
          <div class="status-icon active" v-if="activeAppointments.length > 0">
            <i class="status-dot"></i>
          </div>
          <div class="status-info">
            <h3>进行中咨询</h3>
            <p>{{ activeAppointments.length }}个</p>
            <a @click="goTo('currentChat')" v-if="activeAppointments.length > 0">立即前往</a>
          </div>
        </div>
        
        <div class="status-card">
          <div class="status-icon upcoming">
            <i class="status-dot"></i>
          </div>
          <div class="status-info">
            <h3>即将开始</h3>
            <p>{{ upcomingAppointments.length }}个</p>
            <a @click="showUpcomingAppointments" v-if="upcomingAppointments.length > 0">查看详情</a>
          </div>
        </div>
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
          <!-- 在筛选器下方添加预约限制提示 -->
          <div class="appointment-limit-info" v-if="!canMakeAppointment">
            <p>您当前已有 {{ currentAppointmentsCount }} 个预约，最多允许同时预约 2 个咨询师</p>
          </div>
          
          <div class="filter-item">
            <label>证书等级:</label>
            <select v-model="certificationFilter" @change="applyFilters">
              <option value="all">全部</option>
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
                  :disabled="counselor.status === '繁忙' || !canMakeAppointment"
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
      
      <!-- 预约详情弹窗 -->
      <div class="modal" v-if="showModal">
        <div class="modal-content">
          <span class="close-btn" @click="closeModal">&times;</span>
          <h2>{{ modalTitle }}</h2>
          
          <div v-if="modalType === 'pending'">
            <div v-for="(appointment, index) in pendingAppointments" :key="index" class="appointment-item">
              <div class="appointment-info">
                <h3>{{ appointment.counselorName }} - {{ appointment.type }}</h3>
                <p>预约时间: {{ appointment.dateTime }}</p>
                <p>状态: <span class="status-text pending">等待确认</span></p>
              </div>
              <div class="appointment-actions">
                <button class="cancel-btn" @click="cancelAppointment(appointment.id)">取消预约</button>
              </div>
            </div>
          </div>
          
          <div v-if="modalType === 'upcoming'">
            <div v-for="(appointment, index) in upcomingAppointments" :key="index" class="appointment-item">
              <div class="appointment-info">
                <h3>{{ appointment.counselorName }} - {{ appointment.type }}</h3>
                <p>预约时间: {{ appointment.dateTime }}</p>
                <p>状态: <span class="status-text upcoming">即将开始</span></p>
                <p>距离开始: {{ appointment.timeRemaining }}</p>
              </div>
              <div class="appointment-actions">
                <button class="primary-btn" @click="startChat(appointment)" v-if="appointment.canStart">开始咨询</button>
                <button class="cancel-btn" @click="cancelAppointment(appointment.id)" v-if="!appointment.canStart">取消预约</button>
              </div>
            </div>
          </div>
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
    
    // 弹窗相关
    const showModal = ref(false)
    const modalType = ref('')
    const modalTitle = ref('')
    
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
    
    // 模拟预约数据
    const pendingAppointments = ref([
      {
        id: 101,
        counselorId: 3,
        counselorName: '王芳',
        type: '情绪管理咨询',
        dateTime: '2025-03-15 14:00',
        status: 'pending'
      }
    ])
    
    const activeAppointments = ref([
      {
        id: 102,
        counselorId: 1,
        counselorName: '李明',
        type: '焦虑症咨询',
        dateTime: '2025-03-13 11:00',
        status: 'active'
      }
    ])
    
    const upcomingAppointments = ref([
      {
        id: 103,
        counselorId: 6,
        counselorName: '林强',
        type: '创伤治疗',
        dateTime: '2025-03-14 10:00',
        status: 'confirmed',
        timeRemaining: '23小时',
        canStart: false
      },
      {
        id: 104,
        counselorId: 4,
        counselorName: '赵刚',
        type: '青少年心理咨询',
        dateTime: '2025-03-13 15:00', // 今天下午
        status: 'confirmed',
        timeRemaining: '3小时',
        canStart: true
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
    
    // 计算用户当前有效预约数量
const currentAppointmentsCount = computed(() => {
  return pendingAppointments.value.length + activeAppointments.value.length + upcomingAppointments.value.length;
});

// 检查用户是否可以预约
const canMakeAppointment = computed(() => {
  return currentAppointmentsCount.value < 2;
});

// 3.20修改bookAppointment函数
const bookAppointment = (counselor) => {
  if (!canMakeAppointment.value) {
    alert('您当前已有两个预约，无法创建更多预约。请先完成或取消现有预约。');
    return;
  }
  
  // 其余预约逻辑保持不变
  alert(`您正在预约${counselor.name}咨询师，一旦确认会显示在您的预约列表中`);
  
  /* 
  后端需要实现：
  1. POST /api/appointments
  2. 需要传递的数据: counselorId, userId, dateTime, type
  3. 返回预约ID和状态
  */
}
    
    // 显示待确认预约
    const showPendingAppointments = () => {
      modalTitle.value = '待确认预约'
      modalType.value = 'pending'
      showModal.value = true
    }
    
    // 显示即将开始的预约
    const showUpcomingAppointments = () => {
      modalTitle.value = '即将开始的预约'
      modalType.value = 'upcoming'
      showModal.value = true
    }
    
    // 关闭弹窗
    const closeModal = () => {
      showModal.value = false
    }
    
    // 取消预约
    const cancelAppointment = (appointmentId) => {
      if (confirm('确认取消此预约吗？')) {
        // 实际项目中应该调用API
        alert(`已取消预约 #${appointmentId}`)
        
        /* 
        后端需要实现：
        1. DELETE 或 PUT /api/appointments/{id}/cancel
        2. 更新预约状态为cancelled
        */
      }
    }
    
    // 开始聊天
    const startChat = (appointment) => {
      // 存储当前选中的咨询师信息到store或localStorage
      localStorage.setItem('currentCounselor', JSON.stringify({
        id: appointment.counselorId,
        name: appointment.counselorName,
        appointmentId: appointment.id,
        type: appointment.type
      }))
      
      // 跳转到聊天页面
      router.push('/user/currentChat')
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
        case 'currentChat':
          router.push('/user/currentChat')
          break
        default:
          console.error('Invalid path')
      }
    }
    
    // 加载数据
    onMounted(() => {
      // 在实际项目中，这里应该调用API加载数据
      // fetchAppointments()
      // fetchCounselors()
    })

    return {
      username,
      counselors,
      pendingAppointments,
      activeAppointments,
      upcomingAppointments,
      currentPage,
      totalPages,
      displayedCounselors,
      statusFilter,
      certificationFilter,
      showModal,
      modalType,
      modalTitle,
      goToPage,
      applyFilters,
      bookAppointment,
      canMakeAppointment,
      currentAppointmentsCount,
      showPendingAppointments,
      showUpcomingAppointments,
      closeModal,
      cancelAppointment,
      startChat,
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
  margin-top: 20px;
}

/* 咨询师列表样式 */
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

/* 新增的预约状态卡片样式 */
.appointment-status {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-top: 60px;
  margin-bottom: 20px;
}

.status-card {
  flex: 1;
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.status-card:hover {
  transform: translateY(-3px);
}

.status-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  position: relative;
}

.status-icon.pending {
  background: #fff3cd;
}

.status-icon.active {
  background: #d1e7dd;
}

.status-icon.upcoming {
  background: #cfe2ff;
}

.status-dot {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ffc107;
}

.status-icon.active .status-dot {
  background: #198754;
}

.status-icon.upcoming .status-dot {
  background: #0d6efd;
}

.status-info {
  flex: 1;
}

.status-info h3 {
  margin: 0 0 5px 0;
  font-size: 1rem;
  color: #333;
}

.status-info p {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
  color: #444;
}

.status-info a {
  display: inline-block;
  margin-top: 8px;
  color: #007bff;
  cursor: pointer;
  font-size: 0.8rem;
}

.status-info a:hover {
  text-decoration: underline;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  z-index: 1000;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 1.5rem;
  cursor: pointer;
}

.appointment-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.appointment-limit-info {
  background-color: #fff3cd;
  color: #856404;
  padding: 10px 15px;
  border-radius: 5px;
  margin-bottom: 15px;
  font-size: 0.9rem;
} 

.appointment-info h3 {
  margin-top: 0;
  margin-bottom: 5px;
}

.status-text {
  font-weight: bold;
}

.status-text.pending {
  color: #ffc107;
}

.status-text.upcoming {
  color: #0d6efd;
}

.appointment-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.cancel-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #bb2d3b;
}

.primary-btn {
  background-color: #0d6efd;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.primary-btn:hover {
  background-color: #0a58ca;
}
</style>