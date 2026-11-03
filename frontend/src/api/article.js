import http, { API_BASE } from './http'

// 把后端相对地址拼成完整地址，供组件使用
export { API_BASE }

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

// 更新文章（支持替换/清除图片）
export function updateArticle(id, article, images, clearImages) {
  const form = new FormData()
  form.append('title', article.title)
  form.append('content', article.content)
  if (images && images.length) {
    images.forEach((f) => form.append('images', f))
  }
  if (clearImages) {
    form.append('clearImages', 'true')
  }
  return http.put(`/api/articles/${id}`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 删除文章
export function deleteArticle(id) {
  return http.delete(`/api/articles/${id}`)
}
