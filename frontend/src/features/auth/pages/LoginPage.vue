<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { Home, Mail, Lock, Eye, EyeOff, AlertCircle, Loader2 } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/authStore.js'
import { useFavoritesStore } from '@/stores/favoritesStore.js'
import '@css/pages/auth.css'

const router = useRouter()
const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()

const email = ref('')
const password = ref('')
const showPw = ref(false)
const loading = ref(false)
const generalError = ref('')
const emailError = ref('')
const passwordError = ref('')

function validateEmail() {
  if (!email.value.trim()) { emailError.value = '이메일을 입력해 주세요'; return false }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) { emailError.value = '올바른 이메일 형식이 아닙니다'; return false }
  emailError.value = ''; return true
}

function validatePassword() {
  if (!password.value) { passwordError.value = '비밀번호를 입력해 주세요'; return false }
  if (password.value.length < 8) { passwordError.value = '비밀번호는 8자 이상이어야 합니다'; return false }
  passwordError.value = ''; return true
}

async function handleSubmit() {
  const ok = validateEmail() & validatePassword()
  if (!ok) return
  loading.value = true
  try {
    await authStore.login(email.value.trim(), password.value)
    await favoritesStore.fetchFavorites()
    router.push('/')
  } catch (err) {
    generalError.value = err.data?.message ?? '아이디 또는 비밀번호가 올바르지 않습니다'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-wrap">
      <div class="auth-card card-lg">
        <div class="auth-card-header">
          <div class="auth-logo-icon"><Home :size="20" /></div>
          <h1 style="color:#1A3C6E;font-size:1.5rem;font-weight:700;margin-bottom:0.25rem">로그인</h1>
          <p style="color:#9ca3af;font-size:0.8125rem;margin:0">SSAFY Home에 오신 것을 환영합니다</p>
        </div>

        <form class="auth-card-form" @submit.prevent="handleSubmit" novalidate>
          <div v-if="generalError" class="general-error" style="display:flex">
            <AlertCircle :size="16" />
            <span>{{ generalError }}</span>
          </div>

          <div class="form-group">
            <label class="form-label" for="login-email">이메일</label>
            <div class="input-icon-wrap">
              <span class="input-icon"><Mail :size="16" /></span>
              <input id="login-email" v-model="email" type="email" class="input-base"
                :class="{ 'input-error': emailError }" placeholder="이메일을 입력하세요" autocomplete="email"
                @blur="validateEmail" @input="generalError = ''" />
            </div>
            <div v-if="emailError" class="field-error" style="display:flex">
              <AlertCircle :size="14" /><span>{{ emailError }}</span>
            </div>
          </div>

          <div class="form-group" style="margin-bottom:1.5rem">
            <label class="form-label" for="login-password">비밀번호</label>
            <div class="input-icon-wrap" style="position:relative">
              <span class="input-icon"><Lock :size="16" /></span>
              <input id="login-password" v-model="password" :type="showPw ? 'text' : 'password'"
                class="input-base" :class="{ 'input-error': passwordError }" style="padding-right:3rem"
                placeholder="비밀번호를 입력하세요" autocomplete="current-password"
                @blur="validatePassword" @input="generalError = ''" />
              <button type="button" class="pw-toggle" @click="showPw = !showPw">
                <Eye v-if="!showPw" :size="16" /><EyeOff v-else :size="16" />
              </button>
            </div>
            <div v-if="passwordError" class="field-error" style="display:flex">
              <AlertCircle :size="14" /><span>{{ passwordError }}</span>
            </div>
          </div>

          <button type="submit" class="btn btn-primary btn-full" :disabled="loading" style="font-size:0.9375rem">
            <Loader2 v-if="loading" :size="16" class="animate-spin" />
            <span>{{ loading ? '로그인 중...' : '로그인' }}</span>
          </button>
        </form>
      </div>

      <div class="auth-links">
        <RouterLink to="/signup">회원가입</RouterLink>
        <div class="auth-links-divider"></div>
        <RouterLink to="/password-recovery">비밀번호 찾기</RouterLink>
      </div>
    </div>
  </div>
</template>

