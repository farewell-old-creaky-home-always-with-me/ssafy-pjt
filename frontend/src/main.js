import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'
import { useAuthStore } from './stores/authStore.js'
import { useFavoritesStore } from './stores/favoritesStore.js'

import '../css/reset.css'
import '../css/variables.css'
import '../css/base.css'
import '../css/layout.css'
import '../css/components.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()

authStore.fetchMe().then(() => {
  if (authStore.isLoggedIn()) {
    favoritesStore.fetchFavorites()
  }
  app.mount('#app')
})
