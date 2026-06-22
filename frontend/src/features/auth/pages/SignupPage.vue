<script setup>
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { Home, Mail, Lock, Eye, EyeOff, AlertCircle, Loader2, UserCircle } from 'lucide-vue-next'
import { membersApi } from '@/api/index.js'
import BaseButton from '@/components/base/BaseButton.vue'

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
  const nameValid = validateName()
  const emailValid = validateEmail()
  const passwordValid = validatePassword()
  const ok = nameValid && emailValid && passwordValid
  if (!ok) return
  loading.value = true
  try {
    await membersApi.createMember({ email: email.value.trim(), password: password.value, name: name.value.trim() })
    alert('회원가입이 완료되었습니다. 로그인해 주세요.')
    router.push('/login')
  } catch (err) {
    generalError.value = err.data?.message ?? '회원가입에 실패했습니다'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page flex items-center justify-center py-12 px-4">
    <div class="w-full max-w-[480px]">
      <div class="w-full bg-white rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.1)] border border-gray-100 overflow-hidden">
        <div class="flex flex-col items-center px-8 pt-10 pb-6">
          <div class="w-14 h-14 rounded-2xl bg-blue flex items-center justify-center text-white mb-5 shadow-[0_4px_12px_rgba(45,156,219,0.25)]">
            <Home :size="20" />
          </div>
          <h1 class="text-navy text-2xl font-bold mb-1">회원가입</h1>
          <p class="text-gray-400 text-[0.8125rem] m-0">SSAFY Home 계정을 생성하세요</p>
        </div>

        <form class="px-8 pb-8" @submit.prevent="handleSubmit" novalidate>
          <div v-if="generalError" class="flex items-center gap-2 bg-red/5 border border-red/20 rounded-xl px-4 py-3 mb-5 text-red text-[0.8125rem] font-medium">
            <AlertCircle :size="16" /><span>{{ generalError }}</span>
          </div>

          <div class="mb-4">
            <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="signup-name">이름</label>
            <div class="relative">
              <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><UserCircle :size="16" /></span>
              <input id="signup-name" v-model="name" type="text"
                class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                :class="{ 'border-red! shadow-[0_0_0_3px_rgba(235,87,87,0.15)]!': nameError }"
                placeholder="이름을 입력하세요" @blur="validateName" />
            </div>
            <div v-if="nameError" class="flex items-center gap-1.5 mt-1.5 pl-1 text-red text-xs">
              <AlertCircle :size="14" /><span>{{ nameError }}</span>
            </div>
          </div>

          <div class="mb-4">
            <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="signup-email">이메일</label>
            <div class="relative">
              <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><Mail :size="16" /></span>
              <input id="signup-email" v-model="email" type="email"
                class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                :class="{ 'border-red! shadow-[0_0_0_3px_rgba(235,87,87,0.15)]!': emailError }"
                placeholder="이메일을 입력하세요" @blur="validateEmail" />
            </div>
            <div v-if="emailError" class="flex items-center gap-1.5 mt-1.5 pl-1 text-red text-xs">
              <AlertCircle :size="14" /><span>{{ emailError }}</span>
            </div>
          </div>

          <div class="mb-6">
            <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="signup-password">비밀번호</label>
            <div class="relative">
              <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><Lock :size="16" /></span>
              <input id="signup-password" v-model="password" :type="showPw ? 'text' : 'password'"
                class="w-full pl-11 pr-12 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                :class="{ 'border-red! shadow-[0_0_0_3px_rgba(235,87,87,0.15)]!': passwordError }"
                placeholder="비밀번호 8자 이상" @blur="validatePassword" />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 w-8 h-8 flex items-center justify-center rounded-lg text-gray-400 transition-colors hover:bg-gray-400/20 hover:text-gray-500"
                :aria-label="showPw ? '비밀번호 숨기기' : '비밀번호 보기'"
                :aria-pressed="showPw"
                @click="showPw = !showPw"
              >
                <Eye v-if="!showPw" :size="16" /><EyeOff v-else :size="16" />
              </button>
            </div>
            <div v-if="passwordError" class="flex items-center gap-1.5 mt-1.5 pl-1 text-red text-xs">
              <AlertCircle :size="14" /><span>{{ passwordError }}</span>
            </div>
          </div>

          <BaseButton type="submit" :full="true" :disabled="loading">
            <Loader2 v-if="loading" :size="16" class="animate-spin" />
            <span>{{ loading ? '가입 중...' : '회원가입' }}</span>
          </BaseButton>
        </form>
      </div>

      <div class="flex items-center justify-center gap-4 mt-6">
        <RouterLink to="/login" class="text-gray-500 text-[0.8125rem] font-medium transition-colors hover:text-blue">이미 계정이 있으신가요? 로그인</RouterLink>
      </div>
    </div>
  </div>
</template>
