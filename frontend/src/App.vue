<template>
  <AppNavbar />
  <RouterView />
  <footer>
    <div class="footer-inner">
      <div class="footer-logo">
        <div class="footer-logo-icon"><Home :size="16" /></div>
        <span class="footer-logo-text">SSAFY <span class="blue">Home</span></span>
      </div>
      <div class="footer-links">
        <a href="#">이용약관</a>
        <a href="#">개인정보처리방침</a>
        <a href="#">고객센터</a>
      </div>
      <p class="footer-copy">&copy; 2026 SSAFY Home. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
import { RouterView } from 'vue-router'
import { Home } from 'lucide-vue-next'
import AppNavbar from './components/layout/AppNavbar.vue'
</script>
