<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { AlertCircle, Calendar, ChevronLeft, ChevronRight, MessageSquareText, PenLine, Trash2, UserCircle } from 'lucide-vue-next'
import { boardsApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/authStore.js'
import { formatDate } from '@/utils/date.js'

const router = useRouter()
const authStore = useAuthStore()

const boards = ref([])
const loading = ref(false)
const deletingId = ref(null)
const error = ref('')
const page = ref(1)
const totalPages = ref(1)
const size = 10

const pageRange = computed(() => {
  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  return Array.from({ length: Math.max(0, end - start + 1) }, (_, i) => start + i)
})

function boardId(board) {
  return board.boardId ?? board.id
}

function canDeleteBoard(board) {
  const currentMemberId = authStore.user?.memberId
  const authorId = board.memberId ?? board.authorId ?? board.writerId
  return Boolean(authStore.user?.isAdmin)
    || (currentMemberId != null && authorId != null && String(currentMemberId) === String(authorId))
}

function normalizePage(res, requestedPage) {
  const items = res.items ?? res.content ?? res.data ?? res
  const responseSize = res.size ?? size
  const total = res.total ?? res.totalElements
  return {
    items: Array.isArray(items) ? items : [],
    page: res.page ?? requestedPage,
    totalPages: res.totalPages ?? (total ? Math.max(1, Math.ceil(total / responseSize)) : 1),
  }
}

async function loadPage(nextPage = 1) {
  loading.value = true
  error.value = ''
  try {
    const normalized = normalizePage(await boardsApi.getBoards({ page: nextPage, size }), nextPage)
    boards.value = normalized.items
    page.value = normalized.page
    totalPages.value = normalized.totalPages
  } catch {
    error.value = '공유게시판 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}

async function handleDelete(board) {
  const id = boardId(board)
  if (!id || !confirm('게시글을 삭제하시겠습니까?')) return
  deletingId.value = id
  error.value = ''
  try {
    await boardsApi.deleteBoard(id)
    await loadPage(page.value)
  } catch {
    error.value = '게시글을 삭제하지 못했습니다. 권한을 확인하거나 잠시 후 다시 시도해 주세요.'
  } finally {
    deletingId.value = null
  }
}

onMounted(() => loadPage(1))
</script>

<template>
  <div class="board-page">
    <div class="board-wrap">
      <div class="board-page-head">
        <div>
          <h1>공유게시판</h1>
          <p>동네 정보와 생활 팁을 자유롭게 공유하세요.</p>
        </div>
        <button v-if="authStore.user" class="btn btn-primary btn-sm" @click="router.push('/boards/new')">
          <PenLine :size="15" /> 글쓰기
        </button>
      </div>

      <div v-if="error" class="general-error" style="display:flex">
        <AlertCircle :size="16" /><span>{{ error }}</span>
      </div>
      <div v-else-if="loading" class="board-loading">불러오는 중...</div>
      <template v-else>
        <div class="card board-list-card">
          <div class="board-list-header">
            <span>번호</span>
            <span>제목</span>
            <span>작성자</span>
            <span>작성일</span>
            <span>관리</span>
          </div>

          <div v-if="boards.length === 0" class="board-empty">
            <MessageSquareText :size="42" />
            <p>등록된 게시글이 없습니다.</p>
          </div>

          <div
            v-for="board in boards"
            :key="boardId(board)"
            class="board-row"
            @click="router.push('/boards/' + boardId(board))"
          >
            <span class="board-no">{{ boardId(board) }}</span>
            <span class="board-title">{{ board.title }}</span>
            <span class="board-author"><UserCircle :size="13" />{{ board.authorName ?? '-' }}</span>
            <span class="board-date"><Calendar :size="12" />{{ formatDate(board.createdAt) }}</span>
            <span class="board-actions">
              <button
                v-if="canDeleteBoard(board)"
                class="board-delete-btn"
                :disabled="deletingId === boardId(board)"
                @click.stop="handleDelete(board)"
              >
                <Trash2 :size="13" /> {{ deletingId === boardId(board) ? '삭제 중' : '삭제' }}
              </button>
            </span>
          </div>
        </div>

        <div class="board-pagination">
          <button class="page-btn" :disabled="page === 1" @click="loadPage(page - 1)"><ChevronLeft :size="14" /></button>
          <button v-for="p in pageRange" :key="p" class="page-btn" :class="{ active: p === page }" @click="loadPage(p)">{{ p }}</button>
          <button class="page-btn" :disabled="page >= totalPages" @click="loadPage(page + 1)"><ChevronRight :size="14" /></button>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.board-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.board-wrap { max-width:60rem; margin:0 auto; }
.board-page-head { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:0.75rem; margin-bottom:1.5rem; }
.board-page-head h1 { color:#1A3C6E; font-size:1.5rem; font-weight:700; }
.board-page-head p { color:#9ca3af; font-size:0.8125rem; margin-top:0.25rem; }
.board-list-card { overflow:hidden; }
.board-list-header { display:none; grid-template-columns:72px 1fr 144px 120px 80px; gap:0.75rem; background:#F4F6F9; padding:0.75rem 1.25rem; color:#1A3C6E; font-size:0.75rem; font-weight:600; }
.board-list-header span:not(:nth-child(2)) { text-align:center; }
.board-row { width:100%; text-align:left; display:flex; flex-direction:column; gap:0.375rem; padding:1rem 1.25rem; border:0; border-bottom:1px solid #f3f4f6; background:#fff; cursor:pointer; transition:background 0.15s; }
.board-row:hover { background:rgba(45,156,219,0.03); }
.board-no { display:none; color:#9ca3af; font-size:0.8125rem; text-align:center; }
.board-title { color:#1A3C6E; font-size:0.9375rem; font-weight:600; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.board-author, .board-date { color:#9ca3af; font-size:0.75rem; display:flex; align-items:center; gap:0.25rem; }
.board-actions { min-height:1.75rem; }
.board-delete-btn { display:inline-flex; align-items:center; gap:0.25rem; border:1px solid rgba(220,38,38,0.25); border-radius:0.5rem; background:#fff; color:#DC2626; font-size:0.75rem; font-weight:600; padding:0.375rem 0.625rem; transition:background 0.15s, border-color 0.15s; }
.board-delete-btn:hover:not(:disabled) { background:rgba(220,38,38,0.06); border-color:rgba(220,38,38,0.45); }
.board-delete-btn:disabled { opacity:0.6; cursor:not-allowed; }
.board-loading { padding:4rem; text-align:center; color:#9ca3af; }
.board-empty { padding:4rem 1rem; display:flex; flex-direction:column; align-items:center; color:#9ca3af; gap:0.75rem; }
.board-empty svg { color:#d1d5db; }
.board-pagination { display:flex; justify-content:center; gap:0.25rem; margin-top:1rem; }
@media (min-width:720px) {
  .board-list-header { display:grid; }
  .board-row { display:grid; grid-template-columns:72px 1fr 144px 120px 80px; align-items:center; gap:0.75rem; }
  .board-no { display:block; }
  .board-author, .board-date, .board-actions { justify-content:center; display:flex; }
}
</style>
