import { reactive, computed } from 'vue'

// 简单的全局登录态：用 reactive 存储当前用户与令牌，并提供登录/登出方法。
const state = reactive({
  token: localStorage.getItem('token') || '',
  username: localStorage.getItem('username') || ''
})

const isLoggedIn = computed(() => !!state.token)

function setSession(token, username) {
  state.token = token
  state.username = username
  localStorage.setItem('token', token)
  localStorage.setItem('username', username)
}

function logout() {
  state.token = ''
  state.username = ''
  localStorage.removeItem('token')
  localStorage.removeItem('username')
}

export function useAuth() {
  return { state, isLoggedIn, setSession, logout }
}
