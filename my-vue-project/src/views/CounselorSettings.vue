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
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="top-right">
        <p class="welcome">欢迎回来，{{ counselorInfo.name }}！</p>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="card">
        <h1>咨询师设置</h1>
        
        <div class="info-section">
          <h2>基本信息</h2>
          <div class="info-item">
            <span class="info-label">姓名：</span>
            <span v-if="!editing" class="info-value">{{ counselorInfo.name }}</span>
            <input v-else type="text" v-model="editForm.name" class="edit-input">
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
            <input v-else type="text" v-model="editForm.expertise_area" class="edit-input">
          </div>
          <div class="info-item">
            <span class="info-label">督导身份：</span>
            <span class="info-value">{{ counselorInfo.is_supervisor ? '是' : '否' }}</span>
          </div>
        </div>
        
        <div class="info-section">
          <h2>联系方式</h2>
          <div class="info-item">
            <span class="info-label">手机号：</span>
            <span v-if="!editing" class="info-value">{{ counselorInfo.phone_number || '未设置' }}</span>
            <input v-else type="text" v-model="editForm.phone_number" class="edit-input">
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
            <textarea v-else v-model="editForm.counselor_certificate" class="edit-input certificate-input"></textarea>
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
            <button class="save-btn" @click="saveChanges">保存</button>
            <button class="cancel-btn" @click="cancelEditing">取消</button>
          </template>
        </div>
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
    
    // 咨询师信息，实际应用中应从API获取
    const counselorInfo = reactive({
      counselor_id: 1,
      name: '张心理咨询师',
      avatar: '2ecaew',
      phone_number: '13800138001',
      email: 'counselor1@example.com',
      counselor_certificate: '心理咨询师二级证书',
      is_supervisor: 0,
      status: 'available',
      expertise_area: '焦虑症',
      on_duty: 0
    })
    
    // 编辑表单
    const editForm = reactive({
      name: '',
      phone_number: '',
      email: '',
      counselor_certificate: '',
      status: '',
      expertise_area: '',
      password: ''
    })
    
    // 开始编辑
    const startEditing = () => {
      editForm.name = counselorInfo.name
      editForm.phone_number = counselorInfo.phone_number || ''
      editForm.email = counselorInfo.email || ''
      editForm.counselor_certificate = counselorInfo.counselor_certificate || ''
      editForm.status = counselorInfo.status
      editForm.expertise_area = counselorInfo.expertise_area || ''
      editForm.password = ''
      editing.value = true
    }
    
    // 取消编辑
    const cancelEditing = () => {
      editing.value = false
    }
    
    // 保存更改
    const saveChanges = async () => {
      try {
        // 实际应用中应调用API保存数据
        // const response = await axios.put(`/api/counselors/${counselorInfo.counselor_id}`, editForm)
        
        // 模拟API调用成功
        Object.assign(counselorInfo, {
          name: editForm.name,
          phone_number: editForm.phone_number,
          email: editForm.email,
          counselor_certificate: editForm.counselor_certificate,
          status: editForm.status,
          expertise_area: editForm.expertise_area
        })
        
        // 如果有修改密码，实际应用中应单独处理
        if (editForm.password) {
          console.log('密码已更新')
        }
        
        editing.value = false
        alert('个人信息已更新')
      } catch (error) {
        console.error('保存失败:', error)
        alert('保存失败，请稍后重试')
      }
    }
    
    // 格式化状态显示
    const formatStatus = (status) => {
      const statusMap = {
        available: '在线',
        unavailable: '离线'
      }
      return statusMap[status] || '未知'
    }
    
    // 获取咨询师信息
    const fetchCounselorInfo = async () => {
      try {
        // 实际应用中应从API获取数据
        // const response = await axios.get(`/api/counselors/current`)
        // Object.assign(counselorInfo, response.data)
        console.log('获取咨询师信息')
      } catch (error) {
        console.error('获取咨询师信息失败:', error)
      }
    }
    
    const logout = () => {
      // 实际应用中应调用登出API
      router.push('/login')
    }

    const goTo = (path) => {
      const paths = {
        home: '/counselor/home',
        settings: '/counselor/settings',
        requests: '/counselor/requests',
        chat: '/counselor/chat',
        schedule: '/counselor/schedule',
        history: '/counselor/history',
        evaluation: '/counselor/evaluation'
      }
      const targetPath = paths[path]
      if (targetPath) {
        router.push(targetPath)
      }
    }
    
    onMounted(() => {
      fetchCounselorInfo()
    })

    return {
      counselorInfo,
      editing,
      editForm,
      startEditing,
      cancelEditing,
      saveChanges,
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

.certificate-item {
  align-items: flex-start;
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
  min-height: 80px;
  resize: vertical;
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
</style>