<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <RouterLink to="/" class="navbar-logo">
        <div class="navbar-logo-icon"><Home :size="16" /></div>
        <span class="navbar-logo-text">SSAFY <span class="blue">Home</span></span>
      </RouterLink>

      <div class="navbar-nav">
        <RouterLink to="/" class="nav-link"><Home :size="16" /> 홈</RouterLink>
        <RouterLink to="/search" class="nav-link"><BarChart3 :size="16" /> 실거래가 조회</RouterLink>
        <RouterLink to="/favorites" class="nav-link">
          <Heart :size="16" /> 관심지역
          <span v-if="favCount > 0" class="nav-badge fav-badge">{{ favCount > 99 ? '99+' : favCount }}</span>
        </RouterLink>
        <RouterLink to="/commercial" class="nav-link"><Store :size="16" /> 상권 정보</RouterLink>
        <RouterLink to="/environment" class="nav-link"><TreePine :size="16" /> 환경 정보</RouterLink>
        <RouterLink to="/notices" class="nav-link"><FileText :size="16" /> 공지사항</RouterLink>
      </div>

      <div class="navbar-auth">
        <div class="navbar-divider"></div>
        <template v-if="authStore.user">
          <RouterLink to="/profile" class="nav-link"><UserCircle :size="16" /> {{ authStore.user.name }}님</RouterLink>
          <button class="nav-link" style="background:none;border:none;cursor:pointer" @click="handleLogout">
            <LogOut :size="16" /> 로그아웃
          </button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="nav-link"><LogIn :size="16" /> 로그인</RouterLink>
          <RouterLink to="/profile" class="nav-link"><UserCircle :size="16" /> 마이페이지</RouterLink>
        </template>
      </div>

      <button class="navbar-hamburger" @click="mobileOpen = !mobileOpen" aria-label="메뉴 열기">
        <span v-if="!mobileOpen"><Menu :size="20" /></span>
        <span v-else><X :size="20" /></span>
        <span v-if="favCount > 0" class="hamburger-dot"></span>
      </button>
    </div>

    <div class="mobile-menu" :class="{ open: mobileOpen }">
      <RouterLink to="/" class="mobile-nav-link" @click="mobileOpen = false"><Home :size="16" /> 홈</RouterLink>
      <RouterLink to="/search" class="mobile-nav-link" @click="mobileOpen = false"><BarChart3 :size="16" /> 실거래가 조회</RouterLink>
      <RouterLink to="/favorites" class="mobile-nav-link" @click="mobileOpen = false">
        <Heart :size="16" /> 관심지역
        <span v-if="favCount > 0" class="nav-badge fav-badge">{{ favCount > 99 ? '99+' : favCount }}</span>
      </RouterLink>
      <RouterLink to="/commercial" class="mobile-nav-link" @click="mobileOpen = false"><Store :size="16" /> 상권 정보</RouterLink>
      <RouterLink to="/environment" class="mobile-nav-link" @click="mobileOpen = false"><TreePine :size="16" /> 환경 정보</RouterLink>
      <RouterLink to="/notices" class="mobile-nav-link" @click="mobileOpen = false"><FileText :size="16" /> 공지사항</RouterLink>
      <template v-if="authStore.user">
        <RouterLink to="/profile" class="mobile-nav-link" @click="mobileOpen = false"><UserCircle :size="16" /> {{ authStore.user.name }}님</RouterLink>
        <button class="mobile-nav-link" style="background:none;border:none;cursor:pointer;text-align:left;width:100%" @click="handleLogout">
          <LogOut :size="16" /> 로그아웃
        </button>
      </template>
      <template v-else>
        <RouterLink to="/login" class="mobile-nav-link" @click="mobileOpen = false"><LogIn :size="16" /> 로그인</RouterLink>
        <RouterLink to="/profile" class="mobile-nav-link" @click="mobileOpen = false"><UserCircle :size="16" /> 마이페이지</RouterLink>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Home, BarChart3, Heart, Store, TreePine, FileText, LogIn, LogOut, UserCircle, Menu, X } from 'lucide-vue-next'
import { useAuthStore } from '../stores/auth.js'
import { useFavoritesStore } from '../stores/favorites.js'

const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()
const router = useRouter()
const mobileOpen = ref(false)

const favCount = computed(() => favoritesStore.count)

async function handleLogout() {
  if (!confirm('로그아웃 하시겠습니까?')) return
  await authStore.logout()
  favoritesStore.items = []
  mobileOpen.value = false
  router.push('/')
}
</script>
