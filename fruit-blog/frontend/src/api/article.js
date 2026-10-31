import axios from 'axios'

// 后端地址：本地开发经 vite proxy 走相对路径；
// 部署到 GitHub Pages 时通过 VITE_API_BASE 指向真实后端。
const BASE = import.meta.env.VITE_API_BASE || ''

// 给前端组件用：把后端返回的相对地址（如 /uploads/xxx）拼成完整地址
export const API_BASE = BASE

const http = axios.create({ baseURL: BASE })

// 获取文章列表
export function listArticles() {
  return http.get('/api/articles')
}

// 获取单篇文章详情
export function getArticle(id) {
  return http.get(`/api/articles/${id}`)
}

// 发布文章（支持多张图片）
// article: { title, content }, images: File[]
export function createArticle(article, images) {
  const form = new FormData()
  form.append('title', article.title)
  form.append('content', article.content)
  if (images && images.length) {
    images.forEach((f) => form.append('images', f))
  }
  return http.post('/api/articles', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
