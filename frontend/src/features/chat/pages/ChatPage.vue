<script setup>
import { computed, nextTick, ref } from 'vue'
import { AlertCircle, Bot, Loader2, MessageCircle, Send, Sparkles, UserCircle } from 'lucide-vue-next'
import { chatApi } from '@/api/index.js'

const input = ref('')
const messages = ref([
  {
    id: 1,
    role: 'assistant',
    content: '안녕하세요. 관심 지역, 실거래가, 주거 환경에 대해 질문해 주세요.',
    ragUsed: false,
  },
])
const loading = ref(false)
const error = ref('')
const messageList = ref(null)

const trimmedInput = computed(() => input.value.trim())
const canSubmit = computed(() => trimmedInput.value.length > 0 && !loading.value)

function createMessage(role, content, ragUsed = false) {
  return {
    id: Date.now() + Math.random(),
    role,
    content,
    ragUsed,
  }
}

async function scrollToLatest() {
  await nextTick()
  if (messageList.value) {
    messageList.value.scrollTop = messageList.value.scrollHeight
  }
}

async function handleSubmit() {
  if (!canSubmit.value) return

  const question = trimmedInput.value
  input.value = ''
  error.value = ''
  messages.value.push(createMessage('user', question))
  await scrollToLatest()

  loading.value = true
  try {
    const response = await chatApi.sendChatMessage(question)
    messages.value.push(createMessage('assistant', response.answer || '응답 내용이 없습니다.', response.ragUsed))
  } catch {
    error.value = '답변을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } finally {
    loading.value = false
    await scrollToLatest()
  }
}
</script>

<template>
  <main class="min-h-[calc(100vh-64px)] bg-bg-page py-6 px-4">
    <section class="max-w-[64rem] mx-auto h-[calc(100vh-112px)] min-h-[36rem] flex flex-col bg-white border border-gray-100 rounded-2xl shadow-[0_1px_3px_rgba(0,0,0,0.05)] overflow-hidden">
      <header class="px-5 sm:px-6 py-4 border-b border-gray-100 flex items-center justify-between gap-4">
        <div class="flex items-center gap-3 min-w-0">
          <div class="w-10 h-10 rounded-xl bg-blue/10 text-blue flex items-center justify-center shrink-0">
            <Bot :size="20" />
          </div>
          <div class="min-w-0">
            <h1 class="text-navy text-lg sm:text-xl font-bold truncate">AI 채팅</h1>
            <p class="text-gray-400 text-xs sm:text-[0.8125rem] mt-0.5 truncate">SSAFY Home 데이터 기반으로 질문에 답변합니다.</p>
          </div>
        </div>
        <div class="hidden sm:inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#f0fdf4] text-[#15803d] text-xs font-bold">
          <Sparkles :size="14" />
          로그인 전용
        </div>
      </header>

      <div ref="messageList" class="flex-1 overflow-y-auto px-4 sm:px-6 py-5 space-y-4 bg-[#F8FAFC]">
        <div v-for="message in messages" :key="message.id" class="flex gap-3" :class="message.role === 'user' ? 'justify-end' : 'justify-start'">
          <div v-if="message.role === 'assistant'" class="w-8 h-8 rounded-full bg-blue text-white flex items-center justify-center shrink-0 mt-1">
            <Bot :size="16" />
          </div>

          <article class="max-w-[min(36rem,82%)]">
            <div
              class="px-4 py-3 rounded-2xl text-sm leading-relaxed whitespace-pre-wrap break-words"
              :class="message.role === 'user'
                ? 'bg-blue text-white rounded-tr-md shadow-[0_2px_8px_rgba(45,156,219,0.22)]'
                : 'bg-white text-[#374151] border border-gray-100 rounded-tl-md'"
            >{{ message.content }}</div>
            <div v-if="message.role === 'assistant' && message.ragUsed" class="mt-1.5 inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-blue/10 text-blue text-[0.6875rem] font-bold">
              <MessageCircle :size="12" />
              문서 기반
            </div>
          </article>

          <div v-if="message.role === 'user'" class="w-8 h-8 rounded-full bg-navy text-white flex items-center justify-center shrink-0 mt-1">
            <UserCircle :size="16" />
          </div>
        </div>

        <div v-if="loading" class="flex items-center gap-3 text-gray-400 text-sm">
          <div class="w-8 h-8 rounded-full bg-blue text-white flex items-center justify-center">
            <Loader2 :size="16" class="animate-spin" />
          </div>
          AI가 답변을 작성하는 중...
        </div>
      </div>

      <div class="border-t border-gray-100 bg-white px-4 sm:px-6 py-4">
        <div v-if="error" class="mb-3 flex items-center gap-2 bg-red/5 border border-red/20 rounded-xl px-3 py-2 text-red text-[0.8125rem] font-medium">
          <AlertCircle :size="16" />
          <span>{{ error }}</span>
        </div>

        <form class="flex items-end gap-2" @submit.prevent="handleSubmit">
          <label class="sr-only" for="chat-message">질문 입력</label>
          <textarea
            id="chat-message"
            v-model="input"
            rows="2"
            class="flex-1 resize-none rounded-xl bg-bg-page border border-gray-200 px-4 py-3 text-sm text-navy outline-none transition-colors placeholder:text-gray-400 focus:border-blue focus:bg-white"
            placeholder="예: 강남구 최근 아파트 거래 흐름을 알려줘"
            :disabled="loading"
            @keydown.enter.exact.prevent="handleSubmit"
          ></textarea>
          <button
            type="submit"
            class="w-12 h-12 rounded-xl bg-blue text-white flex items-center justify-center shrink-0 shadow-[0_2px_8px_rgba(45,156,219,0.3)] transition-colors hover:bg-blue-hover disabled:bg-gray-300 disabled:shadow-none disabled:cursor-not-allowed"
            :disabled="!canSubmit"
            aria-label="메시지 전송"
          >
            <Loader2 v-if="loading" :size="18" class="animate-spin" />
            <Send v-else :size="18" />
          </button>
        </form>
      </div>
    </section>
  </main>
</template>
