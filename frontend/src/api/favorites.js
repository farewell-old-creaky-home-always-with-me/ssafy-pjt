import { request } from './client.js'

export function getFavorites() {
  return request({ method: 'GET', url: '/api/favorites' })
}

export function createFavorite(payload) {
  return request({ method: 'POST', url: '/api/favorites', data: payload })
}

export function deleteFavorite(favoriteId) {
  return request({ method: 'DELETE', url: `/api/favorites/${favoriteId}` })
}
