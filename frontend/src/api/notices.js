import { request } from './client.js'

export function getNotices(params) {
  return request({ method: 'GET', url: '/api/notices', params })
}

export function getNoticeDetail(noticeId) {
  return request({ method: 'GET', url: `/api/notices/${noticeId}` })
}

export function createNotice(payload) {
  return request({ method: 'POST', url: '/api/notices', data: payload })
}

export function updateNotice(noticeId, payload) {
  return request({ method: 'PUT', url: `/api/notices/${noticeId}`, data: payload })
}

export function deleteNotice(noticeId) {
  return request({ method: 'DELETE', url: `/api/notices/${noticeId}` })
}
