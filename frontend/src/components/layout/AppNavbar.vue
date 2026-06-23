<template>
  <nav class="sticky top-0 z-50 bg-navy shadow-[0_2px_8px_rgba(0,0,0,0.15)]">
    <div class="max-w-[80rem] mx-auto px-4 lg:px-8 flex items-center justify-between h-16">
      <RouterLink to="/" class="flex items-center gap-2 shrink-0">
        <div class="w-8 h-8 rounded-md bg-blue flex items-center justify-center text-white">
          <Home :size="16" />
        </div>
        <span class="text-white text-xl font-bold tracking-tight">
          SSAFY <span class="text-blue">Home</span>
        </span>
      </RouterLink>

      <div class="hidden lg:flex items-center gap-1">
        <RouterLink to="/" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-exact-active]:text-white [&.router-link-exact-active]:bg-white/15 [&.router-link-exact-active]:font-semibold">
          <Home :size="16" /> 홈
        </RouterLink>
        <RouterLink to="/search" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
          <BarChart3 :size="16" /> 실거래가 조회
        </RouterLink>
        <RouterLink to="/favorites" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
          <Heart :size="16" /> 관심지역
          <span v-if="favCount > 0" class="min-w-[18px] h-[18px] px-1 rounded-full bg-red text-white flex items-center justify-center text-[10px] font-bold">
            {{ favCount > 99 ? '99+' : favCount }}
          </span>
        </RouterLink>
        <RouterLink to="/commercial" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
          <Store :size="16" /> 상권 정보
        </RouterLink>
        <RouterLink to="/environment" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
          <TreePine :size="16" /> 환경 정보
        </RouterLink>
        <RouterLink to="/notices" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
          <FileText :size="16" /> 공지사항
        </RouterLink>
      </div>

      <div class="hidden lg:flex items-center gap-1">
        <div class="w-px h-6 bg-white/15 mx-2"></div>
        <template v-if="authStore.user">
          <RouterLink to="/profile" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
            <UserCircle :size="16" /> {{ authStore.user.name }}님
          </RouterLink>
          <button class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10" @click="handleLogout">
            <LogOut :size="16" /> 로그아웃
          </button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
            <LogIn :size="16" /> 로그인
          </RouterLink>
          <RouterLink to="/profile" class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold">
            <UserCircle :size="16" /> 마이페이지
          </RouterLink>
        </template>
      </div>

      <button
        class="flex lg:hidden text-white p-2 rounded-lg transition-colors hover:bg-white/10 relative"
        @click="mobileOpen = !mobileOpen"
        :aria-label="mobileOpen ? '메뉴 닫기' : '메뉴 열기'"
      >
        <span v-if="!mobileOpen"><Menu :size="20" /></span>
        <span v-else><X :size="20" /></span>
        <span v-if="favCount > 0" class="absolute -top-0.5 -right-0.5 w-2 h-2 rounded-full bg-red"></span>
      </button>
    </div>

    <div :class="['bg-navy border-t border-white/10 pb-3', mobileOpen ? 'block' : 'hidden']">
      <RouterLink to="/" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-exact-active]:text-white [&.router-link-exact-active]:bg-white/15 [&.router-link-exact-active]:font-semibold" @click="mobileOpen = false">
        <Home :size="16" /> 홈
      </RouterLink>
      <RouterLink to="/search" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <BarChart3 :size="16" /> 실거래가 조회
      </RouterLink>
      <RouterLink to="/favorites" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <Heart :size="16" /> 관심지역
        <span v-if="favCount > 0" class="min-w-[18px] h-[18px] px-1 rounded-full bg-red text-white flex items-center justify-center text-[10px] font-bold">
          {{ favCount > 99 ? '99+' : favCount }}
        </span>
      </RouterLink>
      <RouterLink to="/commercial" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <Store :size="16" /> 상권 정보
      </RouterLink>
      <RouterLink to="/environment" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <TreePine :size="16" /> 환경 정보
      </RouterLink>
      <RouterLink to="/notices" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <FileText :size="16" /> 공지사항
      </RouterLink>
      <template v-if="authStore.user">
        <RouterLink to="/profile" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10" @click="mobileOpen = false">
          <UserCircle :size="16" /> {{ authStore.user.name }}님
        </RouterLink>
        <button class="w-full text-left flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10" @click="handleLogout">
          <LogOut :size="16" /> 로그아웃
        </button>
      </template>
      <template v-else>
        <RouterLink to="/login" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10" @click="mobileOpen = false">
          <LogIn :size="16" /> 로그인
        </RouterLink>
        <RouterLink to="/profile" class="flex items-center gap-2 px-6 py-3 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10" @click="mobileOpen = false">
          <UserCircle :size="16" /> 마이페이지
        </RouterLink>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Home, BarChart3, Heart, Store, TreePine, FileText, LogIn, LogOut, UserCircle, Menu, X } from 'lucide-vue-next'
import { useAuthStore } from '../../stores/authStore.js'
import { useFavoritesStore } from '../../stores/favoritesStore.js'

const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()
const router = useRouter()
const mobileOpen = ref(false)

const favCount = computed(() => favoritesStore.count)

async function handleLogout() {
  if (!confirm('로그아웃 하시겠습니까?')) return
  await authStore.logout()
  favoritesStore.clearItems()
  mobileOpen.value = false
  router.push('/')
}
</script>
