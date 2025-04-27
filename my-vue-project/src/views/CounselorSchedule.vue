<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item active" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('evaluation')">用户评估</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>咨询师排班表</h1>
        
        <!-- 日期选择器 -->
        <div class="date-selector">
          <button @click="changeWeek(-1)" class="nav-btn">上一周</button>
          <span class="current-date">{{ currentDateRange }}</span>
          <button @click="changeWeek(1)" class="nav-btn">下一周</button>
        </div>
        
        <!-- 排班表 -->
        <div class="schedule-container">
          <div class="time-labels">
            <div class="time-label"></div>
            <div class="time-label">上午</div>
            <div class="time-label">下午</div>
          </div>
          
          <div class="schedule-grid">
            <!-- 星期列表 -->
            <div class="weekday-row">
              <div v-for="day in weekDays" :key="day.date" class="weekday-cell">
                {{ day.name }}<br>{{ day.date }}
              </div>
            </div>
            
            <!-- 排班时间格 -->
            <div class="schedule-slots">
              <div v-for="day in weekDays" :key="day.date" class="day-column">
                <!-- 上午时间段 -->
                <div 
                  class="time-slot"
                  :class="{ 
                    'working': day.morning, 
                    'empty': !day.morning
                  }"
                >
                  <span v-if="day.morning" class="slot-info">上午 (09:00-11:00)</span>
                  <span v-else class="slot-info">无</span>
                </div>
                
                <!-- 下午时间段 -->
                <div 
                  class="time-slot"
                  :class="{ 
                    'working': day.afternoon, 
                    'empty': !day.afternoon
                  }"
                >
                  <span v-if="day.afternoon" class="slot-info">下午 (14:00-17:00)</span>
                  <span v-else class="slot-info">无</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 图例说明 -->
        <div class="schedule-actions">
          <div class="legend">
            <div class="legend-item">
              <div class="legend-color working"></div>
              <span>工作时间</span>
            </div>
            <div class="legend-item">
              <div class="legend-color empty"></div>
              <span>无</span>
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
  name: 'CounselorSchedule',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '咨询师')
    
    // 获取存储在localStorage中的counselorId
    const counselorId = ref(localStorage.getItem('counselor_id') || userData?.counselorId || userData?.userId || null)
    
    const currentWeekStart = ref(new Date())
    const weekDays = ref([]);
    const loading = ref(true);
    
    // 调整当前日期到本周一
    onMounted(() => {
      console.log('组件已挂载，counselorId:', counselorId.value);
      
      const today = new Date();
      const day = today.getDay() || 7; // 获取星期几，星期天为0转换为7
      if (day !== 1) { // 如果不是星期一
        today.setHours(-24 * (day - 1)); // 将日期设置为本周一
      }
      currentWeekStart.value = today;
      
      // 初始化一周的数据
      initWeekDays();
      
      // 从后端获取排班数据
      fetchScheduleData();
    })
    
    // 计算本周的日期范围
    const currentDateRange = computed(() => {
      const start = new Date(currentWeekStart.value);
      const end = new Date(currentWeekStart.value);
      end.setDate(end.getDate() + 6);
      
      return `${formatDate(start)} 至 ${formatDate(end)}`;
    })
    
    // 格式化日期为 YYYY-MM-DD
    const formatDate = (date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
    
    // 初始化一周的数据
    const initWeekDays = () => {
      const days = [];
      const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(currentWeekStart.value);
        date.setDate(date.getDate() + i);
        const formattedDate = formatDate(date);
        
        days.push({
          name: dayNames[i],
          date: formattedDate,
          morning: false,
          afternoon: false
        });
      }
      
      weekDays.value = days;
    }
    
    // 从后端获取排班数据
    const fetchScheduleData = async () => {
      try {
        if (!counselorId.value) {
          console.error('咨询师ID不存在，无法获取排班数据');
          return;
        }
        
        loading.value = true;
        console.log('正在获取排班数据，使用counselorId:', counselorId.value);
        
        // 调用后端API获取排班数据
        const response = await axios.get(`/api/schedules/counselor/${counselorId.value}`);
        console.log('获取到排班数据:', response.data);
        
        // 处理后端返回的数据
        if (response.data && Array.isArray(response.data)) {
          // 先重置所有日期的排班状态
          weekDays.value.forEach(day => {
            day.morning = false;
            day.afternoon = false;
          });
          
          // 遍历后端返回的排班数据
          response.data.forEach(schedule => {
            // 将后端返回的日期字符串转换为Date对象，并处理时区问题
            const scheduleDate = new Date(schedule.date);
            
            // 调整为本地时间（加上时区偏移）
            const localDate = new Date(scheduleDate.getTime() + scheduleDate.getTimezoneOffset() * 60000);
            
            // 获取日期的年、月、日
            const year = localDate.getFullYear();
            const month = localDate.getMonth() + 1;
            const day = localDate.getDate();
            
            // 创建一个新的日期对象，确保使用本地日期（不含时间）
            const adjustedDate = new Date(year, month - 1, day);
            
            console.log('原始日期:', schedule.date);
            console.log('调整后日期:', formatDate(adjustedDate));
            
            // 获取该日期在当前周的索引
            const dayIndex = getDayIndex(adjustedDate);
            
            console.log('日期索引:', dayIndex, '对应星期:', weekDays.value[dayIndex]?.name);
            
            // 如果日期在当前周内
            if (dayIndex >= 0 && dayIndex < 7) {
              // 根据timeSlot设置对应时间段的排班状态
              if (schedule.timeSlot === 'morning' && schedule.status === 'working') {
                weekDays.value[dayIndex].morning = true;
              } else if (schedule.timeSlot === 'afternoon' && schedule.status === 'working') {
                weekDays.value[dayIndex].afternoon = true;
              }
            }
          });
        }
      } catch (error) {
        console.error('获取排班数据失败:', error);
      } finally {
        loading.value = false;
      }
    };
    
    // 获取日期对应的星期索引（0-6）
    const getDayIndex = (date) => {
      const start = new Date(currentWeekStart.value);
      // 确保比较的是日期而不是时间
      const startYear = start.getFullYear();
      const startMonth = start.getMonth();
      const startDay = start.getDate();
      const startDate = new Date(startYear, startMonth, startDay);
      
      const dateYear = date.getFullYear();
      const dateMonth = date.getMonth();
      const dateDay = date.getDate();
      const compareDate = new Date(dateYear, dateMonth, dateDay);
      
      // 计算日期差异（天数）
      const diffTime = compareDate.getTime() - startDate.getTime();
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
      
      return diffDays;
    };
    
    // 切换周次
    const changeWeek = (offset) => {
      const newDate = new Date(currentWeekStart.value);
      newDate.setDate(newDate.getDate() + (offset * 7));
      currentWeekStart.value = newDate;
      
      // 重新初始化一周的数据
      initWeekDays();
      
      // 重新从后端获取新的一周排班数据
      fetchScheduleData();
    }
    
    const logout = () => {
      localStorage.removeItem('user')
      router.push('/login')
    }

    const goTo = (path) => {
      switch (path) {
        case 'home':
          router.push('/counselor/home')
          break
        case 'settings':
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
      counselorId,
      logout,
      goTo,
      currentDateRange,
      weekDays,
      changeWeek
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

/* 排班表特定样式 */
.date-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0;
  gap: 20px;
}

.current-date {
  font-weight: bold;
  font-size: 1.1rem;
}

.nav-btn {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.nav-btn:hover {
  background-color: #e0e0e0;
}

.schedule-container {
  display: flex;
  margin: 20px 0;
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
}

.time-labels {
  display: flex;
  flex-direction: column;
  width: 60px;
  border-right: 1px solid #ddd;
  background-color: #f8f8f8;
}

.time-label {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  border-bottom: 1px solid #eee;
}

.schedule-grid {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.weekday-row {
  display: flex;
  height: 50px;
  border-bottom: 1px solid #ddd;
  background-color: #f8f8f8;
}

.weekday-cell {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-right: 1px solid #eee;
  font-size: 0.9rem;
}

.schedule-slots {
  display: flex;
  flex: 1;
}

.day-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #eee;
}

.time-slot {
  height: 50px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.time-slot.working {
  background-color: rgba(0, 128, 0, 0.1);
}

.time-slot.empty {
  background-color: rgba(255, 255, 255, 0.1);
}

.slot-info {
  font-size: 0.8rem;
  color: #666;
}

.schedule-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
}

.legend {
  display: flex;
  gap: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.8rem;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 3px;
}

.legend-color.working {
  background-color: rgba(0, 128, 0, 0.1);
  border: 1px solid rgba(0, 128, 0, 0.3);
}

.legend-color.empty {
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
    height: auto;
  }
  
  .sidebar {
    width: 100%;
    border-radius: 0;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }
  
  .sidebar-item {
    margin-bottom: 0;
  }
  
  .main-content {
    padding: 10px;
  }
  
  .schedule-container {
    flex-direction: column;
  }
}
</style>