import { request } from './client.js'

export function login(payload) {
  return request({ method: 'POST', url: '/api/auth/login', data: payload })
}

export function logout() {
  return request({ method: 'POST', url: '/api/auth/logout' })
}

export function getAuthMe() {
  return request({ method: 'GET', url: '/api/auth/me' })
}
