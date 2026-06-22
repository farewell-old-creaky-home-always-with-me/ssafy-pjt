import { request } from './client.js'

export function createMember(payload) {
  return request({ method: 'POST', url: '/api/members', data: payload })
}

export function getMyMember() {
  return request({ method: 'GET', url: '/api/members/me' })
}

export function updateMyMember(payload) {
  return request({ method: 'PUT', url: '/api/members/me', data: payload })
}

export function deleteMyMember() {
  return request({ method: 'DELETE', url: '/api/members/me' })
}
