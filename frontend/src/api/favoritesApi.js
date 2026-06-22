import { http } from './http.js'

export async function getFavorites() {
  const res = await http.get('/api/favorites')
  return res.data
}
export async function createFavorite(payload) {
  const res = await http.post('/api/favorites', payload)
  return res.data
}
export async function deleteFavorite(favoriteId) {
  await http.delete(`/api/favorites/${favoriteId}`)
}
