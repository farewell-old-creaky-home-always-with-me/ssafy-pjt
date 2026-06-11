<template>
  <div class="auth-page">
    <div class="auth-wrap-md">
      <div class="auth-card card-lg">
        <div class="auth-card-header">
          <div class="auth-logo-icon"><Home :size="20" /></div>
          <h1 style="color:#1A3C6E;font-size:1.5rem;font-weight:700;margin-bottom:0.25rem">회원가입</h1>
          <p style="color:#9ca3af;font-size:0.8125rem;margin:0">SSAFY Home 계정을 생성하세요</p>
        </div>

        <form class="auth-card-form" @submit.prevent="handleSubmit" novalidate>
          <div v-if="generalError" class="general-error" style="display:flex">
            <AlertCircle :size="16" /><span>{{ generalError }}</span>
          </div>

          <div class="form-group">
            <label class="form-label" for="signup-name">이름</label>
            <div class="input-icon-wrap">
              <span class="input-icon"><UserCircle :size="16" /></span>
              <input id="signup-name" v-model="name" type="text" class="input-base"
                :class="{ 'input-error': nameError }" placeholder="이름을 입력하세요"
                @blur="validateName" />
            </div>
            <div v-if="nameError" class="field-error" style="display:flex"><AlertCircle :size="14" /><span>{{ nameError }}</span></div>
          </div>

          <div class="form-group">
            <label class="form-label" for="signup-email">이메일</label>
            <div class="input-icon-wrap">
              <span class="input-icon"><Mail :size="16" /></span>
              <input id="signup-email" v-model="email" type="email" class="input-base"
                :class="{ 'input-error': emailError }" placeholder="이메일을 입력하세요"
                @blur="validateEmail" />
            </div>
            <div v-if="emailError" class="field-error" style="display:flex"><AlertCircle :size="14" /><span>{{ emailError }}</span></div>
          </div>

          <div class="form-group">
            <label class="form-label" for="signup-password">비밀번호</label>
            <div class="input-icon-wrap" style="position:relative">
              <span class="input-icon"><Lock :size="16" /></span>
              <input id="signup-password" v-model="password" :type="showPw ? 'text' : 'password'"
                class="input-base" :class="{ 'input-error': passwordError }"
                style="padding-right:3rem" placeholder="비밀번호 8자 이상"
                @blur="validatePassword" />
              <button type="button" class="pw-toggle" @click="showPw = !showPw">
                <Eye v-if="!showPw" :size="16" /><EyeOff v-else :size="16" />
              </button>
            </div>
            <div v-if="passwordError" class="field-error" style="display:flex"><AlertCircle :size="14" /><span>{{ passwordError }}</span></div>
          </div>

          <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
            <Loader2 v-if="loading" :size="16" class="animate-spin" />
            <span>{{ loading ? '가입 중...' : '회원가입' }}</span>
          </button>
        </form>
      </div>
      <div class="auth-links">
        <RouterLink to="/login">이미 계정이 있으신가요? 로그인</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { Home, Mail, Lock, Eye, EyeOff, AlertCircle, Loader2, UserCircle } from 'lucide-vue-next'
import { api } from '../api/index.js'
import '../../css/pages/auth.css'

const router = useRouter()
const name = ref('')
const email = ref('')
const password = ref('')
const showPw = ref(false)
const loading = ref(false)
const generalError = ref('')
const nameError = ref('')
const emailError = ref('')
const passwordError = ref('')

function validateName() {
  if (!name.value.trim()) { nameError.value = '이름을 입력해 주세요'; return false }
  nameError.value = ''; return true
}
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
  const ok = validateName() & validateEmail() & validatePassword()
  if (!ok) return
  loading.value = true
  try {
    await api.post('/api/members', { email: email.value.trim(), password: password.value, name: name.value.trim() })
    alert('회원가입이 완료되었습니다. 로그인해 주세요.')
    router.push('/login')
  } catch (err) {
    generalError.value = err.data?.message ?? '회원가입에 실패했습니다'
  } finally {
    loading.value = false
  }
}
</script>
