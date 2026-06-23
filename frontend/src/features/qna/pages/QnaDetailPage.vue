<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AlertCircle, Calendar, CheckCircle2, ChevronLeft, Clock3, FileQuestion, Pencil, Trash2, UserCircle } from 'lucide-vue-next'
import { qnasApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/authStore.js'
import { formatDate } from '@/utils/date.js'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const qna = ref(null)
const loading = ref(false)
const deleting = ref(false)
const error = ref('')

const qnaId = computed(() => qna.value?.qnaId ?? qna.value?.id ?? route.params.id)
const authorId = computed(() => qna.value?.memberId ?? qna.value?.authorId ?? qna.value?.writerId)
const canManage = computed(() => {
  const currentMemberId = authStore.user?.memberId
  return currentMemberId != null && authorId.value != null && String(currentMemberId) === String(authorId.value)
})
const answered = computed(() => qna.value?.status === 'ANSWERED' || Boolean(qna.value?.answerContent ?? qna.value?.answer))
const answerContent = computed(() => qna.value?.answerContent ?? qna.value?.answer?.content ?? qna.value?.answer ?? '')
const answerCreatedAt = computed(() => qna.value?.answerCreatedAt ?? qna.value?.answeredAt ?? qna.value?.answer?.createdAt)

async function loadQna(id = route.params.id) {
  loading.value = true
  error.value = ''
  try {
    qna.value = await qnasApi.getQnaDetail(id)
  } catch {
    error.value = 'Q&A 상세 내용을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  if (!confirm('질문을 삭제하시겠습니까?')) return
  deleting.value = true
  error.value = ''
  try {
    await qnasApi.deleteQna(qnaId.value)
    router.push('/qnas')
  } catch {
    error.value = '질문을 삭제하지 못했습니다. 권한을 확인하거나 잠시 후 다시 시도해 주세요.'
  } finally {
    deleting.value = false
  }
}

watch(() => route.params.id, (id) => loadQna(id), { immediate: true })
</script>

<template>
  <div class="qna-page">
    <div class="qna-wrap">
      <button class="back-link" @click="router.push('/qnas')">
        <ChevronLeft :size="16" /> 목록으로
      </button>

      <div v-if="loading" class="qna-loading">불러오는 중...</div>
      <div v-else-if="error" class="general-error" style="display:flex">
        <AlertCircle :size="16" />
        <span>{{ error }}</span>
      </div>
      <template v-else-if="qna">
        <article class="card qna-detail-card">
          <header class="qna-detail-header">
            <div class="qna-detail-title-line">
              <span class="qna-status" :class="{ answered }">{{ answered ? '답변 완료' : '답변 대기' }}</span>
              <h1>{{ qna.title }}</h1>
            </div>
            <div class="qna-meta">
              <span><UserCircle :size="14" />{{ qna.authorName ?? qna.memberName ?? qna.writerName ?? '-' }}</span>
              <span><Calendar :size="14" />{{ formatDate(qna.createdAt) }}</span>
            </div>
            <div v-if="canManage" class="qna-actions">
              <button class="btn btn-ghost btn-sm" @click="router.push(`/qnas/${qnaId}/edit`)">
                <Pencil :size="14" /> 수정
              </button>
              <button class="btn btn-outline-danger btn-sm" :disabled="deleting" @click="handleDelete">
                <Trash2 :size="14" /> {{ deleting ? '삭제 중...' : '삭제' }}
              </button>
            </div>
          </header>

          <section class="qna-content">
            <FileQuestion :size="18" />
            <div>{{ qna.content }}</div>
          </section>
        </article>

        <section class="card qna-answer-card">
          <div class="qna-answer-title">
            <CheckCircle2 v-if="answered" :size="18" />
            <Clock3 v-else :size="18" />
            <h2>답변</h2>
          </div>
          <div v-if="answered" class="qna-answer-content">
            <p>{{ answerContent }}</p>
            <span v-if="answerCreatedAt">답변일 {{ formatDate(answerCreatedAt) }}</span>
          </div>
          <p v-else class="qna-answer-empty">아직 답변이 등록되지 않았습니다.</p>
        </section>
      </template>
    </div>
  </div>
</template>

<style scoped>
.qna-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.qna-wrap { max-width:56rem; margin:0 auto; }
.back-link { display:inline-flex; align-items:center; gap:0.375rem; color:#6b7280; font-size:0.8125rem; font-weight:500; cursor:pointer; background:none; border:none; margin-bottom:1rem; transition:color 0.15s; }
.back-link:hover { color:#2D9CDB; }
.qna-loading { padding:4rem; text-align:center; color:#9ca3af; }
.qna-detail-card, .qna-answer-card { overflow:hidden; margin-bottom:1rem; }
.qna-detail-header { padding:1.25rem 1.5rem; border-bottom:1px solid #f3f4f6; }
.qna-detail-title-line { display:flex; align-items:flex-start; gap:0.75rem; flex-wrap:wrap; }
.qna-detail-title-line h1 { color:#1A3C6E; font-size:1.25rem; font-weight:700; line-height:1.4; flex:1; min-width:16rem; }
.qna-status { flex-shrink:0; border-radius:999px; padding:0.25rem 0.625rem; background:#FEF3C7; color:#92400E; font-size:0.75rem; font-weight:700; }
.qna-status.answered { background:#E0F2FE; color:#0369A1; }
.qna-meta { display:flex; align-items:center; gap:1rem; flex-wrap:wrap; margin-top:0.75rem; color:#9ca3af; font-size:0.75rem; }
.qna-meta span { display:flex; align-items:center; gap:0.25rem; }
.qna-actions { display:flex; justify-content:flex-end; gap:0.5rem; margin-top:1rem; flex-wrap:wrap; }
.qna-content { display:grid; grid-template-columns:auto 1fr; gap:0.75rem; padding:1.5rem; color:#4b5563; font-size:0.9375rem; line-height:1.8; white-space:pre-wrap; }
.qna-content svg { color:#2D9CDB; margin-top:0.25rem; }
.qna-answer-card { padding:1.5rem; }
.qna-answer-title { display:flex; align-items:center; gap:0.5rem; color:#1A3C6E; margin-bottom:1rem; }
.qna-answer-title h2 { font-size:1rem; font-weight:700; }
.qna-answer-content p { white-space:pre-wrap; color:#4b5563; line-height:1.8; font-size:0.9375rem; }
.qna-answer-content span { display:block; color:#9ca3af; font-size:0.75rem; margin-top:1rem; }
.qna-answer-empty { color:#9ca3af; font-size:0.875rem; }
@media (max-width:480px) {
  .qna-actions .btn { width:100%; }
  .qna-content { grid-template-columns:1fr; }
}
</style>
