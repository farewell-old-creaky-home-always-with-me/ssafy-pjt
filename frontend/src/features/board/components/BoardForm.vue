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

const titleError = computed(() => {
  if (!form.title.trim()) return '제목을 입력해 주세요.'
  if (form.title.trim().length > 200) return '제목은 200자 이하로 입력해 주세요.'
  return ''
})
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
  <form class="board-form" novalidate @submit.prevent="handleSubmit">
    <div v-if="error" class="general-error" style="display:flex">
      <AlertCircle :size="16" />
      <span>{{ error }}</span>
    </div>

    <div class="form-group">
      <label class="form-label" for="board-title">제목</label>
      <input
        id="board-title"
        v-model="form.title"
        class="board-input"
        type="text"
        maxlength="200"
        placeholder="공유할 이야기의 제목을 입력하세요"
        :disabled="loading"
      />
      <p v-if="titleError" class="board-field-error">{{ titleError }}</p>
    </div>

    <div class="form-group">
      <label class="form-label" for="board-content">내용</label>
      <textarea
        id="board-content"
        v-model="form.content"
        class="board-textarea"
        rows="14"
        placeholder="지역 정보, 생활 팁, 궁금한 이야기를 자유롭게 작성해 주세요"
        :disabled="loading"
      />
      <p v-if="contentError" class="board-field-error">{{ contentError }}</p>
    </div>

    <div class="board-form-actions">
      <button type="button" class="btn btn-ghost" :disabled="loading" @click="emit('cancel')">취소</button>
      <button type="submit" class="btn btn-primary" :disabled="invalid || loading">
        {{ loading ? '저장 중...' : submitLabel }}
      </button>
    </div>
  </form>
</template>

<style scoped>
.board-form { display:flex; flex-direction:column; gap:1rem; padding:1.5rem; }
.board-input, .board-textarea { width:100%; border:1px solid #e5e7eb; border-radius:0.75rem; background:#fff; color:#1A3C6E; font-size:0.875rem; outline:none; transition:border-color 0.15s, box-shadow 0.15s; }
.board-input { height:2.75rem; padding:0 0.875rem; }
.board-textarea { padding:0.875rem; resize:vertical; line-height:1.7; min-height:16rem; }
.board-input:focus, .board-textarea:focus { border-color:#2D9CDB; box-shadow:0 0 0 3px rgba(45,156,219,0.15); }
.board-input:disabled, .board-textarea:disabled { background:#f9fafb; cursor:not-allowed; }
.board-field-error { color:#DC2626; font-size:0.75rem; margin-top:0.375rem; }
.board-form-actions { display:flex; justify-content:flex-end; gap:0.5rem; flex-wrap:wrap; }
@media (max-width:480px) {
  .board-form { padding:1.25rem; }
  .board-form-actions .btn { width:100%; }
}
</style>
