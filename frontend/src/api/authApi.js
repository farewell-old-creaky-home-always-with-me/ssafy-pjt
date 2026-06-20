import { http } from './http.js'

export async function login(payload) {
  const res = await http.post('/api/auth/login', payload)
  return res.data
}
export async function logout() {
  await http.post('/api/auth/logout')
}
export async function getAuthMe() {
  const res = await http.get('/api/auth/me')
  return res.data
}
