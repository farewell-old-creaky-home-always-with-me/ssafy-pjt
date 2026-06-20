import { http } from './http.js'

export async function getPlaces() {
  const res = await http.get('/api/places')
  return res.data
}
export async function createPlace(payload) {
  const res = await http.post('/api/places', payload)
  return res.data
}
export async function updatePlace(placeId, payload) {
  const res = await http.put(`/api/places/${placeId}`, payload)
  return res.data
}
export async function deletePlace(placeId) {
  await http.delete(`/api/places/${placeId}`)
}
