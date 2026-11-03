import axios from 'axios'

// 后端地址：本地开发经 vite proxy 走相对路径；
// 部署到 GitHub Pages 时通过 VITE_API_BASE 指向真实后端。
const BASE = import.meta.env.VITE_API_BASE || ''

// 给前端组件用：把后端返回的相对地址（如 /uploads/xxx）拼成完整地址
export const API_BASE = BASE

const http = axios.create({ baseURL: BASE })

// 请求拦截器：自动在请求头带上登录令牌
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：令牌失效(401)时自动清除登录态
http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
    }
    return Promise.reject(err)
  }
)

export default http
