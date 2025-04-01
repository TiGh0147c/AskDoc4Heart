<template>
  <div>
    <!-- 过渡动画 -->
    <div class="splash-screen" v-if="showSplash">
      <div class="logo-container">
        <div class="logo">问心医言</div>
        <div class="loading-bar">
          <div class="loading-progress" :style="{ width: loadingProgress + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- 登录页面 -->
    <transition name="fade" mode="out-in">
      <div class="container" v-if="!showSplash">
        <div class="card">
          <h1>心理咨询系统</h1>
          <div class="tabs">
            <div 
              class="tab" 
              :class="{ active: true }" 
            >
              登录
            </div>
            <div 
              class="tab" 
              @click="$router.push('/register')"
            >
              注册
            </div>
          </div>

          <div class="role-tabs">
            <div 
              v-for="role in roles" 
              :key="role.value" 
              class="tab" 
              :class="{ active: selectedRole === role.value }" 
              @click="selectedRole = role.value"
            >
              {{ role.label }}
            </div>
          </div>

          <!-- 普通登录表单 -->
          <form @submit.prevent="handleLogin" v-if="!isWechatLogin">
            <div class="input-group">
              <label for="login-identifier">账号/手机号</label>
              <input 
                type="text" 
                id="login-identifier" 
                v-model="loginForm.identifier" 
                required
                placeholder="请输入账号或手机号"
              />
            </div>
            <div class="input-group">
              <label for="password">密码</label>
              <input 
                type="password" 
                id="password" 
                v-model="loginForm.password" 
                required
              />
            </div>
            <button type="submit" class="btn">登录</button>
            <p v-if="loginError" class="error-message">{{ loginError }}</p>
          </form>

          <!-- 微信登录 -->
          <div v-if="selectedRole === 'user'" class="wechat-login-btn-container">
            <button @click="switchToWechatLogin" class="wechat-login-btn">微信登录</button>
          </div>

          <!-- 微信登录 -->
          <div v-else-if="isWechatLogin" class="wechat-login">
            <p>请使用微信扫描二维码登录</p>
            <img :src="wechatQrcodeUrl" alt="微信登录二维码" class="wechat-qrcode">
            <button @click="cancelWechatLogin" class="btn">取消微信登录</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'Login',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const selectedRole = ref('user')
    
    const loginForm = reactive({
      identifier: '', // 可以是用户名或手机号
      password: ''
    })
    
    const loginError = ref('')
    
    const roles = [
      { label: '用户', value: 'user' },
      { label: '咨询师', value: 'counselor' },
      { label: '管理员', value: 'admin' }
    ]
    
    // 添加过渡动画相关状态
    const showSplash = ref(true)
    const loadingProgress = ref(0)
    
    // 微信登录状态
    const isWechatLogin = ref(false)
    const wechatOpenId = ref('') // 存储微信用户的OpenID
    const wechatUnionId = ref('') // 存储微信用户的UnionID
    const wechatQrcodeUrl = ref('/path/to/your/wechat-qrcode.jpg') // 确保路径正确

    onMounted(() => {
      // 检查是否已经显示过加载动画
      if (sessionStorage.getItem('splashShown') !== 'true') {
        // 模拟加载进度
        const loadingInterval = setInterval(() => {
          loadingProgress.value += 5
          if (loadingProgress.value >= 100) {
            clearInterval(loadingInterval)
            setTimeout(() => {
              showSplash.value = false
              sessionStorage.setItem('splashShown', 'true') // 标记加载动画已显示
            }, 500) // 加载完成后延迟500ms再显示登录页面
          }
        }, 100)
      } else {
        showSplash.value = false
      }
    })
    
    const handleLogin = async () => {
      if (!loginForm.identifier || !loginForm.password) {
        loginError.value = '请填写账号和密码'
        return
      }
      
      try {
        loginError.value = ''
        const result = await store.dispatch('login', {
          identifier: loginForm.identifier, // 后端需要支持通过用户名或手机号登录
          password: loginForm.password,
          role: selectedRole.value
        })
        
        if (result.success) {
          // 根据角色重定向到相应的主页
          if (selectedRole.value === 'user') {
            router.push('/user/home')
          } else if (selectedRole.value === 'counselor') {
            router.push('/counselor/home')
          } else if (selectedRole.value === 'admin') {
            router.push('/admin/home')
          }
        } else {
          loginError.value = '登录失败，请检查账号和密码'
        }
      } catch (error) {
        loginError.value = '登录过程中发生错误'
        console.error(error)
      }
    }

    // 切换到微信登录
    const switchToWechatLogin = async () => {
      isWechatLogin.value = true
      // 模拟获取微信登录信息
      // 实际开发中，需要调用微信的Web登录接口获取OpenID和UnionID
      // 示例代码：
      try {
        const response = await store.dispatch('getWechatLoginInfo')
        if (response.success) {
          wechatOpenId.value = response.open_id
          wechatUnionId.value = response.union_id
        } else {
          loginError.value = '获取微信登录信息失败'
        }
      } catch (error) {
        loginError.value = '获取微信登录信息失败'
        console.error(error)
      }
    }

    // 取消微信登录
    const cancelWechatLogin = () => {
      isWechatLogin.value = false
    }

    // 微信登录成功后提交UnionID到后端
    const submitWechatLogin = async () => {
      try {
        const result = await store.dispatch('wechatLogin', {
          open_id: wechatOpenId.value,
          union_id: wechatUnionId.value,
          role: selectedRole.value
        })
        if (result.success) {
          // 根据角色重定向到相应的主页
          if (selectedRole.value === 'user') {
            router.push('/user/home')
          } else if (selectedRole.value === 'counselor') {
            router.push('/counselor/home')
          } else if (selectedRole.value === 'admin') {
            router.push('/admin/home')
          }
        } else {
          loginError.value = '微信登录失败，请稍后再试'
        }
      } catch (error) {
        loginError.value = '微信登录过程中发生错误'
        console.error(error)
      }
    }

    return {
      selectedRole,
      loginForm,
      loginError,
      roles,
      handleLogin,
      showSplash,
      loadingProgress,
      isWechatLogin,
      wechatOpenId,
      wechatUnionId,
      wechatQrcodeUrl,
      switchToWechatLogin,
      cancelWechatLogin,
      submitWechatLogin
    }
  }
}
</script>

<style scoped>
/* 过渡动画样式 */
.splash-screen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #7b4397, #dc2430);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.logo-container {
  text-align: center;
}

.logo {
  font-size: 48px;
  font-weight: bold;
  color: white;
  margin-bottom: 30px;
  animation: fadeIn 1s ease-in-out, pulse 2s infinite;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.7);
}

.loading-bar {
  width: 300px;
  height: 6px;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  overflow: hidden;
}

.loading-progress {
  height: 100%;
  background-color: white;
  border-radius: 3px;
  transition: width 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

/* 原有样式 */
.role-tabs {
  display: flex;
  margin: 20px 0;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ddd;
}

.role-tabs .tab {
  flex: 1;
  padding: 10px;
  background: #f1f1f1;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.role-tabs .tab.active {
  background: #4caf50;
  color: white;
}

.error-message {
  color: red;
  margin-top: 10px;
}

/* 添加登录页面容器的过渡效果 */
.container {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 微信登录样式 */
.wechat-login-btn-container {
  margin-top: 20px;
}

.wechat-login-btn {
  background-color: #00bb29;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
}

.wechat-login-btn:hover {
  background-color: #009623;
}

.wechat-login {
  text-align: center;
}

.wechat-qrcode {
  width: 200px;
  height: 200px;
}

/* 平滑过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>