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

export default {
  name: 'CounselorHome',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '咨询师')
    
    // 打卡相关状态
    const isWorking = ref(false)
    const clockInTime = ref(null)
    const clockOutTime = ref(null)
    const currentTime = ref(new Date())
    const timerInterval = ref(null)
    const lateMinutes = ref(0)
    
    // 模拟后端数据 - 实际应用中应从后端获取
    /* 
      后端数据结构示例:
      {
        schedules: [
          { day: 'Monday', slots: [{start: '09:00', end: '11:00'}, {start: '14:00', end: '16:00'}] },
          { day: 'Wednesday', slots: [{start: '14:00', end: '17:00'}] },
          ...
        ]
      }
    */
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
    const handleClockInOut = () => {
      if (!isWorking.value) {
        // 打卡上班
        clockInTime.value = new Date()
        isWorking.value = true
        // 在这里应该调用后端API记录打卡信息
        // store.dispatch('clockIn', { time: clockInTime.value })
      } else {
        // 打卡下班
        clockOutTime.value = new Date()
        isWorking.value = false
        // 在这里应该调用后端API记录打卡信息
        // store.dispatch('clockOut', { time: clockOutTime.value })
      }
    }
    
    // 获取今日排班信息
    const fetchTodaySchedule = () => {
      // 模拟从后端获取数据
      // 实际应用中应通过API获取：store.dispatch('fetchTodaySchedule')
      const today = currentTime.value.getDay()
      
      // 示例数据：假设今天有排班
      if (today >= 1 && today <= 5) { // 周一到周五
        if (today % 2 === 0) {
          todaySchedule.value = [
            { start: '09:00', end: '11:00' },
            { start: '14:00', end: '17:00' }
          ]
        } else {
          todaySchedule.value = [
            { start: '14:00', end: '17:00' }
          ]
        }
      } else {
        todaySchedule.value = [] // 周末无排班
      }
    }
    
    // 启动定时器，每分钟更新一次
    const startTimer = () => {
      timerInterval.value = setInterval(() => {
        currentTime.value = new Date()
      }, 60000) // 每分钟更新一次
    }
    
    onMounted(() => {
      fetchTodaySchedule()
      startTimer()
    })
    
    onUnmounted(() => {
      if (timerInterval.value) {
        clearInterval(timerInterval.value)
      }
    })

    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    const goTo = (path) => {
      switch (path) {
        case 'counselorSettings':
          router.push('/counselor/settings')
          break
        case 'requests':
          router.push('/counselor/requests')
          break
        case 'chat':
          router.push('/counselor/chat')
          break
        case 'schedule':
          router.push('/counselor/schedule')
          break
        case 'history':
          router.push('/counselor/history')
          break
        case 'evaluation':
          router.push('/counselor/evaluation')
          break
        default:
          console.error('Invalid path')
      }
    }

    return {
      username,
      logout,
      goTo,
      isWorking,
      clockInTime,
      clockOutTime,
      currentTime,
      todaySchedule,
      shouldWork,
      canClockInOut,
      isLate,
      lateMinutes,
      formatTime,
      formatTimeSlot,
      calculateWorkHours,
      handleClockInOut
    }
  }
}
</script>

<style scoped>
/* 基础样式保持与之前一致 */
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
</style>