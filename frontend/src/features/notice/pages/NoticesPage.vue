<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, FileText, Calendar, ChevronLeft, ChevronRight, AlertCircle } from 'lucide-vue-next'
import { noticesApi } from '@/api/index.js'
import { formatDate } from '@/utils/date.js'

const router = useRouter()
const notices = ref([])
const loading = ref(false)
const error = ref('')
const searchQuery = ref('')
const page = ref(1)
const totalPages = ref(1)


const filtered = computed(() =>
  notices.value.filter(n => n.title.toLowerCase().includes(searchQuery.value.toLowerCase()))
)

const pageRange = computed(() => {
  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

async function loadPage(p) {
  loading.value = true
  error.value = ''
  try {
    const res = await noticesApi.getNotices({ page: p, size: 20 })
    notices.value = res.content ?? res
    totalPages.value = res.totalPages ?? 1
    page.value = p
  } catch {
    error.value = '공지사항을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally { loading.value = false }
}

onMounted(() => loadPage(1))
</script>

<template>
  <div class="notices-page">
    <div class="notices-wrap">
      <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:0.75rem;margin-bottom:1.5rem">
        <div>
          <h1 style="color:#1A3C6E;font-size:1.375rem;font-weight:700">공지사항</h1>
          <p style="color:#9ca3af;font-size:0.8125rem;margin-top:0.25rem">SSAFY Home 서비스 관련 공지사항입니다</p>
        </div>
      </div>

      <div class="notice-search">
        <Search :size="18" />
        <input v-model="searchQuery" type="text" placeholder="제목으로 검색..." />
      </div>

      <div v-if="error" class="general-error" style="display:flex">
        <AlertCircle :size="16" /><span>{{ error }}</span>
      </div>

      <div v-if="loading" style="padding:4rem;text-align:center;color:#9ca3af">불러오는 중...</div>
      <div v-else class="card" style="overflow:hidden">
        <div class="notice-list-header">
          <span style="color:#1A3C6E;font-size:0.75rem;font-weight:600;text-align:center">번호</span>
          <span style="color:#1A3C6E;font-size:0.75rem;font-weight:600">제목</span>
          <span style="color:#1A3C6E;font-size:0.75rem;font-weight:600;text-align:center">작성일</span>
          <span style="color:#1A3C6E;font-size:0.75rem;font-weight:600;text-align:center">작성자</span>
        </div>
        <div v-if="filtered.length === 0" style="padding:4rem 1rem;display:flex;flex-direction:column;align-items:center">
          <FileText :size="40" style="color:#d1d5db;margin-bottom:0.75rem" />
          <p style="color:#9ca3af;font-size:0.875rem">공지사항이 없습니다</p>
        </div>
        <button v-for="n in filtered" :key="n.noticeId" class="notice-row" @click="router.push('/notices/' + n.noticeId)">
          <span class="notice-no">{{ n.noticeId }}</span>
          <span class="notice-title">{{ n.title }}</span>
          <span class="notice-date"><Calendar :size="12" />{{ formatDate(n.createdAt) }}</span>
          <span class="notice-views">{{ n.authorName }}</span>
        </button>
      </div>

      <!-- Pagination -->
      <div style="display:flex;justify-content:center;gap:0.25rem;margin-top:1rem">
        <button class="page-btn" :disabled="page === 1" @click="loadPage(page - 1)"><ChevronLeft :size="14" /></button>
        <button v-for="p in pageRange" :key="p" class="page-btn" :class="{ active: p === page }" @click="loadPage(p)">{{ p }}</button>
        <button class="page-btn" :disabled="page >= totalPages" @click="loadPage(page + 1)"><ChevronRight :size="14" /></button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notices-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.notices-wrap { max-width:56rem; margin:0 auto; }
.notice-list-header { display:none; grid-template-columns:60px 1fr 100px 80px; background:#F4F6F9; padding:0.75rem 1.25rem; gap:0.75rem; }
@media (min-width:640px) { .notice-list-header { display:grid; } }
.notice-row { width:100%; text-align:left; display:flex; flex-direction:column; padding:1rem 1.25rem; gap:0.25rem; border-bottom:1px solid #f9fafb; cursor:pointer; background:none; border-left:none; border-right:none; border-top:none; transition:background 0.15s; }
@media (min-width:640px) { .notice-row { display:grid; grid-template-columns:60px 1fr 100px 80px; align-items:center; gap:0.75rem; } }
.notice-row:hover { background:rgba(45,156,219,0.03); }
.notice-no { color:#9ca3af; font-size:0.8125rem; text-align:center; display:none; }
@media (min-width:640px) { .notice-no { display:block; } }
.notice-title { color:#1A3C6E; font-size:0.875rem; font-weight:500; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.notice-date, .notice-views { color:#9ca3af; font-size:0.75rem; display:flex; align-items:center; gap:0.25rem; }
.notice-search { position:relative; margin-bottom:1rem; }
.notice-search svg { position:absolute; left:1rem; top:50%; transform:translateY(-50%); color:#9ca3af; pointer-events:none; }
.notice-search input { width:100%; padding:0.75rem 1rem 0.75rem 2.75rem; border-radius:0.75rem; background:#fff; border:1px solid #e5e7eb; color:#1A3C6E; font-size:0.875rem; outline:none; box-shadow:0 1px 3px rgba(0,0,0,0.05); }
.notice-search input:focus { border-color:#2D9CDB; box-shadow:0 0 0 3px rgba(45,156,219,0.15); }
</style>
