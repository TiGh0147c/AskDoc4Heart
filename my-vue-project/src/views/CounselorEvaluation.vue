<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item active" @click="goTo('evaluation')">用户评估</div>
      <div class="sidebar-item" @click="goTo('help')">督导求助</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>用户评估</h1>
        <p>这里显示用户对您的评价和反馈。</p>
        
        <div class="stats-container">
          <div class="stat-card">
            <div class="stat-value">{{ averageRating.toFixed(1) }}</div>
            <div class="stat-label">平均评分</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ totalReviews }}</div>
            <div class="stat-label">总评价数</div>
          </div>
        </div>

        <div class="filter-controls">
          <div class="filter-group">
            <label for="rating-filter">评分筛选：</label>
            <select id="rating-filter" v-model="ratingFilter">
              <option value="all">所有评分</option>
              <option v-for="n in 5" :key="n" :value="n">{{ n }} 星</option>
            </select>
          </div>
          <div class="filter-group">
            <label for="date-filter">日期筛选：</label>
            <select id="date-filter" v-model="dateFilter">
              <option value="all">所有时间</option>
              <option value="week">最近一周</option>
              <option value="month">最近一个月</option>
              <option value="quarter">最近三个月</option>
            </select>
          </div>
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchTerm" 
              placeholder="搜索评价内容..."
              @input="applyFilters"
            >
          </div>
        </div>

        <div v-if="isLoading" class="loading">
          加载中...
        </div>
        
        <div v-else-if="filteredReviews.length === 0" class="no-reviews">
          <p>暂无符合条件的评价</p>
        </div>
        
        <div v-else class="reviews-container">
          <div v-for="review in paginatedReviews" :key="review.id" class="review-card">
            <div class="review-header">
              <!-- 移除用户头像和名字显示 -->
              <div class="review-rating">
                <span 
                  v-for="star in 5" 
                  :key="star" 
                  :class="['star', { filled: star <= review.rating }]"
                >★</span>
                <span class="rating-date">{{ formatDate(review.date) }}</span>
              </div>
            </div>
            
            <div class="review-content">
              <p v-if="review.evaluationContent && review.evaluationContent.trim() !== '' && review.evaluationContent !== '无评价内容'">{{ review.evaluationContent }}</p>
              <p v-else class="no-comment">用户未留下具体评价</p>
            </div>
            
            <div class="session-info">
              <span>咨询类型: {{ review.consultationType || "心理咨询" }}</span>
              <span>咨询ID: {{ review.sessionId }}</span>
            </div>
          </div>
        </div>
        
        <div class="pagination">
          <button 
            :disabled="currentPage === 1" 
            @click="currentPage--"
            class="page-btn"
          >上一页</button>
          
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          
          <button 
            :disabled="currentPage === totalPages" 
            @click="currentPage++"
            class="page-btn"
          >下一页</button>
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
  name: 'CounselorEvaluation',
  setup() {
    const store = useStore()
    const router = useRouter()

    const username = computed(() => store.getters.username)
    
    // 从localStorage获取counselorId，参考CounselorHome.vue的实现方式
    const userData = JSON.parse(localStorage.getItem('user'))
    const counselorId = ref(localStorage.getItem('counselor_id') || userData?.counselorId || userData?.userId || null)
    
    const isLoading = ref(true)
    const allReviews = ref([])
    const ratingFilter = ref('all')
    const dateFilter = ref('all')
    const searchTerm = ref('')
    const currentPage = ref(1)
    const itemsPerPage = 5 // 每页显示5条评价
    const averageRatingValue = ref(0)
    const totalReviewsCount = ref(0)

    // 从后端获取评价数据
    const fetchReviews = async () => {
      isLoading.value = true
      
      try {
        // 使用新创建的接口获取评价数据和平均分
        const response = await axios.get(`/api/evaluation/counselor/${counselorId.value}`)
        console.log('API返回数据:', response.data) // 添加日志查看返回数据
        
        if (response.data) {
          // 设置平均分和评价总数
          averageRatingValue.value = response.data.averageRating || 0
          totalReviewsCount.value = response.data.evaluationCount || 0 // 使用后端返回的 evaluationCount
          
          // 直接使用后端返回的数据格式，不做转换
          allReviews.value = response.data.evaluations.map(item => {
            console.log('处理评价项:', item) // 添加日志查看每个评价项
            return {
              id: item.evaluationId,
              rating: item.rating,
              evaluationContent: item.evaluation_content, // 使用后端返回的 evaluation_content
              date: item.evaluation_time ? new Date(item.evaluation_time.replace(' ', 'T')) : new Date(), // 使用后端返回的 evaluation_time
              consultationType: item.consultationType || "心理咨询",
              sessionId: item.sessionId
            }
          })
        }
      } catch (error) {
        console.error('获取评价数据失败', error)
      } finally {
        isLoading.value = false
      }
    }
    
    // 过滤评价
    const applyFilters = () => {
      currentPage.value = 1 // 重置到第一页
    }
    
    // 根据筛选条件过滤评价
    const filteredReviews = computed(() => {
      let result = [...allReviews.value]
      console.log('所有评价:', result) // 添加日志查看所有评价
      
      // 评分筛选
      if (ratingFilter.value !== 'all') {
        result = result.filter(review => review.rating === parseInt(ratingFilter.value))
      }
      
      // 日期筛选
      if (dateFilter.value !== 'all') {
        const now = new Date()
        let cutoffDate = new Date()
        
        switch (dateFilter.value) {
          case 'week':
            cutoffDate.setDate(now.getDate() - 7)
            break
          case 'month':
            cutoffDate.setMonth(now.getMonth() - 1)
            break
          case 'quarter':
            cutoffDate.setMonth(now.getMonth() - 3)
            break
        }
        
        result = result.filter(review => new Date(review.date) >= cutoffDate)
      }
      
      // 搜索筛选 - 只搜索评价内容
      if (searchTerm.value.trim()) {
        const term = searchTerm.value.toLowerCase()
        result = result.filter(review => 
          (review.evaluationContent && review.evaluationContent.toLowerCase().includes(term))
        )
      }
      
      return result
    })
    
    // 分页处理
    const paginatedReviews = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return filteredReviews.value.slice(start, end)
    })
    
    // 计算总页数
    const totalPages = computed(() => {
      return Math.ceil(filteredReviews.value.length / itemsPerPage) || 1
    })
    
    // 计算平均评分 - 使用后端返回的数据
    const averageRating = computed(() => {
      return averageRatingValue.value
    })
    
    // 总评价数 - 使用后端返回的数据
    const totalReviews = computed(() => {
      return totalReviewsCount.value
    })
    
    // 格式化日期
    const formatDate = (date) => {
      if (!(date instanceof Date) || isNaN(date)) {
        return '日期未知'
      }
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    }
    
    // 监听筛选条件变化
    watch([ratingFilter, dateFilter], () => {
      applyFilters()
    })
    
    onMounted(() => {
      fetchReviews()
    })

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
        case 'help':
          router.push('/counselor/help')
          break
        default:
          console.error('Invalid path')
      }
    }

    return {
      username,
      logout,
      goTo,
      isLoading,
      paginatedReviews,
      filteredReviews, // 确保返回filteredReviews
      ratingFilter,
      dateFilter,
      searchTerm,
      currentPage,
      totalPages,
      applyFilters,
      averageRating,
      totalReviews,
      formatDate
    }
  }
}
</script>

<style scoped>
/* 保留原有样式 */
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

/* 新增样式 */
.stats-container {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.stat-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  flex: 1;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #007bff;
}

.stat-label {
  color: #6c757d;
  margin-top: 5px;
}

.filter-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 5px;
}

.filter-group label {
  font-size: 0.9rem;
  color: #555;
}

.filter-group select {
  padding: 6px 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
  background-color: #fff;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.search-box input {
  width: 100%;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.no-reviews {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-weight: bold;
}

.review-rating {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.star {
  color: #ddd;
  font-size: 1.2rem;
}

.star.filled {
  color: #ffc107;
}

.rating-date {
  font-size: 0.8rem;
  color: #999;
  margin-top: 5px;
}

.review-content {
  margin: 10px 0;
  line-height: 1.5;
}

.no-comment {
  color: #999;
  font-style: italic;
}

.session-info {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #666;
  border-top: 1px solid #eee;
  padding-top: 10px;
  margin-top: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.page-btn {
  background-color: #f0f0f0;
  border: none;
  padding: 5px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}
</style>