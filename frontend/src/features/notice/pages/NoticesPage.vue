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
  <div class="min-h-[calc(100vh-64px)] bg-bg-page py-8 px-4">
    <div class="max-w-[56rem] mx-auto">
      <div class="flex items-center justify-between flex-wrap gap-3 mb-6">
        <div>
          <h1 class="text-navy text-[1.375rem] font-bold">공지사항</h1>
          <p class="text-gray-400 text-[0.8125rem] mt-1">SSAFY Home 서비스 관련 공지사항입니다</p>
        </div>
      </div>

      <!-- Search -->
      <div class="relative mb-4">
        <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none"><Search :size="18" /></span>
        <input
          v-model="searchQuery"
          type="text"
          class="w-full py-3 pl-11 pr-4 rounded-xl bg-white border border-[#e5e7eb] text-navy text-sm outline-none shadow-[0_1px_3px_rgba(0,0,0,0.05)] transition-all focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)] placeholder:text-gray-400"
          placeholder="제목으로 검색..."
        />
      </div>

      <div v-if="error" class="flex items-center gap-2 bg-red/5 border border-red/20 rounded-xl px-4 py-3 mb-4 text-red text-[0.8125rem] font-medium">
        <AlertCircle :size="16" /><span>{{ error }}</span>
      </div>

      <div v-if="loading" class="py-16 text-center text-gray-400">불러오는 중...</div>
      <div v-else class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] overflow-hidden">
        <!-- Table header (sm+) -->
        <div class="hidden sm:grid grid-cols-[60px_1fr_100px_80px] bg-bg-page px-5 py-3 gap-3">
          <span class="text-navy text-xs font-semibold text-center">번호</span>
          <span class="text-navy text-xs font-semibold">제목</span>
          <span class="text-navy text-xs font-semibold text-center">작성일</span>
          <span class="text-navy text-xs font-semibold text-center">작성자</span>
        </div>

        <div v-if="filtered.length === 0" class="py-16 px-4 flex flex-col items-center">
          <FileText :size="40" class="text-[#d1d5db] mb-3" />
          <p class="text-gray-400 text-sm">공지사항이 없습니다</p>
        </div>

        <button
          v-for="n in filtered"
          :key="n.noticeId"
          class="w-full text-left flex flex-col sm:grid sm:grid-cols-[60px_1fr_100px_80px] sm:items-center px-5 py-4 gap-1 sm:gap-3 border-b border-gray-50 last:border-none cursor-pointer bg-none transition-colors hover:bg-blue/[0.03]"
          @click="router.push('/notices/' + n.noticeId)"
        >
          <span class="hidden sm:block text-gray-400 text-[0.8125rem] text-center">{{ n.noticeId }}</span>
          <span class="text-navy text-sm font-medium truncate">{{ n.title }}</span>
          <span class="text-gray-400 text-xs flex items-center gap-1"><Calendar :size="12" />{{ formatDate(n.createdAt) }}</span>
          <span class="text-gray-400 text-xs">{{ n.authorName }}</span>
        </button>
      </div>

      <!-- Pagination -->
      <div class="flex justify-center gap-1 mt-4">
        <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-500 text-[0.8125rem] transition-colors hover:bg-bg-page hover:text-navy disabled:text-[#d1d5db] disabled:cursor-not-allowed" :disabled="page === 1" @click="loadPage(page - 1)"><ChevronLeft :size="14" /></button>
        <button
          v-for="p in pageRange"
          :key="p"
          class="w-9 h-9 flex items-center justify-center rounded-lg text-[0.8125rem] transition-colors"
          :class="p === page ? 'bg-blue text-white font-semibold cursor-default' : 'text-gray-500 hover:bg-bg-page hover:text-navy'"
          @click="loadPage(p)"
        >{{ p }}</button>
        <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-500 text-[0.8125rem] transition-colors hover:bg-bg-page hover:text-navy disabled:text-[#d1d5db] disabled:cursor-not-allowed" :disabled="page >= totalPages" @click="loadPage(page + 1)"><ChevronRight :size="14" /></button>
      </div>
    </div>
  </div>
</template>
