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
            <div class="time-label">08:00</div>
            <div class="time-label">09:00</div>
            <div class="time-label">10:00</div>
            <div class="time-label">11:00</div>
            <div class="time-label">12:00</div>
            <div class="time-label">13:00</div>
            <div class="time-label">14:00</div>
            <div class="time-label">15:00</div>
            <div class="time-label">16:00</div>
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
                <div 
                  v-for="slot in day.slots" 
                  :key="`${day.date}-${slot.time}`" 
                  class="time-slot"
                  :class="{ 
                    'working': slot.status === 'working', 
                    'empty': slot.status === 'empty'
                  }"
                  @click="toggleSlotStatus(day, slot)"
                >
                  <span v-if="slot.status === 'working'" class="slot-info">工作时间</span>
                  <span v-else class="slot-info">无</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 保存与操作按钮 -->
        <div class="schedule-actions">
          <button @click="saveSchedule" class="save-btn">保存排班设置</button>
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

export default {
  name: 'CounselorSchedule',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const username = computed(() => store.getters.username)
    const currentWeekStart = ref(new Date())
    
    // 调整当前日期到本周一
    onMounted(() => {
      const today = new Date();
      const day = today.getDay() || 7; // 获取星期几，星期天为0转换为7
      if (day !== 1) { // 如果不是星期一
        today.setHours(-24 * (day - 1)); // 将日期设置为本周一
      }
      currentWeekStart.value = today;
      
      // 这里可以添加从后端获取排班数据的代码
      // fetchScheduleData();
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
    
    // 计算本周的天
    const weekDays = computed(() => {
      const days = [];
      const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(currentWeekStart.value);
        date.setDate(date.getDate() + i);
        
        const slots = [];
        
        // 创建上午8点到下午4点，每小时一个时间段
        for (let hour = 8; hour <= 16; hour++) {
          // 随机生成状态用于演示，实际应从后端获取
          // 'working' - 工作时间, 'empty' - 无
          const statuses = ['working', 'empty'];
          const randomStatus = statuses[Math.floor(Math.random() * 2)];
          
          slots.push({
            time: `${hour}:00`,
            status: randomStatus,
            // 这里可以添加预约的详细信息，如用户ID、咨询类型等
            // userInfo: randomStatus === 'working' ? { id: 123, name: '张三' } : null,
          });
        }
        
        days.push({
          name: dayNames[i],
          date: formatDate(date),
          slots: slots
        });
      }
      
      return days;
    })
    
    // 切换周次
    const changeWeek = (offset) => {
      const newDate = new Date(currentWeekStart.value);
      newDate.setDate(newDate.getDate() + (offset * 7));
      currentWeekStart.value = newDate;
      
      // 这里应该重新从后端获取新的一周排班数据
      // fetchScheduleData(formatDate(newDate));
    }
    
    // 切换时间段状态
    const toggleSlotStatus = (day, slot) => {
      if (slot.status === 'working') {
        const confirmCancel = confirm('是否取消任务？');
        if (confirmCancel) {
          const cancelReason = prompt('请输入取消原因：');
          if (cancelReason) {
            // 调用后端接口，传递取消原因
            console.log(`Canceling ${day.date} ${slot.time} with reason: ${cancelReason}`);
            slot.status = 'empty';
          }
        }
      } else {
        // 如果当前状态为“无”，可以切换为“工作时间”
        slot.status = 'working';
      }
      
      // 记录修改，等待保存
      console.log(`Changed ${day.date} ${slot.time} to ${slot.status}`);
    }
    
    // 保存排班设置
    const saveSchedule = () => {
      // 在真实场景中，这里应该将数据发送到后端保存
      console.log('Saving schedule...', weekDays.value);
      
      // 模拟API调用
      setTimeout(() => {
        alert('排班表已保存'); // 实际环境中应使用更友好的提示方式
      }, 800);
      
      /* 
      实际代码可能如下:
      try {
        await api.saveSchedule({
          counselorId: store.getters.userId,
          weekStart: formatDate(currentWeekStart.value),
          scheduleData: weekDays.value
        });
        // 显示成功提示
      } catch (error) {
        // 错误处理
      }
      */
    }
    
    const logout = () => {
      store.dispatch('logout')
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
      logout,
      goTo,
      currentDateRange,
      weekDays,
      changeWeek,
      toggleSlotStatus,
      saveSchedule
    }
  }
}
</script>

<style scoped>
/* 样式保持与咨询师主页一致 */
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
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.time-slot.working {
  background-color: rgba(0, 128, 0, 0.1);
}

.time-slot.working:hover {
  background-color: rgba(0, 128, 0, 0.2);
}

.time-slot.empty {
  background-color: rgba(255, 255, 255, 0.1);
}

.time-slot.empty:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.slot-info {
  font-size: 0.8rem;
  color: #666;
}

.schedule-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.save-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.save-btn:hover {
  background-color: #0056b3;
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
  
  .time-labels {
    width: 100%;
    flex-direction: row;
    border-right: none;
    border-bottom: 1px solid #ddd;
  }
  
  .time-label {
    flex: 1;
    height: 30px;
    border-bottom: none;
    border-right: 1px solid #eee;
  }
  
  .weekday-row {
    height: 40px;
  }
  
  .schedule-slots {
    flex-direction: column;
  }
  
  .day-column {
    flex-direction: row;
    border-right: none;
    border-bottom: 1px solid #eee;
  }
  
  .time-slot {
    flex: 1;
    height: 40px;
    border-bottom: none;
    border-right: 1px solid #eee;
  }
  
  .schedule-actions {
    flex-direction: column;
    gap: 15px;
  }
  
  .legend {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>