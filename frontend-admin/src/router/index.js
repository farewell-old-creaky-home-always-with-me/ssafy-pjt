import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore.js'

const routes = [
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import('@/features/auth/pages/LoginPage.vue'),
    meta: { requiresAdmin: false },
  },
  {
    path: '/',
    name: 'BatchPage',
    component: () => import('@/features/batch/pages/BatchPage.vue'),
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
    return { name: 'LoginPage' }
  }
})

export default router
