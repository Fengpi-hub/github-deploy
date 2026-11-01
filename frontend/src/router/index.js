import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ArticleDetail from '../views/ArticleDetail.vue'
import Admin from '../views/Admin.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/article/:id', name: 'article', component: ArticleDetail, props: true },
  { path: '/admin', name: 'admin', component: Admin }
]

const router = createRouter({
  // 与 vite.config.js 的 base 保持一致，路由才能正确落在 /github-deploy/ 子路径下
  history: createWebHistory('/github-deploy/'),
  routes
})

export default router
