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
        <!-- 用户名字段（仅用户显示） -->
        <div class="input-group" v-if="selectedRole === 'user'">
          <label for="reg-username">用户名</label>
          <input 
            type="text" 
            id="reg-username" 
            v-model="registerForm.username" 
            required
          />
        </div>

        <!-- 姓名字段（仅咨询师显示） -->
        <div class="input-group" v-if="selectedRole === 'counselor'">
          <label for="reg-name">姓名</label>
          <input 
            type="text" 
            id="reg-name" 
            v-model="registerForm.name" 
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
        
        <!-- 性别字段（仅用户显示） -->
        <div class="input-group" v-if="selectedRole === 'user'">
          <label for="reg-gender">性别</label>
          <select 
            id="reg-gender" 
            v-model="registerForm.gender" 
            @change="handleGenderChange"
          >
            <option value="male">男</option>
            <option value="female">女</option>

          </select>
        </div>
        
        <!-- 邮箱字段（仅咨询师显示） -->
        <div class="input-group" v-if="selectedRole === 'counselor'">
          <label for="reg-email">邮箱</label>
          <input 
            type="email" 
            id="reg-email" 
            v-model="registerForm.email" 
            required
            placeholder="请输入邮箱地址"
          />
        </div>
        
        <!-- 资格证字段（仅咨询师显示） -->
        <div class="input-group" v-if="selectedRole === 'counselor'">
          <label for="reg-qualification">资格证</label>
          <select 
            id="reg-qualification" 
            v-model="registerForm.qualification" 
            @change="handleQualificationChange"
          >
            <option value="none">无</option>
            <option value="three">三级</option>
            <option value="two">二级</option>
            <option value="one">一级</option>
          </select>
        </div>
        
        <!-- 资格证证明材料上传（仅咨询师且资格证不为“无”时显示） -->
        <div v-if="selectedRole === 'counselor' && registerForm.qualification !== 'none'" class="input-group">
          <label for="reg-proof">资格证证明材料</label>
          <input 
            type="file" 
            id="reg-proof" 
            @change="handleProofUpload"
            accept="image/*"
            required
          />
        </div>
        
        <!-- 是否成为督导选项（仅咨询师且资格证为“一级”时显示） -->
        <div v-if="selectedRole === 'counselor' && registerForm.qualification === 'one'" class="input-group">
          <label>是否成为督导</label>
          <input 
            type="checkbox" 
            id="reg-supervisor" 
            v-model="registerForm.isSupervisor"
          />
          <label for="reg-supervisor">是</label>
        </div>
        
        <!-- 身份证号字段（仅咨询师显示） -->
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
      username: '', // 用户名（仅用户使用）
      name: '', // 姓名（仅咨询师使用）
      email: '', // 邮箱（仅咨询师使用）
      phone: '',
      gender:'male',
      password: '',
      confirmPassword: '',
      qualification: 'none', // 资格证（仅咨询师使用）
      isSupervisor: false, // 是否成为督导（仅咨询师使用）
      proofFile: null, // 资格证证明材料（仅咨询师使用）
      idCard: '' // 身份证号（仅咨询师使用）
    })
    
    const registerError = ref('')
    
    const roles = [
      { label: '用户', value: 'user' },
      { label: '咨询师', value: 'counselor' },
      { label: '管理员', value: 'admin' }
    ]
    
    const handleGenderChange = () => {
      //没啥用
    }
    const handleQualificationChange = () => {
      // 清空证明材料文件
      registerForm.proofFile = null
    }
    
    const handleProofUpload = (event) => {
      registerForm.proofFile = event.target.files[0]
    }
    
    const handleRegister = async () => {
      if (selectedRole.value === 'user') {
        // 用户注册验证逻辑
        if (!registerForm.username || !registerForm.password) {
          registerError.value = '请填写用户名和密码'
          return
        }
      } else if (selectedRole.value === 'counselor') {
        // 咨询师注册验证逻辑
        if (!registerForm.name || !registerForm.password) {
          registerError.value = '请填写姓名和密码'
          return
        }
        if (!registerForm.phone || registerForm.phone.length !== 11) {
          registerError.value = '请填写有效的11位手机号码'
          return
        }
        if (!registerForm.email) {
          registerError.value = '请填写有效的邮箱地址'
          return
        }
        if (registerForm.qualification !== 'none' && !registerForm.proofFile) {
          registerError.value = '请上传资格证证明材料'
          return
        }
        if (registerForm.qualification === 'one' && registerForm.isSupervisor === undefined) {
          registerError.value = '请选择是否成为督导'
          return
        }
        if (!registerForm.idCard || registerForm.idCard.length !== 18) {
          registerError.value = '请填写有效的18位身份证号码'
          return
        }
      }
      
      if (registerForm.password !== registerForm.confirmPassword) {
        registerError.value = '两次输入的密码不一致'
        return
      }
      
      try {
        registerError.value = ''
        const result = await store.dispatch('register', {
          username: selectedRole.value === 'user' ? registerForm.username : null,
          name: selectedRole.value === 'counselor' ? registerForm.name : null,
          email: selectedRole.value === 'counselor' ? registerForm.email : null,
          phone: registerForm.phone,
          gender: selectedRole.value === 'user' ? registerError.gender :null,
          password: registerForm.password,
          qualification: selectedRole.value === 'counselor' ? registerForm.qualification : null,
          isSupervisor: selectedRole.value === 'counselor' ? registerForm.isSupervisor : null,
          proofFile: selectedRole.value === 'counselor' ? registerForm.proofFile : null,
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
      handleRegister,
      handleGenderChange,
      handleQualificationChange,
      handleProofUpload
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