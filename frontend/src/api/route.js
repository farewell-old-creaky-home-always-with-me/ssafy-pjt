import { http } from './http.js'

export async function getAstarRoute(payload) {
  const res = await http.post('/api/route/astar', payload)
  return res.data
}
