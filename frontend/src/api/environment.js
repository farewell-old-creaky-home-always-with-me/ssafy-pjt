import { request } from './client.js'

export function getEnvironment(params) {
  return request({ method: 'GET', url: '/api/environment', params })
}
