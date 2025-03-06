<template>
  <div class="container">
    <div class="card">
      <h1>心理咨询系统</h1>
      <div class="tabs">
        <div 
          class="tab" 
          @click="$router.push('/login')"
        >
          登录
        </div>
        <div 
          class="tab" 
          :class="{ active: true }" 
        >
          注册
        </div>
      </div>

      <div class="role-tabs">
        <div 
          v-for="role in roles" 
          :key="role.value" 
          class="tab" 
          :class="{ 
            active: selectedRole === role.value,
            disabled: role.value === 'admin'
          }" 
          @click="role.value !== 'admin' && (selectedRole = role.value)"
        >
          {{ role.label }}
        </div>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="input-group">
          <label for="reg-username">用户名</label>
          <input 
            type="text" 
            id="reg-username" 
            v-model="registerForm.username" 
            required
          />
        </div>
        
        <!-- 手机号字段 - 对所有角色都显示 -->
        <div class="input-group">
          <label for="reg-phone">手机号</label>
          <input 
            type="tel" 
            id="reg-phone" 
            v-model="registerForm.phone" 
            required
            pattern="[0-9]{11}"
            placeholder="请输入11位手机号码"
          />
        </div>
        
        <!-- 身份证号字段 - 仅咨询师显示 -->
        <div class="input-group" v-if="selectedRole === 'counselor'">
          <label for="reg-idcard">身份证号</label>
          <input 
            type="text" 
            id="reg-idcard" 
            v-model="registerForm.idCard" 
            required
            pattern="[0-9Xx]{18}"
            placeholder="请输入18位身份证号码"
          />
        </div>
        
        <div class="input-group">
          <label for="reg-password">密码</label>
          <input 
            type="password" 
            id="reg-password" 
            v-model="registerForm.password" 
            required
          />
        </div>
        <div class="input-group">
          <label for="confirm-password">确认密码</label>
          <input 
            type="password" 
            id="confirm-password" 
            v-model="registerForm.confirmPassword" 
            required
          />
        </div>
        <button type="submit" class="btn">注册</button>
        <p v-if="registerError" class="error-message">{{ registerError }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'Register',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const selectedRole = ref('user')
    
    const registerForm = reactive({
      username: '',
      password: '',
      confirmPassword: '',
      phone: '',
      idCard: ''
    })
    
    const registerError = ref('')
    
    const roles = [
      { label: '用户', value: 'user' },
      { label: '咨询师', value: 'counselor' },
      { label: '管理员', value: 'admin' }
    ]
    
    const handleRegister = async () => {
      if (!registerForm.username || !registerForm.password) {
        registerError.value = '请填写用户名和密码'
        return
      }
      
      if (!registerForm.phone || registerForm.phone.length !== 11) {
        registerError.value = '请填写有效的11位手机号码'
        return
      }
      
      if (selectedRole.value === 'counselor' && (!registerForm.idCard || registerForm.idCard.length !== 18)) {
        registerError.value = '请填写有效的18位身份证号码'
        return
      }
      
      if (registerForm.password !== registerForm.confirmPassword) {
        registerError.value = '两次输入的密码不一致'
        return
      }
      
      try {
        registerError.value = ''
        const result = await store.dispatch('register', {
          username: registerForm.username,
          password: registerForm.password,
          phone: registerForm.phone,
          idCard: selectedRole.value === 'counselor' ? registerForm.idCard : null,
          role: selectedRole.value
        })
        
        if (result.success) {
          // 根据角色重定向到相应的主页
          if (selectedRole.value === 'user') {
            router.push('/user/home')
          } else if (selectedRole.value === 'counselor') {
            router.push('/counselor/home')
          }
        } else {
          registerError.value = '注册失败，请稍后再试'
        }
      } catch (error) {
        registerError.value = '注册过程中发生错误'
        console.error(error)
      }
    }
    
    return {
      selectedRole,
      registerForm,
      registerError,
      roles,
      handleRegister
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

.role-tabs .tab.disabled {
  background: #cccccc;
  color: #666666;
  cursor: not-allowed;
  opacity: 0.7;
}

.input-group {
  margin-bottom: 15px;
}

.error-message {
  color: red;
  margin-top: 10px;
}
</style>