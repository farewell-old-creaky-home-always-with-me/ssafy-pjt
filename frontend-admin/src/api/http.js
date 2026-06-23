import axios from 'axios'

function toApiError(error) {
  if (!axios.isAxiosError(error)) return error
  const status = error.response?.status
  const data = error.response?.data ?? null
  const message = data?.message ?? error.message
  return Object.assign(new Error(message), { status, data })
}

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10_000,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(toApiError(error)),
)
