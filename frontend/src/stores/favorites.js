import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '../api/index.js'
import { useAuthStore } from './auth.js'

export const useFavoritesStore = defineStore('favorites', () => {
  const items = ref([]) // [{ favoriteId, regionCode, sidoName, sigunguName, dongName, createdAt }]
  const error = ref(null)

  const count = computed(() => items.value.length)

  async function fetchFavorites() {
    const authStore = useAuthStore()
    if (!authStore.isLoggedIn()) {
      items.value = []
      return
    }
    error.value = null
    try {
      items.value = await api.get('/api/favorites')
    } catch {
      items.value = []
      error.value = '관심지역 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
    }
  }

  async function addFavorite(regionCode) {
    const created = await api.post('/api/favorites', { regionCode })
    items.value = [...items.value, created]
    return created
  }

  async function removeFavorite(favoriteId) {
    await api.delete(`/api/favorites/${favoriteId}`)
    items.value = items.value.filter(f => f.favoriteId !== favoriteId)
  }

  function clearItems() {
    items.value = []
    error.value = null
  }

  return { items, error, count, fetchFavorites, addFavorite, removeFavorite, clearItems }
})
