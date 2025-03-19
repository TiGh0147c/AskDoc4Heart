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
              <option v-for="counselor in counselors" :key="counselor.id" :value="counselor.id">
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
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed } from 'vue'
  import { useStore } from 'vuex'
  import { useRouter } from 'vue-router'
  
  export default {
    name: 'AdminSchedule',
    setup() {
      const store = useStore()
      const router = useRouter()
  
      const username = computed(() => store.getters.username)
      const selectedWeek = ref('current')
      const selectedCounselor = ref('all')
      
      // 模拟数据
      const counselors = ref([
        { id: 1, name: '张医生' },
        { id: 2, name: '李医生' },
        { id: 3, name: '王医生' }
      ])
      
      const schedules = ref([
        { 
          id: 1, 
          counselorId: 1,
          counselorName: '张医生',
          week: 'current',
          slots: {
            1: ['09:00-11:00', '14:00-16:00'],
            3: ['13:00-17:00'],
            5: ['09:00-12:00']
          }
        },
        { 
          id: 2, 
          counselorId: 2,
          counselorName: '李医生',
          week: 'current',
          slots: {
            2: ['10:00-12:00'],
            4: ['14:00-18:00'],
            6: ['09:00-11:00']
          }
        },
        { 
          id: 3, 
          counselorId: 3,
          counselorName: '王医生',
          week: 'next',
          slots: {
            1: ['13:00-17:00'],
            5: ['09:00-12:00'],
            7: ['14:00-16:00']
          }
        }
      ])
      
      const filteredSchedules = computed(() => {
        return schedules.value.filter(schedule => {
          const weekMatch = schedule.week === selectedWeek.value
          const counselorMatch = selectedCounselor.value === 'all' || schedule.counselorId === parseInt(selectedCounselor.value)
          return weekMatch && counselorMatch
        })
      })
      
      const hasSlots = (schedule, day) => {
        return schedule.slots[day] && schedule.slots[day].length > 0
      }
      
      const getTimeSlots = (schedule, day) => {
        if (!schedule.slots[day]) return '休息'
        return schedule.slots[day].join('\n')
      }
      
      const openAddScheduleModal = () => {
        // 实际应用中，这里可以打开一个模态框来添加排班
        console.log('打开添加排班模态框')
      }
      
      const editSchedule = (id) => {
        console.log('编辑排班', id)
      }
      
      const deleteSchedule = (id) => {
        console.log('删除排班', id)
        // 实际应用中，这里应该先确认，然后再删除
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
        selectedWeek,
        selectedCounselor,
        counselors,
        filteredSchedules,
        hasSlots,
        getTimeSlots,
        openAddScheduleModal,
        editSchedule,
        deleteSchedule
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
  </style>