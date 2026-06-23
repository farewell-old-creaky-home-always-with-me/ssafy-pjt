<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronLeft } from 'lucide-vue-next'
import { qnasApi } from '@/api/index.js'
import QnaForm from '../components/QnaForm.vue'

const router = useRouter()
const saving = ref(false)
const error = ref('')

async function handleSubmit(payload) {
  saving.value = true
  error.value = ''
  try {
    const created = await qnasApi.createQna(payload)
    const id = created?.qnaId ?? created?.id
    router.push(id ? `/qnas/${id}` : '/qnas')
  } catch {
    error.value = '질문을 등록하지 못했습니다. 입력 내용을 확인하고 다시 시도해 주세요.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="qna-page">
    <div class="qna-wrap">
      <button class="back-link" @click="router.push('/qnas')">
        <ChevronLeft :size="16" /> 목록으로
      </button>
      <div class="qna-page-head">
        <h1>질문하기</h1>
        <p>궁금한 내용을 작성하면 담당자가 확인 후 답변합니다.</p>
      </div>
      <div class="card">
        <QnaForm submit-label="등록" :loading="saving" :error="error" @submit="handleSubmit" @cancel="router.push('/qnas')" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.qna-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.qna-wrap { max-width:48rem; margin:0 auto; }
.back-link { display:inline-flex; align-items:center; gap:0.375rem; color:#6b7280; font-size:0.8125rem; font-weight:500; cursor:pointer; background:none; border:none; margin-bottom:1rem; transition:color 0.15s; }
.back-link:hover { color:#2D9CDB; }
.qna-page-head { margin-bottom:1rem; }
.qna-page-head h1 { color:#1A3C6E; font-size:1.5rem; font-weight:700; }
.qna-page-head p { color:#9ca3af; font-size:0.8125rem; margin-top:0.25rem; }
</style>
