import { request } from './client.js'

export function getPlaces() {
  return request({ method: 'GET', url: '/api/places' })
}

export function createPlace(payload) {
  return request({ method: 'POST', url: '/api/places', data: payload })
}

export function updatePlace(placeId, payload) {
  return request({ method: 'PUT', url: `/api/places/${placeId}`, data: payload })
}

export function deletePlace(placeId) {
  return request({ method: 'DELETE', url: `/api/places/${placeId}` })
}
