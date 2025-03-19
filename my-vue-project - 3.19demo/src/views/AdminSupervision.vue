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
  import { ref, computed } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    name: 'AdminSupervision',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
      
      // 搜索字段
      const supervisorSearchTerm = ref('')
      const counselorSearchTerm = ref('')
      const assignedCounselorSearchTerm = ref('')
      
      // 选中状态
      const selectedSupervisorId = ref(null)
      const selectedCounselorIds = ref([])
      const selectedAssignedCounselorIds = ref([])
      
      // 模拟数据 - 督导
      const supervisors = ref([
        { 
          id: 1, 
          name: '李教授', 
          specialties: ['抑郁症', '焦虑症'], 
          counselorCount: 2, 
          maxCounselors: 5, 
          assignedCounselors: [1, 3]
        },
        { 
          id: 2, 
          name: '王博士', 
          specialties: ['婚姻家庭', '青少年心理'], 
          counselorCount: 3, 
          maxCounselors: 6, 
          assignedCounselors: [5, 6, 7]
        },
        { 
          id: 3, 
          name: '张主任', 
          specialties: ['创伤治疗', '危机干预'], 
          counselorCount: 1, 
          maxCounselors: 4, 
          assignedCounselors: [2]
        }
      ])
      
      // 模拟数据 - 咨询师
      const counselors = ref([
        { id: 1, name: '小王', specialties: ['抑郁症'], supervisorId: 1 },
        { id: 2, name: '小李', specialties: ['创伤治疗'], supervisorId: 3 },
        { id: 3, name: '小张', specialties: ['焦虑症'], supervisorId: 1 },
        { id: 4, name: '小陈', specialties: ['青少年心理'], supervisorId: null },
        { id: 5, name: '小林', specialties: ['婚姻家庭'], supervisorId: 2 },
        { id: 6, name: '小何', specialties: ['职场压力'], supervisorId: 2 },
        { id: 7, name: '小赵', specialties: ['青少年心理'], supervisorId: 2 },
        { id: 8, name: '小孙', specialties: ['抑郁症'], supervisorId: null }
      ])
      
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
        // 获取未分配或分配给其他督导的咨询师
        let result = counselors.value.filter(c => 
          c.supervisorId === null || 
          (selectedSupervisorId.value && c.supervisorId !== selectedSupervisorId.value)
        )
        
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
        
        // 获取分配给当前选中督导的咨询师
        let result = counselors.value.filter(c => c.supervisorId === selectedSupervisorId.value)
        
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
      const selectSupervisor = (supervisor) => {
        // 切换督导时清空选中的咨询师
        selectedCounselorIds.value = []
        selectedAssignedCounselorIds.value = []
        
        if (selectedSupervisorId.value === supervisor.id) {
          selectedSupervisorId.value = null
        } else {
          selectedSupervisorId.value = supervisor.id
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
      
      const assignCounselors = () => {
        if (!selectedSupervisorId.value || !selectedCounselorIds.value.length) return
        
        const supervisor = supervisors.value.find(s => s.id === selectedSupervisorId.value)
        
        // 检查是否超过容量
        if (supervisor.counselorCount + selectedCounselorIds.value.length > supervisor.maxCounselors) {
          alert(`无法分配，超过督导最大容量 ${supervisor.maxCounselors}`)
          return
        }
        
        // 更新咨询师督导信息
        selectedCounselorIds.value.forEach(id => {
          const counselor = counselors.value.find(c => c.id === id)
          if (counselor) {
            // 如果咨询师之前有督导，减少前督导的计数
            if (counselor.supervisorId) {
              const prevSupervisor = supervisors.value.find(s => s.id === counselor.supervisorId)
              if (prevSupervisor) {
                prevSupervisor.counselorCount--
                const index = prevSupervisor.assignedCounselors.indexOf(counselor.id)
                if (index !== -1) {
                  prevSupervisor.assignedCounselors.splice(index, 1)
                }
              }
            }
            
            // 设置新督导
            counselor.supervisorId = selectedSupervisorId.value
          }
        })
        
        // 更新督导信息
        supervisor.counselorCount += selectedCounselorIds.value.length
        supervisor.assignedCounselors = [
          ...supervisor.assignedCounselors,
          ...selectedCounselorIds.value
        ]
        
        // 清空选择
        selectedCounselorIds.value = []
        
        console.log(`已将 ${selectedCounselorIds.value.length} 名咨询师分配给督导 ${supervisor.name}`)
      }
      
      const removeCounselors = () => {
        if (!selectedSupervisorId.value || !selectedAssignedCounselorIds.value.length) return
        
        const supervisor = supervisors.value.find(s => s.id === selectedSupervisorId.value)
        
        // 更新咨询师督导信息
        selectedAssignedCounselorIds.value.forEach(id => {
          const counselor = counselors.value.find(c => c.id === id)
          if (counselor) {
            counselor.supervisorId = null
          }
        })
        
        // 更新督导信息
        supervisor.counselorCount -= selectedAssignedCounselorIds.value.length
        supervisor.assignedCounselors = supervisor.assignedCounselors.filter(
          id => !selectedAssignedCounselorIds.value.includes(id)
        )
        
        // 清空选择
        selectedAssignedCounselorIds.value = []
        
        console.log(`已移除 ${selectedAssignedCounselorIds.value.length} 名咨询师的督导分配`)
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