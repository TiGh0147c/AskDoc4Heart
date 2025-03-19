<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('manage')">管理注册</div>
      <div class="sidebar-item" @click="goTo('schedules')">排班管理</div>
      <div class="sidebar-item" @click="goTo('accounts')">账号管理</div>
      <div class="sidebar-item" @click="goTo('supervision')">督导绑定</div>
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
        <p>这是管理员的注册管理页面。您可以在这里管理咨询师的注册申请。</p>
        
        <!-- 搜索和筛选区域 -->
        <div class="search-filter">
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="搜索姓名或邮箱" 
              @input="filterRegistrations"
            />
            <button @click="filterRegistrations">搜索</button>
          </div>
          <div class="filter-box">
            <select v-model="statusFilter" @change="filterRegistrations">
              <option value="all">全部状态</option>
              <option value="pending">待审核</option>
              <option value="approved">已通过</option>
              <option value="rejected">已拒绝</option>
            </select>
            <select v-model="dateFilter" @change="filterRegistrations">
              <option value="all">全部时间</option>
              <option value="today">今天</option>
              <option value="week">本周</option>
              <option value="month">本月</option>
            </select>
          </div>
        </div>
        
        <!-- 注册申请列表 -->
        <div class="registration-list">
          <table>
            <thead>
              <tr>
                <th>姓名</th>
                <th>邮箱</th>
                <th>电话</th>
                <th>申请日期</th>
                <th>资质证书</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredRegistrations.length === 0">
                <td colspan="7" class="no-data">暂无数据</td>
              </tr>
              <tr v-for="(reg, index) in filteredRegistrations" :key="index" :class="{ 'highlight': reg.status === 'pending' }">
                <td>{{ reg.name }}</td>
                <td>{{ reg.email }}</td>
                <td>{{ reg.phone }}</td>
                <td>{{ formatDate(reg.applyDate) }}</td>
                <td>
                  <button class="view-btn" @click="viewCredentials(reg)">查看证书</button>
                </td>
                <td>
                  <span :class="'status-' + reg.status">
                    {{ getStatusText(reg.status) }}
                  </span>
                </td>
                <td class="actions">
                  <button 
                    v-if="reg.status === 'pending'" 
                    class="approve-btn" 
                    @click="approveRegistration(reg.id)"
                  >
                    通过
                  </button>
                  <button 
                    v-if="reg.status === 'pending'" 
                    class="reject-btn" 
                    @click="showRejectModal(reg.id)"
                  >
                    拒绝
                  </button>
                  <button 
                    v-if="reg.status !== 'pending'" 
                    class="detail-btn" 
                    @click="viewDetails(reg.id)"
                  >
                    详情
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          
          <!-- 分页控件 -->
          <div class="pagination">
            <button 
              :disabled="currentPage === 1" 
              @click="changePage(currentPage - 1)"
            >
              上一页
            </button>
            <span>{{ currentPage }} / {{ totalPages }}</span>
            <button 
              :disabled="currentPage === totalPages" 
              @click="changePage(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 拒绝理由弹窗 -->
    <div class="modal" v-if="showModal">
      <div class="modal-content">
        <h2>拒绝申请</h2>
        <p>请填写拒绝理由：</p>
        <textarea v-model="rejectReason" placeholder="请输入拒绝理由..."></textarea>
        <div class="modal-buttons">
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="confirm-btn" @click="rejectRegistration">确认拒绝</button>
        </div>
      </div>
    </div>
    
    <!-- 证书查看弹窗 -->
    <div class="modal" v-if="showCredentialsModal">
      <div class="modal-content credentials-modal">
        <h2>资质证书</h2>
        <div class="credentials-container">
          <div v-for="(credential, idx) in currentCredentials" :key="idx" class="credential-item">
            <img :src="credential.url" alt="证书图片" class="credential-img" />
            <p>{{ credential.name }}</p>
          </div>
        </div>
        <div class="modal-buttons">
          <button class="confirm-btn" @click="closeCredentialsModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
//图片

export default {
  name: 'AdminManage',
  setup() {
    const store = useStore()
    const router = useRouter()
    // 用户信息
    const username = computed(() => store.getters.username)
    
    // 注册申请列表数据
    const registrations = ref([])
    const filteredRegistrations = ref([])
    
    // 筛选条件
    const searchQuery = ref('')
    const statusFilter = ref('all')
    const dateFilter = ref('all')
    
    // 分页
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalPages = computed(() => Math.ceil(filteredRegistrations.value.length / pageSize.value))
    
    // 拒绝理由弹窗
    const showModal = ref(false)
    const rejectReason = ref('')
    const currentRejectId = ref(null)
    
    // 证书查看弹窗
    const showCredentialsModal = ref(false)
    const currentCredentials = ref([])

    // 获取注册申请列表（模拟数据）
    const fetchRegistrations = () => {
      // 这里应该调用API获取数据
      // 模拟数据
      registrations.value = [
        {
          id: 1,
          name: '张三',
          email: 'zhangsan@example.com',
          phone: '13800138001',
          applyDate: new Date('2025-03-15'),
          status: 'pending',
          credentials: [
            { name: '心理咨询师证书', url: '/cat.jpg'}
          ]
        },
        {
          id: 2,
          name: '李四',
          email: 'lisi@example.com',
          phone: '13900139002',
          applyDate: new Date('2025-03-10'),
          status: 'approved',
          credentials: [
            { name: '心理咨询师证书', url: '/assets/cert2.jpg' }
          ]
        },
        {
          id: 3,
          name: '王五',
          email: 'wangwu@example.com',
          phone: '13700137003',
          applyDate: new Date('2025-03-18'),
          status: 'rejected',
          credentials: [
            { name: '心理咨询师证书', url: '/assets/cert3.jpg' },

          ],
          rejectReason: '证书信息不完整'
        }
      ]
      
      filterRegistrations()
    }
    
    // 筛选注册申请
    const filterRegistrations = () => {
      let filtered = [...registrations.value]
      
      // 搜索条件
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(reg => 
          reg.name.toLowerCase().includes(query) || 
          reg.email.toLowerCase().includes(query)
        )
      }
      
      // 状态筛选
      if (statusFilter.value !== 'all') {
        filtered = filtered.filter(reg => reg.status === statusFilter.value)
      }
      
      // 日期筛选
      if (dateFilter.value !== 'all') {
        const now = new Date()
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
        
        if (dateFilter.value === 'today') {
          filtered = filtered.filter(reg => {
            const applyDate = new Date(reg.applyDate)
            return applyDate >= today && applyDate < new Date(today.getTime() + 86400000)
          })
        } else if (dateFilter.value === 'week') {
          const weekStart = new Date(today)
          weekStart.setDate(today.getDate() - today.getDay())
          
          filtered = filtered.filter(reg => {
            const applyDate = new Date(reg.applyDate)
            return applyDate >= weekStart
          })
        } else if (dateFilter.value === 'month') {
          const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
          
          filtered = filtered.filter(reg => {
            const applyDate = new Date(reg.applyDate)
            return applyDate >= monthStart
          })
        }
      }
      
      filteredRegistrations.value = filtered
      currentPage.value = 1 // 重置到第一页
    }
    
    // 格式化日期
    const formatDate = (date) => {
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'pending': return '待审核'
        case 'approved': return '已通过'
        case 'rejected': return '已拒绝'
        default: return '未知状态'
      }
    }
    
    // 查看详情
    const viewDetails = (id) => {
      // 跳转到详情页面或弹出详情弹窗
      console.log('查看详情', id)
      // 这里应该调用API获取详细信息
      // router.push(`/admin/registration/${id}`)
    }
    
    // 查看证书
    const viewCredentials = (reg) => {
      currentCredentials.value = reg.credentials
      showCredentialsModal.value = true
    }
    
    // 关闭证书弹窗
    const closeCredentialsModal = () => {
      showCredentialsModal.value = false
      currentCredentials.value = []
    }
    
    // 通过申请
    const approveRegistration = (id) => {
      console.log('通过申请', id)
      // 这里应该调用API更新状态
      // 模拟API调用
      const index = registrations.value.findIndex(reg => reg.id === id)
      if (index !== -1) {
        registrations.value[index].status = 'approved'
        filterRegistrations()
      }
    }
    
    // 显示拒绝弹窗
    const showRejectModal = (id) => {
      currentRejectId.value = id
      rejectReason.value = ''
      showModal.value = true
    }
    
    // 关闭弹窗
    const closeModal = () => {
      showModal.value = false
      currentRejectId.value = null
      rejectReason.value = ''
    }
    
    // 拒绝申请
    const rejectRegistration = () => {
      if (!rejectReason.value.trim()) {
        alert('请填写拒绝理由')
        return
      }
      
      console.log('拒绝申请', currentRejectId.value, rejectReason.value)
      // 这里应该调用API更新状态
      // 模拟API调用
      const index = registrations.value.findIndex(reg => reg.id === currentRejectId.value)
      if (index !== -1) {
        registrations.value[index].status = 'rejected'
        registrations.value[index].rejectReason = rejectReason.value
        filterRegistrations()
      }
      
      closeModal()
    }
    
    // 分页
    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
      }
    }
    
    // 登出
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    // 导航
    const goTo = (path) => {
      switch (path) {
        case 'home':
          router.push('/admin/home')
          break
        case 'manage':
          router.push('/admin/manage')
          break
        case 'schedules':
          router.push('/admin/schedules')
          break
        case 'accounts':
          router.push('/admin/accounts')
          break
        case 'supervision':
          router.push('/admin/supervision')
          break
        case 'notifications':
          router.push('/admin/notifications')
          break
        default:
          console.error('Invalid path')
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchRegistrations()
    })

    return {
      username,
      logout,
      goTo,
      registrations,
      filteredRegistrations,
      searchQuery,
      statusFilter,
      dateFilter,
      currentPage,
      totalPages,
      showModal,
      rejectReason,
      currentRejectId,
      showCredentialsModal,
      currentCredentials,
      filterRegistrations,
      formatDate,
      getStatusText,
      viewDetails,
      viewCredentials,
      closeCredentialsModal,
      approveRegistration,
      showRejectModal,
      closeModal,
      rejectRegistration,
      changePage
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

/* 搜索和筛选区域样式 */
.search-filter {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.search-box {
  display: flex;
  gap: 5px;
}

.search-box input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 200px;
}

.search-box button,
.filter-box select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
}

.search-box button:hover {
  background-color: #f0f0f0;
}

.filter-box {
  display: flex;
  gap: 10px;
}

/* 注册申请列表样式 */
.registration-list {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f8f9fa;
  font-weight: bold;
}

tr:hover {
  background-color: #f5f5f5;
}

tr.highlight {
  background-color: #fff8e1;
}

.no-data {
  text-align: center;
  padding: 20px;
  color: #999;
}

/* 状态样式 */
.status-pending {
  color: #ff9800;
  font-weight: bold;
}

.status-approved {
  color: #4caf50;
  font-weight: bold;
}

.status-rejected {
  color: #f44336;
  font-weight: bold;
}

/* 按钮样式 */
.view-btn, .detail-btn, .approve-btn, .reject-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 5px;
}

.view-btn {
  background-color: #f0f0f0;
  color: #333;
}

.detail-btn {
  background-color: #e0e0e0;
  color: #333;
}

.approve-btn {
  background-color: #4caf50;
  color: white;
}

.reject-btn {
  background-color: #f44336;
  color: white;
}

.view-btn:hover, .detail-btn:hover {
  background-color: #e0e0e0;
}

.approve-btn:hover {
  background-color: #43a047;
}

.reject-btn:hover {
  background-color: #e53935;
}

.actions {
  white-space: nowrap;
}

/* 分页控件样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.pagination button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background-color: #f0f0f0;
}

/* 弹窗样式 */
.modal {
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
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}

.credentials-modal {
  width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal h2 {
  margin-top: 0;
  margin-bottom: 15px;
}

.modal textarea {
  width: 100%;
  height: 120px;
  padding: 10px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn, .confirm-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.confirm-btn {
  background-color: #007bff;
  color: white;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.confirm-btn:hover {
  background-color: #0056b3;
}

/* 证书展示样式 */
.credentials-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.credential-item {
  width: calc(50% - 10px);
  text-align: center;
}

.credential-img {
  width: 100%;
  max-height: 200px;
  object-fit: contain;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 5px;
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    height: auto;
  }
  
  .sidebar {
    width: 100%;
    border-radius: 0;
    margin-bottom: 20px;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .search-filter {
    flex-direction: column;
  }
  
  .top-right {
    position: relative;
    top: 0;
    right: 0;
    margin-bottom: 20px;
  }
  
  .card {
    margin-top: 0;
  }
  
  .credential-item {
    width: 100%;
  }
}
</style>