<script setup>
import { computed, onMounted, ref } from 'vue'
import { AlertCircle, Calendar, Download, FileText, Languages, MapPin, RefreshCw } from 'lucide-vue-next'
import { batchReportsApi } from '@/api/index.js'
import { formatDate } from '@/utils/date.js'

const report = ref(null)
const loading = ref(false)
const error = ref('')
const pdfOpenError = ref('')

const isEmpty = computed(() => error.value === 'NOT_FOUND')
const canOpenPdf = computed(() => report.value?.reportId && report.value?.status === 'PDF_COMPLETED')

function formatDateTime(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  return `${formatDate(value)} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function formatYearMonth(value) {
  if (!value || value.length !== 6) return value ?? '-'
  return `${value.slice(0, 4)}.${value.slice(4, 6)}`
}

function sourceTypeLabel(value) {
  const labels = {
    HOUSE_DEAL: '주택 거래',
  }
  return labels[value] ?? value ?? '-'
}

function reportTypeLabel(value) {
  const labels = {
    REFLECTION: 'AI 요약/번역 리포트',
  }
  return labels[value] ?? value ?? '-'
}

function statusLabel(value) {
  const labels = {
    PDF_COMPLETED: 'PDF 생성 완료',
    COMPLETED: '요약 완료',
    PROCESSING: '생성 중',
    FAILED: '생성 실패',
  }
  return labels[value] ?? value ?? '-'
}

async function loadLatestReport() {
  loading.value = true
  error.value = ''
  pdfOpenError.value = ''
  try {
    report.value = await batchReportsApi.getLatestBatchReport()
  } catch (err) {
    report.value = null
    error.value = err.status === 404 ? 'NOT_FOUND' : 'LOAD_FAILED'
  } finally {
    loading.value = false
  }
}

function openPdf() {
  if (!canOpenPdf.value) return

  pdfOpenError.value = ''
  const pdfWindow = window.open(batchReportsApi.getBatchReportPdfUrl(report.value.reportId), '_blank', 'noopener')
  if (!pdfWindow) {
    pdfOpenError.value = 'PDF를 열 수 없습니다. 팝업 차단 여부를 확인해 주세요.'
  }
}

onMounted(loadLatestReport)
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page py-8 px-4">
    <div class="max-w-[64rem] mx-auto">
      <div class="flex items-center justify-between flex-wrap gap-3 mb-6">
        <div>
          <h1 class="text-navy text-2xl font-bold">AI 리포트</h1>
          <p class="text-gray-400 text-[0.8125rem] mt-1">Batch AI가 생성한 최신 요약/번역 PDF 리포트를 확인하세요.</p>
        </div>
        <button
          class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-white border border-gray-200 text-navy text-sm font-semibold transition-colors hover:border-blue hover:text-blue disabled:opacity-60 disabled:cursor-not-allowed"
          :disabled="loading"
          @click="loadLatestReport"
        >
          <RefreshCw :size="16" :class="{ 'animate-spin': loading }" />
          새로고침
        </button>
      </div>

      <div v-if="loading" class="py-16 text-center text-gray-400">AI 리포트를 불러오는 중...</div>

      <div v-else-if="isEmpty" class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] px-6 py-14 text-center">
        <FileText :size="44" class="text-[#d1d5db] mx-auto mb-4" />
        <h2 class="text-navy text-lg font-bold mb-2">아직 생성된 AI 리포트가 없습니다</h2>
        <p class="text-gray-400 text-sm">Batch 작업이 완료되면 이 화면에서 최신 리포트를 볼 수 있습니다.</p>
      </div>

      <div v-else-if="error" class="flex items-center gap-2 bg-red/5 border border-red/20 rounded-xl px-4 py-3 text-red text-[0.8125rem] font-medium">
        <AlertCircle :size="16" />
        <span>AI 리포트를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.</span>
      </div>

      <article v-else-if="report" class="space-y-4">
        <div v-if="pdfOpenError" class="flex items-center gap-2 bg-red/5 border border-red/20 rounded-xl px-4 py-3 text-red text-[0.8125rem] font-medium">
          <AlertCircle :size="16" />
          <span>{{ pdfOpenError }}</span>
        </div>

        <section class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] overflow-hidden">
          <div class="px-6 py-5 border-b border-gray-100 flex items-start justify-between gap-4 flex-wrap">
            <div>
              <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue/10 text-blue text-xs font-bold mb-3">
                <FileText :size="14" />
                {{ statusLabel(report.status) }}
              </div>
              <h2 class="text-navy text-xl font-bold">{{ reportTypeLabel(report.reportType) }}</h2>
              <p class="text-gray-400 text-xs mt-1">{{ report.pdfFileName || 'PDF 리포트' }}</p>
            </div>
            <button
              class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-blue text-white text-sm font-semibold shadow-[0_2px_8px_rgba(45,156,219,0.3)] transition-colors hover:bg-blue-hover disabled:bg-gray-300 disabled:shadow-none disabled:cursor-not-allowed"
              :disabled="!canOpenPdf"
              @click="openPdf"
            >
              <Download :size="16" />
              PDF 보기
            </button>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-0 border-b border-gray-100">
            <div class="px-6 py-4 border-b sm:border-r sm:border-b-0 border-gray-100">
              <p class="text-gray-400 text-xs mb-1">지역 코드</p>
              <p class="text-navy text-sm font-semibold flex items-center gap-1.5"><MapPin :size="14" />{{ report.regionCode ?? '-' }}</p>
            </div>
            <div class="px-6 py-4 border-b lg:border-r lg:border-b-0 border-gray-100">
              <p class="text-gray-400 text-xs mb-1">기준 월</p>
              <p class="text-navy text-sm font-semibold flex items-center gap-1.5"><Calendar :size="14" />{{ formatYearMonth(report.yearMonth) }}</p>
            </div>
            <div class="px-6 py-4 border-b sm:border-r sm:border-b-0 border-gray-100">
              <p class="text-gray-400 text-xs mb-1">데이터 유형</p>
              <p class="text-navy text-sm font-semibold">{{ sourceTypeLabel(report.sourceType) }}</p>
            </div>
            <div class="px-6 py-4">
              <p class="text-gray-400 text-xs mb-1">생성일</p>
              <p class="text-navy text-sm font-semibold">{{ formatDateTime(report.createdAt) }}</p>
            </div>
          </div>

          <div class="px-6 py-5">
            <p class="text-gray-400 text-xs mb-1">마지막 수정</p>
            <p class="text-navy text-sm font-semibold">{{ formatDateTime(report.updatedAt) }}</p>
          </div>
        </section>

        <section class="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] p-6">
            <div class="flex items-center gap-2 text-navy mb-4">
              <FileText :size="18" />
              <h3 class="text-base font-bold">한국어 요약</h3>
            </div>
            <p class="text-[#4b5563] text-sm leading-relaxed whitespace-pre-wrap">{{ report.summary || '요약 내용이 없습니다.' }}</p>
          </div>

          <div class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] p-6">
            <div class="flex items-center gap-2 text-navy mb-4">
              <Languages :size="18" />
              <h3 class="text-base font-bold">English Summary</h3>
            </div>
            <p class="text-[#4b5563] text-sm leading-relaxed whitespace-pre-wrap">{{ report.translatedSummary || 'No translated summary.' }}</p>
          </div>
        </section>
      </article>
    </div>
  </div>
</template>
