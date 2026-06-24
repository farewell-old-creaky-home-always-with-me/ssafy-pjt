import { http } from './http.js'

export async function getRegions(dong, signal) {
  const res = await http.get('/api/regions', { params: dong ? { dong } : {}, signal })
  return res.data
}
