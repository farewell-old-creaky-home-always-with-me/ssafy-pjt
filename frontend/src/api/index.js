import { request } from './client.js'

export { apiClient, request, toApiError } from './client.js'
export * as authApi from './auth.js'
export * as membersApi from './members.js'
export * as housesApi from './houses.js'
export * as favoritesApi from './favorites.js'
export * as placesApi from './places.js'
export * as routeApi from './route.js'
export * as commercialApi from './commercial.js'
export * as environmentApi from './environment.js'
export * as noticesApi from './notices.js'
export * as statsApi from './stats.js'

export const api = {
  get: (url, params) => request({ method: 'GET', url, params }),
  post: (url, data) => request({ method: 'POST', url, data }),
  put: (url, data) => request({ method: 'PUT', url, data }),
  delete: (url) => request({ method: 'DELETE', url }),
}
