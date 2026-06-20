import axios from 'axios'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

export function toApiError(error) {
  if (!axios.isAxiosError(error)) return error

  const status = error.response?.status
  const data = error.response?.data ?? null
  const message = data?.message ?? error.message

  return Object.assign(new Error(message), { status, data })
}

export async function request(config) {
  try {
    const response = await apiClient.request(config)
    return response.status === 204 ? null : response.data
  } catch (error) {
    throw toApiError(error)
  }
}
