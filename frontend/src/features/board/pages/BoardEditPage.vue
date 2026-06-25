<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AlertCircle, ChevronLeft } from 'lucide-vue-next'
import { boardsApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/authStore.js'
import BoardForm from '../components/BoardForm.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const board = ref(null)
const loading = ref(false)
const saving = ref(false)
const error = ref('')

const boardId = computed(() => board.value?.boardId ?? board.value?.id ?? route.params.id)

async function loadBoard() {
  loading.value = true
  error.value = ''
  try {
    const data = await boardsApi.getBoardDetail(route.params.id)
    if (String(authStore.user?.memberId) !== String(data.memberId ?? data.authorId ?? data.writerId)) {
      alert('작성자만 수정할 수 있습니다.')
      router.push(`/boards/${data.boardId ?? data.id ?? route.params.id}`)
      return
    }
    board.value = data
  } catch (err) {
    if (err.status === 403) {
      alert('작성자만 수정할 수 있습니다.')
      router.push(`/boards/${route.params.id}`)
      return
    }
    error.value = '게시글을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}

async function handleSubmit(payload) {
  saving.value = true
  error.value = ''
  try {
    await boardsApi.updateBoard(boardId.value, payload)
    router.push(`/boards/${boardId.value}`)
  } catch (err) {
    if (err.status === 403) {
      alert('작성자만 수정할 수 있습니다.')
      router.push(`/boards/${boardId.value}`)
      return
    }
    error.value = '게시글을 수정하지 못했습니다. 입력 내용을 확인하고 다시 시도해 주세요.'
  } finally {
    saving.value = false
  }
}

onMounted(loadBoard)
</script>

<template>
  <div class="board-page">
    <div class="board-wrap">
      <button class="back-link" @click="router.push(board ? `/boards/${boardId}` : '/boards')">
        <ChevronLeft :size="16" /> 돌아가기
      </button>
      <div class="board-page-head">
        <h1>게시글 수정</h1>
        <p>등록한 게시글의 제목과 내용을 수정합니다.</p>
      </div>

      <div v-if="loading" class="board-loading">불러오는 중...</div>
      <div v-else-if="error && !board" class="general-error" style="display:flex">
        <AlertCircle :size="16" />
        <span>{{ error }}</span>
      </div>
      <div v-else-if="board" class="card">
        <BoardForm
          submit-label="수정"
          :initial-value="board"
          :loading="saving"
          :error="error"
          @submit="handleSubmit"
          @cancel="router.push(`/boards/${boardId}`)"
        />
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
.board-loading { padding:4rem; text-align:center; color:#9ca3af; }
</style>
