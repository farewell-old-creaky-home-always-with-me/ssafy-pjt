import { http } from './http.js'

export async function sendChatMessage(message) {
  const response = await http.post('/api/chat', { message })
  return response.data
}

export async function uploadDocument(file) {
  const form = new FormData()
  form.append('file', file)
  await http.post('/api/chat/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
