<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item" @click="goTo('tutorial')">教程</div>
      <div class="sidebar-item" @click="goTo('appointment')">预约</div>
      <div class="sidebar-item active" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('currentChat')">当前对话</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>个人设置</h1>
        
        <div v-if="loading" class="loading">加载中...</div>
        
        <template v-else>
          <div class="info-section">
            <h2>基本信息</h2>
            <div class="avatar-section">
              <span class="info-label">头像：</span>
              <div class="avatar-container">
                <img :src="'/basic_avatar/basic_male.jpg'" alt="用户头像" class="avatar-image" />
                <input v-if="editing" type="file" @change="handleAvatarChange" accept="image/*" class="avatar-input" />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">昵称：</span>
              <span v-if="!editing" class="info-value">{{ userInfo.nickname || '未设置' }}</span>
              <input v-else type="text" v-model="editForm.nickname" class="edit-input">
            </div>
            <div class="info-item">
              <span class="info-label">性别：</span>
              <span v-if="!editing" class="info-value">{{ formatGender(userInfo.gender) }}</span>
              <select v-else v-model="editForm.gender" class="edit-input">
                <option value="male">男</option>
                <option value="female">女</option>
                <option value="other">其他</option>
                <option value="unknown">不愿透露</option>
              </select>
            </div>
            <div class="info-item">
              <span class="info-label">出生日期：</span>
              <span v-if="!editing" class="info-value">{{ userInfo.birthday || '未设置' }}</span>
              <input v-else type="date" v-model="editForm.birthday" class="edit-input">
            </div>
            <div class="info-item">
              <span class="info-label">职业：</span>
              <span v-if="!editing" class="info-value">{{ userInfo.occupation || '未设置' }}</span>
              <input v-else type="text" v-model="editForm.occupation" class="edit-input">
            </div>
          </div>
          
          <div class="info-section">
            <h2>联系方式</h2>
            <div class="info-item">
              <span class="info-label">手机号：</span>
              <span class="info-value">{{ userInfo.phoneNumber || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱：</span>
              <span v-if="!editing" class="info-value">{{ userInfo.email || '未设置' }}</span>
              <input v-else type="email" v-model="editForm.email" class="edit-input">
            </div>
          </div>
          
          <div class="info-section">
            <h2>账号安全</h2>
            <div class="info-item">
              <span class="info-label">密码：</span>
              <span v-if="!editing" class="info-value">********</span>
              <input v-else type="password" v-model="editForm.password" placeholder="输入新密码" class="edit-input">
            </div>
          </div>
          
          <div v-if="auditRecords.length > 0" class="info-section">
            <h2>审核记录</h2>
            <div v-for="(record, index) in auditRecords" :key="index" class="audit-record">
              <div class="audit-field">{{ formatField(record.modifyField) }}：{{ record.newValue }}</div>
              <div class="audit-status" :class="record.auditStatus">
                {{ formatStatus(record.auditStatus) }}
              </div>
            </div>
          </div>
          
          <div class="button-group">
            <button v-if="!editing" class="edit-btn" @click="startEditing">修改信息</button>
            <template v-else>
              <button class="save-btn" @click="saveChanges" :disabled="saving">
                {{ saving ? '保存中...' : '保存' }}
              </button>
              <button class="cancel-btn" @click="cancelEditing" :disabled="saving">取消</button>
            </template>
          </div>
          
          <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'UserSettings',
  setup() {
    const router = useRouter()
    const editing = ref(false)
    const loading = ref(true)
    const saving = ref(false)
    const errorMessage = ref('')
    const avatarFile = ref(null)
    
    // 用户信息
    const userInfo = reactive({
      userId: null,
      nickname: '',
      gender: '',
      birthday: '',
      email: '',
      occupation: '',
      phoneNumber: '',
      avatar: null
    })
    
    // 编辑表单
    const editForm = reactive({
      userId: null,
      nickname: '',
      gender: '',
      birthday: '',
      email: '',
      occupation: '',
      password: ''
    })
    
    // 审核记录
    const auditRecords = ref([])
    
    // 开始编辑
    const startEditing = () => {
      editForm.userId = userInfo.userId
      editForm.nickname = userInfo.nickname || ''
      editForm.gender = userInfo.gender || 'unknown'
      editForm.birthday = userInfo.birthday || ''
      editForm.email = userInfo.email || ''
      editForm.occupation = userInfo.occupation || ''
      editForm.password = ''
      editing.value = true
      errorMessage.value = ''
    }
    
    // 取消编辑
    const cancelEditing = () => {
      editing.value = false
      avatarFile.value = null
      errorMessage.value = ''
    }
    
    // 处理头像上传
    const handleAvatarChange = (event) => {
      const file = event.target.files[0]
      if (file) {
        avatarFile.value = file
        // 预览头像
        const reader = new FileReader()
        reader.onload = (e) => {
          userInfo.avatar = e.target.result
        }
        reader.readAsDataURL(file)
      }
    }
    
    // 从localStorage获取用户信息 - 修改为与UserHome.vue一致的方式
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '用户')
    const userId = ref(userData?.user_id || 1)
    
    // 获取用户信息 - 修改后的fetchUserInfo方法
    const fetchUserInfo = async () => {
      try {
        loading.value = true
        const response = await axios.get(
          '/api/profile-management/user/profile',
          { params: { userId: userId.value } }
        )
        
        if (response.status === 200 && response.data) {
          Object.assign(userInfo, {
            userId: userId.value,
            nickname: response.data.nickname || '',
            gender: response.data.gender || 'unknown',
            birthday: response.data.birthday ? 
              new Date(response.data.birthday).toISOString().split('T')[0] : '',
            email: response.data.email || '',
            occupation: response.data.occupation || '',
            phoneNumber: response.data.phoneNumber || '',
            avatar: '/basic_avatar/basic_male.jpg' // 始终使用静态头像
          })
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        errorMessage.value = '获取用户信息失败，请刷新页面重试'
      } finally {
        loading.value = false
      }
    }
    
    // 保存用户信息修改 - 修改后的saveChanges方法
    const saveChanges = async () => {
      saving.value = true
      errorMessage.value = ''
      
      try {
        const formData = new FormData()
        
        // 添加用户信息到FormData
        formData.append('userId', userId.value)
        if (editForm.nickname) formData.append('nickname', editForm.nickname)
        if (editForm.gender) formData.append('gender', editForm.gender)
        if (editForm.birthday) formData.append('birthday', editForm.birthday)
        if (editForm.email) formData.append('email', editForm.email)
        if (editForm.occupation) formData.append('occupation', editForm.occupation)
        if (editForm.password) formData.append('password', editForm.password)
        
        // 添加头像文件
        if (avatarFile.value) {
          formData.append('avatarFile', avatarFile.value)
        }
        
        const response = await axios.post(
          '/api/profile-management/user/modification',
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        )
        
        if (response.status === 200) {
          // 更新本地存储的用户信息
          const updatedUser = {
            ...userData,
            username: editForm.nickname || userData.username
          }
          localStorage.setItem('user', JSON.stringify(updatedUser))
          
          // 更新用户信息和审核记录
          await fetchUserInfo()
          await fetchAuditRecords()
          editing.value = false
          avatarFile.value = null
          alert('个人信息已更新，部分信息可能需要审核通过后才能显示')
        }
      } catch (error) {
        console.error('保存失败:', error)
        errorMessage.value = error.response?.data || '保存失败，请稍后重试'
      } finally {
        saving.value = false
      }
    }
    
    // 格式化性别显示
    const formatGender = (gender) => {
      const genderMap = {
        male: '男',
        female: '女',
        other: '其他',
        unknown: '未设置'
      }
      return genderMap[gender] || '未设置'
    }
    
    // 格式化字段名称
    const formatField = (field) => {
      const fieldMap = {
        nickname: '昵称',
        avatar: '头像',
        gender: '性别',
        birthday: '出生日期',
        email: '邮箱',
        occupation: '职业',
        password: '密码'
      }
      return fieldMap[field] || field
    }
    
    // 格式化审核状态
    const formatStatus = (status) => {
      const statusMap = {
        pending: '审核中',
        approved: '已通过',
        rejected: '已拒绝'
      }
      return statusMap[status] || status
    }
    
    // 获取用户审核记录
    const fetchAuditRecords = async () => {
      try {
        const response = await axios.get(
          '/api/profile-management/user/modification-audits', 
          {
            params: { 
              userId: userId.value,  // 使用userId.value获取实际值
            }
          }
        )
        
        if (response.status === 200) {
          auditRecords.value = response.data
        }
      } catch (error) {
        console.error('获取审核记录失败:', error)
        errorMessage.value = '获取审核记录失败，请稍后重试'  // 添加错误提示
      }
    }
    
    // 退出登录
    const logout = () => {
      // 实际应用中应调用登出API
      localStorage.removeItem('userId')
      router.push('/login')
    }

    // 页面导航
    const goTo = (path) => {
      const paths = {
        home: '/user/home',
        tutorial: '/user/tutorial',
        appointment: '/user/appointment',
        settings: '/user/settings',
        history: '/user/history',
        currentChat: '/user/currentChat'
      }
      const targetPath = paths[path]
      if (targetPath) {
        router.push(targetPath)
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchUserInfo()
      fetchAuditRecords()
    })

    return {
      userInfo,
      editing,
      editForm,
      loading,
      saving,
      errorMessage,
      auditRecords,
      startEditing,
      cancelEditing,
      saveChanges,
      handleAvatarChange,
      formatGender,
      formatField,
      formatStatus,
      logout,
      goTo
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

.info-section {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.info-section h2 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
  font-size: 1.2rem;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.info-label {
  font-weight: bold;
  width: 100px;
  color: #555;
}

.info-value {
  color: #333;
}

.edit-input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  max-width: 300px;
}

.button-group {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.edit-btn, .save-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.edit-btn:hover, .save-btn:hover {
  background-color: #0056b3;
}

.cancel-btn {
  background-color: #f0f0f0;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.loading {
  text-align: center;
  padding: 20px;
  font-size: 1.2rem;
  color: #666;
}

.error-message {
  margin-top: 15px;
  color: #dc3545;
  padding: 10px;
  background-color: #f8d7da;
  border-radius: 4px;
}

.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ddd;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 0.9rem;
  border: 2px solid #ddd;
}

.avatar-input {
  max-width: 200px;
}

.audit-record {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 5px;
}

.audit-status {
  font-weight: bold;
}

.audit-status.pending {
  color: #ffc107;
}

.audit-status.approved {
  color: #28a745;
}

.audit-status.rejected {
  color: #dc3545;
}
</style>