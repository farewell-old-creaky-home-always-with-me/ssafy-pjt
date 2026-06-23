<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { AlertCircle, Calendar, ChevronLeft, ChevronRight, FileQuestion, PenLine, Search } from 'lucide-vue-next'
import { qnasApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/authStore.js'
import { formatDate } from '@/utils/date.js'

const router = useRouter()
const authStore = useAuthStore()

const qnas = ref([])
const loading = ref(false)
const error = ref('')
const page = ref(1)
const totalPages = ref(1)
const filters = reactive({
  keyword: '',
  status: '',
})

const pageRange = computed(() => {
  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  return Array.from({ length: Math.max(0, end - start + 1) }, (_, i) => start + i)
})

function qnaId(qna) {
  return qna.qnaId ?? qna.id
}

function statusLabel(status) {
  return status === 'ANSWERED' ? '답변 완료' : '답변 대기'
}

function normalizePage(res, requestedPage) {
  const items = res.items ?? res.content ?? res.data ?? res
  const size = res.size ?? 10
  const total = res.total ?? res.totalElements
  const totalPageCount = res.totalPages ?? (total ? Math.max(1, Math.ceil(total / size)) : 1)
  return {
    items: Array.isArray(items) ? items : [],
    page: res.page ?? requestedPage,
    totalPages: totalPageCount,
  }
}

async function loadPage(nextPage = 1) {
  loading.value = true
  error.value = ''
  try {
    const params = {
      page: nextPage,
      size: 10,
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
    }
    const normalized = normalizePage(await qnasApi.getQnas(params), nextPage)
    qnas.value = normalized.items
    page.value = normalized.page
    totalPages.value = normalized.totalPages
  } catch {
    error.value = 'Q&A 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}

let searchTimer = null
watch(() => filters.keyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadPage(1), 300)
})

watch(() => filters.status, () => loadPage(1))

onBeforeUnmount(() => {
  clearTimeout(searchTimer)
})

onMounted(() => loadPage(1))
</script>

<template>
  <div class="qna-page">
    <div class="qna-wrap">
      <div class="qna-page-head">
        <div>
          <h1>Q&A</h1>
          <p>궁금한 내용을 남기고 답변 상태를 확인하세요.</p>
        </div>
        <button v-if="authStore.user" class="btn btn-primary btn-sm" @click="router.push('/qnas/new')">
          <PenLine :size="15" /> 질문하기
        </button>
      </div>

      <div class="qna-toolbar">
        <div class="qna-search">
          <Search :size="18" />
          <input v-model="filters.keyword" type="text" placeholder="제목 또는 내용 검색" />
        </div>
        <select v-model="filters.status" class="qna-status-filter" aria-label="답변 상태">
          <option value="">전체</option>
          <option value="WAITING">답변 대기</option>
          <option value="ANSWERED">답변 완료</option>
        </select>
      </div>

      <div v-if="error" class="general-error" style="display:flex">
        <AlertCircle :size="16" /><span>{{ error }}</span>
      </div>
      <div v-else-if="loading" class="qna-loading">불러오는 중...</div>
      <template v-else>
        <div class="card qna-list-card">
          <div class="qna-list-header">
            <span>번호</span>
            <span>제목</span>
            <span>작성자</span>
            <span>상태</span>
            <span>작성일</span>
          </div>

          <div v-if="qnas.length === 0" class="qna-empty">
            <FileQuestion :size="42" />
            <p>등록된 질문이 없습니다.</p>
          </div>

          <button v-for="qna in qnas" :key="qnaId(qna)" class="qna-row" @click="router.push('/qnas/' + qnaId(qna))">
            <span class="qna-no">{{ qnaId(qna) }}</span>
            <span class="qna-title">{{ qna.title }}</span>
            <span class="qna-author">{{ qna.authorName ?? qna.memberName ?? qna.writerName ?? '-' }}</span>
            <span class="qna-status" :class="{ answered: qna.status === 'ANSWERED' }">{{ statusLabel(qna.status) }}</span>
            <span class="qna-date"><Calendar :size="12" />{{ formatDate(qna.createdAt) }}</span>
          </button>
        </div>

        <div class="qna-pagination">
          <button class="page-btn" :disabled="page === 1" @click="loadPage(page - 1)"><ChevronLeft :size="14" /></button>
          <button v-for="p in pageRange" :key="p" class="page-btn" :class="{ active: p === page }" @click="loadPage(p)">{{ p }}</button>
          <button class="page-btn" :disabled="page >= totalPages" @click="loadPage(page + 1)"><ChevronRight :size="14" /></button>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.qna-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.qna-wrap { max-width:60rem; margin:0 auto; }
.qna-page-head { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:0.75rem; margin-bottom:1.5rem; }
.qna-page-head h1 { color:#1A3C6E; font-size:1.5rem; font-weight:700; }
.qna-page-head p { color:#9ca3af; font-size:0.8125rem; margin-top:0.25rem; }
.qna-toolbar { display:flex; gap:0.75rem; margin-bottom:1rem; }
.qna-search { position:relative; flex:1; }
.qna-search svg { position:absolute; left:1rem; top:50%; transform:translateY(-50%); color:#9ca3af; pointer-events:none; }
.qna-search input, .qna-status-filter { width:100%; height:2.75rem; border-radius:0.75rem; background:#fff; border:1px solid #e5e7eb; color:#1A3C6E; font-size:0.875rem; outline:none; box-shadow:0 1px 3px rgba(0,0,0,0.05); }
.qna-search input { padding:0 1rem 0 2.75rem; }
.qna-status-filter { width:9rem; padding:0 0.75rem; }
.qna-list-card { overflow:hidden; }
.qna-list-header { display:none; grid-template-columns:64px 1fr 120px 104px 112px; gap:0.75rem; background:#F4F6F9; padding:0.75rem 1.25rem; color:#1A3C6E; font-size:0.75rem; font-weight:600; }
.qna-list-header span:not(:nth-child(2)) { text-align:center; }
.qna-row { width:100%; text-align:left; display:flex; flex-direction:column; gap:0.375rem; padding:1rem 1.25rem; border:0; border-bottom:1px solid #f3f4f6; background:#fff; cursor:pointer; transition:background 0.15s; }
.qna-row:hover { background:rgba(45,156,219,0.03); }
.qna-no { display:none; color:#9ca3af; font-size:0.8125rem; text-align:center; }
.qna-title { color:#1A3C6E; font-size:0.9375rem; font-weight:600; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.qna-author, .qna-date { color:#9ca3af; font-size:0.75rem; display:flex; align-items:center; gap:0.25rem; }
.qna-status { width:max-content; border-radius:999px; padding:0.25rem 0.625rem; background:#FEF3C7; color:#92400E; font-size:0.75rem; font-weight:700; }
.qna-status.answered { background:#E0F2FE; color:#0369A1; }
.qna-loading { padding:4rem; text-align:center; color:#9ca3af; }
.qna-empty { padding:4rem 1rem; display:flex; flex-direction:column; align-items:center; color:#9ca3af; gap:0.75rem; }
.qna-empty svg { color:#d1d5db; }
.qna-pagination { display:flex; justify-content:center; gap:0.25rem; margin-top:1rem; }
@media (min-width:720px) {
  .qna-list-header { display:grid; }
  .qna-row { display:grid; grid-template-columns:64px 1fr 120px 104px 112px; align-items:center; gap:0.75rem; }
  .qna-no { display:block; }
  .qna-status { justify-self:center; }
  .qna-date { justify-content:center; }
  .qna-author { justify-content:center; }
}
@media (max-width:560px) {
  .qna-toolbar { flex-direction:column; }
  .qna-status-filter { width:100%; }
}
</style>
