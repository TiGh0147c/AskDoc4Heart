import { createStore } from 'vuex'

export default createStore({
  state: {
    isAuthenticated: localStorage.getItem('isAuthenticated') === 'true',
    userRole: localStorage.getItem('userRole') || '',
    username: localStorage.getItem('username') || ''
  },
  mutations: {
    setAuth(state, { isAuthenticated, role, username }) {
      state.isAuthenticated = isAuthenticated
      state.userRole = role
      state.username = username

      localStorage.setItem('isAuthenticated', isAuthenticated)
      localStorage.setItem('userRole', role)
      localStorage.setItem('username', username)
    },
    logout(state) {
      state.isAuthenticated = false
      state.userRole = ''
      state.username = ''

      localStorage.removeItem('isAuthenticated')
      localStorage.removeItem('userRole')
      localStorage.removeItem('username')
    }
  },
  actions: {
    login({ commit }, { username, password, role }) {
      // 这里只是模拟登录，实际项目中应该调用API
      return new Promise((resolve) => {
        // 模拟API请求延迟
        setTimeout(() => {
          commit('setAuth', {
            isAuthenticated: true,
            role,
            username
          })
          resolve({ success: true })
        }, 500)
      })
    },
    register({ commit }, { username, password, role }) {
      // 这里只是模拟注册，实际项目中应该调用API
      return new Promise((resolve) => {
        // 模拟API请求延迟
        setTimeout(() => {
          commit('setAuth', {
            isAuthenticated: true,
            role,
            username
          })
          resolve({ success: true })
        }, 500)
      })
    },
    logout({ commit }) {
      commit('logout')
    }
  },
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    userRole: state => state.userRole,
    username: state => state.username
  }
})