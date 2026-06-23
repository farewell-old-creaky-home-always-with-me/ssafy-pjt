import { http } from './http.js'

export async function getQnas(params) {
  const res = await http.get('/api/qnas', { params })
  return res.data
}

export async function getQnaDetail(qnaId) {
  const res = await http.get(`/api/qnas/${qnaId}`)
  return res.data
}

export async function createQna(payload) {
  const res = await http.post('/api/qnas', payload)
  return res.data
}

export async function updateQna(qnaId, payload) {
  const res = await http.put(`/api/qnas/${qnaId}`, payload)
  return res.data
}

export async function deleteQna(qnaId) {
  await http.delete(`/api/qnas/${qnaId}`)
}
