<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuth } from './auth'

const { state, isLoggedIn, logout } = useAuth()
const router = useRouter()

function handleLogout() {
  logout()
  router.push('/')
}
</script>

<template>
  <header class="site-header">
    <div class="brand">
      <span class="logo">🍓</span>
      <RouterLink to="/" class="brand-name">水果小百科</RouterLink>
    </div>
    <nav>
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/admin" class="admin-link">✍️ 写文章</RouterLink>
      <template v-if="isLoggedIn">
        <span class="user-name">👤 {{ state.username }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </template>
      <template v-else>
        <RouterLink to="/login">登录</RouterLink>
        <RouterLink to="/register" class="admin-link">注册</RouterLink>
      </template>
    </nav>
  </header>

  <main class="site-main">
    <RouterView />
  </main>

  <footer class="site-footer">
    <p>用 Spring Boot + Vue 打造 · 含登录注册与文章完整增删改查</p>
  </footer>
</template>
