import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '../api/index.js'
import { useAuthStore } from './auth.js'

export const useFavoritesStore = defineStore('favorites', () => {
  const items = ref([]) // [{ favoriteId, regionCode, sidoName, sigunguName, dongName, createdAt }]

  const count = computed(() => items.value.length)

  async function fetchFavorites() {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn()) {
      items.value = []
      return
    }
    try {
      items.value = await api.get('/api/favorites')
    } catch {
      items.value = []
    }
  }

  async function addFavorite(regionCode) {
    const created = await api.post('/api/favorites', { regionCode })
    await fetchFavorites()
    return created
  }

  async function removeFavorite(favoriteId) {
    await api.delete(`/api/favorites/${favoriteId}`)
    items.value = items.value.filter(f => f.favoriteId !== favoriteId)
  }

  return { items, count, fetchFavorites, addFavorite, removeFavorite }
})
