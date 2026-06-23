<template>
  <AppNavbar />
  <RouterView />
  <footer class="bg-[#0F2847] text-white/60 py-10">
    <div class="max-w-[80rem] mx-auto px-4 sm:px-6 flex flex-col sm:flex-row sm:justify-between items-center gap-4">
      <div class="flex items-center gap-2">
        <div class="w-7 h-7 rounded-[0.375rem] bg-blue flex items-center justify-center text-white">
          <Home :size="16" />
        </div>
        <span class="text-white/80 text-[0.9375rem] font-semibold">
          SSAFY <span class="text-blue">Home</span>
        </span>
      </div>
      <div class="flex items-center gap-6 text-[0.8125rem]">
        <a href="#" class="text-white/60 transition-colors hover:text-white">이용약관</a>
        <a href="#" class="text-white/60 transition-colors hover:text-white">개인정보처리방침</a>
        <a href="#" class="text-white/60 transition-colors hover:text-white">고객센터</a>
      </div>
      <p class="text-xs">&copy; 2026 SSAFY Home. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
import { RouterView } from 'vue-router'
import { Home } from 'lucide-vue-next'
import AppNavbar from './components/layout/AppNavbar.vue'
</script>
