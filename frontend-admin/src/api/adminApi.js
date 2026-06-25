import { http } from './http.js'

export async function collectHouseDeals(payload) {
  const res = await http.post('/api/admin/batch/house-deals', payload)
  return res.data
}
export async function collectRegionCodes() {
  const res = await http.post('/api/admin/batch/region-codes')
  return res.data
}

export async function generateBatchReport() {
  const res = await http.post('/api/admin/batch/reports')
  return res.data
}

export async function searchRegions(dong) {
  const res = await http.get('/api/regions', { params: { dong } })
  return res.data
}
