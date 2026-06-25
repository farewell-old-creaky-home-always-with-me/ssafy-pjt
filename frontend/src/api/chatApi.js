import { http } from './http.js'

export async function sendChatMessage(message) {
  const response = await http.post('/api/chat', { message })
  return response.data
}
