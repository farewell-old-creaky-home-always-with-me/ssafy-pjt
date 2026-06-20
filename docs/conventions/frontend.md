# Frontend Conventions

## Project Structure

```
src/
├── api/
│   ├── http.js           # Axios instance (shared client)
│   ├── authApi.js
│   └── membersApi.js
├── components/
│   ├── base/             # Domain-free reusable UI
│   └── layout/           # Layout-only components
├── composables/
├── features/             # Domain-scoped pages and components
│   └── {domain}/
│       ├── pages/
│       └── components/
├── router/
│   └── index.js
├── stores/
├── utils/
└── main.js
```

`components/base` — domain-free reusable UI only. `features/{domain}` — pages and components for a specific domain. `stores` — globally shared state only.

## Naming

**Files:**

| Kind | Pattern | Example |
|------|---------|---------|
| Page component | `*Page.vue` | `LoginPage.vue` |
| Base UI component | `Base*.vue` | `BaseButton.vue` |
| Layout component | `App*.vue` | `AppHeader.vue` |
| Domain component | PascalCase, 2+ words | `MemberProfile.vue` |
| Pinia store | `*Store.js` | `authStore.js` |
| Composable | `use*.js` | `usePagination.js` |
| API module | `*Api.js` | `membersApi.js` |

Component names must be two or more words to avoid collision with HTML tags (`App.vue` is exempt).

API module names mirror the REST resource path segment: `/api/members` → `membersApi.js`, `/api/auth` → `authApi.js`. Use plural for collection resources and singular/uncountable for non-collection resources.

**Functions:** Use `handle` prefix for event and component event handlers. Use a descriptive verb for API calls, transforms, and validators.

```javascript
function handleSubmit() {}  // event handler
function fetchMember() {}   // API call
```

**Path alias:** `@` maps to `src`. Use `@` when the relative path is two or more levels deep.

## SFC Order

`<script setup>` → `<template>` → `<style>`

Use `scoped` on component-level styles. Global styles, resets, and CSS variables belong in global CSS files.

## Props and Emits

Declare types and required flag on all props. Use kebab-case for component events. Use `update:modelValue` for `v-model` events.

```javascript
defineProps({
  memberId: { type: Number, required: true },
})
defineEmits(['submit-profile', 'update:modelValue'])
```

## Template

- Always set `:key` on `v-for`.
- Never put `v-if` and `v-for` on the same element — extract filtering into a computed.

## Composable

Extract to a composable only when state logic is reused across multiple components. Simple format functions belong in `utils/`.

## Pinia

Write stores in setup style. Include only state shared across views, derived state, and the actions that mutate it. View-local state and logic stay in the component.

```javascript
export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isLoggedIn = computed(() => user.value !== null)

  async function login(email, password) {
    user.value = await authApi.login(email, password)
  }

  async function logout() {
    await authApi.logout()
    user.value = null
  }

  return { user, isLoggedIn, login, logout }
})
```

## Vue Router

Set `name` and `meta` on every route. Load page components with dynamic import. Router guards handle auth and permission checks only — no data fetching or business logic.

```javascript
{
  path: '/profile',
  name: 'ProfilePage',
  component: () => import('@/features/member/pages/ProfilePage.vue'),
  meta: { requiresAuth: true },
}

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'LoginPage' }
  }
})
```

## API

All HTTP calls go through the Axios instance in `api/http.js`. API modules return `response.data` — callers must not depend on the raw `AxiosResponse` shape. Endpoint strings live inside the API module, not in components or stores.

```javascript
// api/http.js
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
})

// api/memberApi.js
export async function getMember(memberId) {
  const response = await http.get(`/api/members/${memberId}`)
  return response.data
}
```

## Environment Variables

Prefix client-exposed variables with `VITE_`. Never store secrets in frontend `.env` files.

## Prohibited

| Area | Prohibited |
|------|-----------|
| API | `axios` imported directly in a component |
| API | Backend URL strings assembled in components or stores |
| API | Returning raw `AxiosResponse` without extracting `.data` |
| State | View-local state placed in Pinia |
| Router guard | Data fetching or business logic |
| Template | `v-if` and `v-for` on the same element |
| Template | `v-for` without `:key` |
| Component | Single-word component name (except `App.vue`) |
| Env | Secrets stored under `VITE_` prefix |
