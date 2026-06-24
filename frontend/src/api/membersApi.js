import { http } from './http.js'

export async function createMember(payload) {
  const res = await http.post('/api/members', payload)
  return res.data
}
export async function getMyMember() {
  const res = await http.get('/api/members/me')
  return res.data
}
export async function updateMyMember(payload) {
  const res = await http.put('/api/members/me', payload)
  return res.data
}
export async function resetPassword(payload) {
  await http.post('/api/members/password-reset', payload)
}
export async function deleteMyMember() {
  await http.delete('/api/members/me')
}
