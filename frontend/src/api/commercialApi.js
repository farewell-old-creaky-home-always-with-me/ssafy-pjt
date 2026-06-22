import { http } from './http.js'

export async function getCommercials(params) {
  const res = await http.get('/api/commercial', { params })
  return res.data
}
