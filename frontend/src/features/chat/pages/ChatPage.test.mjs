import assert from 'node:assert/strict'
import { readFile } from 'node:fs/promises'
import { test } from 'node:test'

test('chat page is routed, linked, and sends messages through chatApi', async () => {
  const [router, navbar, page] = await Promise.all([
    readFile(new URL('../../../router/index.js', import.meta.url), 'utf8'),
    readFile(new URL('../../../components/layout/AppNavbar.vue', import.meta.url), 'utf8'),
    readFile(new URL('./ChatPage.vue', import.meta.url), 'utf8'),
  ])

  assert.match(router, /path:\s*'\/chat'/)
  assert.match(router, /name:\s*'ChatPage'/)
  assert.match(router, /requiresAuth:\s*true/)
  assert.match(navbar, /to="\/chat"/)
  assert.match(navbar, /AI 채팅/)
  assert.match(page, /sendChatMessage/)
  assert.match(page, /ragUsed/)
  assert.match(page, /handleSubmit/)
})
