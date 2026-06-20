import { http } from './http.js'

export async function getStats() {
  const res = await http.get('/api/stats')
  return res.data
}
