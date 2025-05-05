<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('counselorSettings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('evaluation')">用户评估</div>
      <div class="sidebar-item" @click="goTo('help')">督导求助</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 右上角欢迎信息和退出登录 -->
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <!-- 主内容卡片 -->
      <div class="card">
        <h1>咨询师主页</h1>
        <p>这是咨询师的主页界面。</p>
        
        <!-- 打卡系统 -->
        <div class="clock-in-system">
          <h2>工作状态</h2>
          
          <!-- 咨询开放时间 -->
          <div class="work-info">
            <h3>咨询开放时间</h3>
            <p>周一至周五：上午 9:00-11:00，下午 14:00-17:00</p>
          </div>
          
          <!-- 排班情况 -->
          <div class="work-info">
            <h3>今日排班情况</h3>
            <p v-if="todaySchedule.length === 0" class="no-schedule">今天没有排班</p>
            <div v-else class="schedule-info">
              <p>您今天排班时间为：</p>
              <ul>
                <li v-for="(slot, index) in todaySchedule" :key="index">
                  {{ formatTimeSlot(slot) }}
                </li>
              </ul>
            </div>
          </div>
          
          <!-- 工作状态和打卡 -->
          <div class="work-status">
            <div class="status-info">
              <h3>当前状态</h3>
              <p :class="{'status': true, 'working': isWorking, 'not-working': !isWorking}">
                {{ isWorking ? '上班中' : '未上班' }}
              </p>
              <p v-if="detailedStatus" class="detailed-status">
                {{ detailedStatus }}
              </p>
              <p v-if="isLate && !isWorking && shouldWork" class="late-warning">
                <i class="warning-icon">⚠️</i> 您已迟到 {{ lateMinutes }} 分钟
              </p>
            </div>
            
            <button 
              class="clock-btn" 
              :class="{'clock-in': !isWorking, 'clock-out': isWorking}"
              @click="handleClockInOut"
              :disabled="!canClockInOut"
            >
              {{ isWorking ? '打卡下班' : '打卡上班' }}
            </button>
          </div>
          
          <!-- 今日工作记录 -->
          <div class="work-record" v-if="clockInTime || clockOutTime">
            <h3>今日工作记录</h3>
            <p v-if="clockInTime">上班时间：{{ formatTime(clockInTime) }}</p>
            <p v-if="clockOutTime">下班时间：{{ formatTime(clockOutTime) }}</p>
            <p v-if="clockInTime && clockOutTime">
              工作时长：{{ calculateWorkHours(clockInTime, clockOutTime) }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'CounselorHome',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '咨询师')
    
    // 获取counselorId
    const counselorId = ref(localStorage.getItem('counselor_id') || userData?.counselorId || userData?.userId || null)
    
    // 打卡相关状态
    const isWorking = ref(false)
    const clockInTime = ref(null)
    const clockOutTime = ref(null)
    const currentTime = ref(new Date())
    const timerInterval = ref(null)
    const lateMinutes = ref(0)
    const detailedStatus = ref('')
    
    // 排班数据
    const todaySchedule = ref([])
    
    // 获取当前是否应该在工作
    const shouldWork = computed(() => {
      if (todaySchedule.value.length === 0) return false
      
      const now = currentTime.value
      const hours = now.getHours()
      const minutes = now.getMinutes()
      const currentMinutes = hours * 60 + minutes
      
      return todaySchedule.value.some(slot => {
        const startParts = slot.start.split(':')
        const endParts = slot.end.split(':')
        const startMinutes = parseInt(startParts[0]) * 60 + parseInt(startParts[1])
        const endMinutes = parseInt(endParts[0]) * 60 + parseInt(endParts[1])
        
        return currentMinutes >= startMinutes && currentMinutes <= endMinutes
      })
    })
    
    // 是否可以打卡
    const canClockInOut = computed(() => {
      // 如果今天没有排班，不能打卡
      if (todaySchedule.value.length === 0) return false
      
      // 已经下班了，不能再打卡
      if (clockInTime.value && clockOutTime.value) return false
      
      return true
    })
    
    // 是否迟到
    const isLate = computed(() => {
      if (todaySchedule.value.length === 0 || isWorking.value) return false
      
      const now = currentTime.value
      const hours = now.getHours()
      const minutes = now.getMinutes()
      const currentMinutes = hours * 60 + minutes
      
      // 检查是否有当前时间之前的排班未打卡
      for (const slot of todaySchedule.value) {
        const startParts = slot.start.split(':')
        const startMinutes = parseInt(startParts[0]) * 60 + parseInt(startParts[1])
        
        if (currentMinutes > startMinutes && !isWorking.value) {
          lateMinutes.value = currentMinutes - startMinutes
          return true
        }
      }
      
      return false
    })
    
    // 格式化时间显示
    const formatTime = (date) => {
      if (!date) return '';
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      const seconds = date.getSeconds().toString().padStart(2, '0')
      return `${hours}:${minutes}:${seconds}`
    }
    
    // 格式化时间段显示
    const formatTimeSlot = (slot) => {
      return `${slot.start} - ${slot.end}`
    }
    
    // 计算工作时长
    const calculateWorkHours = (start, end) => {
      if (!start || !end) return '';
      const diff = (end - start) / 1000 / 60
      const hours = Math.floor(diff / 60)
      const minutes = Math.floor(diff % 60)
      return `${hours}小时${minutes}分钟`
    }
    
    // 处理打卡
    const handleClockInOut = async () => {
      if (isWorking.value) {
        // 打卡下班
        clockOutTime.value = new Date()
        isWorking.value = false
        detailedStatus.value = '今日工作已完成'
        
        // 向后端发送打卡下班请求
        try {
          await axios.post(`http://localhost:8080/api/counselor/clock-out`, {
            counselorId: counselorId.value,
            clockOutTime: formatTime(clockOutTime.value)
          })
        } catch (error) {
          console.error('打卡下班失败:', error)
        }
      } else {
        // 打卡上班
        clockInTime.value = new Date()
        isWorking.value = true
        detailedStatus.value = ''
        
        // 向后端发送打卡上班请求
        try {
          await axios.post(`http://localhost:8080/api/counselor/clock-in`, {
            counselorId: counselorId.value,
            clockInTime: formatTime(clockInTime.value)
          })
        } catch (error) {
          console.error('打卡上班失败:', error)
        }
      }
    }
    
    // 加载排班数据
    const loadSchedule = async () => {
      try {
        // 获取当前日期
        const today = new Date()
        const dayOfWeek = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][today.getDay()]
        
        // 从后端获取排班数据
        const response = await axios.get(`http://localhost:8080/api/schedules/counselor/${counselorId.value}`)
        
        if (response.data && Array.isArray(response.data.schedules)) {
          // 找到今天的排班
          const todayScheduleData = response.data.schedules.find(schedule => schedule.day === dayOfWeek)
          
          if (todayScheduleData && Array.isArray(todayScheduleData.slots)) {
            todaySchedule.value = todayScheduleData.slots
          } else {
            todaySchedule.value = []
          }
        }
      } catch (error) {
        console.error('加载排班数据失败:', error)
        todaySchedule.value = []
      }
    }
    
    // 加载打卡状态
    /*const loadClockStatus = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/counselor/clock-status/${counselorId.value}`)
        
        if (response.data) {
          if (response.data.clockInTime) {
            clockInTime.value = new Date(response.data.clockInTime)
            isWorking.value = !response.data.clockOutTime
          }
          
          if (response.data.clockOutTime) {
            clockOutTime.value = new Date(response.data.clockOutTime)
          }
        }
      } catch (error) {
        console.error('加载打卡状态失败:', error)
      }
    }*/
    
    // 更新当前时间
    const updateCurrentTime = () => {
      currentTime.value = new Date()
    }
    
    // 登出
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }
    
    // 页面导航
    const goTo = (path) => {
      const paths = {
        counselorSettings: '/counselor/settings',
        requests: '/counselor/requests',
        chat: '/counselor/chat',
        schedule: '/counselor/schedule',
        history: '/counselor/history',
        evaluation: '/counselor/evaluation',
        help: '/counselor/help'
      }
      
      const targetPath = paths[path]
      if (targetPath) {
        router.push(targetPath)
      } else {
        console.error('Invalid path')
      }
    }
    
    // 组件挂载时
    onMounted(async () => {
      // 加载排班和打卡状态
      await loadSchedule()
      await loadClockStatus()
      
      // 设置定时器，每分钟更新一次当前时间
      updateCurrentTime()
      timerInterval.value = setInterval(() => {
        updateCurrentTime()
      }, 60000)
      
      // 设置定时器，每5分钟刷新一次排班和打卡状态
      const dataRefreshInterval = setInterval(async () => {
        await loadSchedule()
        await loadClockStatus()
      }, 300000)
      
      // 组件卸载时清除定时器
      onUnmounted(() => {
        if (timerInterval.value) {
          clearInterval(timerInterval.value)
        }
        if (dataRefreshInterval) {
          clearInterval(dataRefreshInterval)
        }
      })
    })
    
    return {
      username,
      todaySchedule,
      isWorking,
      clockInTime,
      clockOutTime,
      shouldWork,
      canClockInOut,
      isLate,
      lateMinutes,
      detailedStatus,
      formatTime,
      formatTimeSlot,
      calculateWorkHours,
      handleClockInOut,
      logout,
      goTo
    }
  }
}
</script>

<style scoped>
/* 样式保持不变 */
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

/* 打卡系统样式 */
.clock-in-system {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.work-info {
  margin-bottom: 15px;
  padding: 10px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.work-info h3 {
  margin-top: 0;
  color: #333;
  font-size: 1rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.work-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
}

.status-info {
  flex: 1;
}

.status-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 1rem;
}

.status {
  font-weight: bold;
  font-size: 1.1rem;
}

.working {
  color: #28a745;
}

.not-working {
  color: #6c757d;
}

.late-warning {
  color: #dc3545;
  font-weight: bold;
  display: flex;
  align-items: center;
  margin-top: 5px;
}

.warning-icon {
  margin-right: 5px;
  font-style: normal;
}

.clock-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.clock-in {
  background-color: #28a745;
  color: white;
}

.clock-in:hover {
  background-color: #218838;
}

.clock-out {
  background-color: #dc3545;
  color: white;
}

.clock-out:hover {
  background-color: #c82333;
}

.clock-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.work-record {
  padding: 10px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.work-record h3 {
  margin-top: 0;
  color: #333;
  font-size: 1rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.no-schedule {
  color: #6c757d;
  font-style: italic;
}

.schedule-info ul {
  margin: 5px 0 0 0;
  padding-left: 20px;
}

.schedule-info li {
  margin-bottom: 5px;
}

.detailed-status {
  margin-top: 5px;
  padding: 5px 8px;
  background-color: #f0f8ff;
  border-radius: 4px;
  font-size: 0.9rem;
  color: #0066cc;
  border-left: 3px solid #0066cc;
}
</style>

