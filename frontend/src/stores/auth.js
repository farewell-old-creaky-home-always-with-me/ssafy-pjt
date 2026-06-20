import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '../api/index.js'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null) // { memberId, name, isAdmin }

  async function fetchMe() {
    try {
      const data = await authApi.getAuthMe()
      user.value = data.isAuthenticated
        ? { memberId: data.memberId, name: data.name, isAdmin: data.isAdmin }
        : null
    } catch {
      user.value = null
    }
  }

  async function login(email, password) {
    const data = await authApi.login({ email, password })
    user.value = { memberId: data.memberId, name: data.name, isAdmin: data.isAdmin }
    return data
  }

  async function logout() {
    await authApi.logout()
    user.value = null
  }

  const isLoggedIn = () => user.value !== null

  function patchUser(patch) {
    if (user.value) user.value = { ...user.value, ...patch }
  }

  function clearUser() {
    user.value = null
  }

  return { user, fetchMe, login, logout, isLoggedIn, patchUser, clearUser }
})
