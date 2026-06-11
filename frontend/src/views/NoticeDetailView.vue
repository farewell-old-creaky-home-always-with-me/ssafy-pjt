<template>
  <div class="notices-page">
    <div class="notices-wrap">
      <button class="back-link" @click="router.push('/notices')">
        <ChevronLeft :size="16" /> 목록으로
      </button>

      <div v-if="loading" style="padding:4rem;text-align:center;color:#9ca3af">불러오는 중...</div>
      <div v-else-if="notice" class="card" style="overflow:hidden">
        <div style="padding:1.25rem 1.5rem;border-bottom:1px solid #f3f4f6">
          <h2 style="color:#1A3C6E;font-size:1.25rem;font-weight:700;margin-bottom:0.75rem">{{ notice.title }}</h2>
          <div style="display:flex;align-items:center;gap:1rem;flex-wrap:wrap">
            <span style="color:#9ca3af;font-size:0.75rem;display:flex;align-items:center;gap:0.25rem">
              <FileText :size="14" />{{ notice.authorName }}
            </span>
            <span style="color:#9ca3af;font-size:0.75rem;display:flex;align-items:center;gap:0.25rem">
              <Calendar :size="14" />{{ formatDate(notice.createdAt) }}
            </span>
          </div>
        </div>
        <div style="padding:1.5rem">
          <div style="white-space:pre-wrap;color:#4b5563;font-size:0.875rem;line-height:1.8">{{ notice.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ChevronLeft, FileText, Calendar } from 'lucide-vue-next'
import { api } from '../api/index.js'

const router = useRouter()
const route = useRoute()
const notice = ref(null)
const loading = ref(false)

function formatDate(iso) {
  const d = new Date(iso)
  return `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')}`
}

onMounted(async () => {
  loading.value = true
  try { notice.value = await api.get(`/api/notices/${route.params.id}`) }
  finally { loading.value = false }
})
</script>

<style scoped>
.notices-page { min-height:calc(100vh - 64px); background:#F4F6F9; padding:2rem 1rem; }
.notices-wrap { max-width:56rem; margin:0 auto; }
.back-link { display:inline-flex; align-items:center; gap:0.375rem; color:#6b7280; font-size:0.8125rem; font-weight:500; cursor:pointer; background:none; border:none; margin-bottom:1rem; transition:color 0.15s; }
.back-link:hover { color:#2D9CDB; }
</style>
