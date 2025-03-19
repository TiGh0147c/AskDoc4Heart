<template>
  <div class="container">
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

      <form @submit.prevent="handleLogin">
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
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
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
    
    return {
      selectedRole,
      loginForm,
      loginError,
      roles,
      handleLogin
    }
  }
}
</script>

<style scoped>
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
</style>