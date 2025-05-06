<template>
  <div class="container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" @click="goTo('home')">返回主页</div>
      <div class="sidebar-item active" @click="goTo('settings')">设置</div>
      <div class="sidebar-item" @click="goTo('requests')">用户申请</div>
      <div class="sidebar-item" @click="goTo('chat')">咨询窗口</div>
      <div class="sidebar-item" @click="goTo('schedule')">排班表</div>
      <div class="sidebar-item" @click="goTo('history')">历史会话</div>
      <div class="sidebar-item" @click="goTo('evaluation')">用户评估</div>
      <div class="sidebar-item" @click="goTo('help')">督导求助</div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ username }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>咨询师设置</h1>
        
        <div v-if="loading" class="loading">加载中...</div>
        
        <template v-else>
          <div class="info-section">
            <h2>基本信息</h2>
            <div class="avatar-section">
              <span class="info-label">头像：</span>
              <div class="avatar-container">
                <img v-if="counselorInfo.avatar" :src="counselorInfo.avatar" alt="咨询师头像" class="avatar-image" />
                <div v-else class="avatar-placeholder">无头像</div>
                <input v-if="editing" type="file" @change="handleAvatarChange" accept="image/*" class="avatar-input" />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">姓名：</span>
              <span class="info-value">{{ counselorInfo.name }}</span>
              <!-- 姓名不可修改 -->
            </div>
            <div class="info-item">
              <span class="info-label">性别：</span>
              <span v-if="!editing" class="info-value">{{ counselorInfo.gender === 'male' ? '男' : counselorInfo.gender === 'female' ? '女' : counselorInfo.gender === 'other' ? '其他' : '未设置' }}</span>
              <select v-else v-model="editForm.gender" class="edit-input">
                <option value="male">男</option>
                <option value="female">女</option>
                <option value="other">其他</option>
                <option value="unknown">不愿透露</option>
              </select>
            </div>
            <div class="info-item">
              <span class="info-label">状态：</span>
              <span v-if="!editing" class="info-value">{{ formatStatus(counselorInfo.status) }}</span>
              <select v-else v-model="editForm.status" class="edit-input">
                <option value="available">在线</option>
                <option value="unavailable">离线</option>
              </select>
            </div>
            <div class="info-item">
              <span class="info-label">专业领域：</span>
              <span v-if="!editing" class="info-value">{{ counselorInfo.expertise_area || '未设置' }}</span>
              <input v-else type="text" v-model="editForm.expertiseArea" class="edit-input">
            </div>
            <div class="info-item">
              <span class="info-label">督导身份：</span>
              <span class="info-value">{{ counselorInfo.is_supervisor ? '是' : '否' }}</span>
              <!-- 督导身份不可修改 -->
            </div>
            <div class="info-item">
              <span class="info-label">值班状态：</span>
              <span v-if="!editing" class="info-value">{{ counselorInfo.on_duty ? '值班中' : '非值班' }}</span>
              <select v-else v-model="editForm.onDuty" class="edit-input">
                <option :value="true">值班中</option>
                <option :value="false">非值班</option>
              </select>
            </div>
          </div>
          
          <div class="info-section">
            <h2>联系方式</h2>
            <div class="info-item">
              <span class="info-label">手机号：</span>
              <span class="info-value">{{ counselorInfo.phone_number || '未设置' }}</span>
              <!-- 手机号不可修改 -->
            </div>
            <div class="info-item">
              <span class="info-label">邮箱：</span>
              <span v-if="!editing" class="info-value">{{ counselorInfo.email || '未设置' }}</span>
              <input v-else type="email" v-model="editForm.email" class="edit-input">
            </div>
          </div>
          
          <div class="info-section">
            <h2>资质证书</h2>
            <div class="info-item certificate-item">
              <span class="info-label">证书信息：</span>
              <span v-if="!editing" class="info-value">{{ counselorInfo.counselor_certificate || '未上传' }}</span>
              <textarea v-else v-model="editForm.counselorCertificate" class="edit-input certificate-input"></textarea>
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
  name: 'CounselorSettings',
  setup() {
    const router = useRouter()
    const editing = ref(false)
    const loading = ref(true)
    const saving = ref(false)
    const errorMessage = ref('')
    const avatarFile = ref(null)
    
    // 从localStorage获取用户信息
    const userData = JSON.parse(localStorage.getItem('user'))
    const username = ref(userData?.username || '咨询师')
    
    // 获取存储在localStorage中的counselorId
    const counselorId = localStorage.getItem('counselor_id') || userData?.counselorId || 1
    
    // 咨询师信息
    const counselorInfo = reactive({
      counselor_id: counselorId,
      name: '',
      avatar: '',
      phone_number: '',
      email: '',
      gender: '',
      counselor_certificate: '',
      is_supervisor: false,
      status: '',
      expertise_area: '',
      on_duty: false
    })
    
    // 编辑表单
    const editForm = reactive({
      counselorId: counselorInfo.counselor_id,
      // 注意：name 和 phone_number 在 DTO 中被注释掉了，表示不允许修改
      email: '',
      gender: '',
      counselorCertificate: '',
      status: '',
      expertiseArea: '',
      onDuty: false,
      password: ''
    })
    
    // 开始编辑
    const startEditing = () => {
      editForm.counselorId = counselorInfo.counselor_id
      editForm.email = counselorInfo.email || ''
      editForm.gender = counselorInfo.gender || ''
      editForm.counselorCertificate = counselorInfo.counselor_certificate || ''
      editForm.status = counselorInfo.status
      editForm.expertiseArea = counselorInfo.expertise_area || ''
      editForm.onDuty = counselorInfo.on_duty
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
          counselorInfo.avatar = e.target.result
        }
        reader.readAsDataURL(file)
      }
    }
    
    // 获取咨询师信息
    const fetchCounselorInfo = async () => {
      try {
        loading.value = true
        // 使用从localStorage获取的counselorId
        const counselorId = counselorInfo.counselor_id
        
        console.log('正在获取咨询师信息，使用ID:', counselorId)
        
        const response = await axios.get(
          '/api/profile-management/counselor/profile',
          { params: { counselorId } }
        )
        
        if (response.status === 200 && response.data) {
          const data = response.data
          counselorInfo.name = data.name
          counselorInfo.avatar = data.avatar
          counselorInfo.phone_number = data.phoneNumber
          counselorInfo.email = data.email
          counselorInfo.gender = data.gender
          counselorInfo.counselor_certificate = data.counselorCertificate
          counselorInfo.is_supervisor = data.isSupervisor
          counselorInfo.status = data.status
          counselorInfo.expertise_area = data.expertiseArea
          counselorInfo.on_duty = data.onDuty
        }
      } catch (error) {
        console.error('获取咨询师信息失败:', error)
        errorMessage.value = '获取咨询师信息失败，请刷新页面重试'
      } finally {
        loading.value = false
      }
    }
    
    // 保存更改
    const saveChanges = async () => {
      saving.value = true
      errorMessage.value = ''
      
      try {
        const formData = new FormData()
        
        // 添加咨询师信息到FormData
        formData.append('counselorId', editForm.counselorId)
        
        // 只添加允许修改的字段
        if (editForm.email) formData.append('email', editForm.email)
        if (editForm.gender) formData.append('gender', editForm.gender)
        if (editForm.counselorCertificate) formData.append('counselorCertificate', editForm.counselorCertificate)
        if (editForm.status) formData.append('status', editForm.status)
        if (editForm.expertiseArea) formData.append('expertiseArea', editForm.expertiseArea)
        formData.append('onDuty', editForm.onDuty)
        if (editForm.password) formData.append('password', editForm.password)
        
        // 添加头像文件
        if (avatarFile.value) {
          formData.append('avatarFile', avatarFile.value)
        }
        
        const response = await axios.post(
          '/api/profile-management/counselor/modification',
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        )
        
        if (response.status === 200) {
          await fetchCounselorInfo()
          editing.value = false
          avatarFile.value = null
          alert('咨询师信息已更新')
        }
      } catch (error) {
        console.error('保存失败:', error)
        errorMessage.value = error.response?.data || '保存失败，请稍后重试'
      } finally {
        saving.value = false
      }
    }
    
    // 格式化状态显示
    const formatStatus = (status) => {
      const statusMap = {
        'available': '在线',
        'unavailable': '离线'
      }
      return statusMap[status] || '未设置'
    }
    
    // 退出登录
    const logout = () => {
      localStorage.removeItem('user')
      router.push('/login')
    }

    // 页面导航
    const goTo = (path) => {
      const paths = {
        home: '/counselor/home',
        settings: '/counselor/settings',
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
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      fetchCounselorInfo()
    })

    return {
      username,
      counselorInfo,
      editing,
      editForm,
      loading,
      saving,
      errorMessage,
      startEditing,
      cancelEditing,
      saveChanges,
      handleAvatarChange,
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

.loading {
  text-align: center;
  padding: 20px;
  font-size: 1.2rem;
  color: #666;
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

.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  margin-bottom: 10px;
}

.avatar-input {
  margin-top: 5px;
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

.certificate-input {
  height: 100px;
  resize: vertical;
}

.button-group {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.edit-btn, .save-btn, .cancel-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.edit-btn {
  background-color: #007bff;
  color: white;
}

.save-btn {
  background-color: #28a745;
  color: white;
}

.cancel-btn {
  background-color: #dc3545;
  color: white;
}

.edit-btn:hover {
  background-color: #0056b3;
}

.save-btn:hover {
  background-color: #218838;
}

.cancel-btn:hover {
  background-color: #c82333;
}

.error-message {
  margin-top: 15px;
  color: #dc3545;
  font-weight: bold;
}

.certificate-item {
  align-items: flex-start;
}
</style>

