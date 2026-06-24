import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore.js'

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: () => import('@/features/home/pages/HomePage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/search',
    name: 'SearchPage',
    component: () => import('@/features/house/pages/SearchPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/favorites',
    name: 'FavoritesPage',
    component: () => import('@/features/favorites/pages/FavoritesPage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/commercial',
    name: 'CommercialPage',
    component: () => import('@/features/commercial/pages/CommercialPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/environment',
    name: 'EnvironmentPage',
    component: () => import('@/features/environment/pages/EnvironmentPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/notices',
    name: 'NoticesPage',
    component: () => import('@/features/notice/pages/NoticesPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/notices/:id',
    name: 'NoticeDetailPage',
    component: () => import('@/features/notice/pages/NoticeDetailPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/qnas',
    name: 'QnasPage',
    component: () => import('@/features/qna/pages/QnasPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/qnas/new',
    name: 'QnaCreatePage',
    component: () => import('@/features/qna/pages/QnaCreatePage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/qnas/:id',
    name: 'QnaDetailPage',
    component: () => import('@/features/qna/pages/QnaDetailPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/qnas/:id/edit',
    name: 'QnaEditPage',
    component: () => import('@/features/qna/pages/QnaEditPage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/reports/batch',
    name: 'BatchReportPage',
    component: () => import('@/features/report/pages/BatchReportPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import('@/features/auth/pages/LoginPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/signup',
    name: 'SignupPage',
    component: () => import('@/features/auth/pages/SignupPage.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/profile',
    name: 'ProfilePage',
    component: () => import('@/features/member/pages/ProfilePage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/password-recovery',
    name: 'PasswordRecoveryPage',
    component: () => import('@/features/auth/pages/PasswordRecoveryPage.vue'),
    meta: { requiresAuth: false },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn()) {
    return { name: 'LoginPage' }
  }
})

export default router
