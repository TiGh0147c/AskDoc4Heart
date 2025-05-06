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
          <h1>账号与咨询管理</h1>
          <p>管理用户和咨询师账号信息，以及咨询记录。</p>
          
          <div class="tabs-container">
            <div class="tabs">
              <div 
                class="tab" 
                :class="{ 'active': activeTab === 'users' }" 
                @click="activeTab = 'users'"
              >
                用户账号
              </div>
              <div 
                class="tab" 
                :class="{ 'active': activeTab === 'counselors' }" 
                @click="activeTab = 'counselors'"
              >
                咨询师账号
              </div>
              <div 
                class="tab" 
                :class="{ 'active': activeTab === 'sessions' }" 
                @click="activeTab = 'sessions'"
              >
                咨询记录
              </div>
              <div 
                class="tab" 
                :class="{ 'active': activeTab === 'audits' }" 
                @click="activeTab = 'audits'; fetchUserAudits()"
              >
                用户审核
              </div>
            </div>
            
            <!-- 用户账号标签页 -->
            <div v-if="activeTab === 'users'" class="tab-content">
              <!-- 现有用户账号内容保持不变 -->
              <div class="search-bar">
                <input type="text" v-model="userSearchTerm" placeholder="搜索用户..." class="search-input">
                <button class="action-btn" @click="addNewUser">添加用户</button>
              </div>
              
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>用户名</th>
                    <th>真实姓名</th>
                    <th>联系方式</th>
                    <th>注册时间</th>
                    <th>账号状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="user in filteredUsers" :key="user.id">
                    <td>{{ user.id }}</td>
                    <td>{{ user.username }}</td>
                    <td>{{ user.realName }}</td>
                    <td>{{ user.contact }}</td>
                    <td>{{ user.registrationDate }}</td>
                    <td>
                      <span :class="'status-badge ' + user.status">
                        {{ getStatusText(user.status) }}
                      </span>
                    </td>
                    <td>
                      <button class="small-btn edit-btn" @click="editUser(user.id)">编辑</button>
                      <button class="small-btn" :class="user.status === 'active' ? 'block-btn' : 'activate-btn'" @click="toggleUserStatus(user.id)">
                        {{ user.status === 'active' ? '禁用' : '启用' }}
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 咨询师账号标签页 -->
            <div v-if="activeTab === 'counselors'" class="tab-content">
              <!-- 现有咨询师账号内容保持不变 -->
              <div class="search-bar">
                <input type="text" v-model="counselorSearchTerm" placeholder="搜索咨询师..." class="search-input">
                <button class="action-btn" @click="addNewCounselor">添加咨询师</button>
              </div>
              
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>用户名</th>
                    <th>姓名</th>
                    <th>专业领域</th>
                    <th>资质</th>
                    <th>级别</th>
                    <th>账号状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="counselor in filteredCounselors" :key="counselor.id">
                    <td>{{ counselor.id }}</td>
                    <td>{{ counselor.username }}</td>
                    <td>{{ counselor.name }}</td>
                    <td>{{ counselor.specialties.join(', ') }}</td>
                    <td>{{ counselor.qualifications }}</td>
                    <td>{{ counselor.isSupervisor ? '督导' : '咨询师' }}</td>
                    <td>
                      <span :class="'status-badge ' + counselor.status">
                        {{ getStatusText(counselor.status) }}
                      </span>
                    </td>
                    <td>
                      <button class="small-btn edit-btn" @click="editCounselor(counselor.id)">编辑</button>
                      <button class="small-btn" :class="counselor.status === 'active' ? 'block-btn' : 'activate-btn'" @click="toggleCounselorStatus(counselor.id)">
                        {{ counselor.status === 'active' ? '禁用' : '启用' }}
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 咨询记录标签页 -->
            <div v-if="activeTab === 'sessions'" class="tab-content">
              <!-- 现有咨询记录内容保持不变 -->
              <div class="search-bar">
                <input type="text" v-model="sessionSearchTerm" placeholder="搜索咨询记录..." class="search-input">
                <select v-model="sessionStatusFilter" class="filter-select">
                  <option value="all">全部状态</option>
                  <option value="scheduled">已预约</option>
                  <option value="completed">已完成</option>
                  <option value="cancelled">已取消</option>
                </select>
              </div>
              
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>用户</th>
                    <th>咨询师</th>
                    <th>预约时间</th>
                    <th>咨询类型</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="session in filteredSessions" :key="session.id">
                    <td>{{ session.id }}</td>
                    <td>{{ session.userName }}</td>
                    <td>{{ session.counselorName }}</td>
                    <td>{{ session.scheduledTime }}</td>
                    <td>{{ session.type }}</td>
                    <td>
                      <span :class="'status-badge ' + session.status">
                        {{ getSessionStatusText(session.status) }}
                      </span>
                    </td>
                    <td>
                      <button class="small-btn view-btn" @click="viewSession(session.id)">查看</button>
                      <button v-if="session.status === 'scheduled'" class="small-btn delete-btn" @click="cancelSession(session.id)">取消</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 用户审核标签页 -->
            <div v-if="activeTab === 'audits'" class="tab-content">
              <div class="search-bar">
                <select v-model="auditStatusFilter" class="filter-select" @change="fetchUserAudits()">
                  <option value="">全部状态</option>
                  <option value="pending">待审核</option>
                  <option value="approved">已通过</option>
                  <option value="rejected">已拒绝</option>
                </select>
                <div class="status-info">
                  <span v-if="loading">加载中...</span>
                  <span v-else>共 {{ userAudits.length }} 条记录</span>
                </div>
              </div>
              
              <table v-if="!loading">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>用户ID</th>
                    <th>修改字段</th>
                    <th>新值</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="audit in userAudits" :key="audit.auditId">
                    <td>{{ audit.auditId }}</td>
                    <td>{{ audit.userId }}</td>
                    <td>{{ formatAuditField(audit.modifyField) }}</td>
                    <td>
                      <span v-if="audit.modifyField === 'avatar'">
                        <img :src="audit.newValue" alt="新头像" class="audit-image" />
                      </span>
                      <span v-else>{{ audit.newValue }}</span>
                    </td>
                    <td>
                      <span :class="'status-badge ' + audit.auditStatus">
                        {{ formatAuditStatus(audit.auditStatus) }}
                      </span>
                    </td>
                    <td>
                      <div v-if="audit.auditStatus === 'pending'">
                        <button class="small-btn approve-btn" @click="reviewAudit(audit.auditId, 'approved')">通过</button>
                        <button class="small-btn reject-btn" @click="reviewAudit(audit.auditId, 'rejected')">拒绝</button>
                      </div>
                      <span v-else>已处理</span>
                    </td>
                  </tr>
                </tbody>
              </table>
              <div v-else-if="loading" class="loading-container">
                加载中...
              </div>
              <div v-else-if="userAudits.length === 0" class="empty-message">
                暂无审核记录
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
  import axios from 'axios'
  
  export default {
    name: 'AdminAccounts',
    setup() {
      const store = useStore()
      const router = useRouter()
      const loading = ref(false)
  
      const username = computed(() => store.getters.username)
      const activeTab = ref('users')
      
      // 搜索和过滤数据
      const userSearchTerm = ref('')
      const counselorSearchTerm = ref('')
      const sessionSearchTerm = ref('')
      const sessionStatusFilter = ref('all')
      const auditStatusFilter = ref('')
      
      // 用户审核数据
      const userAudits = ref([])
      
      // 模拟用户数据
      const users = ref([
        { id: 1, username: 'user1', realName: '李明', contact: '13800138000', registrationDate: '2024-01-15', status: 'active' },
        { id: 2, username: 'user2', realName: '王芳', contact: '13900139000', registrationDate: '2024-02-20', status: 'inactive' },
        { id: 3, username: 'user3', realName: '张华', contact: '13700137000', registrationDate: '2024-03-05', status: 'active' }
      ])
      
      // 模拟咨询师数据
      const counselors = ref([
        { id: 1, username: 'counselor1', name: '张医生', specialties: ['抑郁症', '焦虑症'], qualifications: '心理咨询师二级', isSupervisor: false, status: 'active' },
        { id: 2, username: 'counselor2', name: '李医生', specialties: ['婚姻家庭', '青少年心理'], qualifications: '心理咨询师一级', isSupervisor: true, status: 'active' },
        { id: 3, username: 'counselor3', name: '王医生', specialties: ['创伤治疗', '职场压力'], qualifications: '心理咨询师二级', isSupervisor: false, status: 'inactive' }
      ])
      
      // 模拟咨询会话数据
      const sessions = ref([
        { id: 1, userName: '李明', counselorName: '张医生', scheduledTime: '2025-03-21 14:00', type: '个人咨询', status: 'scheduled' },
        { id: 2, userName: '王芳', counselorName: '李医生', scheduledTime: '2025-03-18 10:00', type: '家庭咨询', status: 'completed' },
        { id: 3, userName: '张华', counselorName: '王医生', scheduledTime: '2025-03-15 16:00', type: '危机干预', status: 'cancelled' }
      ])
      
      // 过滤数据
      const filteredUsers = computed(() => {
        if (!userSearchTerm.value) return users.value
        const searchTerm = userSearchTerm.value.toLowerCase()
        return users.value.filter(user => 
          user.username.toLowerCase().includes(searchTerm) ||
          user.realName.toLowerCase().includes(searchTerm) ||
          user.contact.includes(searchTerm)
        )
      })
      
      const filteredCounselors = computed(() => {
        if (!counselorSearchTerm.value) return counselors.value
        const searchTerm = counselorSearchTerm.value.toLowerCase()
        return counselors.value.filter(counselor => 
          counselor.username.toLowerCase().includes(searchTerm) ||
          counselor.name.toLowerCase().includes(searchTerm) ||
          counselor.specialties.some(s => s.toLowerCase().includes(searchTerm))
        )
      })
      
      const filteredSessions = computed(() => {
        let result = sessions.value
        
        if (sessionStatusFilter.value !== 'all') {
          result = result.filter(session => session.status === sessionStatusFilter.value)
        }
        
        if (sessionSearchTerm.value) {
          const searchTerm = sessionSearchTerm.value.toLowerCase()
          result = result.filter(session => 
            session.userName.toLowerCase().includes(searchTerm) ||
            session.counselorName.toLowerCase().includes(searchTerm) ||
            session.type.toLowerCase().includes(searchTerm)
          )
        }
        
        return result
      })
      
      // 辅助函数获取状态文本
      const getStatusText = (status) => {
        return status === 'active' ? '正常' : '禁用'
      }
      
      const getSessionStatusText = (status) => {
        switch (status) {
          case 'scheduled': return '已预约'
          case 'completed': return '已完成'
          case 'cancelled': return '已取消'
          default: return status
        }
      }
      
      // 格式化审核字段名称
      const formatAuditField = (field) => {
        switch (field) {
          case 'nickname': return '昵称'
          case 'avatar': return '头像'
          default: return field
        }
      }
      
      // 格式化审核状态
      const formatAuditStatus = (status) => {
        switch (status) {
          case 'pending': return '待审核'
          case 'approved': return '已通过'
          case 'rejected': return '已拒绝'
          default: return status
        }
      }
      
      // 获取用户审核记录
      const fetchUserAudits = async () => {
        try {
          loading.value = true
          const response = await axios.get(
            '/api/profile-management/administrator/user-modification-audits',
            { params: { status: auditStatusFilter.value || null } }
          )
          
          if (response.status === 200) {
            userAudits.value = response.data
          }
        } catch (error) {
          console.error('获取用户审核记录失败:', error)
        } finally {
          loading.value = false
        }
      }
      
      // 审核用户修改
      const reviewAudit = async (auditId, decision) => {
        try {
          loading.value = true
          const response = await axios.post(
            `/api/profile-management/administrator/user-modification-audits/${auditId}/review`,
            null,
            { params: { decision } }
          )
          
          if (response.status === 200) {
            // 更新本地审核记录状态
            const audit = userAudits.value.find(a => a.auditId === auditId)
            if (audit) {
              audit.auditStatus = decision
            }
          }
        } catch (error) {
          console.error('审核操作失败:', error)
        } finally {
          loading.value = false
        }
      }
      
      // 账号操作函数
      const addNewUser = () => {
        console.log('添加新用户')
      }
      
      const editUser = (id) => {
        console.log('编辑用户', id)
      }
      
      const toggleUserStatus = (id) => {
        const user = users.value.find(u => u.id === id)
        if (user) {
          user.status = user.status === 'active' ? 'inactive' : 'active'
          console.log(`将用户 ${id} 状态切换为 ${user.status}`)
        }
      }
      
      const addNewCounselor = () => {
        console.log('添加新咨询师')
      }
      
      const editCounselor = (id) => {
        console.log('编辑咨询师', id)
      }
      
      const toggleCounselorStatus = (id) => {
        const counselor = counselors.value.find(c => c.id === id)
        if (counselor) {
          counselor.status = counselor.status === 'active' ? 'inactive' : 'active'
          console.log(`将咨询师 ${id} 状态切换为 ${counselor.status}`)
        }
      }
      
      // 咨询记录操作函数
      const viewSession = (id) => {
        console.log('查看咨询记录', id)
      }
      
      const cancelSession = (id) => {
        const session = sessions.value.find(s => s.id === id)
        if (session) {
          session.status = 'cancelled'
          console.log(`取消咨询记录 ${id}`)
        }
      }
  
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
      
      // 初始化时加载数据
      onMounted(() => {
        if (activeTab.value === 'audits') {
          fetchUserAudits()
        }
      })
  
      return {
        username,
        logout,
        goTo,
        activeTab,
        userSearchTerm,
        counselorSearchTerm,
        sessionSearchTerm,
        sessionStatusFilter,
        auditStatusFilter,
        filteredUsers,
        filteredCounselors,
        filteredSessions,
        userAudits,
        loading,
        getStatusText,
        getSessionStatusText,
        formatAuditField,
        formatAuditStatus,
        fetchUserAudits,
        reviewAudit,
        addNewUser,
        editUser,
        toggleUserStatus,
        addNewCounselor,
        editCounselor,
        toggleCounselorStatus,
        viewSession,
        cancelSession
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
  
  .tabs-container {
    margin-top: 20px;
  }
  
  .tabs {
    display: flex;
    border-bottom: 1px solid #ddd;
    margin-bottom: 20px;
  }
  
  .tab {
    padding: 10px 20px;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    transition: all 0.2s;
  }
  
  .tab.active {
    border-bottom: 2px solid #007bff;
    color: #007bff;
    font-weight: bold;
  }
  
  .tab:hover {
    background-color: #f8f9fa;
  }
  
  .tab-content {
    min-height: 300px;
  }
  
  .search-bar {
    display: flex;
    margin-bottom: 20px;
    gap: 10px;
    justify-content: space-between;
  }
  
  .search-input {
    flex: 1;
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
  }
  
  .filter-select {
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
    min-width: 120px;
  }
  
  .action-btn {
    background-color: #28a745;
    color: white;
    border: none;
    padding: 8px 15px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .action-btn:hover {
    background-color: #218838;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
  }
  
  th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
  }
  
  th {
    background-color: #f2f2f2;
    font-weight: bold;
  }
  
  .status-badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 0.8rem;
    font-weight: bold;
  }
  
  .status-badge.active {
    background-color: #d4edda;
    color: #155724;
  }
  
  .status-badge.inactive {
    background-color: #f8d7da;
    color: #721c24;
  }
  
  .status-badge.scheduled {
    background-color: #cce5ff;
    color: #004085;
  }
  
  .status-badge.completed {
    background-color: #d4edda;
    color: #155724;
  }
  
  .status-badge.cancelled {
    background-color: #f8d7da;
    color: #721c24;
  }
  
  .status-badge.pending {
    background-color: #fff3cd;
    color: #856404;
  }
  
  .status-badge.approved {
    background-color: #d4edda;
    color: #155724;
  }
  
  .status-badge.rejected {
    background-color: #f8d7da;
    color: #721c24;
  }
  
  .small-btn {
    padding: 3px 8px;
    margin: 0 2px;
    border-radius: 3px;
    border: none;
    cursor: pointer;
    font-size: 0.8rem;
  }
  
  .edit-btn {
    background-color: #ffc107;
    color: #212529;
  }
  
  .view-btn {
    background-color: #17a2b8;
    color: white;
  }
  
  .block-btn {
    background-color: #dc3545;
    color: white;
  }
  
  .activate-btn {
    background-color: #28a745;
    color: white;
  }
  
  .delete-btn {
    background-color: #dc3545;
    color: white;
  }
  
  .approve-btn {
    background-color: #28a745;
    color: white;
  }
  
  .reject-btn {
    background-color: #dc3545;
    color: white;
  }
  
  .edit-btn:hover, .view-btn:hover, .block-btn:hover, .activate-btn:hover, .delete-btn:hover, .approve-btn:hover, .reject-btn:hover {
    opacity: 0.9;
  }
  
  .audit-image {
    max-width: 50px;
    max-height: 50px;
    border-radius: 4px;
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
    font-size: 1.2rem;
    color: #6c757d;
  }
  
  .empty-message {
    text-align: center;
    padding: 30px;
    color: #6c757d;
    font-style: italic;
  }
  
  .status-info {
    display: flex;
    align-items: center;
    color: #6c757d;
  }
  </style>