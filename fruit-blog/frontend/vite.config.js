import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 说明：
// 1) 本地开发时，前端跑在 5173，后端跑在 8080。
//    这里用 proxy 把 /api 和 /uploads 代理到后端，避免跨域问题。
// 2) 部署到 GitHub Pages 这类纯静态托管时，没有后端在同一域名下，
//    需要把 VITE_API_BASE 设成你后端的可访问地址（见 deploy.yml）。
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080'
    }
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true
  }
})
