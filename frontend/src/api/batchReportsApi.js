import { http } from './http.js'

export async function getLatestBatchReport() {
  const res = await http.get('/api/reports/batch/latest')
  return res.data
}

export async function getBatchReport(reportId) {
  const res = await http.get(`/api/reports/batch/${reportId}`)
  return res.data
}

export function getBatchReportPdfUrl(reportId) {
  const baseUrl = import.meta.env.VITE_API_BASE_URL ?? ''
  const normalizedBaseUrl = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl
  return `${normalizedBaseUrl}/api/reports/batch/${reportId}/pdf`
}
