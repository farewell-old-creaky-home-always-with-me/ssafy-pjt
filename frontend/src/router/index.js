import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const routes = [
  { path: '/',                   component: () => import('../views/HomeView.vue') },
  { path: '/search',             component: () => import('../views/SearchView.vue') },
  { path: '/favorites',          component: () => import('../views/FavoritesView.vue'), meta: { requiresAuth: true } },
  { path: '/commercial',         component: () => import('../views/CommercialView.vue') },
  { path: '/environment',        component: () => import('../views/EnvironmentView.vue') },
  { path: '/notices',            component: () => import('../views/NoticesView.vue') },
  { path: '/notices/:id',        component: () => import('../views/NoticeDetailView.vue') },
  { path: '/login',              component: () => import('../views/LoginView.vue') },
  { path: '/signup',             component: () => import('../views/SignupView.vue') },
  { path: '/profile',            component: () => import('../views/ProfileView.vue'), meta: { requiresAuth: true } },
  { path: '/password-recovery',  component: () => import('../views/PasswordRecoveryView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn()) {
    return '/login'
  }
})

export default router
