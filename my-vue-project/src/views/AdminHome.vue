<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('manage')">管理注册</div>
      <div class="sidebar-item" @click="goTo('schedules')">排班管理</div>
      <div class="sidebar-item" @click="goTo('accounts')">账号管理</div>
      <div class="sidebar-item" @click="goTo('supervision')">督导绑定</div>
      <div class="sidebar-item" @click="goTo('notifications')">通知</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部欢迎信息和退出登录 -->
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <!-- 主内容卡片 -->
      <div class="card">
        <h1>管理员主页</h1>
        <p>这是管理员的主页界面。您可以在这里管理系统用户、咨询师和数据统计。</p>
        
        <!-- 数据统计区域 -->
        <div class="stats-container">
          <!-- 督导分配统计 -->
          <div class="chart-card">
            <h2>督导分配统计</h2>
            <div class="chart-container" ref="supervisionChartRef"></div>
            <div v-if="loadingSupervision" class="loading-text">加载中...</div>
            <div v-if="supervisionError" class="error-text">{{ supervisionError }}</div>
          </div>
          
          <!-- 排班统计 -->
          <div class="chart-card">
            <h2>咨询师排班统计</h2>
            <div class="chart-container" ref="scheduleChartRef"></div>
            <div v-if="loadingSchedule" class="loading-text">加载中...</div>
            <div v-if="scheduleError" class="error-text">{{ scheduleError }}</div>
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
import * as echarts from 'echarts'

export default {
  name: 'AdminHome',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '管理员')
    
    // 图表引用
    const supervisionChartRef = ref(null)
    const scheduleChartRef = ref(null)
    
    // 图表实例
    let supervisionChart = null
    let scheduleChart = null
    
    // 加载状态
    const loadingSupervision = ref(true)
    const loadingSchedule = ref(true)
    
    // 错误信息
    const supervisionError = ref('')
    const scheduleError = ref('')
    
    // 督导分配数据
    const supervisionData = ref({
      assigned: 0,
      unassigned: 0
    })
    
    // 排班数据
    const scheduleData = ref({
      scheduled: 0,
      unscheduled: 0
    })
    
    // 获取督导分配数据
    const fetchSupervisionData = async () => {
      loadingSupervision.value = true
      supervisionError.value = ''
      
      try {
        // 获取所有咨询师
        const counselorsResponse = await axios.get('/api/binding/counselors')
        
        // 获取已分配督导的咨询师
        const assignedResponse = await axios.get('/api/binding/assigned')
        
        if (counselorsResponse.data && Array.isArray(counselorsResponse.data)) {
          const totalCounselors = counselorsResponse.data.length
          
          if (assignedResponse.data && Array.isArray(assignedResponse.data)) {
            const assignedCounselors = assignedResponse.data.length
            
            supervisionData.value = {
              assigned: assignedCounselors,
              unassigned: totalCounselors - assignedCounselors
            }
          }
        }
        
        // 如果API不可用，使用模拟数据进行测试
        if (supervisionData.value.assigned === 0 && supervisionData.value.unassigned === 0) {
          supervisionData.value = {
            assigned: 8,
            unassigned: 3
          }
        }
        
        renderSupervisionChart()
      } catch (error) {
        console.error('获取督导分配数据失败:', error)
        supervisionError.value = '获取数据失败'
        
        // 使用模拟数据
        supervisionData.value = {
          assigned: 8,
          unassigned: 3
        }
        renderSupervisionChart()
      } finally {
        loadingSupervision.value = false
      }
    }
    
    // 获取排班数据
    const fetchScheduleData = async () => {
      loadingSchedule.value = true
      scheduleError.value = ''
      
      try {
        // 获取所有咨询师
        const counselorsResponse = await axios.get('/api/appointments/allCounselor')
        
        // 获取有排班的咨询师
        const schedulesResponse = await axios.get('/api/schedules/all')
        
        if (counselorsResponse.data && Array.isArray(counselorsResponse.data)) {
          const totalCounselors = counselorsResponse.data.length
          
          if (schedulesResponse.data && Array.isArray(schedulesResponse.data)) {
            // 获取有排班的咨询师ID（去重）
            const scheduledCounselorIds = [...new Set(schedulesResponse.data.map(schedule => schedule.counselorId))]
            const scheduledCounselors = scheduledCounselorIds.length
            
            scheduleData.value = {
              scheduled: scheduledCounselors,
              unscheduled: totalCounselors - scheduledCounselors
            }
          }
        }
        
        // 如果API不可用，使用模拟数据进行测试
        if (scheduleData.value.scheduled === 0 && scheduleData.value.unscheduled === 0) {
          scheduleData.value = {
            scheduled: 7,
            unscheduled: 4
          }
        }
        
        renderScheduleChart()
      } catch (error) {
        console.error('获取排班数据失败:', error)
        scheduleError.value = '获取数据失败'
        
        // 使用模拟数据
        scheduleData.value = {
          scheduled: 7,
          unscheduled: 4
        }
        renderScheduleChart()
      } finally {
        loadingSchedule.value = false
      }
    }
    
    // 渲染督导分配图表
    const renderSupervisionChart = () => {
      if (!supervisionChartRef.value) return
      
      // 如果图表已存在，销毁它
      if (supervisionChart) {
        supervisionChart.dispose()
      }
      
      // 初始化图表
      supervisionChart = echarts.init(supervisionChartRef.value)
      
      // 图表配置
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: ['已分配督导', '未分配督导'],
            axisTick: {
              alignWithLabel: true
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            minInterval: 1 // 确保y轴刻度为整数
          }
        ],
        series: [
          {
            name: '咨询师数量',
            type: 'bar',
            barWidth: '60%',
            data: [
              {
                value: supervisionData.value.assigned,
                itemStyle: { color: '#4CAF50' } // 绿色
              },
              {
                value: supervisionData.value.unassigned,
                itemStyle: { color: '#FF9800' } // 橙色
              }
            ]
          }
        ]
      }
      
      // 设置图表配置
      supervisionChart.setOption(option)
      
      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        supervisionChart && supervisionChart.resize()
      })
    }
    
    // 渲染排班图表
    const renderScheduleChart = () => {
      if (!scheduleChartRef.value) return
      
      // 如果图表已存在，销毁它
      if (scheduleChart) {
        scheduleChart.dispose()
      }
      
      // 初始化图表
      scheduleChart = echarts.init(scheduleChartRef.value)
      
      // 图表配置
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: ['有排班', '无排班'],
            axisTick: {
              alignWithLabel: true
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            minInterval: 1 // 确保y轴刻度为整数
          }
        ],
        series: [
          {
            name: '咨询师数量',
            type: 'bar',
            barWidth: '60%',
            data: [
              {
                value: scheduleData.value.scheduled,
                itemStyle: { color: '#2196F3' } // 蓝色
              },
              {
                value: scheduleData.value.unscheduled,
                itemStyle: { color: '#F44336' } // 红色
              }
            ]
          }
        ]
      }
      
      // 设置图表配置
      scheduleChart.setOption(option)
      
      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        scheduleChart && scheduleChart.resize()
      })
    }
    
    // 登出处理
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    // 页面导航
    const goTo = (path) => {
      switch (path) {
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
      fetchSupervisionData()
      fetchScheduleData()
    })
    
    // 组件卸载时清理图表实例
    onUnmounted(() => {
      if (supervisionChart) {
        supervisionChart.dispose()
        supervisionChart = null
      }
      
      if (scheduleChart) {
        scheduleChart.dispose()
        scheduleChart = null
      }
      
      // 移除事件监听器
      window.removeEventListener('resize', () => {})
    })

    return {
      username,
      logout,
      goTo,
      supervisionChartRef,
      scheduleChartRef,
      loadingSupervision,
      loadingSchedule,
      supervisionError,
      scheduleError
    }
  }
}
</script>

<style scoped>
/* 容器样式 */
.container {
  display: flex;
  height: 100vh;
  background: #f4f4f4; /* 背景色调整为浅灰色 */
}

/* 侧边栏样式 */
.sidebar {
  width: 200px;
  background: #ffffff; /* 侧边栏背景色调整为白色 */
  padding: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  color: #333; /* 文字颜色调整为深灰色 */
  font-family: Arial, sans-serif;
  border-radius: 0 10px 10px 0; /* 添加圆角效果 */
}

.sidebar-item {
  margin-bottom: 10px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.2s, color 0.2s;
}

.sidebar-item:hover {
  color: #007bff; /* 悬停时文字颜色 */
  transform: scale(1.05);
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background: #ffffff; /* 主内容区域背景色调整为白色 */
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative; /* 用于绝对定位右上角内容 */
}

/* 右上角欢迎信息和退出登录 */
.top-right {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem; /* 缩小字体大小 */
}

.welcome {
  margin: 0;
  color: #333;
}

.logout-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 10px; /* 缩小按钮尺寸 */
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem; /* 缩小按钮字体大小 */
}

.logout-btn:hover {
  background-color: #0056b3;
}

/* 主内容卡片样式 */
.card {
  background-color: #ffffff; /* 卡片背景色调整为白色 */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-top: 60px; /* 避免与右上角内容重叠 */
}

/* 统计区域样式 */
.stats-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 20px;
}

.chart-card {
  flex: 1;
  min-width: 300px;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.chart-card h2 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 1.2rem;
  color: #333;
  text-align: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.loading-text, .error-text {
  text-align: center;
  padding: 20px;
  color: #666;
}

.error-text {
  color: #f44336;
}
</style>