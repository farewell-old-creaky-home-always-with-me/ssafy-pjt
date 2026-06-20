import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAuthMe, login as apiLogin, logout as apiLogout } from '../api/authApi.js'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null) // { memberId, name, isAdmin }

  async function fetchMe() {
    try {
      const data = await getAuthMe()
      user.value = data.isAuthenticated && data.isAdmin
        ? { memberId: data.memberId, name: data.name, isAdmin: true }
        : null
    } catch {
      user.value = null
    }
  }

  async function login(email, password) {
    const data = await apiLogin({ email, password })
    if (!data.isAdmin) {
      throw Object.assign(new Error('관리자 계정이 아닙니다.'), { status: 403 })
    }
    user.value = { memberId: data.memberId, name: data.name, isAdmin: true }
    return data
  }

  async function logout() {
    await apiLogout()
    user.value = null
  }

  const isLoggedIn = () => user.value !== null

  return { user, fetchMe, login, logout, isLoggedIn }
})
