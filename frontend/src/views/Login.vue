<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { login } from '../api/auth'
import { useAuth } from '../auth'

const { setSession } = useAuth()
const router = useRouter()

const username = ref('')
const password = ref('')
const message = ref('')
const isError = ref(false)
const loading = ref(false)

async function submit() {
  message.value = ''
  if (!username.value.trim() || !password.value) {
    message.value = '请输入用户名和密码'
    isError.value = true
    return
  }
  loading.value = true
  try {
    const { data } = await login(username.value.trim(), password.value)
    setSession(data.token, data.username)
    message.value = '登录成功，正在跳转…'
    isError.value = false
    setTimeout(() => router.push('/'), 600)
  } catch (e) {
    message.value = e.response?.data?.message || '登录失败，请检查用户名和密码'
    isError.value = true
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-card">
      <h1 class="page-title">🔐 登录</h1>
      <form class="editor" @submit.prevent="submit">
        <label class="field">
          <span>用户名</span>
          <input v-model="username" type="text" placeholder="用户名" autocomplete="username" />
        </label>
        <label class="field">
          <span>密码</span>
          <input v-model="password" type="password" placeholder="密码" autocomplete="current-password" />
        </label>
        <div class="actions">
          <button type="submit" :disabled="loading">
            {{ loading ? '登录中…' : '登录' }}
          </button>
        </div>
        <p v-if="message" :class="['msg', isError ? 'error' : 'ok']">{{ message }}</p>
        <p class="auth-switch">
          还没有账号？<RouterLink to="/register">去注册</RouterLink>
        </p>
      </form>
    </div>
  </section>
</template>
