<script setup>
import { computed, reactive, watch } from 'vue'
import { AlertCircle } from 'lucide-vue-next'

const props = defineProps({
  initialValue: {
    type: Object,
    default: () => ({ title: '', content: '' }),
  },
  loading: {
    type: Boolean,
    default: false,
  },
  submitLabel: {
    type: String,
    default: '저장',
  },
  error: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['submit', 'cancel'])

const form = reactive({
  title: '',
  content: '',
})

const titleError = computed(() => form.title.trim() ? '' : '제목을 입력해 주세요.')
const contentError = computed(() => form.content.trim() ? '' : '내용을 입력해 주세요.')
const invalid = computed(() => Boolean(titleError.value || contentError.value))

watch(
  () => props.initialValue,
  (value) => {
    form.title = value?.title ?? ''
    form.content = value?.content ?? ''
  },
  { immediate: true },
)

function handleSubmit() {
  if (invalid.value || props.loading) return
  emit('submit', {
    title: form.title.trim(),
    content: form.content.trim(),
  })
}
</script>

<template>
  <form class="qna-form" @submit.prevent="handleSubmit" novalidate>
    <div v-if="error" class="general-error" style="display:flex">
      <AlertCircle :size="16" />
      <span>{{ error }}</span>
    </div>

    <div class="form-group">
      <label class="form-label" for="qna-title">제목</label>
      <input
        id="qna-title"
        v-model="form.title"
        class="qna-input"
        type="text"
        maxlength="100"
        placeholder="질문 제목을 입력하세요"
        :disabled="loading"
      />
      <p v-if="titleError" class="qna-field-error">{{ titleError }}</p>
    </div>

    <div class="form-group">
      <label class="form-label" for="qna-content">내용</label>
      <textarea
        id="qna-content"
        v-model="form.content"
        class="qna-textarea"
        rows="12"
        placeholder="궁금한 내용을 자세히 작성해 주세요"
        :disabled="loading"
      />
      <p v-if="contentError" class="qna-field-error">{{ contentError }}</p>
    </div>

    <div class="qna-form-actions">
      <button type="button" class="btn btn-ghost" :disabled="loading" @click="emit('cancel')">취소</button>
      <button type="submit" class="btn btn-primary" :disabled="invalid || loading">
        {{ loading ? '저장 중...' : submitLabel }}
      </button>
    </div>
  </form>
</template>

<style scoped>
.qna-form { display:flex; flex-direction:column; gap:1rem; padding:1.5rem; }
.qna-input, .qna-textarea { width:100%; border:1px solid #e5e7eb; border-radius:0.75rem; background:#fff; color:#1A3C6E; font-size:0.875rem; outline:none; transition:border-color 0.15s, box-shadow 0.15s; }
.qna-input { height:2.75rem; padding:0 0.875rem; }
.qna-textarea { padding:0.875rem; resize:vertical; line-height:1.7; min-height:14rem; }
.qna-input:focus, .qna-textarea:focus { border-color:#2D9CDB; box-shadow:0 0 0 3px rgba(45,156,219,0.15); }
.qna-input:disabled, .qna-textarea:disabled { background:#f9fafb; cursor:not-allowed; }
.qna-field-error { color:#DC2626; font-size:0.75rem; margin-top:0.375rem; }
.qna-form-actions { display:flex; justify-content:flex-end; gap:0.5rem; flex-wrap:wrap; }
@media (max-width:480px) {
  .qna-form { padding:1.25rem; }
  .qna-form-actions .btn { width:100%; }
}
</style>
