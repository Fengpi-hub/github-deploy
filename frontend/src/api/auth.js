import http from './http'

// 注册：POST /api/auth/register  { username, password }
export function register(username, password) {
  return http.post('/api/auth/register', { username, password })
}

// 登录：POST /api/auth/login  { username, password }
export function login(username, password) {
  return http.post('/api/auth/login', { username, password })
}
