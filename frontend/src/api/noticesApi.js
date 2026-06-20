import { http } from './http.js'

export async function getNotices(params) {
  const res = await http.get('/api/notices', { params })
  return res.data
}
export async function getNoticeDetail(noticeId) {
  const res = await http.get(`/api/notices/${noticeId}`)
  return res.data
}
export async function createNotice(payload) {
  const res = await http.post('/api/notices', payload)
  return res.data
}
export async function updateNotice(noticeId, payload) {
  const res = await http.put(`/api/notices/${noticeId}`, payload)
  return res.data
}
export async function deleteNotice(noticeId) {
  await http.delete(`/api/notices/${noticeId}`)
}
