<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AlertCircle, Calendar, ChevronLeft, MessageSquareText, Pencil, Trash2, UserCircle } from 'lucide-vue-next'
import { boardsApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/authStore.js'
import { formatDate } from '@/utils/date.js'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const board = ref(null)
const loading = ref(false)
const deleting = ref(false)
const error = ref('')

const boardId = computed(() => board.value?.boardId ?? board.value?.id ?? route.params.id)
const authorId = computed(() => board.value?.memberId ?? board.value?.authorId ?? board.value?.writerId)
const isAuthor = computed(() => {
  const currentMemberId = authStore.user?.memberId
  return currentMemberId != null && authorId.value != null && String(currentMemberId) === String(authorId.value)
})
const canDelete = computed(() => isAuthor.value || Boolean(authStore.user?.isAdmin))

async function loadBoard(id = route.params.id) {
  loading.value = true
  error.value = ''
  try {
    board.value = await boardsApi.getBoardDetail(id)
  } catch {
    error.value = '게시글을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  if (!confirm('게시글을 삭제하시겠습니까?')) return
  deleting.value = true
  error.value = ''
  try {
    await boardsApi.deleteBoard(boardId.value)
    router.push('/boards')
  } catch {
    error.value = '게시글을 삭제하지 못했습니다. 권한을 확인하거나 잠시 후 다시 시도해 주세요.'
  } finally {
    deleting.value = false
  }
}

watch(() => route.params.id, (id) => loadBoard(id), { immediate: true })
</script>

<template>
  <div class="board-page">
    <div class="board-wrap">
      <button class="back-link" @click="router.push('/boards')">
        <ChevronLeft :size="16" /> 목록으로
      </button>

      <div v-if="loading" class="board-loading">불러오는 중...</div>
      <div v-else-if="error" class="general-error" style="display:flex">
        <AlertCircle :size="16" />
        <span>{{ error }}</span>
      </div>
      <article v-else-if="board" class="card board-detail-card">
        <header class="board-detail-header">
          <div class="board-title-line">
            <MessageSquareText :size="18" />
            <h1>{{ board.title }}</h1>
          </div>
          <div class="board-meta">
            <span><UserCircle :size="14" />{{ board.authorName ?? '-' }}</span>
            <span><Calendar :size="14" />{{ formatDate(board.createdAt) }}</span>
            <span v-if="board.updatedAt">수정 {{ formatDate(board.updatedAt) }}</span>
          </div>
          <div v-if="isAuthor || canDelete" class="board-actions">
            <button v-if="isAuthor" class="btn btn-ghost btn-sm" @click="router.push(`/boards/${boardId}/edit`)">
              <Pencil :size="14" /> 수정
            </button>
            <button v-if="canDelete" class="btn btn-outline-danger btn-sm" :disabled="deleting" @click="handleDelete">
              <Trash2 :size="14" /> {{ deleting ? '삭제 중...' : '삭제' }}
            </button>
          </div>
        </header>

        <section class="board-content">
          {{ board.content }}
        </section>
      </article>
    </div>
  </div>
</template>

<style scoped>
.board-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.board-wrap { max-width:56rem; margin:0 auto; }
.back-link { display:inline-flex; align-items:center; gap:0.375rem; color:#6b7280; font-size:0.8125rem; font-weight:500; cursor:pointer; background:none; border:none; margin-bottom:1rem; transition:color 0.15s; }
.back-link:hover { color:#2D9CDB; }
.board-loading { padding:4rem; text-align:center; color:#9ca3af; }
.board-detail-card { overflow:hidden; }
.board-detail-header { padding:1.25rem 1.5rem; border-bottom:1px solid #f3f4f6; }
.board-title-line { display:flex; align-items:flex-start; gap:0.625rem; color:#1A3C6E; }
.board-title-line svg { color:#2D9CDB; flex-shrink:0; margin-top:0.25rem; }
.board-title-line h1 { font-size:1.25rem; font-weight:700; line-height:1.4; }
.board-meta { display:flex; align-items:center; gap:1rem; flex-wrap:wrap; margin-top:0.75rem; color:#9ca3af; font-size:0.75rem; }
.board-meta span { display:flex; align-items:center; gap:0.25rem; }
.board-actions { display:flex; justify-content:flex-end; gap:0.5rem; margin-top:1rem; flex-wrap:wrap; }
.board-content { padding:1.5rem; color:#4b5563; font-size:0.9375rem; line-height:1.85; white-space:pre-wrap; min-height:12rem; }
@media (max-width:480px) {
  .board-actions .btn { width:100%; }
}
</style>
