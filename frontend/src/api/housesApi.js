import { http } from './http.js'

export async function searchHouses(params) {
  const res = await http.get('/api/houses', { params })
  return res.data
}
export async function getHouseDetail(houseId) {
  const res = await http.get(`/api/houses/${houseId}`)
  return res.data
}
