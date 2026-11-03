import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ArticleDetail from '../views/ArticleDetail.vue'
import Admin from '../views/Admin.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import EditArticle from '../views/EditArticle.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/article/:id', name: 'article', component: ArticleDetail, props: true },
  { path: '/admin', name: 'admin', component: Admin, meta: { requiresAuth: true } },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/edit/:id', name: 'edit', component: EditArticle, props: true, meta: { requiresAuth: true } }
]

const router = createRouter({
  // 与 vite.config.js 的 base 保持一致，路由才能正确落在 /github-deploy/ 子路径下
  history: createWebHistory('/github-deploy/'),
  routes
})

// 路由守卫：需要登录的页面未登录则跳转登录页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
