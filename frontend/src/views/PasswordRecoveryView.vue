<template>
  <div class="auth-page">
    <div class="auth-wrap-sm">
      <div class="auth-card card-lg">
        <div class="auth-card-header">
          <div class="auth-logo-icon"><Home :size="20" /></div>
          <h1 style="color:#1A3C6E;font-size:1.5rem;font-weight:700;margin-bottom:0.25rem">비밀번호 찾기</h1>
          <p style="color:#9ca3af;font-size:0.8125rem;margin:0">가입 이메일을 입력하여 비밀번호를 재설정하세요</p>
        </div>

        <div v-if="!submitted" class="auth-card-form">
          <div class="form-group">
            <label class="form-label" for="recovery-email">이메일</label>
            <div class="input-icon-wrap">
              <span class="input-icon"><Mail :size="16" /></span>
              <input id="recovery-email" v-model="email" type="email" class="input-base" placeholder="가입 시 이메일을 입력하세요" />
            </div>
          </div>
          <button class="btn btn-primary btn-full" @click="handleSubmit">인증 메일 발송</button>
        </div>

        <div v-else style="padding:1.5rem;text-align:center">
          <CheckCircle2 :size="48" style="color:#27AE60;margin-bottom:1rem" />
          <p style="color:#1A3C6E;font-weight:600">입력하신 이메일로 안내 메일을 발송했습니다.</p>
          <p style="color:#9ca3af;font-size:0.8125rem;margin-top:0.5rem">메일함을 확인해 주세요.</p>
        </div>
      </div>
      <div class="auth-links">
        <RouterLink to="/login">로그인으로 돌아가기</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { Home, Mail, CheckCircle2 } from 'lucide-vue-next'
import '../../css/pages/auth.css'

const email = ref('')
const submitted = ref(false)

function handleSubmit() {
  if (!email.value.trim()) return
  submitted.value = true
}
</script>
