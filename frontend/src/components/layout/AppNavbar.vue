<template>
  <nav class="sticky top-0 z-50 bg-navy shadow-[0_2px_8px_rgba(0,0,0,0.15)]">
    <div class="max-w-[80rem] mx-auto px-4 lg:px-8 flex items-center justify-between h-16 gap-4">
      <!-- Logo -->
      <RouterLink to="/" class="flex items-center gap-2 shrink-0">
        <div class="w-8 h-8 rounded-md bg-blue flex items-center justify-center text-white">
          <Home :size="16" />
        </div>
        <span class="text-white text-xl font-bold tracking-tight whitespace-nowrap">
          SSAFY <span class="text-blue">Home</span>
        </span>
      </RouterLink>

      <!-- Desktop nav -->
      <div class="hidden lg:flex items-center gap-0.5 flex-1 justify-center">
        <RouterLink
          to="/"
          class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 [&.router-link-exact-active]:text-white [&.router-link-exact-active]:bg-white/15 [&.router-link-exact-active]:font-semibold"
        >
          <Home :size="15" /> 홈
        </RouterLink>

        <RouterLink
          to="/search"
          class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold"
        >
          <BarChart3 :size="15" /> 실거래가 조회
        </RouterLink>

        <RouterLink
          to="/favorites"
          class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold"
        >
          <Heart :size="15" /> 관심지역
          <span v-if="favCount > 0" class="min-w-[18px] h-[18px] px-1 rounded-full bg-red text-white flex items-center justify-center text-[10px] font-bold">
            {{ favCount > 99 ? '99+' : favCount }}
          </span>
        </RouterLink>

        <!-- 지역 정보 dropdown -->
        <div class="relative group">
          <button
            :class="[
              'flex items-center gap-1.5 px-3 py-2 rounded-lg text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 cursor-pointer',
              isInfoActive ? 'text-white bg-white/15 font-semibold' : 'text-white/70'
            ]"
          >
            <MapPin :size="15" /> 지역 정보
            <ChevronDown :size="13" class="transition-transform duration-200 group-hover:rotate-180" />
          </button>
          <div class="absolute top-full left-1/2 -translate-x-1/2 pt-2 opacity-0 invisible -translate-y-1 group-hover:opacity-100 group-hover:visible group-hover:translate-y-0 transition-all duration-150 z-50">
            <div class="bg-navy border border-white/15 rounded-xl shadow-xl py-1.5 min-w-[148px]">
              <RouterLink
                to="/commercial"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <Store :size="14" /> 상권 정보
              </RouterLink>
              <RouterLink
                to="/environment"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <TreePine :size="14" /> 환경 정보
              </RouterLink>
            </div>
          </div>
        </div>

        <!-- 커뮤니티 dropdown -->
        <div class="relative group">
          <button
            :class="[
              'flex items-center gap-1.5 px-3 py-2 rounded-lg text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 cursor-pointer',
              isCommunityActive ? 'text-white bg-white/15 font-semibold' : 'text-white/70'
            ]"
          >
            <MessageSquare :size="15" /> 커뮤니티
            <ChevronDown :size="13" class="transition-transform duration-200 group-hover:rotate-180" />
          </button>
          <div class="absolute top-full left-1/2 -translate-x-1/2 pt-2 opacity-0 invisible -translate-y-1 group-hover:opacity-100 group-hover:visible group-hover:translate-y-0 transition-all duration-150 z-50">
            <div class="bg-navy border border-white/15 rounded-xl shadow-xl py-1.5 min-w-[148px]">
              <RouterLink
                to="/notices"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <Bell :size="14" /> 공지사항
              </RouterLink>
              <RouterLink
                to="/boards"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <FileText :size="14" /> 공유게시판
              </RouterLink>
              <RouterLink
                to="/qnas"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <HelpCircle :size="14" /> Q&A
              </RouterLink>
            </div>
          </div>
        </div>

        <!-- AI 서비스 dropdown -->
        <div class="relative group">
          <button
            :class="[
              'flex items-center gap-1.5 px-3 py-2 rounded-lg text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 cursor-pointer',
              isAiActive ? 'text-white bg-white/15 font-semibold' : 'text-white/70'
            ]"
          >
            <Sparkles :size="15" /> AI 서비스
            <ChevronDown :size="13" class="transition-transform duration-200 group-hover:rotate-180" />
          </button>
          <div class="absolute top-full left-1/2 -translate-x-1/2 pt-2 opacity-0 invisible -translate-y-1 group-hover:opacity-100 group-hover:visible group-hover:translate-y-0 transition-all duration-150 z-50">
            <div class="bg-navy border border-white/15 rounded-xl shadow-xl py-1.5 min-w-[148px]">
              <RouterLink
                to="/reports/batch"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <BarChart3 :size="14" /> AI 리포트
              </RouterLink>
              <RouterLink
                to="/chat"
                class="flex items-center gap-2 px-4 py-2.5 text-white/70 text-sm whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 rounded-lg mx-1 [&.router-link-active]:text-white [&.router-link-active]:bg-white/10"
              >
                <Bot :size="14" /> AI 채팅
              </RouterLink>
            </div>
          </div>
        </div>
      </div>

      <!-- Desktop auth -->
      <div class="hidden lg:flex items-center gap-1 shrink-0">
        <div class="w-px h-6 bg-white/15 mx-1"></div>
        <template v-if="authStore.user">
          <RouterLink
            to="/profile"
            class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold"
          >
            <UserCircle :size="15" /> {{ authStore.user.name }}님
          </RouterLink>
          <button
            class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 cursor-pointer"
            @click="handleLogout"
          >
            <LogOut :size="15" /> 로그아웃
          </button>
        </template>
        <template v-else>
          <RouterLink
            to="/login"
            class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold"
          >
            <LogIn :size="15" /> 로그인
          </RouterLink>
          <RouterLink
            to="/profile"
            class="flex items-center gap-1.5 px-3 py-2 rounded-lg text-white/70 text-[0.8125rem] font-medium whitespace-nowrap transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold"
          >
            <UserCircle :size="15" /> 마이페이지
          </RouterLink>
        </template>
      </div>

      <!-- Mobile menu button -->
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

    <!-- Mobile menu -->
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
      <div class="px-6 py-2 text-white/30 text-xs font-semibold uppercase tracking-wider">지역 정보</div>
      <RouterLink to="/commercial" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <Store :size="16" /> 상권 정보
      </RouterLink>
      <RouterLink to="/environment" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <TreePine :size="16" /> 환경 정보
      </RouterLink>
      <div class="px-6 py-2 text-white/30 text-xs font-semibold uppercase tracking-wider">커뮤니티</div>
      <RouterLink to="/notices" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <Bell :size="16" /> 공지사항
      </RouterLink>
      <RouterLink to="/boards" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <FileText :size="16" /> 공유게시판
      </RouterLink>
      <RouterLink to="/qnas" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <HelpCircle :size="16" /> Q&A
      </RouterLink>
      <div class="px-6 py-2 text-white/30 text-xs font-semibold uppercase tracking-wider">AI 서비스</div>
      <RouterLink to="/reports/batch" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <BarChart3 :size="16" /> AI 리포트
      </RouterLink>
      <RouterLink to="/chat" class="flex items-center gap-2 px-8 py-2.5 text-white/70 text-sm transition-colors hover:text-white hover:bg-white/10 [&.router-link-active]:text-white [&.router-link-active]:bg-white/15 [&.router-link-active]:font-semibold" @click="mobileOpen = false">
        <Bot :size="16" /> AI 채팅
      </RouterLink>
      <div class="mx-6 my-2 border-t border-white/10"></div>
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
import { RouterLink, useRouter, useRoute } from 'vue-router'
import {
  Home, BarChart3, Heart, Store, TreePine, FileText,
  LogIn, LogOut, UserCircle, Menu, X, Bot,
  MapPin, MessageSquare, Bell, HelpCircle, Sparkles, ChevronDown
} from 'lucide-vue-next'
import { useAuthStore } from '../../stores/authStore.js'
import { useFavoritesStore } from '../../stores/favoritesStore.js'

const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()
const router = useRouter()
const route = useRoute()
const mobileOpen = ref(false)

const favCount = computed(() => favoritesStore.count)

const isInfoActive = computed(() =>
  ['/commercial', '/environment'].some(p => route.path.startsWith(p))
)
const isCommunityActive = computed(() =>
  ['/notices', '/boards', '/qnas'].some(p => route.path.startsWith(p))
)
const isAiActive = computed(() =>
  ['/reports', '/chat'].some(p => route.path.startsWith(p))
)

async function handleLogout() {
  if (!confirm('로그아웃 하시겠습니까?')) return
  await authStore.logout()
  favoritesStore.clearItems()
  mobileOpen.value = false
  router.push('/')
}
</script>
