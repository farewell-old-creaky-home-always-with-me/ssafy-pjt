import { request } from './client.js'

export function getAstarRoute(payload) {
  return request({ method: 'POST', url: '/api/route/astar', data: payload })
}
