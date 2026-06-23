<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ChevronLeft, FileText, Calendar, AlertCircle } from 'lucide-vue-next'
import { noticesApi } from '@/api/index.js'
import { formatDate } from '@/utils/date.js'

const router = useRouter()
const route = useRoute()
const notice = ref(null)
const loading = ref(false)
const error = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    notice.value = await noticesApi.getNoticeDetail(route.params.id)
  } catch {
    error.value = '공지사항을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page py-8 px-4">
    <div class="max-w-[56rem] mx-auto">
      <button
        class="inline-flex items-center gap-1.5 text-gray-500 text-[0.8125rem] font-medium cursor-pointer mb-4 transition-colors hover:text-blue"
        @click="router.push('/notices')"
      >
        <ChevronLeft :size="16" /> 목록으로
      </button>

      <div v-if="loading" class="py-16 text-center text-gray-400">불러오는 중...</div>
      <div v-else-if="error" class="flex items-center gap-2 px-5 py-4 bg-[#FEF2F2] border border-[#FECACA] rounded-xl text-[#DC2626] text-sm">
        <AlertCircle :size="16" class="shrink-0" />
        {{ error }}
      </div>
      <div v-else-if="notice" class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] overflow-hidden">
        <div class="px-6 py-5 border-b border-gray-100">
          <h2 class="text-navy text-xl font-bold mb-3">{{ notice.title }}</h2>
          <div class="flex items-center gap-4 flex-wrap">
            <span class="text-gray-400 text-xs flex items-center gap-1">
              <FileText :size="14" />{{ notice.authorName }}
            </span>
            <span class="text-gray-400 text-xs flex items-center gap-1">
              <Calendar :size="14" />{{ formatDate(notice.createdAt) }}
            </span>
          </div>
        </div>
        <div class="px-6 py-6">
          <div class="whitespace-pre-wrap text-[#4b5563] text-sm leading-relaxed">{{ notice.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
