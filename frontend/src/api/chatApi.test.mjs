import assert from 'node:assert/strict'
import { test } from 'node:test'

test('sendChatMessage posts message to chat endpoint and returns response data', async () => {
  const httpModule = await import('./http.js')
  const calls = []
  const originalPost = httpModule.http.post
  httpModule.http.post = async (url, body) => {
    calls.push({ url, body })
    return { data: { answer: 'AI 응답입니다.', ragUsed: true } }
  }

  try {
    const { sendChatMessage } = await import('./chatApi.js')
    const result = await sendChatMessage('서울 아파트 가격 알려줘')

    assert.deepEqual(calls, [
      { url: '/api/chat', body: { message: '서울 아파트 가격 알려줘' } },
    ])
    assert.deepEqual(result, { answer: 'AI 응답입니다.', ragUsed: true })
  } finally {
    httpModule.http.post = originalPost
  }
})
