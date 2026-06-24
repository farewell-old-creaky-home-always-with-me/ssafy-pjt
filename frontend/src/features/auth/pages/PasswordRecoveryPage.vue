<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { Home, Mail, CheckCircle2, UserCircle, Phone, AlertCircle, Loader2 } from 'lucide-vue-next'
import { membersApi } from '@/api/index.js'
import BaseButton from '@/components/base/BaseButton.vue'

const name = ref('')
const email = ref('')
const phone = ref('')
const submitted = ref(false)
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  if (!name.value.trim() || !email.value.trim() || !phone.value.trim()) {
    error.value = '이름, 이메일, 전화번호를 모두 입력해 주세요'
    return
  }
  loading.value = true
  error.value = ''
  try {
    await membersApi.resetPassword({
      name: name.value.trim(),
      email: email.value.trim(),
      phone: phone.value.trim()
    })
    submitted.value = true
  } catch (err) {
    error.value = err.data?.message ?? '임시 비밀번호 발송에 실패했습니다'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page flex items-center justify-center py-12 px-4">
    <div class="w-full max-w-[440px]">
      <div class="w-full bg-white rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.1)] border border-gray-100 overflow-hidden">
        <div class="flex flex-col items-center px-8 pt-10 pb-6">
          <div class="w-14 h-14 rounded-2xl bg-blue flex items-center justify-center text-white mb-5 shadow-[0_4px_12px_rgba(45,156,219,0.25)]">
            <Home :size="20" />
          </div>
          <h1 class="text-navy text-2xl font-bold mb-1">비밀번호 찾기</h1>
          <p class="text-gray-400 text-[0.8125rem] m-0">가입 이메일을 입력하여 비밀번호를 재설정하세요</p>
        </div>

        <form v-if="!submitted" class="px-8 pb-8" @submit.prevent="handleSubmit" novalidate>
          <div v-if="error" class="flex items-center gap-2 bg-red/5 border border-red/20 rounded-xl px-4 py-3 mb-5 text-red text-[0.8125rem] font-medium">
            <AlertCircle :size="16" /><span>{{ error }}</span>
          </div>

          <div class="mb-4">
            <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="recovery-name">이름</label>
            <div class="relative">
              <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><UserCircle :size="16" /></span>
              <input id="recovery-name" v-model="name" type="text"
                class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                placeholder="가입 시 이름을 입력하세요" />
            </div>
          </div>

          <div class="mb-4">
            <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="recovery-email">이메일</label>
            <div class="relative">
              <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><Mail :size="16" /></span>
              <input id="recovery-email" v-model="email" type="email"
                class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                placeholder="가입 시 이메일을 입력하세요" />
            </div>
          </div>

          <div class="mb-6">
            <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="recovery-phone">전화번호</label>
            <div class="relative">
              <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><Phone :size="16" /></span>
              <input id="recovery-phone" v-model="phone" type="tel"
                class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                placeholder="010-1234-5678" />
            </div>
          </div>

          <BaseButton type="submit" :full="true" :disabled="loading">
            <Loader2 v-if="loading" :size="16" class="animate-spin" />
            <span>{{ loading ? '발송 중...' : '임시 비밀번호 발송' }}</span>
          </BaseButton>
        </form>

        <div v-else class="px-6 pb-6 pt-4 text-center">
          <CheckCircle2 :size="48" class="text-green mx-auto mb-4" />
          <p class="text-navy font-semibold">입력하신 이메일로 안내 메일을 발송했습니다.</p>
          <p class="text-gray-400 text-[0.8125rem] mt-2">메일함을 확인해 주세요.</p>
        </div>
      </div>

      <div class="flex items-center justify-center gap-4 mt-6">
        <RouterLink to="/login" class="text-gray-500 text-[0.8125rem] font-medium transition-colors hover:text-blue">로그인으로 돌아가기</RouterLink>
      </div>
    </div>
  </div>
</template>
