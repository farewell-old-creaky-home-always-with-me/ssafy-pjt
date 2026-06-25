import { http } from './http.js'

export async function getBoards(params) {
  const res = await http.get('/api/boards', { params })
  return res.data
}

export async function getBoardDetail(boardId) {
  const res = await http.get(`/api/boards/${boardId}`)
  return res.data
}

export async function createBoard(payload) {
  const res = await http.post('/api/boards', payload)
  return res.data
}

export async function updateBoard(boardId, payload) {
  const res = await http.put(`/api/boards/${boardId}`, payload)
  return res.data
}

export async function deleteBoard(boardId) {
  await http.delete(`/api/boards/${boardId}`)
}
