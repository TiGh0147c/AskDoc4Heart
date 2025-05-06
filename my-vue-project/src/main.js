import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'

// 配置axios默认值
axios.defaults.baseURL = 'http://192.168.1.102:8080/'

createApp(App)
  .use(router)
  .use(store)
  .mount('#app')