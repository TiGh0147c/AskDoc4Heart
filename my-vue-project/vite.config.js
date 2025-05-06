import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import fs from 'fs'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 3000,
    strictPort: false, // 如果端口被占用，自动尝试下一个可用端口
    cors: true, // 启用CORS
    allowedHosts: [
      'df2b-180-160-46-14.ngrok-free.app',
      // ... other allowed hosts if any
    ],
    proxy: {
      '/api': {
        //target: 'http://192.168.204.55:3000/',
        target: 'http://192.168.1.102:8080', // 默认后端端口
        changeOrigin: true,
        configure: (proxy, _options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            console.log('发送请求到:', req.url);
          });
        },
      }
    },
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
      'Access-Control-Allow-Headers': 'X-Requested-With, Content-Type, Authorization'
    }
  }
})