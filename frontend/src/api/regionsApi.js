import { http } from './http.js'

export async function getRegions(dong) {
  const res = await http.get('/api/regions', { params: dong ? { dong } : {} })
  return res.data
}
