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
          <h1>咨询师排班管理</h1>
          <p>在此页面管理咨询师的工作时间安排。</p>
          
          <div class="filter-bar">
            <select v-model="selectedWeek" class="filter-select">
              <option value="current">本周</option>
              <option value="next">下周</option>
              <option value="after">两周后</option>
            </select>
            <select v-model="selectedCounselor" class="filter-select">
              <option value="all">全部咨询师</option>
              <option v-for="counselor in counselors" :key="counselor.counselorId" :value="counselor.counselorId">
                {{ counselor.name }}
              </option>
            </select>
            <button class="action-btn" @click="openAddScheduleModal">添加排班</button>
          </div>
  
          <div class="schedule-table">
            <table>
              <thead>
                <tr>
                  <th>咨询师</th>
                  <th>周一</th>
                  <th>周二</th>
                  <th>周三</th>
                  <th>周四</th>
                  <th>周五</th>
                  <th>周六</th>
                  <th>周日</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="schedule in filteredSchedules" :key="schedule.id">
                  <td>{{ schedule.counselorName }}</td>
                  <td v-for="day in 7" :key="day" :class="{ 'has-slots': hasSlots(schedule, day) }">
                    {{ getTimeSlots(schedule, day) }}
                  </td>
                  <td>
                    <button class="small-btn edit-btn" @click="editSchedule(schedule.id)">编辑</button>
                    <button class="small-btn delete-btn" @click="deleteSchedule(schedule.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- 添加排班模态框 -->
          <div v-if="showAddModal" class="modal">
            <div class="modal-content">
              <span class="close" @click="closeModal">&times;</span>
              <h2>添加排班</h2>
              <div class="form-group">
                <label>咨询师:</label>
                <select v-model="newSchedule.counselorId" class="form-control">
                  <option v-for="counselor in counselors" :key="counselor.counselorId" :value="counselor.counselorId">
                    {{ counselor.name }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>周次:</label>
                <select v-model="newSchedule.week" class="form-control">
                  <option value="current">本周</option>
                  <option value="next">下周</option>
                  <option value="after">两周后</option>
                </select>
              </div>
              <div class="form-group">
                <label>工作日和时间段:</label>
                <div class="day-time-selector">
                  <div v-for="day in 7" :key="day" class="day-time-option">
                    <div class="day-header">
                      <input type="checkbox" :id="'day-'+day" v-model="newSchedule.days[day]">
                      <label :for="'day-'+day">{{ getDayName(day) }}</label>
                    </div>
                    <div class="time-options" v-if="newSchedule.days[day]">
                      <div>
                        <input type="checkbox" :id="'morning-'+day" v-model="newSchedule.slots[day].morning">
                        <label :for="'morning-'+day">上午 (09:00-11:00)</label>
                      </div>
                      <div>
                        <input type="checkbox" :id="'afternoon-'+day" v-model="newSchedule.slots[day].afternoon">
                        <label :for="'afternoon-'+day">下午 (14:00-17:00)</label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label>时间段:</label>
                <div v-for="day in 7" :key="day" v-if="newSchedule.days[day]" class="time-slots">
                  <p>{{ getDayName(day) }}:</p>
                  <div>
                    <input type="checkbox" :id="'morning-'+day" v-model="newSchedule.slots[day].morning">
                    <label :for="'morning-'+day">上午 (09:00-11:00)</label>
                  </div>
                  <div>
                    <input type="checkbox" :id="'afternoon-'+day" v-model="newSchedule.slots[day].afternoon">
                    <label :for="'afternoon-'+day">下午 (14:00-17:00)</label>
                  </div>
                </div>
              </div>
              <div class="form-actions">
                <button class="btn save-btn" @click="saveSchedule">保存</button>
                <button class="btn cancel-btn" @click="closeModal">取消</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, reactive, onMounted } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  export default {
    name: 'AdminSchedule',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
      const selectedWeek = ref('current')
      const selectedCounselor = ref('all')
      
      // 咨询师数据
      const counselors = ref([])
      
      // 排班数据
      const schedules = ref([])
      
      // 模态框控制
      const showAddModal = ref(false)
      const isEditing = ref(false)
      const editingId = ref(null)
      
      // 新排班数据
      const newSchedule = reactive({
        id: null,
        counselorId: null,
        counselorName: '',
        week: 'current',
        days: {1: false, 2: false, 3: false, 4: false, 5: false, 6: false, 7: false},
        slots: {
          1: {morning: false, afternoon: false},
          2: {morning: false, afternoon: false},
          3: {morning: false, afternoon: false},
          4: {morning: false, afternoon: false},
          5: {morning: false, afternoon: false},
          6: {morning: false, afternoon: false},
          7: {morning: false, afternoon: false}
        }
      })
      
      // 获取咨询师数据
      const fetchCounselors = async () => {
        try {
          // 修改API路径，使用正确的咨询师获取接口
          const response = await axios.get('/api/appointments/allCounselor')
          counselors.value = response.data
        } catch (error) {
          console.error('获取咨询师数据失败:', error)
        }
      }
      
      // 获取排班数据
      const fetchSchedules = async () => {
        try {
          // 使用正确的排班获取接口
          const response = await axios.get('/api/schedules/all')
          // 转换后端数据格式为前端所需格式
          schedules.value = formatSchedules(response.data)
        } catch (error) {
          console.error('获取排班数据失败:', error)
        }
      }
      
      // 格式化排班数据
      const formatSchedules = (data) => {
        const formattedSchedules = []
        
        // 按咨询师和周次分组
        const groupedData = {}
        
        data.forEach(schedule => {
          // 根据日期计算周次
          const scheduleDate = new Date(schedule.date)
          const today = new Date()
          const diffTime = scheduleDate.getTime() - today.getTime()
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
          
          let week = 'current'
          if (diffDays > 7 && diffDays <= 14) {
            week = 'next'
          } else if (diffDays > 14) {
            week = 'after'
          }
          
          const key = `${schedule.counselorId}-${week}`
          if (!groupedData[key]) {
            groupedData[key] = {
              id: schedule.scheduleId,
              counselorId: schedule.counselorId,
              counselorName: schedule.counselorName || getCounselorName(schedule.counselorId),
              week: week,
              slots: {}
            }
          }
          
          // 解析日期获取星期几
          const day = scheduleDate.getDay() === 0 ? 7 : scheduleDate.getDay() // 将周日从0转为7
          
          if (!groupedData[key].slots[day]) {
            groupedData[key].slots[day] = []
          }
          
          // 添加时间段
          groupedData[key].slots[day].push(schedule.timeSlot === 'morning' ? '上午 (09:00-11:00)' : '下午 (14:00-18:00)')
        })
        
        // 转换为数组
        Object.values(groupedData).forEach(schedule => {
          formattedSchedules.push(schedule)
        })
        
        return formattedSchedules
      }
      
      // 根据咨询师ID获取咨询师姓名
      const getCounselorName = (counselorId) => {
        const counselor = counselors.value.find(c => c.counselorId === counselorId)
        return counselor ? counselor.name : '未知咨询师'
      }
      
      // 过滤排班数据
      const filteredSchedules = computed(() => {
        return schedules.value.filter(schedule => {
          const weekMatch = schedule.week === selectedWeek.value
          const counselorMatch = selectedCounselor.value === 'all' || schedule.counselorId === parseInt(selectedCounselor.value)
          return weekMatch && counselorMatch
        })
      })
      
      // 检查某天是否有排班
      const hasSlots = (schedule, day) => {
        return schedule.slots[day] && schedule.slots[day].length > 0
      }
      
      // 获取某天的时间段
      const getTimeSlots = (schedule, day) => {
        if (!schedule.slots[day]) return '休息'
        return schedule.slots[day].join('\n')
      }
      
      // 打开添加排班模态框
      const openAddScheduleModal = () => {
        resetNewSchedule()
        showAddModal.value = true
        isEditing.value = false
      }
      
      // 重置新排班数据
      const resetNewSchedule = () => {
        newSchedule.id = null
        newSchedule.counselorId = counselors.value.length > 0 ? counselors.value[0].counselorId : null
        newSchedule.counselorName = ''
        newSchedule.week = 'current'
        
        for (let i = 1; i <= 7; i++) {
          newSchedule.days[i] = false
          newSchedule.slots[i] = {morning: false, afternoon: false}
        }
      }
      
      // 关闭模态框
      const closeModal = () => {
        showAddModal.value = false
      }
      
      // 获取星期几的名称
      const getDayName = (day) => {
        const dayNames = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
        return dayNames[day]
      }
      
      // 保存排班
      const saveSchedule = async () => {
        try {
          // 收集选中的日期和时间段
          const schedulesToSave = [];
          
          for (let day = 1; day <= 7; day++) {
            if (newSchedule.days[day]) {
              schedulesToSave.push({
                counselorId: parseInt(newSchedule.counselorId),
                date: calculateDate(newSchedule.week, day),
                timeSlot: 'morning',
                status: 'available'
              });
            }
            if (newSchedule.slots[day].afternoon) {
              schedulesToSave.push({
                counselorId: parseInt(newSchedule.counselorId),
                date: calculateDate(newSchedule.week, day),
                timeSlot: 'afternoon', 
                status: 'available'
              });
            }
          }
          
          // 修改为逐个发送排班数据
          for (const schedule of schedulesToSave) {
            await axios.post('/api/schedules/create', schedule, {
              headers: {
                'Content-Type': 'application/json'
              }
            });
          }
          
          // 刷新数据
          await fetchSchedules();
          closeModal();
          alert('排班保存成功！');
        } catch (error) {
          console.error('保存排班失败:', error);
          if (error.response) {
            alert(`保存失败: ${error.response.data}`);
          } else {
            alert('网络连接异常，请重试');
          }
        }
      }
      
      // 计算日期
      const calculateDate = (week, day) => {
        const today = new Date()
        const currentDay = today.getDay() === 0 ? 7 : today.getDay()
        let daysToAdd = day - currentDay
        
        if (week === 'next') {
          daysToAdd += 7
        } else if (week === 'after') {
          daysToAdd += 14
        }
        
        const targetDate = new Date(today)
        targetDate.setDate(today.getDate() + daysToAdd)
        
        // 格式化为YYYY-MM-DD
        const year = targetDate.getFullYear()
        const month = String(targetDate.getMonth() + 1).padStart(2, '0')
        const date = String(targetDate.getDate()).padStart(2, '0')
        return `${year}-${month}-${date}`
      }
      
      // 编辑排班
      const editSchedule = (id) => {
        const schedule = schedules.value.find(s => s.id === id)
        if (!schedule) return
        
        resetNewSchedule()
        
        newSchedule.id = schedule.id
        newSchedule.counselorId = schedule.counselorId
        newSchedule.week = schedule.week
        
        // 设置已选择的日期和时间段
        Object.keys(schedule.slots).forEach(day => {
          const dayNum = parseInt(day)
          newSchedule.days[dayNum] = true
          
          schedule.slots[day].forEach(slot => {
            if (slot.includes('上午')) {
              newSchedule.slots[dayNum].morning = true
            }
            if (slot.includes('下午')) {
              newSchedule.slots[dayNum].afternoon = true
            }
          })
        })
        
        showAddModal.value = true
        isEditing.value = true
        editingId.value = id
      }
      
      // 删除排班
      const deleteSchedule = async (id) => {
        if (confirm('确定要删除这个排班吗？')) {
          try {
            // 使用正确的删除排班接口
            await axios.delete(`/api/schedules/clear/${id}`)
            await fetchSchedules()
          } catch (error) {
            console.error('删除排班失败:', error)
            alert('删除排班失败，请重试')
          }
        }
      }
      
      // 退出登录
      const logout = () => {
        store.dispatch('logout')
        router.push('/login')
      }
      
      // 页面导航
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
      
      // 页面加载时获取数据
      onMounted(async () => {
        await fetchCounselors()
        await fetchSchedules()
      })
  
      return {
        username,
        logout,
        goTo,
        selectedWeek,
        selectedCounselor,
        counselors,
        filteredSchedules,
        hasSlots,
        getTimeSlots,
        openAddScheduleModal,
        editSchedule,
        deleteSchedule,
        showAddModal,
        closeModal,
        newSchedule,
        getDayName,
        saveSchedule
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
  
  .filter-bar {
    display: flex;
    margin-bottom: 20px;
    gap: 10px;
  }
  
  .filter-select {
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
  }
  
  .action-btn {
    background-color: #28a745;
    color: white;
    border: none;
    padding: 8px 15px;
    border-radius: 4px;
    cursor: pointer;
    margin-left: auto;
  }
  
  .action-btn:hover {
    background-color: #218838;
  }
  
  .schedule-table {
    overflow-x: auto;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
  }
  
  th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: center;
  }
  
  th {
    background-color: #f2f2f2;
    font-weight: bold;
  }
  
  .has-slots {
    background-color: #e8f4ff;
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
  
  .delete-btn {
    background-color: #dc3545;
    color: white;
  }
  
  .edit-btn:hover {
    background-color: #e0a800;
  }
  
  .delete-btn:hover {
    background-color: #c82333;
  }
  
  /* 模态框样式 */
  .modal {
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    width: 80%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
    position: relative;
  }
  
  .close {
    position: absolute;
    top: 10px;
    right: 15px;
    font-size: 24px;
    cursor: pointer;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
  }
  
  .form-control {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  .day-selector {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .day-option {
    display: flex;
    align-items: center;
    gap: 5px;
  }
  
  .time-slots {
    margin: 10px 0;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
  }
  
  .btn {
    padding: 8px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .save-btn {
    background-color: #28a745;
    color: white;
  }
  
  .cancel-btn {
    background-color: #6c757d;
    color: white;
  }
  
  .save-btn:hover {
    background-color: #218838;
  }
  
  .cancel-btn:hover {
    background-color: #5a6268;
  }
  </style>
.day-time-selector {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.day-time-option {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 8px;
}

.day-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.time-options {
  margin-left: 24px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}