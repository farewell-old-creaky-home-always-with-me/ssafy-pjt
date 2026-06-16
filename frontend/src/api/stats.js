import { request } from './client.js'

export function getStats() {
  return request({ method: 'GET', url: '/api/stats' })
}
