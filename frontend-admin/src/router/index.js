import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const routes = [
  { path: '/login', component: () => import('../views/LoginView.vue') },
  {
    path: '/',
    component: () => import('../views/BatchView.vue'),
    meta: { requiresAdmin: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  if (!to.meta.requiresAdmin) return

  const authStore = useAuthStore()
  if (!authStore.isLoggedIn()) {
    await authStore.fetchMe()
  }
  if (!authStore.isLoggedIn()) {
    return '/login'
  }
})

export default router
