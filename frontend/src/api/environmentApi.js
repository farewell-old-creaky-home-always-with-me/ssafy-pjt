import { http } from './http.js'

export async function getEnvironment(params) {
  const res = await http.get('/api/environment', { params })
  return res.data
}
