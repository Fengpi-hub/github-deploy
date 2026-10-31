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
  history: createWebHistory(),
  routes
})

export default router
