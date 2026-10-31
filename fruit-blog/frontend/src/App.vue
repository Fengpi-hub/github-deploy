<script setup>
import { RouterLink, RouterView } from 'vue-router'
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
    </nav>
  </header>

  <main class="site-main">
    <RouterView />
  </main>

  <footer class="site-footer">
    <p>用 Spring Boot + Vue 打造 · 学习 GitHub Actions 部署静态网站</p>
  </footer>
</template>
