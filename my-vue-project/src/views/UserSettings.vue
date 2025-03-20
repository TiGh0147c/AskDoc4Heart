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
        
        <div class="info-section">
          <div class="info-item">
            <span class="info-label">用户名：</span>
            <span class="info-value">{{ username }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">出生日期：</span>
            <span class="info-value">{{ userInfo.birthDate }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号：</span>
            <span class="info-value">{{ maskPhone(userInfo.phone) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">密码：</span>
            <span class="info-value">********</span>
          </div>
          
          <button class="edit-btn" @click="openEditModal">修改个人信息</button>
        </div>
      </div>
    </div>

    <!-- 修改信息的弹窗 -->
    <div class="modal" v-if="showModal">
      <div class="modal-content">
        <span class="close-btn" @click="closeModal">&times;</span>
        <h2>验证身份</h2>
        <p>请验证您的手机号以修改个人信息</p>
        
        <div class="form-group">
          <label for="phone">手机号</label>
          <input 
            type="text" 
            id="phone" 
            v-model="verificationForm.phone" 
            placeholder="请输入您的手机号"
          >
        </div>
        
        <div class="form-group verification-group">
          <label for="code">验证码</label>
          <input 
            type="text" 
            id="code" 
            v-model="verificationForm.code" 
            placeholder="请输入验证码"
          >
          <button 
            class="send-code-btn" 
            @click="sendVerificationCode" 
            :disabled="countdown > 0"
          >
            {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
          </button>
        </div>
        
        <div class="form-group captcha-group">
          <label for="captcha">图形验证码</label>
          <input 
            type="text" 
            id="captcha" 
            v-model="verificationForm.captcha" 
            placeholder="请输入图形验证码"
          >
          <div class="captcha-img" @click="refreshCaptcha">
            {{ captchaText }}
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="confirm-btn" @click="verifyIdentity">确认</button>
        </div>
      </div>
    </div>

    <!-- 修改个人信息的弹窗 -->
    <div class="modal" v-if="showEditInfoModal">
      <div class="modal-content">
        <span class="close-btn" @click="closeEditInfoModal">&times;</span>
        <h2>修改个人信息</h2>
        
        <div class="form-group">
          <label for="edit-username">用户名</label>
          <input 
            type="text" 
            id="edit-username" 
            v-model="editForm.username" 
            placeholder="请输入新用户名"
          >
        </div>
        
        <div class="form-group">
          <label for="edit-birthdate">出生日期</label>
          <input 
            type="date" 
            id="edit-birthdate" 
            v-model="editForm.birthDate"
          >
        </div>
        
        <div class="form-group">
          <label for="edit-phone">手机号</label>
          <input 
            type="text" 
            id="edit-phone" 
            v-model="editForm.phone" 
            placeholder="请输入新手机号"
          >
        </div>
        
        <div class="form-group">
          <label for="edit-password">新密码（选填）</label>
          <input 
            type="password" 
            id="edit-password" 
            v-model="editForm.password" 
            placeholder="不修改请留空"
          >
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeEditInfoModal">取消</button>
          <button class="confirm-btn" @click="updateUserInfo">保存修改</button>
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
  name: 'UserSettings',
  setup() {
    const store = useStore()
    const router = useRouter()

    const username = computed(() => store.getters.username)
    
    // 模拟用户信息
    const userInfo = ref({
      birthDate: '1990-01-01',
      phone: '13800138000',
    })
    
    // 验证弹窗相关状态
    const showModal = ref(false)
    const countdown = ref(0)
    const verificationForm = ref({
      phone: '',
      code: '',
      captcha: ''
    })
    const captchaText = ref('')
    
    // 修改信息弹窗状态
    const showEditInfoModal = ref(false)
    const editForm = ref({
      username: '',
      birthDate: '',
      phone: '',
      password: ''
    })
    
    // 初始化图形验证码
    const refreshCaptcha = () => {
      const characters = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
      let result = ''
      for (let i = 0; i < 4; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length))
      }
      captchaText.value = result
    }
    
    // 手机号加密显示
    const maskPhone = (phone) => {
      if (!phone || phone.length < 11) return phone
      return phone.substring(0, 3) + '****' + phone.substring(7)
    }
    
    // 打开修改信息验证弹窗
    const openEditModal = () => {
      verificationForm.value.phone = userInfo.value.phone
      showModal.value = true
      refreshCaptcha()
    }
    
    // 关闭验证弹窗
    const closeModal = () => {
      showModal.value = false
      verificationForm.value = {
        phone: '',
        code: '',
        captcha: ''
      }
      countdown.value = 0
    }
    
    // 打开修改信息弹窗
    const openEditInfoModal = () => {
      editForm.value = {
        username: username.value,
        birthDate: userInfo.value.birthDate,
        phone: userInfo.value.phone,
        password: ''
      }
      showEditInfoModal.value = true
    }
    
    // 关闭修改信息弹窗
    const closeEditInfoModal = () => {
      showEditInfoModal.value = false
    }
    
    // 发送验证码
    const sendVerificationCode = () => {
      if (!verificationForm.value.phone) {
        alert('请输入手机号')
        return
      }
      
      // 这里只是前端模拟，实际需要调用后端接口发送验证码
      countdown.value = 60
      
      // 倒计时逻辑
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
      
      alert(`验证码已发送到 ${verificationForm.value.phone}`)
    }
    
    // 验证身份
    const verifyIdentity = () => {
      if (!verificationForm.value.phone) {
        alert('请输入手机号')
        return
      }
      
      if (!verificationForm.value.code) {
        alert('请输入验证码')
        return
      }
      
      if (!verificationForm.value.captcha) {
        alert('请输入图形验证码')
        return
      }
      
      if (verificationForm.value.captcha.toUpperCase() !== captchaText.value) {
        alert('图形验证码错误')
        refreshCaptcha()
        return
      }
      
      // 假设验证成功（实际需要调用后端验证）
      closeModal()
      openEditInfoModal()
    }
    
    // 更新用户信息
    const updateUserInfo = () => {
      // 这里只是前端模拟，实际需要调用后端接口更新用户信息
      userInfo.value = {
        birthDate: editForm.value.birthDate,
        phone: editForm.value.phone
      }
      
      // 如果有修改密码，需要额外处理
      if (editForm.value.password) {
        console.log('需要更新密码:', editForm.value.password)
      }
      
      closeEditInfoModal()
      alert('个人信息已更新')
    }
    
    const logout = () => {
      store.dispatch('logout')
      router.push('/login')
    }

    const goTo = (path) => {
      switch (path) {
        case 'home':
          router.push('/user/home')
          break
        case 'tutorial':
          router.push('/user/tutorial')
          break
        case 'appointment':
          router.push('/user/appointment')
          break
        case 'settings':
          router.push('/user/settings')
          break
        case 'history':
          router.push('/user/history')
          break
        case 'currentChat':
          router.push('/user/currentChat')
            break
        default:
          console.error('Invalid path')
      }
    }
    
    onMounted(() => {
      refreshCaptcha()
    })

    return {
      username,
      userInfo,
      logout,
      goTo,
      maskPhone,
      showModal,
      openEditModal,
      closeModal,
      verificationForm,
      sendVerificationCode,
      countdown,
      captchaText,
      refreshCaptcha,
      verifyIdentity,
      showEditInfoModal,
      editForm,
      closeEditInfoModal,
      updateUserInfo
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

/* 新增样式 */
.info-section {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
}

.info-label {
  font-weight: bold;
  width: 100px;
  color: #555;
}

.info-value {
  color: #333;
}

.edit-btn {
  margin-top: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.edit-btn:hover {
  background-color: #0056b3;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  width: 400px;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 24px;
  cursor: pointer;
  color: #777;
}

.close-btn:hover {
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.verification-group {
  display: flex;
  gap: 10px;
}

.verification-group input {
  flex: 1;
}

.send-code-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0 10px;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 0.9rem;
}

.send-code-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.captcha-group {
  display: flex;
  gap: 10px;
}

.captcha-group input {
  flex: 1;
}

.captcha-img {
  width: 100px;
  height: 40px;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  letter-spacing: 5px;
  cursor: pointer;
  color: #333;
  user-select: none;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #f0f0f0;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-btn:hover {
  background-color: #0056b3;
}
</style>