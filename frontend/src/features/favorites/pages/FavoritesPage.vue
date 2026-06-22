<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, X, Search, Heart, MapPin, Calendar, Trash2, AlertTriangle, Loader2 } from 'lucide-vue-next'
import { useFavoritesStore } from '@/stores/favoritesStore.js'
import { formatDate } from '@/utils/date.js'
import BaseButton from '@/components/base/BaseButton.vue'

const favoritesStore = useFavoritesStore()

const showAdd = ref(false)
const newRegionCode = ref('')
const adding = ref(false)
const searchQuery = ref('')
const pendingDelete = ref(null)
const deleting = ref(false)

const filtered = computed(() => {
  const q = searchQuery.value.toLowerCase()
  return favoritesStore.items.filter(f =>
    !q || ((f.dongName ?? '') + (f.sigunguName ?? '') + (f.sidoName ?? '')).toLowerCase().includes(q)
  )
})

async function handleAdd() {
  if (!newRegionCode.value.trim() || newRegionCode.value.length !== 10) {
    alert('지역코드는 10자리여야 합니다')
    return
  }
  adding.value = true
  try {
    await favoritesStore.addFavorite(newRegionCode.value.trim())
    newRegionCode.value = ''
    showAdd.value = false
  } catch (err) {
    alert(err.data?.message ?? '추가에 실패했습니다')
  } finally {
    adding.value = false
  }
}

function startDelete(item) { pendingDelete.value = item }

async function confirmDelete() {
  if (!pendingDelete.value) return
  deleting.value = true
  try {
    await favoritesStore.removeFavorite(pendingDelete.value.favoriteId)
  } finally {
    deleting.value = false
    pendingDelete.value = null
  }
}

onMounted(() => favoritesStore.fetchFavorites())
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page py-8 px-4">
    <div class="max-w-3xl mx-auto">
      <!-- Header -->
      <div class="flex items-center justify-between flex-wrap gap-3 mb-6">
        <div>
          <h1 class="text-navy text-[1.375rem] font-bold">관심지역</h1>
          <p class="text-gray-400 text-[0.8125rem] mt-1">
            저장된 관심지역 <span class="text-blue">{{ favoritesStore.count }}</span>개
          </p>
        </div>
        <BaseButton size="sm" @click="showAdd = !showAdd">
          <Plus v-if="!showAdd" :size="16" /><X v-else :size="16" />
          {{ showAdd ? '취소' : '관심지역 추가' }}
        </BaseButton>
      </div>

      <!-- Add form -->
      <div v-if="showAdd" class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] p-5 mb-6">
        <h3 class="text-navy text-[0.9375rem] font-semibold mb-4">새 관심지역 등록</h3>
        <div class="flex flex-col sm:flex-row sm:items-end gap-3">
          <div class="flex-1">
            <label class="block text-navy text-xs font-semibold mb-1.5">지역코드 (10자리)</label>
            <input
              v-model="newRegionCode"
              type="text"
              class="w-full px-4 py-2.5 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)] placeholder:text-gray-400"
              placeholder="예: 1168010100"
              maxlength="10"
            />
          </div>
          <BaseButton size="sm" variant="navy" class="h-10 whitespace-nowrap" :disabled="adding" @click="handleAdd">
            <Loader2 v-if="adding" :size="14" class="animate-spin" />추가
          </BaseButton>
        </div>
        <p class="text-gray-400 text-xs mt-2">지역코드는 검색 페이지의 실거래가 상세 정보에서 확인할 수 있습니다.</p>
      </div>

      <!-- Search -->
      <div v-if="favoritesStore.count > 0" class="relative mb-4">
        <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none"><Search :size="18" /></span>
        <input
          v-model="searchQuery"
          type="text"
          class="w-full py-3 pl-11 pr-4 rounded-xl bg-white border border-[#e5e7eb] text-navy text-sm outline-none shadow-[0_1px_3px_rgba(0,0,0,0.05)] transition-all focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)] placeholder:text-gray-400"
          placeholder="지역명으로 검색..."
        />
      </div>

      <!-- Error -->
      <div v-if="favoritesStore.error" class="flex items-center gap-2 px-5 py-4 bg-[#FEF2F2] border border-[#FECACA] rounded-xl text-[#DC2626] text-sm mb-4">
        <AlertTriangle :size="16" class="shrink-0" />
        {{ favoritesStore.error }}
      </div>

      <!-- Empty state -->
      <div v-if="filtered.length === 0" class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] flex flex-col items-center justify-center py-16 px-4">
        <div class="w-16 h-16 rounded-full bg-bg-page flex items-center justify-center mb-4">
          <Heart :size="32" class="text-[#d1d5db]" />
        </div>
        <p class="text-navy text-base font-semibold mb-1">
          {{ searchQuery ? '검색 결과가 없습니다' : '등록된 관심지역이 없습니다' }}
        </p>
        <p class="text-gray-400 text-[0.8125rem] text-center">
          {{ searchQuery ? '다른 검색어로 시도해 주세요' : '상단의 추가 버튼으로 관심지역을 등록해 보세요' }}
        </p>
      </div>

      <!-- List -->
      <div v-else class="flex flex-col gap-3">
        <div
          v-for="item in filtered"
          :key="item.favoriteId"
          class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] px-5 py-4 flex items-center gap-4 transition-shadow hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)]"
        >
          <div class="w-10 h-10 rounded-xl bg-blue/10 flex items-center justify-center shrink-0 text-blue">
            <MapPin :size="18" />
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-navy text-[0.9375rem] font-semibold truncate">{{ item.dongName || item.sigunguName }}</p>
            <div class="flex items-center gap-3 mt-1 flex-wrap">
              <span class="text-gray-400 text-xs flex items-center gap-1"><MapPin :size="12" />{{ item.sidoName }} {{ item.sigunguName }}</span>
              <span class="text-gray-400 text-xs flex items-center gap-1"><Calendar :size="12" />{{ formatDate(item.createdAt) }}</span>
            </div>
          </div>
          <button
            class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0 text-[#d1d5db] transition-colors hover:bg-red/5 hover:text-red"
            aria-label="삭제"
            @click="startDelete(item)"
          ><Trash2 :size="16" /></button>
        </div>
      </div>
    </div>
  </div>

  <!-- Delete modal -->
  <div v-if="pendingDelete" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
    <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="pendingDelete = null"></div>
    <div class="relative bg-white rounded-2xl shadow-[0_20px_60px_rgba(0,0,0,0.2)] w-full max-w-[380px] overflow-hidden">
      <div class="px-6 pt-8 pb-2 text-center">
        <div class="w-14 h-14 rounded-full bg-red/10 flex items-center justify-center mx-auto mb-4">
          <AlertTriangle :size="24" class="text-red" />
        </div>
        <h3 class="text-navy text-lg font-bold mb-2">관심지역 삭제</h3>
        <p class="text-gray-400 text-[0.8125rem]">이 관심지역을 삭제하시겠습니까?</p>
      </div>
      <div class="flex gap-3 px-6 py-6">
        <BaseButton variant="ghost" :full="true" @click="pendingDelete = null">취소</BaseButton>
        <BaseButton variant="danger" :full="true" :disabled="deleting" @click="confirmDelete">
          <Loader2 v-if="deleting" :size="14" class="animate-spin" />삭제
        </BaseButton>
      </div>
    </div>
  </div>
</template>
