import { request } from './client.js'

export function searchHouses(params) {
  return request({ method: 'GET', url: '/api/houses', params })
}

export function getHouseDetail(houseId) {
  return request({ method: 'GET', url: `/api/houses/${houseId}` })
}
