import { request } from './client.js'

export function getCommercials(params) {
  return request({ method: 'GET', url: '/api/commercial', params })
}
