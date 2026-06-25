<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronLeft } from 'lucide-vue-next'
import { boardsApi } from '@/api/index.js'
import BoardForm from '../components/BoardForm.vue'

const router = useRouter()
const saving = ref(false)
const error = ref('')

async function handleSubmit(payload) {
  saving.value = true
  error.value = ''
  try {
    const created = await boardsApi.createBoard(payload)
    const id = created?.boardId ?? created?.id
    router.push(id ? `/boards/${id}` : '/boards')
  } catch {
    error.value = '게시글을 등록하지 못했습니다. 입력 내용을 확인하고 다시 시도해 주세요.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="board-page">
    <div class="board-wrap">
      <button class="back-link" @click="router.push('/boards')">
        <ChevronLeft :size="16" /> 목록으로
      </button>
      <div class="board-page-head">
        <h1>게시글 작성</h1>
        <p>공유하고 싶은 지역 정보나 이야기를 작성해 주세요.</p>
      </div>
      <div class="card">
        <BoardForm submit-label="등록" :loading="saving" :error="error" @submit="handleSubmit" @cancel="router.push('/boards')" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.board-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.board-wrap { max-width:48rem; margin:0 auto; }
.back-link { display:inline-flex; align-items:center; gap:0.375rem; color:#6b7280; font-size:0.8125rem; font-weight:500; cursor:pointer; background:none; border:none; margin-bottom:1rem; transition:color 0.15s; }
.back-link:hover { color:#2D9CDB; }
.board-page-head { margin-bottom:1rem; }
.board-page-head h1 { color:#1A3C6E; font-size:1.5rem; font-weight:700; }
.board-page-head p { color:#9ca3af; font-size:0.8125rem; margin-top:0.25rem; }
</style>
