import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'

// 配置axios默认值
axios.defaults.baseURL = 'http://localhost:8080'

createApp(App)
  .use(router)
  .use(store)
  .mount('#app')