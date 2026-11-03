<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { register } from '../api/auth'
import { useAuth } from '../auth'

const { setSession } = useAuth()
const router = useRouter()

const username = ref('')
const password = ref('')
const confirm = ref('')
const message = ref('')
const isError = ref(false)
const loading = ref(false)

async function submit() {
  message.value = ''
  if (!username.value.trim() || !password.value) {
    message.value = '请填写用户名和密码'
    isError.value = true
    return
  }
  if (username.value.trim().length < 3) {
    message.value = '用户名至少 3 个字符'
    isError.value = true
    return
  }
  if (password.value.length < 6) {
    message.value = '密码至少 6 个字符'
    isError.value = true
    return
  }
  if (password.value !== confirm.value) {
    message.value = '两次输入的密码不一致'
    isError.value = true
    return
  }
  loading.value = true
  try {
    const { data } = await register(username.value.trim(), password.value)
    setSession(data.token, data.username)
    message.value = '注册成功，正在跳转…'
    isError.value = false
    setTimeout(() => router.push('/'), 600)
  } catch (e) {
    message.value = e.response?.data?.message || '注册失败，请稍后重试'
    isError.value = true
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-card">
      <h1 class="page-title">📝 注册</h1>
      <form class="editor" @submit.prevent="submit">
        <label class="field">
          <span>用户名（≥3 字符）</span>
          <input v-model="username" type="text" placeholder="给自己起个名字" autocomplete="username" />
        </label>
        <label class="field">
          <span>密码（≥6 字符）</span>
          <input v-model="password" type="password" placeholder="设置密码" autocomplete="new-password" />
        </label>
        <label class="field">
          <span>确认密码</span>
          <input v-model="confirm" type="password" placeholder="再输入一次密码" autocomplete="new-password" />
        </label>
        <div class="actions">
          <button type="submit" :disabled="loading">
            {{ loading ? '注册中…' : '注册并登录' }}
          </button>
        </div>
        <p v-if="message" :class="['msg', isError ? 'error' : 'ok']">{{ message }}</p>
        <p class="auth-switch">
          已有账号？<RouterLink to="/login">去登录</RouterLink>
        </p>
      </form>
    </div>
  </section>
</template>
