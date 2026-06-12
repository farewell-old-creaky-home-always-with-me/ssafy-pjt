const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    credentials: 'include',
    headers: { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers },
    ...options,
  })
  if (!res.ok) {
    const data = await res.json().catch(() => null)
    throw Object.assign(new Error(data?.message ?? res.statusText), { status: res.status, data })
  }
  if (res.status === 204) return null
  return res.json()
}

export const api = {
  get:    (path, params) => request(path + (params ? '?' + new URLSearchParams(params) : '')),
  post:   (path, body)   => request(path, { method: 'POST', body: JSON.stringify(body) }),
  put:    (path, body)   => request(path, { method: 'PUT',  body: JSON.stringify(body) }),
  delete: (path)         => request(path, { method: 'DELETE' }),
}
