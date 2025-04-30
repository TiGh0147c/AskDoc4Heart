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
          <h1>督导绑定管理</h1>
          <p>在这里管理咨询师与督导之间的绑定关系，让普通咨询师可以向督导求助。</p>
          
          <div class="supervision-controls">
            <div class="control-section">
              <h2>可用督导</h2>
              <div class="search-bar">
                <input type="text" v-model="supervisorSearchTerm" placeholder="搜索督导..." class="search-input">
              </div>
              <div class="supervisor-list">
                <div 
                  v-for="supervisor in filteredSupervisors" 
                  :key="supervisor.id"
                  class="supervisor-card"
                  @click="selectSupervisor(supervisor)"
                  :class="{ 'selected': selectedSupervisorId === supervisor.id }"
                >
                  <div class="supervisor-avatar">{{ supervisor.name.charAt(0) }}</div>
                  <div class="supervisor-info">
                    <div class="supervisor-name">{{ supervisor.name }}</div>
                    <div class="supervisor-specialties">专长: {{ supervisor.specialties.join(', ') }}</div>
                    <div class="supervisor-stats">
                      <span class="stat">现有咨询师: {{ supervisor.counselorCount }}</span>
                      <span class="stat">最大容量: {{ supervisor.maxCounselors }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="control-arrows">
              <button 
                class="arrow-btn" 
                :disabled="!selectedSupervisorId || !selectedCounselorIds.length" 
                @click="assignCounselors"
              >
                ←
              </button>
              <button 
                class="arrow-btn" 
                :disabled="!selectedSupervisorId || !selectedAssignedCounselorIds.length" 
                @click="removeCounselors"
              >
                →
              </button>
            </div>
            
            <div class="control-section">
              <h2>咨询师分配</h2>
              <div class="assignment-panel" v-if="selectedSupervisorId">
                <div class="selected-supervisor-info">
                  <h3>{{ getSelectedSupervisor?.name || '' }} 的分配</h3>
                  <div class="supervisor-stats">
                    <span class="stat">现有咨询师: {{ getSelectedSupervisor?.counselorCount || 0 }}</span>
                    <span class="stat">最大容量: {{ getSelectedSupervisor?.maxCounselors || 0 }}</span>
                  </div>
                </div>
                
                <div class="search-bar">
                  <input type="text" v-model="assignedCounselorSearchTerm" placeholder="搜索已分配咨询师..." class="search-input">
                </div>
                
                <div class="assigned-counselors-list">
                  <div
                    v-for="counselor in filteredAssignedCounselors"
                    :key="counselor.id"
                    class="counselor-item"
                    @click="toggleAssignedCounselor(counselor.id)"
                    :class="{ 'selected': selectedAssignedCounselorIds.includes(counselor.id) }"
                  >
                    <div class="counselor-name">{{ counselor.name }}</div>
                    <div class="counselor-specialties">专长: {{ counselor.specialties.join(', ') }}</div>
                  </div>
                  <div v-if="filteredAssignedCounselors.length === 0" class="empty-message">
                    没有分配的咨询师
                  </div>
                </div>
              </div>
              
              <div class="assignment-panel" v-else>
                <div class="empty-message">请先选择一位督导</div>
              </div>
            </div>
          </div>
          
          <div class="unassigned-counselors-section">
            <h2>未分配督导的咨询师</h2>
            <div class="search-bar">
              <input type="text" v-model="counselorSearchTerm" placeholder="搜索未分配咨询师..." class="search-input">
            </div>
            
            <div class="counselors-list">
              <div
                v-for="counselor in filteredUnassignedCounselors"
                :key="counselor.id"
                class="counselor-item"
                @click="toggleCounselor(counselor.id)"
                :class="{ 'selected': selectedCounselorIds.includes(counselor.id) }"
              >
                <div class="counselor-name">{{ counselor.name }}</div>
                <div class="counselor-specialties">专长: {{ counselor.specialties.join(', ') }}</div>
              </div>
              <div v-if="filteredUnassignedCounselors.length === 0" class="empty-message">
                所有咨询师都已分配督导
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, watch } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  export default {
    name: 'AdminSupervision',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      // 从localStorage获取用户信息
      const userData = JSON.parse(localStorage.getItem('user'))
      const username = ref(userData?.username || '管理员')
      
      // 搜索字段
      const supervisorSearchTerm = ref('')
      const counselorSearchTerm = ref('')
      const assignedCounselorSearchTerm = ref('')
      
      // 选中状态
      const selectedSupervisorId = ref(null)
      const selectedCounselorIds = ref([])
      const selectedAssignedCounselorIds = ref([])
      
      // 督导和咨询师数据
      const supervisors = ref([])
      const counselors = ref([])
      const assignedCounselors = ref([]) // 存储已分配给当前选中督导的咨询师
      const unassignedCounselors = ref([]) // 存储未分配督导的咨询师
  
      // 加载督导和咨询师数据
      const loadData = async () => {
        try {
          // 获取所有督导
          const supervisorsResponse = await axios.get('/api/binding/supervisors')
          
          // 获取所有咨询师（非督导）
          const counselorsResponse = await axios.get('/api/binding/counselors')
          
          // 处理督导数据
          if (supervisorsResponse.data && Array.isArray(supervisorsResponse.data)) {
            supervisors.value = supervisorsResponse.data.map(supervisor => ({
              id: supervisor.supervisorId,
              name: supervisor.name || '未知督导',
              specialties: [supervisor.expertiseArea || '咨询'], // 使用expertiseArea作为专长
              counselorCount: 0, // 初始化为0，后面会根据绑定关系更新
              maxCounselors: 5 // 默认最大容量
            }))
          }
          
          // 处理咨询师数据
          if (counselorsResponse.data && Array.isArray(counselorsResponse.data)) {
            counselors.value = counselorsResponse.data.map(counselor => ({
              id: counselor.counselorId,
              name: counselor.name || '未知咨询师',
              specialties: [counselor.expertiseArea || '咨询'], // 使用expertiseArea作为专长
              supervisorId: null // 默认未分配督导
            }))
          }
          
          // 加载未分配督导的咨询师
          await loadUnassignedCounselors()
          
          console.log('加载的督导数据:', supervisors.value)
          console.log('加载的咨询师数据:', counselors.value)
        } catch (error) {
          console.error('加载数据失败:', error)
        }
      }

      // 加载未分配督导的咨询师
      const loadUnassignedCounselors = async () => {
        try {
          // 获取所有绑定关系
          const bindingsResponse = await axios.get('/api/binding/all')
          
          if (bindingsResponse.data && Array.isArray(bindingsResponse.data)) {
            // 创建一个Map来存储每个咨询师的督导ID
            const counselorSupervisorMap = new Map()
            
            // 遍历所有绑定关系，找出每个咨询师的督导
            bindingsResponse.data.forEach(binding => {
              if (binding.supervisorId) {
                counselorSupervisorMap.set(binding.counselorId, binding.supervisorId)
              }
            })
            
            // 更新咨询师的督导ID
            counselors.value.forEach(counselor => {
              if (counselorSupervisorMap.has(counselor.id)) {
                counselor.supervisorId = counselorSupervisorMap.get(counselor.id)
              }
            })
            
            // 更新未分配督导的咨询师列表
            unassignedCounselors.value = counselors.value.filter(c => c.supervisorId === null)
          }
        } catch (error) {
          console.error('加载未分配督导的咨询师失败:', error)
        }
      }
      
      // 当选择督导时，加载该督导绑定的咨询师
      const loadAssignedCounselors = async (supervisorId) => {
        if (!supervisorId) {
          assignedCounselors.value = []
          return
        }
        
        try {
          // 使用接口获取督导绑定的咨询师
          const response = await axios.get(`/api/binding/counselors/${supervisorId}`)
          
          if (response.data && Array.isArray(response.data)) {
            // 将后端返回的数据转换为前端需要的格式
            assignedCounselors.value = response.data.map(counselor => ({
              id: counselor.counselorId,
              name: counselor.name || '未知咨询师',
              specialties: [counselor.expertiseArea || '咨询'],
              supervisorId: supervisorId
            }))
            
            // 更新督导的咨询师数量
            const supervisor = supervisors.value.find(s => s.id === supervisorId)
            if (supervisor) {
              supervisor.counselorCount = assignedCounselors.value.length
            }
          }
        } catch (error) {
          console.error(`加载督导ID ${supervisorId} 的咨询师失败:`, error)
          assignedCounselors.value = []
        }
      }

      // 过滤功能
      const filteredSupervisors = computed(() => {
        if (!supervisorSearchTerm.value) return supervisors.value
        const searchTerm = supervisorSearchTerm.value.toLowerCase()
        return supervisors.value.filter(supervisor => 
          supervisor.name.toLowerCase().includes(searchTerm) ||
          supervisor.specialties.some(s => s.toLowerCase().includes(searchTerm))
        )
      })
      
      const filteredUnassignedCounselors = computed(() => {
        let result = unassignedCounselors.value
        
        // 如果选择了督导，也显示分配给其他督导的咨询师
        if (selectedSupervisorId.value) {
          // 添加分配给其他督导的咨询师
          const otherAssignedCounselors = counselors.value.filter(c => 
            c.supervisorId !== null && 
            c.supervisorId !== selectedSupervisorId.value
          )
          result = [...result, ...otherAssignedCounselors]
        }
        
        if (counselorSearchTerm.value) {
          const searchTerm = counselorSearchTerm.value.toLowerCase()
          result = result.filter(counselor => 
            counselor.name.toLowerCase().includes(searchTerm) ||
            counselor.specialties.some(s => s.toLowerCase().includes(searchTerm))
          )
        }
        
        return result
      })
      
      const getSelectedSupervisor = computed(() => {
        return supervisors.value.find(s => s.id === selectedSupervisorId.value)
      })
      
      const filteredAssignedCounselors = computed(() => {
        if (!selectedSupervisorId.value) return []
        
        let result = assignedCounselors.value
        
        if (assignedCounselorSearchTerm.value) {
          const searchTerm = assignedCounselorSearchTerm.value.toLowerCase()
          result = result.filter(counselor => 
            counselor.name.toLowerCase().includes(searchTerm) ||
            counselor.specialties.some(s => s.toLowerCase().includes(searchTerm))
          )
        }
        
        return result
      })
      
      // 操作函数
      const selectSupervisor = async (supervisor) => {
        // 切换督导时清空选中的咨询师
        selectedCounselorIds.value = []
        selectedAssignedCounselorIds.value = []
        
        if (selectedSupervisorId.value === supervisor.id) {
          selectedSupervisorId.value = null
          assignedCounselors.value = []
        } else {
          selectedSupervisorId.value = supervisor.id
          // 加载该督导绑定的咨询师
          await loadAssignedCounselors(supervisor.id)
        }
      }
      
      const toggleCounselor = (counselorId) => {
        const index = selectedCounselorIds.value.indexOf(counselorId)
        if (index === -1) {
          selectedCounselorIds.value.push(counselorId)
        } else {
          selectedCounselorIds.value.splice(index, 1)
        }
      }
      
      const toggleAssignedCounselor = (counselorId) => {
        const index = selectedAssignedCounselorIds.value.indexOf(counselorId)
        if (index === -1) {
          selectedAssignedCounselorIds.value.push(counselorId)
        } else {
          selectedAssignedCounselorIds.value.splice(index, 1)
        }
      }
      
      // 修改解除绑定方法
      const removeCounselors = async () => {
        if (!selectedSupervisorId.value || !selectedAssignedCounselorIds.value.length) return
        
        try {
          const requests = selectedAssignedCounselorIds.value.map(counselorId => {
            const record = {
              supervisorId: selectedSupervisorId.value,
              counselorId: counselorId,
              bindingStatus: 'unbound', // 使用 unbound 状态表示解绑
              createdAt: new Date().toISOString() // 添加创建时间确保这是最新的记录
            }
            return axios.post('/api/binding/add', record)
          })
          
          const results = await Promise.all(requests)
          console.log('解绑结果:', results)
          
          // 重新加载数据
          await loadData()
          // 重新加载当前督导的咨询师
          await loadAssignedCounselors(selectedSupervisorId.value)
          
          selectedAssignedCounselorIds.value = [] // 清空选中的已分配咨询师
        } catch (error) {
          console.error('解绑失败:', error)
          // 可以添加错误提示UI
        }
      }
      
      const assignCounselors = async () => {
        if (!selectedSupervisorId.value || !selectedCounselorIds.value.length) return
        
        try {
          const requests = selectedCounselorIds.value.map(counselorId => {
            const record = {
              supervisorId: selectedSupervisorId.value,
              counselorId: counselorId,
              bindingStatus: 'bound',
              createdAt: new Date().toISOString() // 添加创建时间确保这是最新的记录
            }
            return axios.post('/api/binding/add', record)
          })
          
          const results = await Promise.all(requests)
          console.log('绑定结果:', results)
          
          // 重新加载数据
          await loadData()
          // 重新加载当前督导的咨询师
          await loadAssignedCounselors(selectedSupervisorId.value)
          
          selectedCounselorIds.value = [] // 清空选中的未分配咨询师
        } catch (error) {
          console.error('绑定失败:', error)
          // 可以添加错误提示UI
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
  
      // 初始化加载数据
      onMounted(() => {
        loadData()
      })
  
      return {
        username,
        logout,
        goTo,
        supervisorSearchTerm,
        counselorSearchTerm,
        assignedCounselorSearchTerm,
        selectedSupervisorId,
        selectedCounselorIds,
        selectedAssignedCounselorIds,
        filteredSupervisors,
        filteredUnassignedCounselors,
        filteredAssignedCounselors,
        getSelectedSupervisor,
        selectSupervisor,
        toggleCounselor,
        toggleAssignedCounselor,
        assignCounselors,
        removeCounselors
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
  
  h2 {
    font-size: 1.3rem;
    margin-top: 20px;
    margin-bottom: 15px;
    color: #333;
    border-bottom: 1px solid #eee;
    padding-bottom: 8px;
  }
  
  .search-bar {
    margin-bottom: 15px;
  }
  
  .search-input {
    width: 100%;
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
  }
  
  .supervision-controls {
    display: flex;
    margin-top: 30px;
    gap: 15px;
    min-height: 300px;
  }
  
  .control-section {
    flex: 1;
    border: 1px solid #ddd;
    border-radius: 6px;
    padding: 15px;
    background-color: #f9f9f9;
  }
  
  .control-arrows {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 15px;
  }
  
  .arrow-btn {
    background-color: #007bff;
    color: white;
    border: none;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    font-size: 1.2rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .arrow-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }
  
  .supervisor-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
    max-height: 300px;
    overflow-y: auto;
  }
  
  .supervisor-card {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 6px;
    background-color: white;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .supervisor-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  }
  
  .supervisor-card.selected {
    border-color: #007bff;
    background-color: #e8f4ff;
  }
  
  .supervisor-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: #007bff;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    font-weight: bold;
  }
  
  .supervisor-info {
    flex: 1;
  }
  
  .supervisor-name {
    font-weight: bold;
    font-size: 1rem;
  }
  
  .supervisor-specialties {
    font-size: 0.85rem;
    color: #666;
    margin: 3px 0;
  }
  
  .supervisor-stats {
    font-size: 0.8rem;
    display: flex;
    gap: 10px;
  }
  
  .stat {
    color: #555;
  }
  
  .assignment-panel {
    min-height: 200px;
  }
  
  .selected-supervisor-info {
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px dashed #ddd;
  }
  
  .counselors-list, .assigned-counselors-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 200px;
    overflow-y: auto;
  }
  
  .counselor-item {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    background-color: white;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .counselor-item:hover {
    background-color: #f9f9f9;
  }
  
  .counselor-item.selected {
    border-color: #007bff;
    background-color: #e8f4ff;
  }
  
  .counselor-name {
    font-weight: bold;
    font-size: 0.9rem;
  }
  
  .counselor-specialties {
    font-size: 0.8rem;
    color: #666;
    margin-top: 3px;
  }
  
  .unassigned-counselors-section {
    margin-top: 30px;
  }
  
  .empty-message {
    color: #666;
    font-style: italic;
    padding: 15px;
    text-align: center;
    background-color: #f9f9f9;
    border-radius: 4px;
  }
  </style>